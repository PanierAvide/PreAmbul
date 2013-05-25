/*
 * Copyright or © or Copr. Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN (03/31/2013)
 * 
 * This software is a computer program whose purpose is to guide the user during hikes.
 *
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */

package net.line2soft.preambul.controllers;

import net.line2soft.preambul.models.Location;
import net.line2soft.preambul.views.SlippyMapActivity;

import org.mapsforge.core.GeoPoint;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * This class is the controller the map part of the application.
 * It's made using the singleton pattern.
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class MapController {
// ATTRIBUTES
	/** Current instance of MapController **/
	private static MapController instance = null;
	/** Application context **/
	private Context ctx;
	/** The location shown on the map **/
	private Location currentLocation;
	/** The path to display on map. Values are: -1 to hide all excursions, 0 to show all excursions, >=1 to show one specific excursion **/
	private int pathToDisplay = 0;
	/** The navigation instruction to display on map. Values are: -1 for no one, >=0 for a precise instruction. **/
	private int instructionToDisplay = -1;
	/** The GeoPoint to display **/
	private GeoPoint pointToDisplay = null;
	/** The point which was selected by user on map **/
	private GeoPoint selectedPoint = null;

// CONSTRUCTOR
	/**
	 * Class constructor
	 * @param ctx An application context
	 */
	private MapController(Context ctx) {
		this.ctx = ctx;
	}

// ACCESSORS
	/**
	 * Returns the current instance of MapController
	 * @param ctx An application context
	 * @return The current MapControler
	 */
	public static MapController getInstance(Context ctx) {
		if(MapController.instance==null) {
			synchronized(MapController.class){
				if(MapController.instance == null) {
					MapController.instance = new MapController(ctx);
				}
			}
		}
		return MapController.instance;
	}
	
	/**
	 * Returns the current instance of MapController (context not needed)
	 * @return The current MapControler
	 */
	public static MapController getInstance() {
		if(MapController.instance==null) {
			synchronized(MapController.class){
				if(MapController.instance == null) {
					MapController.instance = new MapController(null);
				}
			}
		}
		return MapController.instance;
	}

	/**
	 * Returns the current context
	 * @return The context
	 */
	public Context getContext() {
		return ctx;
	}

	/**
	 * Returns the current location, shown on the map
	 * @return The current location
	 */
	public Location getCurrentLocation() {
		return currentLocation;
	}
	
	/**
	 * Get the path to display on map
	 * @return The path to display (Values: -1 to hide all excursions, 0 to show all excursions, >=1 to show one specific excursion)
	 */
	public int getPathToDisplay() {
		return pathToDisplay;
	}
	
	/**
	 * Get the instruction to display
	 * @return The instruction to display (Values: -1 for no one, >=0 for a precise instruction)
	 */
	public int getInstructionToDisplay() {
		return instructionToDisplay;
	}
	
	/**
	 * Get the point to display
	 * @return The point to display (null if no one)
	 */
	public GeoPoint getPointToDisplay() {
		return pointToDisplay;
	}
	
	/**
	 * Get the selected point on map
	 * @return The selected point
	 */
	public GeoPoint getSelectedPoint() {
		return selectedPoint;
	}

// MODIFIERS
	/**
	 * Set the current location
	 * @param loc The new location
	 */
	public void setCurrentLocation(Location loc) {
		currentLocation = loc;
	}

	/**
	 * Set the path to display on map
	 * @param i The path to display (Values: -1 to hide all excursions, 0 to show all excursions, >=1 to show one specific excursion)
	 */
	public void setPathToDisplay(int i) {
		pathToDisplay = i;
	}
	
	/**
	 * Set the navigation instruction to display on map
	 * @param itemId The instruction to display (Values: -1 for none, >=0 for an instruction)
	 */
	public void setInstructionToDisplay(int i) {
		instructionToDisplay = i;
	}
	
	/**
	 * Set the point to display on map
	 * @param point The point to display
	 */
	public void setPointToDisplay(GeoPoint point) {
		pointToDisplay = point;
	}
	
	/**
	 * Set the selected point on map
	 * @param point The selected point
	 */
	public void setPointSelected(GeoPoint point) {
		selectedPoint = point;
	}
	
	/**
	 * Resets all values contained in MapController (instruction to display, path to display, selected point and point to display)
	 */
	public void resetValues() {
		this.setInstructionToDisplay(-1);
		this.setPathToDisplay(0);
		this.setPointSelected(null);
		this.setPointToDisplay(null);
	}

// OTHER METHODS
	/**
	 * Resets the current instance. Has only interest for unit tests.
	 */
	public static void resetInstance() {
		instance = null;
	}
	/**
	 * Starts the SlippyMapActivity and launch its displayPosition method
	 */
	public void startSlippyMapWithPositionCentered(Activity act,double lat,double lon){
		Intent intent = new Intent(act, SlippyMapActivity.class);
		setPointToDisplay(new GeoPoint(lat, lon));
		act.startActivity(intent);
	}
}
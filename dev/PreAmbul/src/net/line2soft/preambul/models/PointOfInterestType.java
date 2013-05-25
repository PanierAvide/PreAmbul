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

package net.line2soft.preambul.models;

import java.util.Arrays;
import java.util.HashMap;

import net.line2soft.preambul.utils.RWFile;


import android.graphics.drawable.Drawable;

/**
 * This class represents a type of a point of interest
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class PointOfInterestType {
// ATTRIBUTES
	/** The ID of the type, which is its name in english **/
	private String id;
	/** The name of the category **/
	private String name;
	/** The icon of the category **/
	private Drawable icon;
	/** The list of POIs **/
	private HashMap<String,PointOfInterest> pois;
	/** The category which contains this type **/
	private PointOfInterestCategory category;

// CONSTRUCTOR
	/**
	 * Class constructor
	 * @param id The ID of the type (its name in english)
	 * @param name The displayable name of the type
	 * @param icon The icon
	 */
	public PointOfInterestType(String id, String name, Drawable icon, PointOfInterestCategory category) {
		this.id = id;
		this.name = name;
		this.icon = icon;
		this.category = category;
		pois = new HashMap<String,PointOfInterest>();
	}

// ACCESSORS
	/**
	 * Returns the ID of the type
	 * @return The ID
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Returns the name of the type
	 * @return The name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the icon of the type
	 * @return The icon
	 */
	public Drawable getIcon() {
		return icon;
	}
	
	/**
	 * Return all POIs in this type
	 * @return All POIs
	 */
	public PointOfInterest[] getPois() {
		return Arrays.copyOf(pois.values().toArray(), pois.size(), PointOfInterest[].class);
	}
	
	/**
	 * Returns the wanted {@link PointOfInterest}
	 * @param id The ID of the POI (see {@link RWFile}.formatCoordinates(lat, lon))
	 * @return The wanted POI
	 */
	public PointOfInterest getPoi(String id) {
		return pois.get(id);
	}
	
	/**
	 * Returns the category which contains this type of {@link PointOfInterest}
	 * @return The category
	 */
	public PointOfInterestCategory getCategory() {
		return category;
	}

// MODIFIERS
	/**
	 * Adds a new {@link PointOfInterest} in this type.
	 * @param poi The POI to add
	 */
	public void addPoint(PointOfInterest poi) {
		pois.put(RWFile.formatCoordinates(poi.getPoint().getLatitude(), poi.getPoint().getLongitude()),poi);
	}
}
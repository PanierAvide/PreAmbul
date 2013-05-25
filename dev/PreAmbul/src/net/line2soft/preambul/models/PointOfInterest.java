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

import net.line2soft.preambul.utils.RWFile;

import org.mapsforge.core.GeoPoint;


import java.io.File;
import java.net.URL;

/**
 * This class represents a point of interest : shops, points of view, places for tourism...
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class PointOfInterest extends NamedPoint {
// ATTRIBUTES
	/** description of a POI */
	private String description;
	/** Sound associate to a POI */
	private File sound;
	/** Link associate to a POI */
	private URL link;
	/** The type associated to this POI */
	private PointOfInterestType type;

// CONSTRUCTOR

	/**
	 * Class constructor
	 * @param point The geodesic point where the POI is located
	 * @param isFavorite 
	 * @param link 
	 * @param cmt 
	 * @param desc 
	 * @param name
	 * @param type
	 */
	public PointOfInterest(GeoPoint point, String name, String desc, String cmt, URL link, PointOfInterestType type, boolean isFavorite) {
		super(point, name, cmt, isFavorite);
		this.description = desc;
		this.sound = null;
		this.link = link;
		this.type = type;
	}

// ACCESSORS
	/**
	 * Get the geodesic point of the POI
	 * @return The point
	 */
	public GeoPoint getPoint() {
		return point;
	}
	
	/**
	 * Get the description of a point
	 * @return The description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Get the comment of a point
	 * @return The comment
	 */
	public String getComment() {
		return comment;
	}
	
	/**
	 * Get the type of a point, may return null if type isn't set
	 * @return The comment
	 */
	public PointOfInterestType getType(){
		return type;
	}
	
	/**
	 * Get the sound of a point
	 * @return The file
	 */
	public File getSound() {
		return sound;
	}
	
	/** 
	 * Get the link of a point
	 * @return The link
	 */
	public URL getLink() {
		return link;
	}
	/**
	 * Returns the POI ID
	 * @return The ID
	 */
	public String getId() {
		return RWFile.formatCoordinates(point.getLatitude(), point.getLongitude());
	}
	
// MODIFIERS
	/**
	 * Set the geodesic point of the POI
	 * @param point The point
	 */
	public void setPoint(GeoPoint point) {
		this.point = point;
	}
	
	/**
	 * Set the sound file associate to a POI
	 */
	public void setSound(File f) {
		sound = f;
	}
	
	/**
	 * Set the type associated to a POI
	 */
	public void setType(PointOfInterestType t) {
		type=t;
	}
}
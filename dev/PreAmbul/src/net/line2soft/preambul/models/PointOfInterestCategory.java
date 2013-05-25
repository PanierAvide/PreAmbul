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
import android.graphics.drawable.Drawable;

/**
 * This class represents a category of a point of interest : natural, dining, places....
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class PointOfInterestCategory {
// ATTRIBUTES
	/** The ID of the category, which is its name in english **/
	private String id;
	/** The name of the category **/
	private String name;
	/** The types contained in this category **/
	private HashMap<String, PointOfInterestType> types;
	/** The icon of the category **/
	private Drawable icon;

// CONSTRUCTOR
	/**
	 * Class constructor
	 * @param id The ID of the category (its name in english)
	 * @param name The name of the category (displayable name)
	 * @param icon The associated icon
	 */
	public PointOfInterestCategory(String id, String name, Drawable icon) {
		this.id = id;
		this.name = name;
		this.icon = icon;
		types = new HashMap<String, PointOfInterestType>();
	}

// ACCESSORS
	/**
	 * Returns the wanted type
	 * @param id The ID of the wanted type
	 * @return The type
	 */
	public PointOfInterestType getType(String id) {
		if(id=="other" && !types.containsKey("other")) {
			types.put("other", new PointOfInterestType("other", "Autres", null, this));
		}
		return types.get(id);
	}
	
	/**
	 * Returns all the types contained in this category.
	 * @return The types
	 */
	public PointOfInterestType[] getTypes() {
		return Arrays.copyOf(types.values().toArray(), types.size(), PointOfInterestType[].class);
	}
	
	/**
	 * Returns the ID of the category
	 * @return The ID
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Returns the name of the category
	 * @return The name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the icon of the category
	 * @return The icon
	 */
	public Drawable getIcon() {
		return icon;
	}

// MODIFIERS
	/**
	 * Add a new type in this category
	 * @param currentType The type to add
	 */
	public void addType(PointOfInterestType currentType) {
		types.put(currentType.getId(), currentType);
	}
}
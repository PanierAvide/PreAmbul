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

import java.util.HashMap;

import net.line2soft.preambul.R;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * This class represents a locomotion, which is a kind of transport (by feet, bicycle, ...)
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class Locomotion {
// ATTRIBUTES
	/** The list of locomotions **/
	private static HashMap<String,Locomotion> list;
	/** The key of the locomotion **/
	private String key;
	/** The name which can be displayed **/
	private String displayableName;
	/** The associated icon **/
	private Drawable icon;

// CONSTRUCTOR
	/**
	 * Class constructor
	 * @param key The name of the locomotion (in english)
	 */
	private Locomotion(String key, String displayableName) {
		this.key = key;
		this.displayableName = displayableName;
	}
	
// ACCESSORS
	/**
	 * Returns the icon of the locomotion
	 * @return The icon
	 */
	public Drawable getIcon() {
		return icon;
	}
	
	/**
	 * Returns the name of the locomotion
	 * @return The name
	 */
	public String getName() {
		return displayableName;
	}
	
	/**
	 * The key of the locomotion
	 * @return The key
	 */
	public String getKey() {
		return key;
	}
	
	/**
	 * Get a locomotion
	 * @param key The name of the wanted locomotion
	 * @return The locomotion object
	 */
	public static Locomotion getLocomotion(Context ctx, String key) {
		if(list == null) {
			loadLocomotions(ctx);
		}
		return list.get(key);
	}
	
	/**
	 * Returns the size of the locomotions list
	 * @return The size
	 */
	public static int getCount(Context ctx) {
		if(list==null) {
			loadLocomotions(ctx);
		}
		return list.size();
	}
	
	/**
	 * Returns the list of locomotions currently loaded
	 * @param ctx An application context
	 * @return The list of locomotions
	 */
	/*public static String listToString(Context ctx) {
		if(list==null) {
			loadLocomotions(ctx);
		}
		String result = "Locomotions list:";
		for(Locomotion l : list.values()) {
			result += "\n\t-"+l.getKey()+" (Name: "+l.getName()+")";
		}
		return result;
	}*/
	
	public static String findKey(Context ctx, String name) {
		if(list==null) {
			loadLocomotions(ctx);
		}
		String result = "other";
		for(Locomotion l : list.values()) {
			if(name.equals(l.getName())) { result = l.getKey(); break; }
		}
		return result;
	}

// MODIFIERS
	/**
	 * Edits the icon of the locomotion
	 * @param icon The new icon
	 */
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	
// OTHER METHODS
	/**
	 * Loads locomotions from resources
	 * @param ctx An application context
	 */
	private static void loadLocomotions(Context ctx) {
		String[] locomotionsStr = ctx.getResources().getStringArray(R.array.locomotion);
		String[] locomotionsStrDis = ctx.getResources().getStringArray(R.array.locomotion_fr);
		list = new HashMap<String,Locomotion>(locomotionsStr.length);
		for(int i=0; i < locomotionsStr.length; i++) {
			list.put(locomotionsStr[i], new Locomotion(locomotionsStr[i], locomotionsStrDis[i]));
		}
	}
	
	/**
	 * Is the wanted locomotion existing ?
	 * @param ctx An application context
	 * @param key The wanted locomotion
	 * @return True if it's existing, false else
	 */
	public static boolean exists(Context ctx, String key) {
		if(list==null) {
			loadLocomotions(ctx);
		}
		return list.containsKey(key);
	}
	
	/**
	 * Is this object the same as the object in parameter ?
	 * @param l The other object
	 * @return True if they are the same, false else
	 */
	public boolean equals(Locomotion l) {
		return key.equals(l.getKey());
	}
}
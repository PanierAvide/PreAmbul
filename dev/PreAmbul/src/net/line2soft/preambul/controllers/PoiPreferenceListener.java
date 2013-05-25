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

import java.util.HashMap;
import java.util.Set;

import net.line2soft.preambul.models.PointOfInterestCategory;
import net.line2soft.preambul.models.PointOfInterestType;
import net.line2soft.preambul.views.PoiPreferenceActivity;

import net.line2soft.preambul.R;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * This class reacts to events in {@link PoiPreferenceActivity}
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class PoiPreferenceListener implements OnClickListener, OnPreferenceClickListener {
// ATTRIBUTES
	/** The current activity **/
	private PoiPreferenceActivity activity;
	/** All the POIs categories **/
	HashMap<String,PointOfInterestCategory> categories;

// CONSTRUCTOR
	/**
	 * Class constructor
	 * @param act The current activity
	 */
	public PoiPreferenceListener(PoiPreferenceActivity act, HashMap<String,PointOfInterestCategory> cat) {
		activity = act;
		categories=cat;
	}
	
	@Override
	public void onClick(View arg0) {
		if(arg0==activity.findViewById(R.id.buttonCheckAll)){
			Set<String> keys=categories.keySet();
        	PointOfInterestCategory currentCateg;
            for(String key :keys){
            	currentCateg=categories.get(key);
            	//Set the preferenceCategory  for the current POICategory
            	PointOfInterestType[] currentTypes=currentCateg.getTypes();
            	for(PointOfInterestType currentType:currentTypes){
            		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(activity);
        			SharedPreferences.Editor editor = settings.edit();
        			editor.putBoolean("type"+currentType.getId(),true);
        			editor.commit();
            		((CheckBoxPreference)activity.findPreference("type"+currentType.getId())).setChecked(true); 		
            	}
            }			
		}else if(arg0==activity.findViewById(R.id.buttonUncheckAll)){
			Set<String> keys=categories.keySet();
        	PointOfInterestCategory currentCateg;
        	
            for(String key :keys){
            	currentCateg=categories.get(key);
            	//Set the preferenceCategory  for the current POICategory
            	PointOfInterestType[] currentTypes=currentCateg.getTypes();
            	for(PointOfInterestType currentType:currentTypes){
            		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(activity);
        			SharedPreferences.Editor editor = settings.edit();
        			editor.putBoolean("type"+currentType.getId(),false);
        			editor.commit();
            		((CheckBoxPreference)activity.findPreference("type"+currentType.getId())).setChecked(false); 		
            	}
            }		
		}
	}
	
	@Override
	public boolean onPreferenceClick(Preference arg) {
		Boolean result=false;
		if(arg instanceof CheckBoxPreference){
			arg=(CheckBoxPreference)arg;
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(activity);
			SharedPreferences.Editor editor = settings.edit();
			if(settings.getBoolean(arg.getKey(),true)){
    			editor.putBoolean(arg.getKey(),true);
    			editor.commit();
			}else{
    			editor.putBoolean(arg.getKey(),false);
    			editor.commit();
			}
		}else{
			Set<String>keys =categories.keySet();
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(activity);
			SharedPreferences.Editor editor = settings.edit();
            for(String key :keys){
            	if(arg.getKey().equals("allButton"+key)){
            		PointOfInterestType[] types=categories.get(key).getTypes();
            		Boolean areAllChecked=false;
            		for(PointOfInterestType i :types){
            			if(settings.getBoolean("type"+i.getId(),false)){
            				areAllChecked=true;
            			}
            		}
            		for(PointOfInterestType i:types){            			
            			if(areAllChecked){
            				((CheckBoxPreference)activity.findPreference("type"+i.getId())).setChecked(false);
                			editor.putBoolean("type"+i.getName(),false);
                			editor.commit();
                		}else{
                			((CheckBoxPreference)activity.findPreference("type"+i.getId())).setChecked(true);
                			editor.putBoolean("type"+i.getName(),true); 
                			editor.commit();               			
                		}
            		}
            	}
            }
		}
		return result;
	}
}
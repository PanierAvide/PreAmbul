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

package net.line2soft.preambul.views;

import java.util.HashMap;
import java.util.Set;

import net.line2soft.preambul.controllers.PoiPreferenceListener;
import net.line2soft.preambul.models.PointOfInterestCategory;
import net.line2soft.preambul.models.PointOfInterestType;
import net.line2soft.preambul.utils.RWFile;

import net.line2soft.preambul.R;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
 
/**
 * The activity of PoiPreference
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class PoiPreferenceActivity extends PreferenceActivity {
//ATTRIBUTES
	/** The POIs categories **/
	HashMap<String, PointOfInterestCategory> categories;
	/** The listener **/
	PoiPreferenceListener listener;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		//Check if this version of Android allows to use custom title bars
		boolean feature=requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		/** Set layout **/
		//Base layout
		super.onCreate(savedInstanceState);
		//set title bar
		if(feature){
	        	getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.activity_title_bar);
	            TextView myTitleText = (TextView) findViewById(R.id.textView0);
	            myTitleText.setText(getString(R.string.title_activity_poi_preference));	
	    }
		setContentView(R.layout.activity_poi_preference);
		
		try {
			categories = RWFile.readPoiCategoryXML(this, R.xml.poi_categories_fr);
		} catch (Exception e) {
			findViewById(R.id.buttonCheckAll).setVisibility(View.GONE);
			findViewById(R.id.buttonUncheckAll).setVisibility(View.GONE);
		}
		
		listener=new PoiPreferenceListener(this, categories);
		findViewById(R.id.buttonCheckAll).setOnClickListener(listener);
		findViewById(R.id.buttonUncheckAll).setOnClickListener(listener);
		
        setPreferenceScreen(createPreferenceHierarchy());
    }
    
    /**
     * Create a preference hierarchy
     * @return root
     */
    private PreferenceScreen createPreferenceHierarchy() {
        // Root
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
        
		try {
            Set<String> keys=categories.keySet();
        	PointOfInterestCategory currentCateg;
        	PreferenceCategory prefCat;
        	Preference allButton;
        	
            for(String key :keys){
            	//Set the preferenceCategory  for the current POICategory
            	prefCat=new PreferenceCategory(this);
            	currentCateg=categories.get(key);
            	prefCat.setTitle(currentCateg.getName());
            	root.addPreference(prefCat);
            	
            	//Set the "ALL" button
            	allButton=new Preference(this);
            	allButton.setKey("allButton"+key);
            	allButton.setTitle(R.string.name_button_toggle);
            	allButton.setSummary("");
            	allButton.setOnPreferenceClickListener(listener);
            	allButton.setEnabled(true);
            	prefCat.addPreference(allButton);
            	
            	//Set the types inside the preferenceCategory
            	PointOfInterestType[] currentTypes=currentCateg.getTypes();
            	CheckBoxPreference currentPref;
            	for(PointOfInterestType currentType:currentTypes){
                	currentPref=new CheckBoxPreference(this);
                	currentPref.setKey("type"+currentType.getId());
                	currentPref.setTitle(currentType.getName());
                	currentPref.setOnPreferenceClickListener(listener);
                	currentPref.setPersistent(true);
                	currentPref.setChecked(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("type"+currentType.getId(), true));
                	prefCat.addPreference(currentPref);            		
            	}
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
        return root;
    }

	@Override
	protected void onDestroy() {
	    RWFile.unbindDrawables(findViewById(R.id.RootView));
	    System.gc();
	    super.onDestroy();
	}
}
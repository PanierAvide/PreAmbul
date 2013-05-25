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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.line2soft.preambul.controllers.LocationUninstallListener;
import net.line2soft.preambul.controllers.LocationsController;
import net.line2soft.preambul.models.Location;
import net.line2soft.preambul.models.LocationComparator;
import net.line2soft.preambul.views.LocationActivity;

import net.line2soft.preambul.R;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Activity used to uninstall locations.
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class LocationUninstallActivity extends LocationActivity {
// ATTRIBUTES
	/** attribute used to access the data model through a controller**/
	private LocationsController controller;
	/** listener for the whole activity**/
	private LocationUninstallListener listener;

// MODIFIERS
	@Override
	public void setLocationList(Location[] newList) {
		ListView list = (ListView) findViewById(R.id.uninstallListView);
		List<Location> listColl = Arrays.asList(newList);
		Collections.sort(listColl, new LocationComparator());
		Object[] locTmp = listColl.toArray();
		newList = Arrays.copyOf(locTmp, locTmp.length, Location[].class);
        list.setAdapter(new CheckableLocationAdapter(this,newList));
	}

// OTHER METHODS
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Calls the superclass constructor
		super.onCreate(savedInstanceState);
		//Check if this version of Android allows to use custom title bars		
		boolean feature=requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		//set the content as set in the xml
		setContentView(R.layout.activity_location_uninstall);
		listener=new LocationUninstallListener(this);
		
		//if this version allows custom title bars set the custom bar and the textView in it
        if(feature){
        	getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.activity_title_bar);
            TextView myTitleText = (TextView) findViewById(R.id.textView0);
            myTitleText.setText(getString(R.string.title_activity_location_uninstall));
        }

		//get an instance of the controller and set the ListView with the data. If no data, display a toast
		controller=LocationsController.getInstance(this);
		HashMap<Integer,Location> coll=controller.getInstalledLocations();
		if(!coll.isEmpty()){
			Object[] locTmp = coll.values().toArray();
			Location[] items = Arrays.copyOf(locTmp, locTmp.length, Location[].class);
			ListView list = (ListView) findViewById(R.id.uninstallListView);
			setLocationList(items);
			list.setOnItemClickListener(listener);
			
			//set the content of the autoCompleteTextView and its adapter and listener
			int i=0;
			String[] autoComp= new String[items.length];
			while(i<items.length){
				autoComp[i]=items[i].getName();
				i++;
			}
			AutoCompleteTextView autoText=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);
			autoText.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,autoComp));
			autoText.setOnItemClickListener(listener);
			autoText.setOnEditorActionListener(listener);
			//set the listeners of the buttons
			findViewById(R.id.buttonUninstall).setOnClickListener(listener);
		}
		else {
			displayInfo(getString(R.string.message_no_installed_locations));
			finish();
		}
		ImageButton deleteButton = (ImageButton) findViewById(R.id.deleteButton);
		deleteButton.setOnClickListener(listener);
		findViewById(R.id.view1).requestFocus();
	}
	
	/**
	 * Fill a list of location
	 */
	private void fillLocationList() {
		HashMap<Integer,Location> coll=controller.getInstalledLocations();
		if(!coll.isEmpty()){
			Object[] locTmp = coll.values().toArray();
			Location[] items = Arrays.copyOf(locTmp, locTmp.length, Location[].class);
			setLocationList(items);
			
			//set the content of the autoCompleteTextView and its adapter and listener
			int i=0;
			String[] autoComp= new String[items.length];
			while(i<items.length){
				autoComp[i]=items[i].getName();
				i++;
			}
			AutoCompleteTextView autoText=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);
			autoText.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,autoComp));
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  super.onConfigurationChanged(newConfig);
	  setContentView(R.layout.activity_location_uninstall);
	  fillLocationList();
	}
}
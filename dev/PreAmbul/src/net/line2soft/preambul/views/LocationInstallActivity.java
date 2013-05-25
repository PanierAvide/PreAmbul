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

import net.line2soft.preambul.controllers.LocationInstallListener;
import net.line2soft.preambul.controllers.LocationsController;
import net.line2soft.preambul.models.Location;
import net.line2soft.preambul.models.LocationComparator;
import net.line2soft.preambul.utils.Network;
import net.line2soft.preambul.views.LocationActivity;

import net.line2soft.preambul.R;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Activity used to install locations.
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class LocationInstallActivity extends LocationActivity {
// ATTRIBUTES
	/** attribute used to access the data model through a controller**/
	private LocationsController controller;
	/** listener for the whole activity**/
	private LocationInstallListener listener;
	/** The asynchronous task which downloads location list **/
	private static DownloadLocationListTask dllt;

// MODIFIERS
	@Override
	public void setLocationList(Location[] newList) {
		ListView list = (ListView) findViewById(R.id.installListView);
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
		setContentView(R.layout.activity_location_install);
		//if this version allows custom title bars set the custom bar and the textView in it
        if(feature){
        	getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.activity_title_bar);
            TextView myTitleText = (TextView) findViewById(R.id.textView0);
            myTitleText.setText(getString(R.string.title_activity_location_install));	
        }
        
        //Add listener to buttons
        listener = new LocationInstallListener(this);
		findViewById(R.id.view1).requestFocus();
		findViewById(R.id.deleteButton).setOnClickListener(listener);
		
		//Get the location list
		showWaitDialog(getString(R.string.message_wait), getString(R.string.message_network_check));
		dllt = new DownloadLocationListTask();
		dllt.execute(this);
	}
	
	/**
	 * Fills the locations list when the list's download is done.
	 */
	private void fillLocationList() {
		//Get needed views
		ListView list = (ListView) findViewById(R.id.installListView);
		AutoCompleteTextView autoText=(AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
		
		HashMap<Integer, Location> coll = null;
		try {
			coll = dllt.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(coll != null && !coll.isEmpty()){
			Object[] locTmp = coll.values().toArray();
			Location[] items = Arrays.copyOf(locTmp, locTmp.length, Location[].class);

			if(items != null) {
				//Sort instructions by ID
				List<Location> itemsList = Arrays.asList(items);
				Collections.sort(itemsList, new LocationComparator());
				items=itemsList.toArray(new Location[itemsList.size()]);
			}
			
			String[] autoComp= new String[items.length];
	        list.setAdapter(new CheckableLocationAdapter(this,items));
	        //set the listener of the listView
	        list.setOnItemClickListener(listener);
	        
	        //set the content of the autoCompleteTextView and its adapter and listener
			int i=0;
			while(i<items.length){
				autoComp[i]=items[i].getName();
				i++;
			}
			autoText.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,autoComp));
			autoText.setOnItemClickListener(listener);
			autoText.setOnEditorActionListener(listener);
			//set the buttons listeners
			findViewById(R.id.buttonInstall).setOnClickListener(listener);
		}
		else {
			displayInfo(getString(R.string.message_no_installable_location));
			finish();
		}
		hideWaitDialog();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  super.onConfigurationChanged(newConfig);
	  if(LocationsController.getInstance(this).isDownloadRunning()) {
		  showWaitDialog(getString(R.string.message_wait), getString(R.string.message_running_download));
	  }
	  else if(LocationsController.getInstance(this).isInstallRunning()) {
		  showWaitDialog(getString(R.string.message_wait), getString(R.string.message_install_running));
	  }
	  fillLocationList();
	}

// INNER CLASS
	private class DownloadLocationListTask extends AsyncTask<LocationActivity,Void,HashMap<Integer,Location>> {
		@Override
		protected HashMap<Integer, Location> doInBackground(LocationActivity... activity) {
			LocationActivity act = activity[0];
			HashMap<Integer,Location> coll = null;
			//if no airplane mode and Internet available
			if(Network.checkAvailableNetwork(act)==true) {
				//if connection to the locations server is possible
				controller=LocationsController.getInstance(act);
				if(Network.checkConnection(controller.ONLINE_LIST_URL)==true) {
					coll = controller.getInstallableLocations();
				}
				else {
					//if the server is unavailable, display a toast
					act.displayInfo(getString(R.string.message_unreachable_server));
					act.finish();
				}
			}
			else {
				//if the network interface doesn't allow an access to the Internet, show a dialog to the user
				act.showNoConnectionDialog();
			}
			return coll;
		}
		@Override
		protected void onPostExecute(HashMap<Integer,Location> result) {
			super.onPostExecute(result);
			fillLocationList();
		}
	}
}
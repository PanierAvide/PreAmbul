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

import net.line2soft.preambul.views.CheckableLocationAdapter;
import net.line2soft.preambul.views.LocationInstallActivity;
import net.line2soft.preambul.R;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

/**
 * This listener reacts to events in {@link LocationInstallActivity}.
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class LocationInstallListener implements AdapterView.OnItemClickListener, View.OnClickListener, TextView.OnEditorActionListener {
// ATTRIBUTES
	/** The current activity to listen **/
	private LocationInstallActivity activity;

// CONSTRUCTOR
	/**
	 * Class constructor.
	 * @param act The activity to listen
	 */
	public LocationInstallListener(LocationInstallActivity act) {
		activity = act;
	}

// OTHER METHODS
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		CheckBox cbItem = (CheckBox) view.findViewById(R.id.locationSelected);
		//If a checkbox was found, toggle (only for location list)
		if(cbItem != null) {
			//Reaction to location item click (toggle checkbox)
			cbItem.toggle();
			((CheckableLocationAdapter) parent.getAdapter()).setItemCheck(position, cbItem.isChecked());
		}
		else {
			//Reaction to autocompletetextview suggestion
			LocationsController.getInstance(activity).searchLocationAndUpdateView(((TextView) view).getText().toString(), LocationsController.INSTALL_VIEW);
		}
	}

	@Override
	public void onClick(View v) {
		//Reaction to click on install
		if(v==activity.findViewById(R.id.buttonInstall)) {
	        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	        builder.setCancelable(true);
	        builder.setMessage(R.string.message_install_photos);
	        builder.setTitle(R.string.dialog_title_install_photos);
	        //Set the listener to install the locations and their photos
	        builder.setPositiveButton(R.string.name_button_yes, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	                activity.showWaitDialog(activity.getString(R.string.message_wait), activity.getString(R.string.message_running_download));
	    			ListView list = (ListView) activity.findViewById(R.id.installListView);
	    			activity.findViewById(R.id.buttonInstall).setClickable(false);
	    			try {
	    				//Get locations to install
	    				int[] checkedLocationsIds = ((CheckableLocationAdapter) list.getAdapter()).getCheckedLocationsIds();
	    				if(checkedLocationsIds.length > 0) {
	    					//Install
	    					LocationsController.getInstance(activity).installLocationsPhotosPackages(checkedLocationsIds);
	    					
	    					/*activity.hideWaitDialog();
	    					activity.displayInfo(activity.getString(R.string.location_install_finished_message));
	    					activity.onBackPressed();
	    					activity.finish();*/
	    				}
	    				else {
	    					//Show message, user should check one or more location
	    					activity.hideWaitDialog();
	    					activity.findViewById(R.id.buttonInstall).setClickable(true);
	    					activity.displayInfo(activity.getString(R.string.message_no_checked_installable_location));
	    				}
	    			} catch (LocationManagementException e) {
	    				//Display error
	    				activity.hideWaitDialog();
	    				activity.displayInfo(e.getMessage());
	    				activity.onBackPressed();
	    				activity.finish();
	    			}
	            }
	        });
	        
	        //Set the listener to install the locations without their photos
	        builder.setNegativeButton(R.string.name_button_no, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	activity.showWaitDialog(activity.getString(R.string.message_wait), activity.getString(R.string.message_running_download));
	    			ListView list = (ListView) activity.findViewById(R.id.installListView);
	    			activity.findViewById(R.id.buttonInstall).setClickable(false);
	    			try {
	    				//Get locations to install
	    				int[] checkedLocationsIds = ((CheckableLocationAdapter) list.getAdapter()).getCheckedLocationsIds();
	    				if(checkedLocationsIds.length > 0) {
	    					//Install
	    					LocationsController.getInstance(activity).installLocationsPackages(checkedLocationsIds);
	    					/*activity.hideWaitDialog();
	    					activity.displayInfo(activity.getString(R.string.location_install_finished_message));
	    					activity.onBackPressed();
	    					activity.finish();*/
	    				}
	    				else {
	    					//Show message, user should check one or more location
	    					activity.hideWaitDialog();
	    					activity.findViewById(R.id.buttonInstall).setClickable(true);
	    					activity.displayInfo(activity.getString(R.string.message_no_checked_installable_location));
	    				}
	    			} catch (LocationManagementException e) {
	    				//Display error
	    				activity.hideWaitDialog();
	    				activity.displayInfo(e.getMessage());
	    				activity.onBackPressed();
	    				activity.finish();
	    			}
	            }
	        });
	        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
	            public void onCancel(DialogInterface dialog) {
	            	dialog.dismiss();
	            }
	        });

	        builder.show();
	        
	        
			
		}
		//Reaction to delete button
		else if(v==activity.findViewById(R.id.deleteButton)) {
			LocationsController.getInstance(activity).searchLocationAndUpdateView("", LocationsController.INSTALL_VIEW);
		}
	}


	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		//Reaction to click on search button in keyboard
		LocationsController.getInstance(activity).searchLocationAndUpdateView(v.getText().toString(), LocationsController.INSTALL_VIEW);
		return true;
	}
}
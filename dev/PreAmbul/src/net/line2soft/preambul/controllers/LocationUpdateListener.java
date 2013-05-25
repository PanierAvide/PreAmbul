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
import net.line2soft.preambul.views.LocationUpdateActivity;
import net.line2soft.preambul.R;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

/**
 * This class reacts to events in {@link LocationUpdateActivity}.
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class LocationUpdateListener implements AdapterView.OnItemClickListener, View.OnClickListener, TextView.OnEditorActionListener  {
// ATTRIBUTES
	/** The current activity to listen **/
	private LocationUpdateActivity activity;

// CONSTRUCTOR
	/**
	 * Class constructor.
	 * @param act The activity to listen
	 */
	public LocationUpdateListener(LocationUpdateActivity act) {
		activity = act;
	}

// OTHER METHODS
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		CheckBox cbItem = (CheckBox) view.findViewById(R.id.locationSelected);
		//If a checkbox was found, toggle (only for location list)
		if(cbItem != null) {
			//Reaction to location item click
			cbItem.toggle();
			((CheckableLocationAdapter) parent.getAdapter()).setItemCheck(position, cbItem.isChecked());
		}
		else {
			//Reaction to AutoCompleteTextView suggestion item click
			LocationsController.getInstance(activity).searchLocationAndUpdateView(((TextView) view).getText().toString(), LocationsController.UNINSTALL_VIEW);
		}
	}

	@Override
	public void onClick(View v) {
		//Reaction to click on install
		if(v==activity.findViewById(R.id.buttonUpdate)) {
			activity.findViewById(R.id.buttonUpdate).setClickable(false);
			ListView list = (ListView) activity.findViewById(R.id.updateListView);
			try {
				//Get checked locations
				int[] checkedLocationsIds = ((CheckableLocationAdapter) list.getAdapter()).getCheckedLocationsIds();
				if(checkedLocationsIds.length > 0) {
					//Update locations
					activity.showWaitDialog(activity.getString(R.string.message_wait), activity.getString(R.string.message_running_download));
					LocationsController.getInstance(activity).updateLocationsPackages(checkedLocationsIds);
					/*activity.hideWaitDialog();
					activity.displayInfo(activity.getString(R.string.location_update_finished_message));
					activity.onBackPressed();
					activity.finish();*/
				}
				else {
					//No checked locations, display message to user
					activity.hideWaitDialog();
					activity.findViewById(R.id.buttonUpdate).setClickable(true);
					activity.displayInfo(activity.getString(R.string.message_no_checked_updatable_location));
				}
			} catch (LocationManagementException e) {
				//Display error to user
				activity.hideWaitDialog();
				activity.displayInfo(e.getMessage());
				activity.onBackPressed();
				activity.finish();
			}
		}
		//Reaction to click on delete button
		else if(v==activity.findViewById(R.id.deleteButton)) {
			LocationsController.getInstance(activity).searchLocationAndUpdateView("", LocationsController.UPDATE_VIEW);
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		//Reaction to click on search button in keyboard
		LocationsController.getInstance(activity).searchLocationAndUpdateView(v.getText().toString(), LocationsController.UPDATE_VIEW);
		return true;
	}
}
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

import net.line2soft.preambul.utils.Network;
import net.line2soft.preambul.views.ApplicationPreferenceActivity;
import net.line2soft.preambul.views.LocationInstallActivity;
import net.line2soft.preambul.views.LocationSelectionActivity;
import net.line2soft.preambul.views.LocationUninstallActivity;
import net.line2soft.preambul.views.LocationUpdateActivity;
import net.line2soft.preambul.R;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

/**
 * This listener reacts to events in {@link LocationSelectionActivity}
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class LocationSelectionListener implements AdapterView.OnItemClickListener, TextView.OnEditorActionListener, View.OnClickListener {
// ATTRIBUTES
	/** The current activity to listen **/
	private LocationSelectionActivity activity;

// CONSTRUCTOR
	/**
	 * Class constructor.
	 * @param locationSelectionActivity The activity to listen
	 */
	public LocationSelectionListener(LocationSelectionActivity locationSelectionActivity) {
		activity = locationSelectionActivity;
	}

// OTHER METHODS
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//AutoCompleteTextView suggestion items click: launch search
		if(view.getClass().equals(TextView.class)) {
			LocationsController.getInstance(activity).searchLocationAndUpdateView(((TextView) view).getText().toString(), LocationsController.SELECTION_VIEW);
		}
		//Location list item click: launch location
		else {
			LocationsController.getInstance(activity).launchLocation((int) parent.getAdapter().getItemId(position));
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		//Reaction to click on search button in keyboard
		LocationsController.getInstance(activity).searchLocationAndUpdateView(v.getText().toString(), LocationsController.SELECTION_VIEW);
		return true;
	}

	/** 
	 * The reaction to the different options 
	 * @param item The item selected
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result;
		switch (item.getItemId()) {
    	  //if the option selected is install
	      case R.id.install:
	    	  activity.showWaitDialog(activity.getString(R.string.message_wait),activity.getString(R.string.message_network_check));
	    	  boolean value=Network.checkAvailableNetwork(activity);
	    	  activity.hideWaitDialog();
	    	  if(value==false) {
	    		  activity.showNoConnectionDialog();
	    		  result = false;
	    	  }
	    	  else {
	    		  //switch to the activity LocationInstallActivity
	    		  Intent intentInstall = new Intent(activity, LocationInstallActivity.class);
	    		  activity.startActivity(intentInstall);
	    		  result = true;
	    	  }
	          break;
	      //if the option selected is uninstall
	      case R.id.uninstall:
	    	  //switch to the activity LocationUninstallActivity
	    	  Intent intentUninstall = new Intent(activity, LocationUninstallActivity.class);
	    	  activity.startActivity(intentUninstall);
	    	  result = true;
	    	  break;
	      //if the option selected is update
	      case R.id.update:
	    	  activity.showWaitDialog(activity.getString(R.string.message_wait),activity.getString(R.string.message_network_check));
	    	  boolean valueNetwork=Network.checkAvailableNetwork(activity);
	    	  activity.hideWaitDialog();
	    	  if(valueNetwork==false) {
	    		  activity.showNoConnectionDialog();
	    		  result = false;
	    	  }
	    	  else {
	    		  //switch to the activity LocationUpdateActivity
	    		  Intent intentUpdate = new Intent(activity, LocationUpdateActivity.class);
	    		  activity.startActivity(intentUpdate);
	    		  result = true;
	    	  }
	          break;
	      //if the option selected is options
	      case R.id.options:
	    	  	Intent it = new Intent(activity, ApplicationPreferenceActivity.class);
	    	  	activity.startActivity(it);
	    	    result=true;	    	  
	            break;
	            
	      default:
	    	  result = false;
	    	  break;
		}
		return result;
		
	}

	@Override
	public void onClick(View v) {
		//Reaction to delete search button
		LocationsController.getInstance(activity).searchLocationAndUpdateView("", LocationsController.SELECTION_VIEW);
	}
}
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

import java.util.Calendar;

import net.line2soft.preambul.views.ApplicationPreferenceActivity;
import net.line2soft.preambul.views.CompassView;
import net.line2soft.preambul.views.ExcursionListActivity;
import net.line2soft.preambul.views.FavoriteEditActivity;
import net.line2soft.preambul.views.FavoriteListActivity;
import net.line2soft.preambul.views.SlippyMapActivity;

import org.mapsforge.android.maps.MapView;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import net.line2soft.preambul.R;

/**
 * This class reacts to events in {@link SlippyMapActivity}
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class SlippyMapListener implements LocationListener, OnClickListener, ViewPager.OnPageChangeListener, SensorEventListener, OnTouchListener {
// ATTRIBUTES
	/** The current activity to listen **/
	private SlippyMapActivity activity;
	/** The location manager **/
	private LocationManager locationManager;
	/** Values for gravity sensor **/
	private float[] mGravity = new float[3];
	/** Values for magnetic sensor **/
	private float[] mMagnetic = new float[3];
	/** Values for rotation matrix **/
	private float[] mRotationM = new float[9];
	/** Values for rotation matrix after remapping **/
	private float[] mRemapedRotationM = new float[9];
	/** Values for final orientation **/
	private float[] mOrientation = new float[3];

// CONSTRUCTOR
	/**
	 * Class constructor.
	 * @param locationSelectionActivity The activity to listen
	 */
	public SlippyMapListener(SlippyMapActivity activity) {
		this.activity = activity;
	}

// OTHER METHODS
	/** 
	 * The reaction to the different options 
	 * @param item The item selected
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result;
		switch (item.getItemId()) {
    	  //if the option selected is whereAmI
	      case R.id.where_am_i:
	    	  //Location of user
	    	  locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
	    	  //Test if GPS is enabled
	    	  if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
	    		  //Get location
		          Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		          //Test if location is recent (less than 2 minutes)
		          if(location != null && location.getTime() > Calendar.getInstance().getTimeInMillis() - 2 * 60 * 1000) {
		              activity.displayLocation(location.getLatitude(), location.getLongitude());
		          }
		          else {
		        	  activity.displayInfo(activity.getString(R.string.message_map_search_position));
		              locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		          }
	    	  }
	    	  else {
	    		  //Display dialog to alert user
	    		  activity.showDialogGpsNotEnabled();
	    	  }
	          result = true;
	    	  break;
			    
	      //if the option selected is favorites
	      case R.id.favorites:
	    	  	Intent it = new Intent(activity, FavoriteListActivity.class);
	    	  	activity.startActivity(it);
	    	    result=true;	    	  
	    	    break;

	      //if the option selected is options
	      case R.id.options:
	    	  	Intent it2 = new Intent(activity, ApplicationPreferenceActivity.class);
	    	  	activity.startActivity(it2);
	    	    result=true;	    	  
	            break;

	      default:
	    	    result = false;
	    	    break;
		}
		return result;		
	}
	
	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			activity.displayLocation(location.getLatitude(), location.getLongitude());
            locationManager.removeUpdates(this);
        }
	}
	
	@Override
	public void onProviderDisabled(String provider) {}
	
	@Override
	public void onProviderEnabled(String provider) {}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}

	@Override
	public void onClick(View v) {
		//Click on "Excursion" button
		if(v==activity.findViewById(R.id.imageButton1)) {
			Intent it = new Intent(activity, ExcursionListActivity.class);
			activity.startActivity(it);
		}
		else if(v == activity.findViewById(R.id.imageLeft)) {
			ViewPager pager=(ViewPager) activity.findViewById(R.id.pager_nav);
			pager.setCurrentItem(pager.getCurrentItem()-1, true);
		}
		//Click on right button in instructions
		else if(v == activity.findViewById(R.id.imageRight)) {
			ViewPager pager=(ViewPager) activity.findViewById(R.id.pager_nav);
			pager.setCurrentItem(pager.getCurrentItem()+1, true);
			
		}
		else if(v == activity.findViewById(R.id.favoritesButton)){
			Intent it = new Intent(activity, FavoriteEditActivity.class);
			MapController.getInstance(activity).setPointSelected(((MapView)(activity.findViewById(R.id.mapView))).getMapPosition().getMapCenter());
			it.putExtra(FavoriteEditActivity.FAVORITE_ID, "0");
			activity.startActivity(it);
			activity.onPause();
		}
		else if(v == activity.findViewById(R.id.compass)) {
			CompassView cpv = (CompassView) activity.findViewById(R.id.compass);
			cpv.setVisibility(View.GONE);
			CompassView cpvBig = (CompassView) activity.findViewById(R.id.compassBig);
			cpvBig.setVisibility(View.VISIBLE);
		}
		else if(v == activity.findViewById(R.id.compassBig)) {
			CompassView cpv = (CompassView) activity.findViewById(R.id.compass);
			cpv.setVisibility(View.VISIBLE);
			CompassView cpvBig = (CompassView) activity.findViewById(R.id.compassBig);
			cpvBig.setVisibility(View.GONE);
		}
		else if(v == activity.findViewById(R.id.imageViewValidate)) {
			MapController.getInstance(activity).setPointSelected(((MapView)(activity.findViewById(R.id.mapView))).getMapPosition().getMapCenter());
			Intent it = new Intent(activity, FavoriteEditActivity.class);
			activity.startActivity(it);
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {;}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {;}

	@Override
	public void onPageSelected(int arg0) {
		activity.setNavigationInstructionToDisplay(arg0);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		if(sensor.getType() == Sensor.TYPE_ORIENTATION) {
			CompassView cp = (CompassView) activity.findViewById(R.id.compass);
			CompassView cpBig = (CompassView) activity.findViewById(R.id.compassBig);
			if(accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE || accuracy == SensorManager.SENSOR_STATUS_ACCURACY_LOW) {
				activity.setAccurate(false);
				activity.displayInfo(activity.getString(R.string.message_compass_not_accurate));
				cp.setVisibility(View.GONE);
				cpBig.setVisibility(View.GONE);
			} else {
				activity.setAccurate(true);
				if(cp.getVisibility() == View.GONE && cpBig.getVisibility() == View.GONE){
					cp.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		CompassView cp = (CompassView) activity.findViewById(R.id.compass);
		CompassView cpBig = (CompassView) activity.findViewById(R.id.compassBig);
		switch(event.sensor.getType()) {
	        case Sensor.TYPE_ACCELEROMETER:
	        	System.arraycopy(event.values, 0, mGravity, 0, 3); 
	            break;
	        case Sensor.TYPE_MAGNETIC_FIELD:
	        	System.arraycopy(event.values, 0, mMagnetic, 0, 3);
	            break;
	        default:
	            return;
        }
		if (SensorManager.getRotationMatrix(mRotationM, null, mGravity, mMagnetic)){
			SensorManager.remapCoordinateSystem(mRotationM, SensorManager.AXIS_X, SensorManager.AXIS_Z, mRemapedRotationM);
			SensorManager.getOrientation(mRemapedRotationM, mOrientation);
			int mAzimuth = (int) Math.round((Math.toDegrees(mOrientation[0])) *2)/2;
			cp.updateDirection(mAzimuth);
			cpBig.updateDirection(mAzimuth);
		} 
	}

	@Override
	public boolean onTouch(View v, MotionEvent arg1) {
		if(v==activity.findViewById(R.id.mapView)) {
			activity.hideBalloons();
		}
		return false;
	}
}
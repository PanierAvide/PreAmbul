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

import net.line2soft.preambul.models.NamedPoint;
import net.line2soft.preambul.views.ExcursionInfoActivity;
import net.line2soft.preambul.views.ExcursionListActivity;
import net.line2soft.preambul.views.LocationSelectionActivity;
import net.line2soft.preambul.views.SlippyMapActivity;

import org.mapsforge.core.GeoPoint;

import net.line2soft.preambul.R;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

/**
 * This listener reacts to events in {@link ExcursionInfoActivity}
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class ExcursionInfoListener implements View.OnClickListener, AdapterView.OnItemClickListener,ViewPager.OnPageChangeListener {
// ATTRIBUTES
	/** The current activity **/
	private ExcursionInfoActivity activity;

// CONSTRUCTOR
	/**
	 * Class constructor
	 * @param act The current activity
	 */
	public ExcursionInfoListener(ExcursionInfoActivity act) {
		activity = act;
	}

// OTHER METHODS
	@Override
	public void onClick(View arg0) {
		//Launch excursion button
		if(arg0 == activity.findViewById(R.id.button_load_excursion)) {
			MapController.getInstance(activity).setPathToDisplay(activity.getIntent().getIntExtra(ExcursionListActivity.EXCURSION_ID, 0));
			launchMap(null);
		}

		else if(arg0 == activity.findViewById(R.id.imageLeft)) {
			ViewPager pager=(ViewPager) activity.findViewById(R.id.pager_images);
			pager.setCurrentItem(pager.getCurrentItem()-1, true);
		}
		//Click on right button in instructions
		else if(arg0 == activity.findViewById(R.id.imageRight)) {
			ViewPager pager=(ViewPager) activity.findViewById(R.id.pager_images);
			pager.setCurrentItem(pager.getCurrentItem()+1, true);			
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//Instruction click: display on map
		if(parent == activity.findViewById(R.id.listView1)) {
			MapController.getInstance(activity).setPathToDisplay(activity.getIntent().getIntExtra(ExcursionListActivity.EXCURSION_ID, 0));
			MapController.getInstance(activity).setInstructionToDisplay((int) parent.getAdapter().getItemId(position));
			launchMap(null);
		}
		//POI list item click : show item on map
		else if(parent == activity.findViewById(R.id.listView2)) {
			launchMap(((NamedPoint) parent.getItemAtPosition(position)).getPoint());
		}
	}
	
	/**
	 * Launches the map
	 */
	private void launchMap(GeoPoint point) {
		boolean excursionsBeforeMap = PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("displayExcursionsBeforeMap", false);
		Intent it = new Intent(activity, SlippyMapActivity.class);
		if(excursionsBeforeMap) { it.putExtra(LocationSelectionActivity.LOCATION_ID, MapController.getInstance(activity).getCurrentLocation().getId()); }
		if(point != null) { MapController.getInstance(activity).setPointToDisplay(point); }
		activity.startActivity(it);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {}

	@Override
	public void onPageSelected(int arg0) {		
		//Change visibility of these buttons
		ImageView right = (ImageView) activity.findViewById(R.id.imageRight);
		ImageView left = (ImageView) activity.findViewById(R.id.imageLeft);
		left.setVisibility(View.VISIBLE);
		right.setVisibility(View.VISIBLE);
		int idPhoto=((ViewPager)activity.findViewById(R.id.pager_images)).getCurrentItem();
		int nbPhotos = activity.getImagesFile().length;
		if(idPhoto == 0) {
			left.setVisibility(View.INVISIBLE);
		}
		if(idPhoto == nbPhotos -1) {
			right.setVisibility(View.INVISIBLE);
		}
	}
}
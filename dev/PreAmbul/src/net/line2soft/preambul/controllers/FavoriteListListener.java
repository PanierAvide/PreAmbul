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

import java.util.ArrayList;
import java.util.Arrays;

import net.line2soft.preambul.models.NamedPoint;
import net.line2soft.preambul.models.PointOfInterest;
import net.line2soft.preambul.utils.RWFile;
import net.line2soft.preambul.views.FavoriteEditActivity;
import net.line2soft.preambul.views.FavoriteListActivity;
import net.line2soft.preambul.views.PoiInfoActivity;
import net.line2soft.preambul.views.SlippyMapActivity;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import net.line2soft.preambul.R;

/** This listener reacts to events in {@link FavoriteListActivity} 
 *  @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class FavoriteListListener implements OnItemClickListener, OnEditorActionListener, OnClickListener {
//ATTRIBUTES
	/** The activity */
	private FavoriteListActivity activity;

//CONSTRUCTOR
	/** The constructor
	 * @param favoriteListActivity the instance of the activity to listen
	 */
	public FavoriteListListener(FavoriteListActivity favoriteListActivity) {
		activity = favoriteListActivity;
	}

//OTHER METHODS
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//Click on a autoCompleteTextView item
		if(view instanceof TextView) {
			ArrayList<NamedPoint> result;
			try {
				result = MapController.getInstance(activity).getCurrentLocation().findFavorites(activity, ((TextView)view).getText().toString());
				activity.setFavoriteList(Arrays.copyOf(result.toArray(), result.size(), NamedPoint[].class));
			} catch (Exception e) {
				activity.displayInfo(activity.getString(R.string.message_favorites_read_error));
				e.printStackTrace();
			}
		} else {
			//Click on item
			NamedPoint fav = (NamedPoint) parent.getAdapter().getItem(position);
			Intent it = new Intent(activity, PoiInfoActivity.class);
			if(!(fav.getClass().equals(PointOfInterest.class))){
				it.putExtra(FavoriteListActivity.NAMEDPOINTFAVORITE_KEY, RWFile.formatCoordinates(fav.getPoint().getLatitude(), fav.getPoint().getLongitude()));
			}else{
				it.putExtra(SlippyMapActivity.POI_KEY, RWFile.formatCoordinates(fav.getPoint().getLatitude(), fav.getPoint().getLongitude()));
				it.putExtra(SlippyMapActivity.POI_CATEG, ((PointOfInterest)fav).getType().getCategory().getId());
				it.putExtra(SlippyMapActivity.POI_TYPE, ((PointOfInterest)fav).getType().getId());
			}
			activity.startActivity(it);
		}
	}
	
	

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		ArrayList<NamedPoint> result;
		try {
			result = MapController.getInstance(activity).getCurrentLocation().findFavorites(activity, v.getText().toString());
			activity.setFavoriteList(Arrays.copyOf(result.toArray(), result.size(), NamedPoint[].class));
		} catch (Exception e) {
			activity.displayInfo(activity.getString(R.string.message_favorites_read_error));
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		if(v == activity.findViewById(R.id.buttonAddFavorite)) {
			Intent it = new Intent(activity, FavoriteEditActivity.class);
			it.putExtra(FavoriteEditActivity.FAVORITE_ID, "0");
			activity.startActivity(it);
		}
		//Reaction to delete button
		else if(v==activity.findViewById(R.id.deleteButton)) {
			AutoCompleteTextView view=((AutoCompleteTextView)activity.findViewById(R.id.autoCompleteTextView1));
			view.setText("");
			ArrayList<NamedPoint> result;
			try {
				result = MapController.getInstance(activity).getCurrentLocation().findFavorites(activity, ((TextView) view).getText().toString());
				activity.setFavoriteList(Arrays.copyOf(result.toArray(), result.size(), NamedPoint[].class));
			} catch (Exception e) {
				activity.displayInfo(activity.getString(R.string.message_favorites_read_error));
				e.printStackTrace();
			}
		}
	}

}
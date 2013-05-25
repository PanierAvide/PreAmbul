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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import net.line2soft.preambul.models.NamedPoint;
import net.line2soft.preambul.models.PointOfInterest;
import net.line2soft.preambul.utils.RWFile;
import net.line2soft.preambul.views.FavoriteEditActivity;
import net.line2soft.preambul.views.PoiInfoActivity;

import org.mapsforge.core.GeoPoint;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import net.line2soft.preambul.R;

/**
 * This class reacts to events in {@link PoiInfoActivity}
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class PoiInfoListener implements OnClickListener, OnCompletionListener {
// ATTRIBUTE
	/** The associated activity **/
	private PoiInfoActivity activity;

// CONSTRUCTOR
	/**
	 * Class constructor
	 * @param act The current activity
	 */
	public PoiInfoListener(PoiInfoActivity act){
		activity=act;
	}
	
	@Override
	public void onClick(View view) {
		if(view==activity.findViewById(R.id.button1)) {
			GeoPoint point=activity.getPoint().getPoint();		
			MapController.getInstance().startSlippyMapWithPositionCentered(activity,point.getLatitude(), point.getLongitude());
		}
		if(view==activity.findViewById(R.id.soundButton)){
			activity.playSound(((PointOfInterest)activity.getPoint()).getSound());
		}
		if(view==activity.findViewById(R.id.favoriteButton)){
			File file=new File(activity.getFilesDir().getPath()+File.separator+"locations"+File.separator+MapController.getInstance(activity).getCurrentLocation().getId()+File.separator+"bookmarks"+File.separator+"pois.xml");
			try{
				if(activity.getPoint().isFavorite()){
					activity.getPoint().setFavorite(false);
					RWFile.saveFavoritesPoi(file,MapController.getInstance(activity).getCurrentLocation().getPOIs(activity));			
					((ImageButton)activity.findViewById(R.id.favoriteButton)).setImageResource(R.drawable.icon_favorites_empty);
				}else{
					activity.getPoint().setFavorite(true);
					RWFile.saveFavoritesPoi(file, MapController.getInstance(activity).getCurrentLocation().getPOIs(activity));				
					((ImageButton)activity.findViewById(R.id.favoriteButton)).setImageResource(R.drawable.icon_favorites);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onCompletion(MediaPlayer arg0) {
		((ImageButton)activity.findViewById(R.id.soundButton)).setImageResource(R.drawable.play_button);
	}
	
	/** 
	 * The reaction to the different options 
	 * @param item The item selected
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result;
		switch (item.getItemId()) {
	      case R.id.delete_favorite:
	    	  	//delete favorite
				try {
					HashMap<String, NamedPoint> favorites = MapController.getInstance(activity).getCurrentLocation().getNamedFavorites(activity);
					//Remove favorite
					favorites.remove(RWFile.formatCoordinates(activity.getPoint().getPoint().getLatitude(), activity.getPoint().getPoint().getLongitude()));
					//Save
					File xml = new File(activity.getFilesDir().getPath()+File.separator+"locations"+File.separator+MapController.getInstance(activity).getCurrentLocation().getId()+File.separator+"bookmarks"+File.separator+"favorites.gpx");
					NamedPoint[] favoritesArray = Arrays.copyOf(favorites.values().toArray(), favorites.size(), NamedPoint[].class);
					RWFile.saveFavoritesGPX(xml, favoritesArray);
					MapController.getInstance(activity).getCurrentLocation().loadNamedFavorites(activity);
					activity.displayInfo(activity.getString(R.string.message_favorite_deleted));
				} catch (IOException e) {
					activity.displayInfo(activity.getString(R.string.message_favorite_delete_error));
					e.printStackTrace();
				} finally {
					activity.onBackPressed();
					result = true;
				}
				break;
	      case R.id.edit_favorite:
    		  //switch to the favorite edit activity
    		  Intent intent = new Intent(activity, FavoriteEditActivity.class);
    		  intent.putExtra(FavoriteEditActivity.FAVORITE_ID, RWFile.formatCoordinates(activity.getPoint().getPoint().getLatitude(), activity.getPoint().getPoint().getLongitude()));
    		  activity.startActivity(intent);
    		  result = true;
	          break;
	      default:
	    	  result = false;
	    	  break;
		}
		return result;
		
	}
}
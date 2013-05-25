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
import net.line2soft.preambul.utils.RWFile;
import net.line2soft.preambul.views.FavoriteEditActivity;
import net.line2soft.preambul.views.FavoriteListActivity;
import net.line2soft.preambul.views.SlippyMapActivity;

import org.mapsforge.core.GeoPoint;

import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import net.line2soft.preambul.R;

/**
 * This listener reacts to events in {@link FavoriteEditActivity}
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class FavoriteEditListener implements OnClickListener {
//ATTRIBUTES
	/** The associated activity **/
	private FavoriteEditActivity activity;

//CONSTRUCTOR
	/**
	 * Class constructor
	 * @param activity The current activity
	 */
	public FavoriteEditListener(FavoriteEditActivity activity){
		this.activity = activity;
	}

//OTHER METHODS
	@Override
	public void onClick(View v){
		if(v ==activity.findViewById(R.id.photoButton)){
			if(Camera.getNumberOfCameras()>0){
				dispatchTakePictureIntent(FavoriteEditActivity.TAKE_PICTURE);
			}else{
				activity.displayInfo(activity.getString(R.string.message_no_camera));
			}
		}else if(v==activity.findViewById(R.id.buttonSetCoordinates)){
			activity.setOldPoint(activity.getPoint());
		    Intent intent = new Intent(activity, SlippyMapActivity.class);
		    intent.putExtra(SlippyMapActivity.MAP_SET_COORDINATES, true);
		    activity.startActivity(intent);
		}else if(v==activity.findViewById(R.id.buttonValidate)){
			//Test if fields are filled
			if(activity.getTextTitle().length() > 0) {
				if(activity.getPoint() != null) {
					//Create the favorite
					NamedPoint favoriteToAdd = new NamedPoint(activity.getPoint(), activity.getTextTitle(), activity.getTextDescription(), true);
					favoriteToAdd.setPhoto(((ImageView)activity.findViewById(R.id.tphoto)).getDrawingCache());
					
					//Get favorites
					try {
						HashMap<String,NamedPoint> favorites = MapController.getInstance(activity).getCurrentLocation().getNamedFavorites(activity);
						//Add the new favorite
						favorites.put(RWFile.formatCoordinates(favoriteToAdd.getPoint().getLatitude(), favoriteToAdd.getPoint().getLongitude()), favoriteToAdd);
						GeoPoint oldPoint = activity.getOldPoint();
						if(oldPoint != null && (activity.getPoint().getLatitude() != oldPoint.getLatitude() || activity.getPoint().getLongitude() != oldPoint.getLongitude())) {
							favorites.remove(RWFile.formatCoordinates(activity.getOldPoint().getLatitude(), activity.getOldPoint().getLongitude()));
						}
						activity.setOldPoint(null);
						//Save
						File xml = new File(activity.getFilesDir().getPath()+File.separator+"locations"+File.separator+MapController.getInstance(activity).getCurrentLocation().getId()+File.separator+"bookmarks"+File.separator+"favorites.gpx");
						NamedPoint[] favoritesArray = Arrays.copyOf(favorites.values().toArray(), favorites.size(), NamedPoint[].class);
						RWFile.saveFavoritesGPX(xml, favoritesArray);
						MapController.getInstance(activity).getCurrentLocation().loadNamedFavorites(activity);
					} catch (Exception e) {
						activity.displayInfo(activity.getString(R.string.message_favorite_creation_error));
						e.printStackTrace();
					} finally {
						Intent intent = new Intent(activity, FavoriteListActivity.class);
						activity.setFavoriteToEdit(null);
						activity.startActivity(intent);
						activity.finish();
					}
					//TODO : Ne pas oublier de supprimer le dossier activity.getCacheDir(),"temp"
				}
				else {
					activity.displayInfo(activity.getString(R.string.message_favorite_no_point));
				}
			}
			else {
				activity.displayInfo(activity.getString(R.string.message_favorite_no_name));
			}
		}
	}
	
	/**
	 * Launches the intent to take a picture
	 * @param actionCode The action code
	 */
	private void dispatchTakePictureIntent(int actionCode) {
		File storageDir = new File( Environment.getExternalStoragePublicDirectory(
		        Environment.DIRECTORY_PICTURES
			    ),"temporary"); 
		storageDir.mkdirs();
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    File f=null;
		try {
			f = activity.createImageFile(true, storageDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
	    activity.startActivityForResult(takePictureIntent, actionCode);
	}
}

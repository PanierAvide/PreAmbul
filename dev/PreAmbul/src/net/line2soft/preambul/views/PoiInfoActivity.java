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

import java.io.File;

import net.line2soft.preambul.controllers.MapController;
import net.line2soft.preambul.controllers.PoiInfoListener;
import net.line2soft.preambul.models.NamedPoint;
import net.line2soft.preambul.models.PointOfInterest;
import net.line2soft.preambul.models.PointOfInterestCategory;
import net.line2soft.preambul.models.PointOfInterestType;
import net.line2soft.preambul.utils.RWFile;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import net.line2soft.preambul.R;

/**
 * The activity of a point of interest
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class PoiInfoActivity extends Activity {
//ATTRIBUTES
	/** The current point **/
	private NamedPoint thePoint;
	/** The audio player **/
	private MediaPlayer mPlayer;
	/** The listener **/
	private PoiInfoListener listener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Check if this version of Android allows to use custom title bars
		boolean feature=requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		/** Set layout **/
		//Base layout
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_poi_info);
		//set title bar
		if(feature){
	        	getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.activity_title_bar);
	            TextView myTitleText = (TextView) findViewById(R.id.textView0);
	            myTitleText.setText(getString(R.string.title_activity_excursion_info));	
	    }
		
		//Get NamedPoint ID
		PointOfInterestType type;
		String key = getIntent().getStringExtra(FavoriteListActivity.NAMEDPOINTFAVORITE_KEY);
		listener=new PoiInfoListener(this);
		if(key==null){
			//TRAITEMENT POI
			key = getIntent().getStringExtra(SlippyMapActivity.POI_KEY);
			String categoryString= getIntent().getStringExtra(SlippyMapActivity.POI_CATEG);
			String typeString= getIntent().getStringExtra(SlippyMapActivity.POI_TYPE);
			if(key!=null && categoryString!=null && typeString != null){
				//get the favorite's infos
				try {
					PointOfInterestCategory category=MapController.getInstance(this).getCurrentLocation().getPoiCategory(this, categoryString);
					type = category.getType(typeString);
					thePoint=type.getPoi(key);
					//Set the logo
					ImageView logo=((ImageView)findViewById(R.id.poiLogo));
					logo.setImageDrawable((type.getIcon() != null) ? type.getIcon() : category.getIcon());
					//Set the title
					((TextView)findViewById(R.id.titleText)).setText(thePoint.getName());
					//Set the coordinates
					TextView text;
					String description = ((PointOfInterest)thePoint).getDescription();
					if(description != null && description.length() > 0) {
						findViewById(R.id.coordinates).setVisibility(View.VISIBLE);
						text=((TextView)findViewById(R.id.coordinatesText));
						text.setText(description);
					} else {
						findViewById(R.id.coordinates).setVisibility(View.GONE);
					}
					//Set the description
					if(thePoint.getComment() != null && thePoint.getComment().length() > 0) {
						findViewById(R.id.description).setVisibility(View.VISIBLE);
						((TextView)findViewById(R.id.descriptionText)).setText(Html.fromHtml(thePoint.getComment()));
					} else {
						findViewById(R.id.description).setVisibility(View.GONE);
					}
					//Set the weblink
					text=((TextView)findViewById(R.id.webText));
					if(((PointOfInterest)thePoint).getLink() != null) {
						text.setText((((PointOfInterest)thePoint).getLink().toExternalForm()));
						findViewById(R.id.webLink).setVisibility(View.VISIBLE);
					} else {
						findViewById(R.id.webLink).setVisibility(View.GONE);
					}
					//Set the photo
					Bitmap photo=thePoint.getPhoto();
					if(photo != null) {
						((ImageView)findViewById(R.id.photoView)).setImageBitmap(photo);
						findViewById(R.id.photo).setVisibility(View.VISIBLE);
					} else {
						((ImageView)findViewById(R.id.photoView)).setImageBitmap(null);
						findViewById(R.id.photo).setVisibility(View.GONE);
					}
					//Set the button
					((Button)findViewById(R.id.button1)).setVisibility(View.GONE);
					//Set the soundbutton
					if(((PointOfInterest)thePoint).getSound()!=null){
						mPlayer=null;
						ImageButton sb=(ImageButton)findViewById(R.id.soundButton);
						sb.setVisibility(View.VISIBLE);
						sb.setOnClickListener(listener);
					}else{
						((ImageButton)findViewById(R.id.soundButton)).setVisibility(View.INVISIBLE);
					}
					//Set the favoriteButton
					ImageButton im =((ImageButton)findViewById(R.id.favoriteButton));
					im.setOnClickListener(listener);
					if(thePoint.isFavorite()){
						im.setImageResource(R.drawable.icon_favorites);
					}else{
						im.setImageResource(R.drawable.icon_favorites_empty);
					}
					im.setVisibility(View.VISIBLE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				displayInfo(getString(R.string.message_poi_not_found));
				onBackPressed();
			}
		}else{
			try {
				thePoint=MapController.getInstance(this).getCurrentLocation().getFavorite(this, key);
				//TRAITEMENT FAVORIS
				//Set the logo
				ImageView poiLogo = (ImageView)findViewById(R.id.poiLogo);
				poiLogo.setImageDrawable(getResources().getDrawable(R.drawable.icon_favorites));
				poiLogo.setAdjustViewBounds(true);
				poiLogo.setMaxHeight((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics()));
				poiLogo.setMaxWidth((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics()));
				//Set the title
				((TextView)findViewById(R.id.titleText)).setText(thePoint.getName());
				//Set the coordinates
				findViewById(R.id.coordinates).setVisibility(View.GONE);
				//Set the description
				if(thePoint.getComment() != null && thePoint.getComment().length() > 0) {
					findViewById(R.id.description).setVisibility(View.VISIBLE);
					((TextView)findViewById(R.id.descriptionText)).setText(Html.fromHtml(thePoint.getComment()));
				} else {
					findViewById(R.id.description).setVisibility(View.GONE);
				}
				//Set the weblink
				findViewById(R.id.webLink).setVisibility(View.GONE);
				//Set the photo
				Bitmap photo=thePoint.getPhoto();
				if(photo!=null){
					findViewById(R.id.photo).setVisibility(View.VISIBLE);
					((ImageView)findViewById(R.id.photoView)).setImageBitmap(photo);
				}else{
					findViewById(R.id.photo).setVisibility(View.GONE);
					((ImageView)findViewById(R.id.photoView)).setImageBitmap(null);
				}
				//Set the button
				((Button)findViewById(R.id.button1)).setVisibility(View.VISIBLE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//Set the soundbutton
			((ImageButton)findViewById(R.id.soundButton)).setVisibility(View.INVISIBLE);
			//Set the favoriteButton
			((ImageButton)findViewById(R.id.favoriteButton)).setVisibility(View.INVISIBLE);
		}
		((Button)findViewById(R.id.button1)).setOnClickListener(listener);
	}
	@Override
	public void onPause() {
	    super.onPause();
		if(mPlayer != null) {
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
			((ImageButton)this.findViewById(R.id.soundButton)).setImageResource(R.drawable.play_button);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	    ((ImageView)findViewById(R.id.imageView1)).setImageBitmap(RWFile.readImageBackground(R.drawable.back_excursion_info, this));
	}
	
	@Override
	protected void onDestroy() {
	    RWFile.unbindDrawables(findViewById(R.id.RootView));
	    System.gc();
	    super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(!thePoint.getClass().equals(PointOfInterest.class)) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.activity_poi_info, menu);
		}
		return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return listener.onOptionsItemSelected(item);
	}
	
// ACCESSORS
	/**
	 * Returns the currently shown point
	 * @return The current point
	 */
	public NamedPoint getPoint(){
		return thePoint;
	}
	
	/**
	 * Returns the current media player
	 * @return The current player
	 */
	public MediaPlayer getMediaPlayer(){
		return mPlayer;
	}
	
// OTHERS
	/**
	 * Plays the wanted sound
	 * @param file The sound
	 */
	public void playSound(File file) {
		if(mPlayer != null) {
			mPlayer.stop();
			mPlayer.release();
			mPlayer=null;
			((ImageButton)this.findViewById(R.id.soundButton)).setImageResource(R.drawable.play_button);
		}else{
			mPlayer = MediaPlayer.create(this, Uri.fromFile(file));
			mPlayer.setVolume(1, 1);
			mPlayer.start();
			((ImageButton)this.findViewById(R.id.soundButton)).setImageResource(R.drawable.pause_button);
			mPlayer.setOnCompletionListener(listener);
		}
	}
	
	/**
	 * Display a short message to user.
	 * @param message The message to display
	 */
	public void displayInfo(CharSequence message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
}
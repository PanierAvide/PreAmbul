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
import java.io.IOException;

import net.line2soft.preambul.controllers.FavoriteEditListener;
import net.line2soft.preambul.controllers.MapController;
import net.line2soft.preambul.models.NamedPoint;
import net.line2soft.preambul.utils.RWFile;

import org.mapsforge.core.GeoPoint;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import net.line2soft.preambul.R;

/** 
 * The Activity of favorite's edition view
 * @author équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class FavoriteEditActivity extends Activity{
// ATTRIBUTES
	/** Used to identify a favorite **/
	public static final String FAVORITE_ID = "net.line2soft.preambul.FAVORITE_ID";
	//Codes for activity requests
	/** Get a position on map **/
	public static final int PICK_MAP_POSITION = 50;
	/** Take a picture **/
	public static final int TAKE_PICTURE = 51;
	/** The listener **/
	private FavoriteEditListener listener;
	/** The view which contains photo **/
	private ImageView mImageView;
	/** The currently edited favorite **/
	private static NamedPoint favoriteToEdit;
	/** The path to access the current picture **/
	private static String mCurrentPhotoPath;
	/** The location of the favorite **/
	private static GeoPoint point;
	/** The name of the favorite **/
	private static String title;
	/** The description of the favorite **/
	private static String description;
	/** The old location of the favorite **/
	private static GeoPoint oldPoint;

// ACCESSORS
	/**
	 * Returns the address of the photo on disk
	 * @return The address of the photo
	 */
	public String getCurrentPhotoPath() {
		return mCurrentPhotoPath;
	}
	
	/**
	 * Returns the map position of the favorite
	 * @return The position
	 */
	public GeoPoint getPoint() {
		return point;
	}
	
	/**
	 * Returns the name of the point (content of the name textfield)
	 * @return The name
	 */
	public String getTextTitle() {
		return ((EditText)findViewById(R.id.titleText)).getText().toString();
	}
	
	/**
	 * Returns the description of the point (content of the description textfield)
	 * @return The description
	 */
	public String getTextDescription() {
		return ((EditText)findViewById(R.id.descriptionText)).getText().toString();
	}
	
	/**
	 * Returns the currently edited favorite
	 * @return The currently edited favorite
	 */
	public NamedPoint getFavoriteToEdit() {
		return favoriteToEdit;
	}
	
	/**
	 * Returns the old location of the edited favorite
	 * @return The old location
	 */
	public GeoPoint getOldPoint() {
		return oldPoint;
	}
	
// MODIFIERS
	/**
	 * Edits the currently edited favorite
	 * Also updates text fields of the activity
	 * @param f The favorite to edit
	 */
	public void setFavoriteToEdit(NamedPoint f){
		if(f==null){
			favoriteToEdit = new NamedPoint(null, null, null, true);
			((EditText)findViewById(R.id.titleText)).setText(title);
			((EditText)findViewById(R.id.descriptionText)).setText(description);
			if(point!=null){
				((TextView)findViewById(R.id.latText)).setText(String.valueOf(point.getLatitude()));
				((TextView)findViewById(R.id.longText)).setText(String.valueOf(point.getLongitude()));
			} else {
				findViewById(R.id.coordinates).setVisibility(View.GONE);
			}
			if(mCurrentPhotoPath != null) {
				setPic();
			}
		}
		else{
			favoriteToEdit=f;
			title = f.getName();
			description = f.getComment();
			point = f.getPoint();
			((EditText)findViewById(R.id.titleText)).setText(favoriteToEdit.getName());
			((EditText)findViewById(R.id.descriptionText)).setText(favoriteToEdit.getComment());
			((TextView)findViewById(R.id.latText)).setText(String.valueOf(favoriteToEdit.getPoint().getLatitude()));
			((TextView)findViewById(R.id.longText)).setText(String.valueOf(favoriteToEdit.getPoint().getLongitude()));
			if(favoriteToEdit.getPhoto()!=null){
				((ImageView)findViewById(R.id.tphoto)).setImageBitmap(favoriteToEdit.getPhoto());
			}
		}
	}
	
	/**
	 * Edits the image to display, and displays it
	 */
	private void setPic() {
	    // Get the dimensions for the view
		DisplayMetrics metrics = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(metrics);
	  
	    Bitmap bitmap = RWFile.readImage(new File(mCurrentPhotoPath), metrics.widthPixels, metrics.heightPixels, this);
	    if(mImageView == null) { mImageView = ((ImageView)findViewById(R.id.tphoto)); }
	    mImageView.setImageBitmap(bitmap);
	}
	
	/**
	 * Set the old location of the edited point
	 * @param point The old location
	 */
	public void setOldPoint(GeoPoint point) {
		oldPoint = point;
	}
	
// OTHER METHODS
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);	
		listener = new FavoriteEditListener(this);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		//Check if this version of Android allows to use custom title bars
		boolean feature=requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		//set the content as set in the xml
		setContentView(R.layout.activity_favorite_edit);
		
		//initialize title bar
		if(feature){
        	getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.activity_title_bar);
            TextView myTitleText = (TextView) findViewById(R.id.textView0);
            myTitleText.setText(getString(R.string.title_activity_favorite_edit));
        }
		findViewById(R.id.view1).requestFocus();
		mImageView=(ImageView)findViewById(R.id.tphoto);
		findViewById(R.id.photoButton).setOnClickListener(listener);
		findViewById(R.id.buttonSetCoordinates).setOnClickListener(listener);
		findViewById(R.id.buttonValidate).setOnClickListener(listener);	
		
		//Initialize values of fields
		if(getIntent().getStringExtra(FAVORITE_ID) == null || getIntent().getStringExtra(FAVORITE_ID).equals("0")){
			setFavoriteToEdit(null);
		}
		else{
			try {
				NamedPoint tmp = MapController.getInstance(this).getCurrentLocation().getNamedFavorites(this).get(getIntent().getStringExtra(FAVORITE_ID));
				if(tmp != null) {
					setFavoriteToEdit(tmp);
				} else {
					displayInfo(getString(R.string.message_favorite_not_found));
					finish();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//Try to get map point selected by user
		if(MapController.getInstance(this).getSelectedPoint() != null) {
			point = MapController.getInstance(this).getSelectedPoint();
		}
		if(point!=null){
			findViewById(R.id.coordinates).setVisibility(View.VISIBLE);
			((TextView)findViewById(R.id.latText)).setText(String.valueOf(point.getLatitude()));
			((TextView)findViewById(R.id.longText)).setText(String.valueOf(point.getLongitude()));
		}else{
			findViewById(R.id.coordinates).setVisibility(View.GONE);
		}

		((ImageView)findViewById(R.id.imageView1)).setImageBitmap(RWFile.readImageBackground(R.drawable.back_excursion_info, this));
	}
	
	@Override
	protected void onPause() {
		title=getTextTitle();
		description=getTextDescription();
		point=MapController.getInstance(this).getSelectedPoint();
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
//		if(mCurrentPhotoPath != null && mCurrentPhotoPath.length() > 0) {
//			RWFile.deleteDirectory((new File(mCurrentPhotoPath)).getParentFile().getParentFile());
//		}
//		RWFile.unbindDrawables(findViewById(R.id.RootView));
//	    System.gc();
		((BitmapDrawable)((ImageView)findViewById(R.id.imageView1)).getDrawable()).getBitmap().recycle();
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		Intent it = new Intent(this, FavoriteListActivity.class);
		startActivity(it);
		finish();
	}
	
	@Override
	public void finish() {
		MapController.getInstance(this).setPointSelected(null);
		point=null;
		mCurrentPhotoPath=null;
		mImageView=null;
		favoriteToEdit=null;
		title=null;
		description=null;
		setFavoriteToEdit(null);
		super.finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == TAKE_PICTURE) {
			//handleSmallCameraPhoto(data);
			setPic();
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		//set the content as set in the xml
		setContentView(R.layout.activity_favorite_edit);
		findViewById(R.id.view1).requestFocus();
		mImageView=(ImageView)findViewById(R.id.tphoto);
		findViewById(R.id.photoButton).setOnClickListener(listener);
		findViewById(R.id.buttonSetCoordinates).setOnClickListener(listener);
		findViewById(R.id.buttonValidate).setOnClickListener(listener);
		try {
			setFavoriteToEdit(MapController.getInstance(this).getCurrentLocation().getFavorite(this, FAVORITE_ID));
		} catch (IOException e) {
			e.printStackTrace();
		}
		((ImageView)findViewById(R.id.imageView1)).setImageBitmap(RWFile.readImageBackground(R.drawable.back_excursion_info, this));
	}

	/**
	 * Displays a message to user
	 * @param message The message to display
	 */
	public void displayInfo(CharSequence message) {
		try {
			Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		}
		catch(RuntimeException e) {
			Looper.prepare();
			Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * Creates an image to the wanted directory
	 * @param tmp Is this image temporary
	 * @param dir The directory to save the image in
	 * @return The saved image
	 * @throws IOException
	 */
	public File createImageFile(boolean tmp, File dir) throws IOException {
	    // Create an image file name
		if(tmp){
			dir=new File(dir.getAbsolutePath()+"temp.jpg");
			dir.createNewFile();
		}
	    mCurrentPhotoPath = dir.getAbsolutePath();
	    return dir;
	}
}
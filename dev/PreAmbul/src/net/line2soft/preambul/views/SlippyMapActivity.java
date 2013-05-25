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
import java.util.HashMap;

import net.line2soft.preambul.controllers.LocationsController;
import net.line2soft.preambul.controllers.MapController;
import net.line2soft.preambul.controllers.SlippyMapListener;
import net.line2soft.preambul.models.Excursion;
import net.line2soft.preambul.models.Location;
import net.line2soft.preambul.models.NamedPoint;
import net.line2soft.preambul.models.PointOfInterest;
import net.line2soft.preambul.utils.RWFile;

import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.overlay.ArrayBalloonItemizedOverlay;
import org.mapsforge.android.maps.overlay.ArrayWayTextOverlay;
import org.mapsforge.android.maps.overlay.BalloonItemizedOverlay;
import org.mapsforge.android.maps.overlay.ItemizedOverlay;
import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.android.maps.overlay.OverlayWay;
import org.mapsforge.android.maps.overlay.OverlayWayText;
import org.mapsforge.core.GeoPoint;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import net.line2soft.preambul.R;

/**
 * This activity displays the location map.
 * You can navigate, zoom/unzoom.
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class SlippyMapActivity extends MapActivity {
// ATTRIBUTES
	/** listener for the whole activity**/
	private SlippyMapListener listener;
	/** Contains overlay paths **/
	private ArrayWayTextOverlay overlayPaths;
	/** Contains current path segment **/
	private OverlayWayText segment;
	/** The list of markers to display **/
	private HashMap<String, OverlayItem> markers;
	/** Contains overlay items for POIs **/
	private ArrayBalloonItemizedOverlay overlayPoiItemMarker;
	/** Is the map in edit mode **/
	private boolean addFavorite = false;
	/** Is the compass accurate ? **/
	private static boolean isCompassAccurate = true;
	
	//Intents extras
	/** Used to identify a POI **/
	public static final String POI_KEY = "net.line2soft.preambul.POI_KEY";
	/** Used to identify the type of a POI **/
	public static final String POI_TYPE = "net.line2soft.preambul.POI_TYPE";
	/** Used to identify the category of a POI **/
	public static final String POI_CATEG = "net.line2soft.preambul.POI_CATEG";
	/** Used to know that the map have to be in edit mode **/
	public static final String MAP_SET_COORDINATES = "net.line2soft.preambul.MAP_SET_COORDINATES";

// OTHER METHODS
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Remove the title bar (more space for map)
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//Set content view
		setContentView(R.layout.activity_slippy_map);
        //Get the map data from given ID
        int id = getIntent().getIntExtra(LocationSelectionActivity.LOCATION_ID, 0);
        if(id > 0) {
    		//Set map if SDCard is available (needed for Mapsforge)
        	if(RWFile.isExternalStorageReadable() && RWFile.isExternalStorageWritable()) {
        		MapView mv = (MapView) findViewById(R.id.mapView);
        		//Edit map style
        		try {
					mv.setRenderTheme(RWFile.copyRenderRulesIntoFiles(this));
				} catch (Exception e) {
					//We use the default render rules if custom wasn't found or if an error occurs
					e.printStackTrace();
					mv.setRenderTheme(MapView.DEFAULT_RENDER_THEME);
				}
        		mv.setClickable(true);
        		mv.setBuiltInZoomControls(true);
        		//Change zoom location
        		mv.getMapZoomControls().setZoomControlsGravity(Gravity.TOP + Gravity.RIGHT);
        		//Load map data
        		File map = new File(getFilesDir().getPath()+File.separator+"locations"+File.separator+id+File.separator+"map"+File.separator+"osmdata.map");
        		if(map.exists() && map.isFile() && map.canRead()) {
        			mv.setMapFile(map);
        			//Set bounds
        			Location locToLoad = LocationsController.getInstance(this).getInstalledLocations().get(id);
        			double pTopLimit = locToLoad.getBoundaryBox().getTopLeftCorner().getY();
        			double pBottomLimit = locToLoad.getBoundaryBox().getBottomRightCorner().getY();
        			double pLeftLimit = locToLoad.getBoundaryBox().getTopLeftCorner().getX();
        			double pRightLimit = locToLoad.getBoundaryBox().getBottomRightCorner().getX();
        			mv.setMapViewLimits(pTopLimit, pBottomLimit, pLeftLimit, pRightLimit);
        			//Set listener
            		listener = new SlippyMapListener(this);
            		
            		//Init paths list
            		overlayPaths = new ArrayWayTextOverlay(null, null);
            		//init markers list
            		markers = new HashMap<String, OverlayItem>();
            		//init poi markers list
            		overlayPoiItemMarker = new ArrayBalloonItemizedOverlay(null, mv, this);
            		
            		//Add overlays
            		mv.getOverlays().add(overlayPaths);
            		mv.getOverlays().add(overlayPoiItemMarker);
            		
            		mv.setOnTouchListener(listener);
            		
            		//Normal or edit mode ?
                    addFavorite = getIntent().getBooleanExtra(MAP_SET_COORDINATES, false);
        		}
        		else {
        			onBackPressed();
        			displayInfo(getString(R.string.message_map_not_exists_error));
        		}
        	}
        	else {
        		onBackPressed();
        		displayInfo(getString(R.string.message_no_external_memory_error));
        	}
        }
        else {
    		onBackPressed();
        	displayInfo(getString(R.string.message_map_id_error));
        }
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		//Enable map
		MapView mv = (MapView) findViewById(R.id.mapView);
		overlayPoiItemMarker.addItems(markers.values());
		initMap(addFavorite);
		mv.setEnabled(true);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		//Normal or edit mode ?
        addFavorite = intent.getBooleanExtra(MAP_SET_COORDINATES, false);
        
		super.onNewIntent(intent);
	}
	
	@Override
	public void onPause() {
		//Unregister compass listener
		CompassView cpv = (CompassView) findViewById(R.id.compass);
		CompassView cpvBig = (CompassView) findViewById(R.id.compassBig);
		if(cpv.getVisibility() == View.VISIBLE || cpvBig.getVisibility() == View.VISIBLE) {
			SensorManager mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
			mySensorManager.unregisterListener(listener);
		}
		
		//Disable map
		MapView mv = (MapView) findViewById(R.id.mapView);
		mv.setEnabled(false);
		displayExcursion(-1);
		overlayPoiItemMarker.clear();
		
		//Delete value of SET_COORDINATES
		getIntent().removeExtra(MAP_SET_COORDINATES);
	    
		super.onPause();
	}
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		boolean isInEditModeReally = findViewById(R.id.validatePositionButton).getVisibility() == View.VISIBLE;
		if(isInEditModeReally) {
			Intent it = new Intent(this, FavoriteEditActivity.class);
			this.startActivity(it);
		} else {
			super.onBackPressed();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        addFavorite = getIntent().getBooleanExtra("SET_COORDINATES", false);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_slippy_map, menu);
        if(addFavorite==true){
			menu.findItem(R.id.favorites).setVisible(false);
			menu.findItem(R.id.options).setVisible(false);
		}
		return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return listener.onOptionsItemSelected(item);
	}
	
	/**
	 * Init map's overlays (compass, buttons, ...), attach listener.
	 * You can launch map on normal or edit mode (to pick a position).
	 * @param addFavorite If true, edit mode (to add favorite), if false normal mode
	 */
	private void initMap(boolean addFavorite) {
		if(addFavorite==false){
			//Launch map in normal mode
			findViewById(R.id.list_item).setVisibility(View.VISIBLE);
			findViewById(R.id.drawer).setVisibility(View.VISIBLE);
			
			//Add listener on excursions button
			ImageButton btExcursionsImg = (ImageButton) findViewById(R.id.imageButton1);
    		btExcursionsImg.setOnClickListener(listener);
    		btExcursionsImg.setVisibility(View.VISIBLE);
    		findViewById(R.id.excursionButton).setVisibility(View.VISIBLE);
    		((ImageButton) findViewById(R.id.imageRight)).setOnClickListener(listener);
    		((ImageButton) findViewById(R.id.imageLeft)).setOnClickListener(listener);
    		((ImageButton) findViewById(R.id.favoritesButton)).setOnClickListener(listener);
    		
    		//Display POIs on map
    		try {
    			PointOfInterest[] pois = MapController.getInstance(this).getCurrentLocation().getPOIs(this);
    			Drawable icon = null;
    			OverlayItem item = null;
    			boolean displayPoiType = true;
    			for(PointOfInterest poi : pois) {
    				displayPoiType = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("type"+poi.getType().getId(), true);
    				if(displayPoiType) {
    					icon = (poi.getType().getIcon() == null) ? poi.getType().getCategory().getIcon() : poi.getType().getIcon();
    					item = new OverlayItem(poi.getPoint(), poi.getName(), poi.getComment(), BalloonItemizedOverlay.boundLeftCenter(icon));
    					overlayPoiItemMarker.addItem(item, poi);
    				}
    			}
    		} catch (Exception e) {
    			displayInfo(getString(R.string.message_map_poi_load_error));
    			e.printStackTrace();
    		}
    		
    		//Display favorites on map
    		try {
    			HashMap<String,NamedPoint> favorites = MapController.getInstance(this).getCurrentLocation().getNamedFavorites(this);
    			Drawable icon = getResources().getDrawable(R.drawable.marker_favorite);
    			OverlayItem item = null;
    			for(NamedPoint favPoint : favorites.values()) {
					item = new OverlayItem(favPoint.getPoint(), favPoint.getName(), favPoint.getComment(), BalloonItemizedOverlay.boundLeftCenter(icon));
					overlayPoiItemMarker.addItem(item, favPoint);
    			}
    		} catch (IOException e) {
    			displayInfo(getString(R.string.message_map_favorite_load_error));
    			e.printStackTrace();
    		}
    		
    		//Display excursions and instruction on map
    		int pathToDisplay = MapController.getInstance(this).getPathToDisplay();
    		displayExcursion(pathToDisplay);
    		int instructionToDisplay = MapController.getInstance(this).getInstructionToDisplay() -1;
    		//Set instruction, only if > 0 because if instruction=0, was set when displaying excursion
    		if(instructionToDisplay > 0) {
    			setNavigationInstructionToDisplay(instructionToDisplay);
    		}
    		//Display of the bookmarksButton
    		boolean displaybookmarksButton= PreferenceManager.getDefaultSharedPreferences(this).getBoolean("displayBookmarksButton", true);
    		ImageButton img = (ImageButton) findViewById(R.id.favoritesButton);
    		if(displaybookmarksButton){
    			img.setVisibility(View.VISIBLE);
    		}else{
    			img.setVisibility(View.GONE);
    		}
    		//Display point
    		GeoPoint pointToDisplay = MapController.getInstance(this).getPointToDisplay();
    		if(pointToDisplay != null) {
    			displayPosition(pointToDisplay.getLatitude(), pointToDisplay.getLongitude());
    			MapController.getInstance(this).setPointToDisplay(null);
    		}
    		//Launch compass
    		boolean displayCompass = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("displayCompass", true);
    		CompassView cpv = (CompassView) findViewById(R.id.compass);
    		CompassView cpvBig = (CompassView) findViewById(R.id.compassBig);
    		if(displayCompass) {
    			SensorManager mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    	        Sensor mAccelerometer = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    	        Sensor mField = mySensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    		    if(mField != null && mAccelerometer != null){
    		    	mySensorManager.registerListener(listener, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    		        mySensorManager.registerListener(listener, mField, SensorManager.SENSOR_DELAY_UI);
    		        cpv.setOnClickListener(listener);
    		        if(isCompassAccurate) {
	    		        cpv.setVisibility(View.VISIBLE);
    		        } else {
    		        	displayInfo(getString(R.string.message_compass_not_accurate));
    		        	cpv.setVisibility(View.GONE);
    		        }
    		        cpvBig.setVisibility(View.GONE);
    		        cpvBig.setOnClickListener(listener);
    		    } else { cpv.setVisibility(View.GONE); cpvBig.setVisibility(View.GONE); }
    		} else { cpv.setVisibility(View.GONE); cpvBig.setVisibility(View.GONE); }
    		
    		//Hide validate button
    		findViewById(R.id.validatePositionButton).setVisibility(View.GONE);
    		findViewById(R.id.imageViewValidate).setVisibility(View.GONE);
    		findViewById(R.id.mapPointer).setVisibility(View.GONE);
		}
		else{
			//Launch map in edit mode, to set coordinates of a favorite
			findViewById(R.id.list_item).setVisibility(View.GONE);
			findViewById(R.id.compass).setVisibility(View.GONE);
			findViewById(R.id.drawer).setVisibility(View.GONE);
			findViewById(R.id.favoritesButton).setVisibility(View.GONE);
			
			findViewById(R.id.mapPointer).setVisibility(View.VISIBLE);
			
			//Display validate button and add listener
    		findViewById(R.id.validatePositionButton).setVisibility(View.VISIBLE);
    		findViewById(R.id.imageViewValidate).setVisibility(View.VISIBLE);
    		findViewById(R.id.imageViewValidate).setOnClickListener(listener);
    		
    		this.addFavorite = false;
		}
	}
	
	/**
	 * Set if compass is accurate or not
	 * @param accurate True if accurate, false else
	 */
	public void setAccurate(boolean accurate) {
		isCompassAccurate = accurate;
	}
	
	/**
	 * Display a short message to user.
	 * @param message The message to display
	 */
	public void displayInfo(CharSequence message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Displays excursions
	 * @param id The ID of the excursion to display (Values: -1 to hide all excursions, 0 to show all excursions, >=1 to show one specific excursion)
	 */
	public void displayExcursion(int id) {
		try {
			overlayPaths.clear();
			removeMarker("NAV");
			//Hide all excursions
			if(id == -1) {
				hideNavigationInstruction();
			}
			//Display one excursion
			else if (id > 0) {
				Paint color = createPaint(getResources().getColor(R.color.Green));
				OverlayWay path = MapController.getInstance(this).getCurrentLocation().getExcursions(this).get(id).getPath();
				path.setPaint(color, null);
				overlayPaths.addWay(new OverlayWayText(path, null));
				showNavigationInstruction(id);
			}
			//Display all excursions
			else {
				//Init colors
				Paint[] colors = new Paint[30];
				colors[0] = createPaint(getResources().getColor(R.color.GreenYellow));
				colors[1] = createPaint(getResources().getColor(R.color.Orange));
				colors[2] = createPaint(getResources().getColor(R.color.Blue)); 
				colors[3] = createPaint(getResources().getColor(R.color.DarkRed));
				colors[4] = createPaint(getResources().getColor(R.color.Purple));
				colors[5] = createPaint(getResources().getColor(R.color.Pink));
				colors[6] = createPaint(getResources().getColor(R.color.Black));
				colors[7] = createPaint(getResources().getColor(R.color.Indigo));
				colors[8] = createPaint(getResources().getColor(R.color.Gold));
				colors[9] = createPaint(getResources().getColor(R.color.DarkOrange));
				colors[10] = createPaint(getResources().getColor(R.color.Violet));
				colors[11] = createPaint(getResources().getColor(R.color.Brown));
				colors[12] = createPaint(getResources().getColor(R.color.PowderBlue));
				colors[13] = createPaint(getResources().getColor(R.color.Cyan));
				colors[14] = createPaint(getResources().getColor(R.color.DarkGreen));
				colors[15] = createPaint(getResources().getColor(R.color.DarkSalmon));
				colors[16] = createPaint(getResources().getColor(R.color.DarkViolet));
				colors[17] = createPaint(getResources().getColor(R.color.DarkGoldenrod));
				colors[18] = createPaint(getResources().getColor(R.color.Thistle));
				colors[19] = createPaint(getResources().getColor(R.color.DarkTurquoise));
				colors[20] = createPaint(getResources().getColor(R.color.FireBrick));
				colors[21] = createPaint(getResources().getColor(R.color.Aquamarine));
				colors[22] = createPaint(getResources().getColor(R.color.Aqua));
				colors[23] = createPaint(getResources().getColor(R.color.BurlyWood));
				colors[24] = createPaint(getResources().getColor(R.color.Crimson));
				colors[25] = createPaint(getResources().getColor(R.color.DeepPink));
				colors[26] = createPaint(getResources().getColor(R.color.Gainsboro));
				colors[27] = createPaint(getResources().getColor(R.color.Moccasin));
				colors[28] = createPaint(getResources().getColor(R.color.Tomato));
				colors[29] = createPaint(getResources().getColor(R.color.SlateBlue));
				
				HashMap<Integer,Excursion> exc = MapController.getInstance(this).getCurrentLocation().getExcursions(this);
				int addedPaths = 0;
				for(int i : exc.keySet()) {
					OverlayWay path = MapController.getInstance(this).getCurrentLocation().getExcursions(this).get(i).getPath();
					if(addedPaths < colors.length){ 
						path.setPaint(colors[addedPaths], null);
					}
					else {
						//generate random color
						int red = (int) (Math.random() * 255);
						int green = (int) (Math.random() * 255);
						int blue = (int) (Math.random() * 255);
						int color = Color.rgb(red, green, blue);
						//set paint
						path.setPaint(createPaint(color), null);
					}
					overlayPaths.addWay(new OverlayWayText(path, MapController.getInstance(this).getCurrentLocation().getExcursions(this).get(i).getName()));
					addedPaths++;
				}
				hideNavigationInstruction();
			}
		} catch (Exception e) {
			displayInfo(getString(R.string.message_map_cant_load_overlays));
			e.printStackTrace();
			hideNavigationInstruction();
		}
	}
	
	/**
	 * Shows to user the wanted position on the map (zoom and center), or a message if he's outside
	 * @param lat The latitude
	 * @param lon The longitude
	 * @param mapMarker A marker
	 */
	public void displayLocation(double lat, double lon) {
		MapView mv = (MapView) findViewById(R.id.mapView);
		boolean positionValid = mv.getMapViewLimits().getBottomLimit() <= lat && mv.getMapViewLimits().getTopLimit() >= lat && mv.getMapViewLimits().getLeftLimit() <= lon && mv.getMapViewLimits().getRightLimit() >= lon;
		if(positionValid) {
			byte zoom = mv.getMapZoomControls().getZoomLevelMax();
			if(mv.getMapZoomControls().getZoomLevelMax() - 4 > mv.getMapZoomControls().getZoomLevelMin()) { zoom = (byte) (mv.getMapZoomControls().getZoomLevelMax() - 4); }
			mv.getController().setZoom(zoom);
			GeoPoint location = new GeoPoint(lat, lon);
			mv.getController().setCenter(location);
			//Show user on map
			removeMarker("USER");
			addMarkerOnMap(location, "USER", getResources().getDrawable(R.drawable.marker_location_gps));
		}
		else {
			displayInfo(getString(R.string.message_map_position_out_of_bounds));
		}
	}

	/**
	 * Shows to user the given position on the map (zoom and center), or a message if he's outside
	 * @param lat The latitude
	 * @param lon The longitude
	 */
	public void displayPosition(double lat, double lon) {
		MapView mv = (MapView) findViewById(R.id.mapView);
		boolean positionValid = mv.getMapViewLimits().getBottomLimit() <= lat && mv.getMapViewLimits().getTopLimit() >= lat && mv.getMapViewLimits().getLeftLimit() <= lon && mv.getMapViewLimits().getRightLimit() >= lon;
		if(positionValid) {
			byte zoom = mv.getMapZoomControls().getZoomLevelMax();
			if(mv.getMapZoomControls().getZoomLevelMax() - 4 > mv.getMapZoomControls().getZoomLevelMin()){
				zoom = (byte) (mv.getMapZoomControls().getZoomLevelMax() - 4); 
			}
			mv.getController().setZoom(zoom);
			GeoPoint position = new GeoPoint(lat, lon);
			mv.getController().setCenter(position);
		}
		else {
			displayInfo(getString(R.string.message_map_position_out_of_bounds));
		}
	}
	
	/**
	 * Creates a {@link Paint} object with a certain color
	 * @param color The wanted color
	 * @param alpha The alpha channel
	 * @return The created Paint object
	 */
	private Paint createPaint(int color, int alpha) {
		Paint pnt = new Paint(Paint.ANTI_ALIAS_FLAG);
		pnt.setColor(color);
		pnt.setAlpha(alpha);
		pnt.setStyle(Paint.Style.STROKE);
		pnt.setStrokeWidth(7);
		pnt.setStrokeJoin(Paint.Join.ROUND);
		pnt.setStrokeCap(Paint.Cap.ROUND);
		//pnt.setPathEffect(new DashPathEffect(new float[] { 20, 20 }, 0));
		return pnt;
	}
	
	/**
	 * Creates a {@link Paint} object with a certain color
	 * @param color The wanted color
	 * @return The created Paint object
	 */
	private Paint createPaint(int color) {
		return createPaint(color, 170);
	}
	
	/**
	 * Displays a dialog which says that GPS isn't enabled.
	 */
	public void showDialogGpsNotEnabled() {
	    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle(R.string.dialog_title_no_gps);
	    builder.setMessage(R.string.message_gps_not_enabled)
	           .setCancelable(false)
	           .setPositiveButton(R.string.name_button_location_config, new DialogInterface.OnClickListener() {
	               public void onClick(final DialogInterface dialog, final int id) {
	                   startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	               }
	           })
	           .setNegativeButton(R.string.name_button_cancel, new DialogInterface.OnClickListener() {
	               public void onClick(final DialogInterface dialog, final int id) {
	                    dialog.cancel();
	               }
	           });
	    final AlertDialog alert = builder.create();
	    alert.show();
	}
	
	//METHODS RELATED TO NAVIGATION INSTRUCTIONS (SLIDING DRAWER)
	/**
	 * Change the navigation instruction to display.
	 * It updates the current path segment and set zoom and location.
	 * @param id The ID of the instruction
	 */
	public void setNavigationInstructionToDisplay(int id) {
		try {
			//Display the segment on map
			MapView mv = (MapView) findViewById(R.id.mapView);
			//Get and set segment
			Paint color = createPaint(getResources().getColor(R.color.Green), 170);
			OverlayWay path = MapController.getInstance(this).getCurrentLocation().getExcursions(this).get(MapController.getInstance().getPathToDisplay()).getInstructions()[id].getSegment();
			Paint haloColor = createPaint(getResources().getColor(R.color.Blue), 100);
			haloColor.setStrokeWidth(17);
			path.setPaint(color, haloColor);
			if(segment != null) { overlayPaths.removeWay(segment); }
			segment = new OverlayWayText(path, "");
			overlayPaths.addWay(segment);
			//Set zoom and center
			GeoPoint start = path.getWayNodes()[0][0];
			boolean positionValid = mv.getMapViewLimits().getBottomLimit() <= start.getLatitude() && mv.getMapViewLimits().getTopLimit() >= start.getLatitude() && mv.getMapViewLimits().getLeftLimit() <= start.getLongitude() && mv.getMapViewLimits().getRightLimit() >= start.getLongitude();
			if(positionValid) {
				mv.zoomToPoint(start);
				//Show destination on map
				removeMarker("NAV");
				addMarkerOnMap(start, "NAV", getResources().getDrawable(R.drawable.marker_location_nav));
			}
			else {
				displayInfo(getString(R.string.message_map_position_out_of_bounds));
			}
			
			//Change the instruction
			ViewPager myPager = (ViewPager) findViewById(R.id.pager_nav);
			myPager.setCurrentItem(id);
			
			//Change images
			ImageView right = (ImageView) findViewById(R.id.imageRight);
			ImageView left = (ImageView) findViewById(R.id.imageLeft);
			left.setVisibility(View.VISIBLE);
			right.setVisibility(View.VISIBLE);
			int nbInstructions = MapController.getInstance(this).getCurrentLocation().getExcursions(this).get(MapController.getInstance().getPathToDisplay()).getInstructions().length;
			if(id == 0) {
				left.setVisibility(View.INVISIBLE);
			}
			else if(id == nbInstructions -1) {
				right.setVisibility(View.INVISIBLE);
			}
			
			//MapController.getInstance(this).setInstructionToDisplay(id);
		} catch (Exception e) {
			displayInfo(getString(R.string.message_map_navigation_error));
			e.printStackTrace();
		}
	}
	
	/**
	 * Displays the sliding drawer which contains navigation instructions, and loads instructions
	 * @param id The ID of the {@link Excursion} to display
	 */
	private void showNavigationInstruction(int id) {
		try {
			//Set view pager
			NavigationInstructionPagerAdapter adapter = new NavigationInstructionPagerAdapter(MapController.getInstance(this).getCurrentLocation().getExcursions(this).get(id).getInstructions());
			ViewPager myPager = (ViewPager) findViewById(R.id.pager_nav);
			myPager.setAdapter(adapter);
			myPager.setOnPageChangeListener(listener);
			MapView mv = (MapView) findViewById(R.id.mapView);
			//Change zoom
			int zoomMoyen = mv.getMapZoomControls().getZoomLevelMin() + (int) ((mv.getMapZoomControls().getZoomLevelMax() - mv.getMapZoomControls().getZoomLevelMin())/2);
			mv.getController().setZoom(zoomMoyen);
			//Display sliding drawer
			SlidingDrawer slide = (SlidingDrawer) findViewById(R.id.drawer);
			slide.setVisibility(View.VISIBLE);
			setNavigationInstructionToDisplay(0);
			if(!slide.isOpened()) { slide.animateOpen(); }
		} catch (Exception e) {
			displayInfo(getString(R.string.message_map_navigation_error));
			e.printStackTrace();
			hideNavigationInstruction();
		}
	}
	
	/**
	 * Hides the sliding drawer which contains navigation instructions
	 */
	private void hideNavigationInstruction() {
		SlidingDrawer slide = (SlidingDrawer) findViewById(R.id.drawer);
		slide.setVisibility(View.GONE);
	}

	//METHODS RELATED TO MAP MARKERS
	/**
	 * Adds a new marker to display on map
	 * @param loc The location of the marker
	 * @param name The name of the marker
	 * @param icon The associated icon
	 */
	public void addMarkerOnMap(GeoPoint loc, String name, Drawable icon) {
		icon=new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(((BitmapDrawable)icon).getBitmap(), 56, 56, true));
		OverlayItem marker = new OverlayItem(loc, null, null, ItemizedOverlay.boundCenterBottom(icon));
		markers.put(name, marker);
		overlayPoiItemMarker.addItem(marker, null);
	}
	
	/**
	 * Removes a marker from the map
	 * @param name The name of the wanted marker
	 */
	public void removeMarker(String name) {
		overlayPoiItemMarker.removeItem(markers.get(name));
		markers.remove(name);
	}
	
	/**
	 * Hides all the open balloons
	 */
	public void hideBalloons() {
		overlayPoiItemMarker.hideAllBalloons();
	}
}
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
import java.io.FileFilter;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.line2soft.preambul.controllers.ExcursionInfoListener;
import net.line2soft.preambul.controllers.MapController;
import net.line2soft.preambul.models.Excursion;
import net.line2soft.preambul.models.Locomotion;
import net.line2soft.preambul.models.NamedPoint;
import net.line2soft.preambul.models.NamedPointComparator;
import net.line2soft.preambul.models.NavigationInstruction;
import net.line2soft.preambul.models.PointOfInterest;
import net.line2soft.preambul.utils.RWFile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;
import net.line2soft.preambul.R;

/**
 * This class represents the informations of an excursion
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class ExcursionInfoActivity extends FragmentActivity {
// ATTRIBUTES
	/** Activity's listener **/
	private ExcursionInfoListener listener;
	/** The list of pictures for the gallery **/
	private File[] imagesFile;

// OTHER METHODS
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Check if this version of Android allows to use custom title bars
		boolean feature=requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		/** Set layout **/
		//Base layout
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_excursion_info);
		//set title bar
		if(feature){
	        	getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.activity_title_bar);
	            TextView myTitleText = (TextView) findViewById(R.id.textView0);
	            myTitleText.setText(getString(R.string.title_activity_excursion_info));	
	    }
		
		//Get excursion ID
		int id = getIntent().getIntExtra(ExcursionListActivity.EXCURSION_ID, 0);
		if(id > 0) {
			//Set listener
			listener = new ExcursionInfoListener(this);
			//Set tabs
	        
	        TabHost tabHost=(TabHost)findViewById(R.id.tabHost);
	        tabHost.setup();
	
	        TabSpec spec1=tabHost.newTabSpec("Info");
	        spec1.setContent(R.id.tab1);
	        spec1.setIndicator("",getResources().getDrawable(R.drawable.description_tab));
		    spec1.setContent(R.id.tab1);
		    
	        TabSpec spec2=tabHost.newTabSpec("POI");
	        spec2.setIndicator("",getResources().getDrawable(R.drawable.pois_tab));
		    spec2.setContent(R.id.tab2);
		    
	        TabSpec spec3=tabHost.newTabSpec("Instructions");
	        spec3.setIndicator("",getResources().getDrawable(R.drawable.instruction_tab));
		    spec3.setContent(R.id.tab3);
		    
		    TabSpec spec4=tabHost.newTabSpec("Photos");
		    spec4.setIndicator("",getResources().getDrawable(R.drawable.photos_tab));
		    spec4.setContent(R.id.tab4);
	
	        tabHost.addTab(spec1);
	        tabHost.addTab(spec2);
	        tabHost.addTab(spec3);
		    tabHost.addTab(spec4);
		    
		    //set the info tab
		    try {
				Excursion exc=MapController.getInstance(this).getCurrentLocation().getExcursions(this).get(id);
				//Display locomotions
				Locomotion[] locomotionsItems = exc.getLocomotions();
				LinearLayout locomotionsLayout = (LinearLayout) findViewById(R.id.locomotionsLayout);
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				for(int i=0; i < locomotionsItems.length; i++) {
					if(locomotionsItems[i].getIcon() == null) {
						//Define icon if undefined
						int imageResource = getResources().getIdentifier("locomotion_" + locomotionsItems[i].getKey(), "drawable", getPackageName());
						if(imageResource != 0) {
							Drawable ic = getResources().getDrawable(imageResource);
							locomotionsItems[i].setIcon(ic);
						}
					}
					ImageView img = (ImageView) inflater.inflate(R.layout.locomotion_item, null);
					img.setImageDrawable(locomotionsItems[i].getIcon());
					locomotionsLayout.addView(img);
				}

			    int value=exc.getDifficulty();
			    if(value==Excursion.DIFFICULTY_NONE){
					ImageView view=(ImageView)findViewById(R.id.ImageView2);
					view.setImageResource(R.drawable.difficulte1);
			    }else if(value==Excursion.DIFFICULTY_EASY){
					ImageView view=(ImageView)findViewById(R.id.ImageView2);
					view.setImageResource(R.drawable.difficulte2);
			    	
			    }else if(value==Excursion.DIFFICULTY_MEDIUM){
					ImageView view=(ImageView)findViewById(R.id.ImageView2);
					view.setImageResource(R.drawable.difficulte3);

			    }else if(value==Excursion.DIFFICULTY_HARD){
					ImageView view=(ImageView)findViewById(R.id.ImageView2);
					view.setImageResource(R.drawable.difficulte4);
			    	
			    }else if(value==Excursion.DIFFICULTY_EXPERT){
					ImageView view=(ImageView)findViewById(R.id.ImageView2);
					view.setImageResource(R.drawable.difficulte5);
			    	
			    }
				String time = ExcursionAdapter.convertTime(exc.getTime());
				
			    TextView view=(TextView)findViewById(R.id.textView1);
				view.setText(time);
				

				Double length = Double.valueOf(exc.getLength() / 1000);
				String lengthString = length.toString().substring(0, length.toString().lastIndexOf(".")+2)+" km";
			    view=(TextView)findViewById(R.id.textView2);			    
				view.setText(lengthString);
				
			    view=(TextView)findViewById(R.id.textView3);
				view.setText(exc.getDescription());
				
		    } catch (Exception e) {
				e.printStackTrace();
				onBackPressed();
				displayInfo(getString(R.string.message_excursion_not_found));
			}
		    MapController exc=MapController.getInstance(this);
		    
		    //set the POI tab
		    try{
		    	PointOfInterest[] pois=exc.getCurrentLocation().getExcursions(this).get(id).getSurroundingPois(this);
		    	List<NamedPoint> itemsList = Arrays.asList(((NamedPoint[])pois));
				Collections.sort(itemsList, new NamedPointComparator());
				pois=itemsList.toArray(new PointOfInterest[itemsList.size()]);
		    	if(pois.length > 0) {
			    	ListView listPoi = (ListView) findViewById(R.id.listView2);
			        listPoi.setAdapter(new FavoriteAdapter(getLayoutInflater(), pois, this));
			        listPoi.setOnItemClickListener(listener);
			        listPoi.setVisibility(View.VISIBLE);
			        findViewById(R.id.NoPOIs).setVisibility(View.GONE);
		    	}
		    }catch(Exception e){
		    	e.printStackTrace();
		    	System.err.println("Couldn't fill POI tab");
		    }
		    
		    
			//set the instruction tab
			ListView list = (ListView) findViewById(R.id.listView1);
			if(list!=null){
		        try {
		        	NavigationInstruction[] instructions = exc.getCurrentLocation().getExcursions(this).get(id).getInstructions();
					if(instructions.length!=0){
						((TextView) findViewById(R.id.NoInstructions)).setVisibility(View.GONE);
					}
					list.setAdapter(new ExcursionInfoInstructionAdapter(this,instructions));
					list.setOnItemClickListener(listener);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			//Set the picture tab
			String imagePath = Environment.getExternalStorageDirectory().getPath()+File.separator+"Android"+File.separator+"data"+File.separator+"net.line2soft.preambul"+File.separator+"files"
							+File.separator+MapController.getInstance(this).getCurrentLocation().getId()+File.separator+"excursions"+File.separator+getIntent().getIntExtra(ExcursionListActivity.EXCURSION_ID, 0);
			FileFilter filter = new FileFilter() {
			    @Override
			    public boolean accept(File file) {
			        return file.getAbsolutePath().matches(".*\\.jpg");
			    }
			};
			imagesFile = new File(imagePath).listFiles(filter);
			((TextView) findViewById(R.id.NoPhotos)).setVisibility(View.VISIBLE);
			if(imagesFile!=null){
				if(imagesFile.length > 0){
					((TextView) findViewById(R.id.NoPhotos)).setVisibility(View.GONE);
					ImagePagerAdapter adapter = new ImagePagerAdapter(getSupportFragmentManager(),imagesFile, this);
					ViewPager myPager = (ViewPager) findViewById(R.id.pager_images);
					myPager.setAdapter(adapter);
					myPager.setOnPageChangeListener(listener);
					
					//Set listener on right and left buttons in picture tab
					(findViewById(R.id.imageRight)).setOnClickListener(listener);
					(findViewById(R.id.imageLeft)).setOnClickListener(listener);		
					//Change visibility of these buttons
					ImageView right = (ImageView) findViewById(R.id.imageRight);
					ImageView left = (ImageView) findViewById(R.id.imageLeft);
					left.setVisibility(View.INVISIBLE);
					right.setVisibility(View.INVISIBLE);
					int idPhoto=((ViewPager)findViewById(R.id.pager_images)).getCurrentItem();
					int nbPhotos = getImagesFile().length;
					if(idPhoto != nbPhotos -1) {
						right.setVisibility(View.VISIBLE);
					}
				}
			}

			//Set listener on launch button
			Button launch = (Button) findViewById(R.id.button_load_excursion);
			launch.setOnClickListener(listener);
			
		}else {
			onBackPressed();
			displayInfo(getString(R.string.message_excursion_not_found));
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		((ImageView)findViewById(R.id.imageView1)).setImageBitmap(RWFile.readImageBackground(R.drawable.back_excursion_info, this));
	}
	
	@Override
	protected void onPause() {
		super.onPause();		
	}
	
	@Override
	protected void onDestroy() {
		((BitmapDrawable)((ImageView)findViewById(R.id.imageView1)).getDrawable()).getBitmap().recycle();
	    super.onDestroy();
	}
	
//ACCESSORS
	/**
	 * Returns the array of pictures of the gallery
	 * @return The array of pictures
	 */
	public File[] getImagesFile(){
		return imagesFile;
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
	 * Loads a bitmap
	 * @param resId The resource ID
	 * @param mImageView The image view to put the image in
	 */
	public void loadBitmap(int resId, ImageView mImageView) {
        BitmapWorkerTask task = new BitmapWorkerTask(mImageView, this);
        task.execute(resId);
    }
	
	private class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
	    private final WeakReference<ImageView> imageViewReference;
	    private final Context context;

	    public BitmapWorkerTask(ImageView imageView, Context ctx) {
	        // Use a WeakReference to ensure the ImageView can be garbage collected
	        imageViewReference = new WeakReference<ImageView>(imageView);
	        context=ctx;
	    }

	    // Decode image in background.
	    @Override
	    protected Bitmap doInBackground(Integer... params) {
	        File file=imagesFile[params[0]];
		    DisplayMetrics metrics = new DisplayMetrics();
		    getWindowManager().getDefaultDisplay().getMetrics(metrics);
	        return RWFile.readImage(file, metrics.widthPixels, metrics.heightPixels, context);
	    }

	    // Once complete, see if ImageView is still around and set bitmap.
	    @Override
	    protected void onPostExecute(Bitmap bitmap) {
	        if (imageViewReference != null && bitmap != null) {
	            final ImageView imageView = imageViewReference.get();
	            if (imageView != null) {
	                imageView.setImageBitmap(bitmap);
	            }
	        }
	    }
	}
}
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.line2soft.preambul.controllers.FavoriteListListener;
import net.line2soft.preambul.controllers.MapController;
import net.line2soft.preambul.models.NamedPoint;
import net.line2soft.preambul.models.NamedPointComparator;
import net.line2soft.preambul.utils.RWFile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import net.line2soft.preambul.R;

/**
 * This class represents a list of favorite
 * @author équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class FavoriteListActivity extends Activity {
// ATTRIBUTES
	/** Used to identify a favorite **/ 
	public static final String FAVORITE_KEY = "net.line2soft.preambul.FAVORITE_KEY";
	/** Used to identify a favorite category **/
	public static final String FAVORITE_CATEG = "net.line2soft.preambul.FAVORITE_CATEG";
	/** Used to identify a favorite type **/
	public static final String FAVORITE_TYPE = "net.line2soft.preambul.FAVORITE_TYPE";
	/** Used to identify a favorite ID **/
	public static final String NAMEDPOINTFAVORITE_KEY = "net.line2soft.preambul.NAMEDPOINTFAVORITE_KEY";

	/** The listener of the class. */
	private FavoriteListListener listener;
	/** The list to be displayed. */
	private NamedPoint[] favoriteList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);	
		listener = new FavoriteListListener(this);
		//Check if this version of Android allows to use custom title bars
		boolean feature=requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		//set the content as set in the xml
		setContentView(R.layout.activity_favorite_list);
		
		if(feature){
        	getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.activity_title_bar);
            TextView myTitleText = (TextView) findViewById(R.id.textView0);
            myTitleText.setText(getString(R.string.title_activity_favorite_list));	
        }
		
		
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		
		//Get favorites
		try {
			HashMap<String, NamedPoint> coll=MapController.getInstance(this).getCurrentLocation().getFavorites(this);			
			
			Object[] locTmp = coll.values().toArray();
			favoriteList = Arrays.copyOf(locTmp, locTmp.length, NamedPoint[].class);
			
			setFavoriteList(favoriteList);
			
			//Init listview
			ListView list = (ListView) findViewById(R.id.favoritesList);
	        list.setOnItemClickListener(listener);
	        findViewById(R.id.view1).requestFocus();
	        
	        //Init AutoCompleteTextView
			AutoCompleteTextView autoText=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);
			autoText.setOnItemClickListener(listener);
			autoText.setOnEditorActionListener(listener);
			
			
			((Button) findViewById(R.id.buttonAddFavorite)).setOnClickListener(listener);
			
			((ImageButton) findViewById(R.id.deleteButton)).setOnClickListener(listener);
			
		} catch (FileNotFoundException e) {
			displayInfo(getString(R.string.message_favorites_no_one));
			e.printStackTrace();
		}  catch (IOException e) {
			displayInfo(getString(R.string.message_favorites_read_error));
			e.printStackTrace();
		}
		((ImageView)findViewById(R.id.imageView1)).setImageBitmap(RWFile.readImageBackground(R.drawable.back_excursion_info, this));
	}
	
	@Override
	public void onBackPressed() {
		Intent it = new Intent(this, SlippyMapActivity.class);
		startActivity(it);
		finish();
	}
	
	/**
	 * Changes the favorite list and update search values
	 * @param newList The new favorite list
	 */
	public void setFavoriteList(NamedPoint[] newList) {
		List<NamedPoint> itemsList = Arrays.asList(newList);
		Collections.sort(itemsList, new NamedPointComparator());
		favoriteList=itemsList.toArray(new NamedPoint[itemsList.size()]);
		
		//Init listview
		ListView list = (ListView) findViewById(R.id.favoritesList);
        list.setAdapter(new FavoriteAdapter(getLayoutInflater(), favoriteList,this));
        
        //Init AutoCompleteTextView
        int i=0;
		String[] autoComp= new String[favoriteList.length];
		while(i<favoriteList.length){
			autoComp[i]=favoriteList[i].getName();
			i++;
		}
		AutoCompleteTextView autoText=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);
		autoText.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,autoComp));
	}
	
	@Override
	protected void onDestroy() {
		((BitmapDrawable)((ImageView)findViewById(R.id.imageView1)).getDrawable()).getBitmap().recycle();
	    super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
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
}
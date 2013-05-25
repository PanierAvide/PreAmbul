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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.line2soft.preambul.controllers.ExcursionListListener;
import net.line2soft.preambul.controllers.MapController;
import net.line2soft.preambul.models.Excursion;
import net.line2soft.preambul.models.ExcursionComparator;
import net.line2soft.preambul.models.Locomotion;
import net.line2soft.preambul.utils.RWFile;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
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
 * The main activity of the location management part.
 * It's used to select location, and to go in the others activities of the location management part.
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class ExcursionListActivity extends Activity {
// ATTRIBUTES
	/** Attribute used in order to know what is the excursion to display **/
	public static final String EXCURSION_ID = "net.line2soft.preambul.EXCURSION_ID";
	/** listener for the whole activity **/
	private ExcursionListListener listener;
	/** List of shown excursions **/
	private Excursion[] excursionList;
	
// ACCESSORS
    /**
     * Returns the list of excursions
     * @return The excursions
     */
    public Excursion[] getExcursionList(){
    	return excursionList;
    }
    
// MODIFIERS
    /**
     * Changes the excursion list
     * @param newList The new list
     */
	public void setExcursionList(Excursion[] newList) {
		ListView list = (ListView) findViewById(R.id.excursionList);
		excursionList = newList;
		List<Excursion> listColl = Arrays.asList(excursionList);
		Collections.sort(listColl, new ExcursionComparator());
		excursionList = listColl.toArray(new Excursion[listColl.size()]);
        list.setAdapter(new ExcursionAdapter(this,newList, listener));
        if(newList.length==0) { displayInfo("Aucun résultat trouvé"); }
	}

// OTHER METHODS
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		

		listener = new ExcursionListListener(this);

		//Check if this version of Android allows to use custom title bars
		boolean feature=requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		//set the content as set in the xml
		setContentView(R.layout.activity_excursion_list);
		//if this version allows custom title bars set the custom bar and the textView in it
        if(feature){
        	getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.activity_title_bar);
            TextView myTitleText = (TextView) findViewById(R.id.textView0);
            myTitleText.setText(getString(R.string.title_activity_excursion_list));	
        }
        
        //Set the MultiSpinner to select locomotion
        MultiSpinner multiSpinner = (MultiSpinner) findViewById(R.id.multi_spinner);
        ArrayList<String> itemsLocomotion = new ArrayList<String>();
        Locomotion[] availableLocomotions = MapController.getInstance(this).getCurrentLocation().getAvailableLocomotions(this);
        for(Locomotion l : availableLocomotions) {
        	itemsLocomotion.add(l.getName());        	
        }
        multiSpinner.setItems(itemsLocomotion, getString(R.string.name_locomotion_types), listener);
        

		//Get excursions
		HashMap<Integer, Excursion> coll;
		try {
			if(excursionList == null) {
				coll = MapController.getInstance(this).getCurrentLocation().getExcursions(this);
				Object[] locTmp = coll.values().toArray();
				setExcursionList(Arrays.copyOf(locTmp, locTmp.length, Excursion[].class));
			}
			
			//Init listview
			ListView list = (ListView) findViewById(R.id.excursionList);
	        list.setAdapter(new ExcursionAdapter(this,excursionList, listener));
	        list.setOnItemClickListener(listener);
	        findViewById(R.id.view1).requestFocus();
	        
	        //Init AutoCompleteTextView
	        int i=0;
			String[] autoComp= new String[excursionList.length];
			while(i<excursionList.length){
				autoComp[i]=excursionList[i].getName();
				i++;
			}
			AutoCompleteTextView autoText=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);
			autoText.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,autoComp));
			autoText.setOnItemClickListener(listener);
			autoText.setOnEditorActionListener(listener);
			
			//Listeners on buttons
			Button bDisplayAll = (Button) findViewById(R.id.buttonDisplayAll);
			bDisplayAll.setOnClickListener(listener);
			Button bDisplayNone = (Button) findViewById(R.id.buttonDisplayNone);
			bDisplayNone.setOnClickListener(listener);
			ImageButton bDelete = (ImageButton) findViewById(R.id.deleteButton);
			bDelete.setOnClickListener(listener);
		} catch (FileNotFoundException e) {
			displayInfo(getString(R.string.message_excursions_xml_not_found));
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			displayInfo(getString(R.string.message_excursions_read_error));
			e.printStackTrace();
		} catch (IOException e) {
			displayInfo(getString(R.string.message_excursions_read_error));
			e.printStackTrace();
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		((ImageView)findViewById(R.id.imageView1)).setImageBitmap(RWFile.readImageBackground(R.drawable.back_excursions, this));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
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
	 * Returns the current search query
	 * @return The query
	 */
	public String getSearchQuery() {
		AutoCompleteTextView autoText=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);
		return autoText.getText().toString();
	}
}
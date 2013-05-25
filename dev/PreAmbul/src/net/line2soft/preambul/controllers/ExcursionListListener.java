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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.line2soft.preambul.models.Excursion;
import net.line2soft.preambul.models.Locomotion;
import net.line2soft.preambul.utils.RWFile;
import net.line2soft.preambul.views.ExcursionAdapter;
import net.line2soft.preambul.views.ExcursionInfoActivity;
import net.line2soft.preambul.views.ExcursionListActivity;
import net.line2soft.preambul.views.LocationSelectionActivity;
import net.line2soft.preambul.views.MultiSpinner;
import net.line2soft.preambul.views.SlippyMapActivity;
import net.line2soft.preambul.views.MultiSpinner.MultiSpinnerListener;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import net.line2soft.preambul.R;

/**
 * This listener reacts to events in {@link ExcursionListActivity}
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class ExcursionListListener implements AdapterView.OnItemClickListener, TextView.OnEditorActionListener, View.OnClickListener, MultiSpinnerListener {
// ATTRIBUTES
	/** The current activity to listen **/
	private ExcursionListActivity activity;

// CONSTRUCTOR
	/**
	 * Class constructor.
	 * @param locationSelectionActivity The activity to listen
	 */
	public ExcursionListListener(ExcursionListActivity locationSelectionActivity) {
		activity = locationSelectionActivity;
	}

// OTHER METHODS
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//AutoCompleteTextView suggestion items click: launch search
		if(view.getClass().equals(TextView.class)) {
			search(((TextView) view).getText().toString());
		}
		//Excursion list item click: display info
		else {
			int idExc = (int) parent.getAdapter().getItemId(position);
			Intent it = new Intent(activity, ExcursionInfoActivity.class);
			it.putExtra(ExcursionListActivity.EXCURSION_ID, idExc);
			activity.startActivity(it);
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		//Reaction to click on search button in keyboard
		search(v.getText().toString());
		return true;
	}

	@Override
	public void onClick(View v) {
		//Reaction to buttons
		//Delete the search
		if(v==activity.findViewById(R.id.deleteButton)) {
			search("");
		}
		//Display all excursions
		else if(v==activity.findViewById(R.id.buttonDisplayAll)) {
			MapController.getInstance(activity).setPathToDisplay(0);
			//Go back to map
			launchMap();
		}
		//Hide all excursions
		else if(v==activity.findViewById(R.id.buttonDisplayNone)) {
			MapController.getInstance(activity).setPathToDisplay(-1);
			//Go back to map
			launchMap();
		}
		//Set a favorite excursion
		else if(v instanceof ImageButton) {
			final int position = ((ListView)activity.findViewById(R.id.excursionList)).getPositionForView((View)v.getParent().getParent());
			Excursion exc=(Excursion) ((ExcursionAdapter)((ListView)v.getParent().getParent().getParent()).getAdapter()).getItem(position);
			File file=new File(activity.getFilesDir().getPath()+File.separator+"locations"+File.separator+MapController.getInstance(activity).getCurrentLocation().getId()+File.separator+"bookmarks"+File.separator+"excursions.xml");
			if(exc.isFavorite()){
				exc.setFavorite(false);
				((ImageButton)v).setImageResource(R.drawable.icon_favorites_empty);
				activity.setExcursionList(activity.getExcursionList());
				try {
					RWFile.saveFavoritesExcursions(file, activity.getExcursionList());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				exc.setFavorite(true);
				((ImageButton)v).setImageResource(R.drawable.icon_favorites);	
				activity.setExcursionList(activity.getExcursionList());
				try {
					RWFile.saveFavoritesExcursions(file, activity.getExcursionList());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/** The research method */
	private void search(String query) {
		Locomotion[] selectedLocomotionsArray = null;
		//Get selected locomotions
		MultiSpinner mspin = (MultiSpinner) activity.findViewById(R.id.multi_spinner);
		if(mspin != null) {
			ArrayList<Locomotion> selectedLocomotions = new ArrayList<Locomotion>();
			List<String> items = mspin.getItems();
			boolean[] selectedItems = mspin.getSelected();
			for(int i=0; i < selectedItems.length; i++) {
				String key = Locomotion.findKey(activity, items.get(i));
				if(selectedItems[i]) { selectedLocomotions.add(Locomotion.getLocomotion(activity, key)); }
			}
			selectedLocomotionsArray = Arrays.copyOf(selectedLocomotions.toArray(), selectedLocomotions.size(), Locomotion[].class);
		}
		try {
			ArrayList<Excursion> result = (selectedLocomotionsArray == null) ? MapController.getInstance(activity).getCurrentLocation().findExcursions(activity, query) : MapController.getInstance(activity).getCurrentLocation().findExcursions(activity, query, selectedLocomotionsArray); 
			//Convert hashmap to array
			Object[] sR = result.toArray();
			Excursion[] resultArray = Arrays.copyOf(sR, sR.length, Excursion[].class);
			//Set new list
			activity.setExcursionList(resultArray);
			//Clean text field if no query
			if(query.trim().equals("")) {
				((TextView) activity.findViewById(R.id.autoCompleteTextView1)).setText("");
			}
		} catch (FileNotFoundException e) {
			activity.displayInfo(activity.getString(R.string.message_excursions_xml_not_found));
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			activity.displayInfo(activity.getString(R.string.message_excursions_read_error));
			e.printStackTrace();
		} catch (IOException e) {
			activity.displayInfo(activity.getString(R.string.message_excursions_read_error));
			e.printStackTrace();
		}
	}

	@Override
	public void onItemsSelected(boolean[] selected) {
		//Reaction to locomotion selection
		search(activity.getSearchQuery());
	}
	
	/**
	 * Launches the map
	 */
	private void launchMap() {
		boolean excursionsBeforeMap = PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("displayExcursionsBeforeMap", false);
		if(excursionsBeforeMap) {
			Intent it = new Intent(activity, SlippyMapActivity.class);
			it.putExtra(LocationSelectionActivity.LOCATION_ID, MapController.getInstance(activity).getCurrentLocation().getId());
			activity.startActivity(it);
		} else { activity.onBackPressed(); }
	}
}
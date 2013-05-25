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

import java.util.ArrayList;

import net.line2soft.preambul.models.Location;

import net.line2soft.preambul.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This adapter allows loading location in a list view, where each location can be checked.
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class CheckableLocationAdapter extends BaseAdapter {
// ATTRIBUTES
	/** The inflater **/
	private LayoutInflater _inflater;
	/** The list of locations **/
	private Location[] _items;
	/** The list of is a location checked **/
	private boolean[] isChecked;
	
// CONSTRUCTOR
	/**
	 * Class constructor
	 * @param context Application context
	 * @param items The list of locations to show
	 */
	public CheckableLocationAdapter(Context context, Location[] items) {
		_inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		_items = items;
		isChecked = new boolean[items.length];
		for(int i=0; i<isChecked.length; i++) {
			isChecked[i] = false;
		}
	}

// ACCESSORS
	@Override
	public int getCount() {
		return _items.length;
	}

	@Override
	public Object getItem(int position) {
		Object result = null;
		if(position >=0 && position < getCount()) {
			result = _items[position];
		}
		return result;
	}

	@Override
	/**
	 * @return The queried ID, or -1 if the object wasn't found.
	 */
	public long getItemId(int position) {
		int result = -1;
		try {
			result = _items[position].getId();
		}
		catch(ArrayIndexOutOfBoundsException aioobe) {}
		return result;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = _inflater.inflate(R.layout.checkable_location_item, null);
			ViewHolder holder = new ViewHolder();
			//Set views as objects
			holder.locationLogo = (ImageView) convertView.findViewById(R.id.locationLogo);
			holder.locationName = (TextView) convertView.findViewById(R.id.locationName);
	       	holder.locationVersion = (TextView) convertView.findViewById(R.id.locationVersion);
			convertView.setTag(holder);
		}

		Location item = (Location) getItem(position);
		if(item != null) {
			//Edit views
			ViewHolder holder = (ViewHolder)convertView.getTag();
			holder.locationLogo.setImageURI(item.getLogo());
			holder.locationName.setText(item.getName());
			holder.locationVersion.setText("Version "+item.getVersion());
		}
        
        return convertView;
	}

	/**
	 * Returns the checked locations in the adapter
	 * @return The array of selected {@link Location}s
	 */
	public int[] getCheckedLocationsIds() {
		ArrayList<Integer> checkedLocations = new ArrayList<Integer>();
		//Look at each item
		for(int i=0; i < getCount(); i++) {
			if(isChecked[i]) { checkedLocations.add(_items[i].getId()); }
		}
		//Convert to int[]
		int[] result = new int[checkedLocations.size()];
		for(int j=0; j < checkedLocations.size(); j++) {
			result[j] = checkedLocations.get(j);
		}
		return result;
	}
	
// MODIFIERS
	/**
	 * Set if an item is checked or not
	 * @param id The wanted item's ID
	 * @param value The boolean value of check
	 */
	public void setItemCheck(int id, boolean value) {
		isChecked[id] = value;
	}
	
// INNER CLASS
	static class ViewHolder {
		ImageView locationLogo;
        TextView locationName;
        TextView locationVersion;
	}
}
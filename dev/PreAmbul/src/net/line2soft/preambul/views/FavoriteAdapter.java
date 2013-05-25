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

import net.line2soft.preambul.models.NamedPoint;
import net.line2soft.preambul.models.PointOfInterest;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import net.line2soft.preambul.R;

/**
 * The FavoriteAdapter class
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class FavoriteAdapter extends BaseAdapter {
// ATTRIBUTES
	/** The inflater **/
	private LayoutInflater inflater;
	/** The items **/
	private NamedPoint[] items;
	/** The favorite icon **/
	private Drawable iconFavorite;
	private Context ctx;
	
// CONSTRUCTROR
	/**
	 * Constructor of the FavoriteAdapter class
	 * @param i, items[]
	 */
	public FavoriteAdapter(LayoutInflater i, NamedPoint[] items, Context context) {
		this.inflater = i;
		this.items = items;
		this.ctx=context;
		iconFavorite = ctx.getResources().getDrawable(R.drawable.icon_favorites);
	}

// ACCESSORS
	/**
	 * Return the count
	 * @return The count
	 */
	public int getCount() {
		return items.length;
	}
	
	/**
	 * Return an item at a given position
	 * @param position
	 * @return The item
	 */
	public Object getItem(int position) {
		return items[position];
	}
	
	/**
	 * Return the id of an item
	 * @param position
	 * @return The id
	 */
	public long getItemId(int position) {
		return position;
	}
	
	/**
	 * Return the view
	 * @param position, convertView, parent
	 * @return The view
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = inflater.inflate(R.layout.favorite_item, null);
			ViewHolder holder = new ViewHolder();
			holder.favoriteName = (TextView) convertView.findViewById(R.id.favoriteName);
			holder.imageView1 = (ImageView) convertView.findViewById(R.id.imageView1);
			convertView.setTag(holder);
		}
		
		NamedPoint item = (NamedPoint) getItem(position);
		if(item != null) {
			//Edit views
			ViewHolder holder = (ViewHolder)convertView.getTag();
			holder.favoriteName.setText(item.getName());
			if(item instanceof PointOfInterest){
				Drawable icon = (((PointOfInterest) item).getType().getIcon() != null) ? ((PointOfInterest) item).getType().getIcon() : ((PointOfInterest) item).getType().getCategory().getIcon();
				if(icon == null) {
					holder.imageView1.setImageDrawable(iconFavorite);
					holder.imageView1.setAdjustViewBounds(true);
					holder.imageView1.setMaxHeight((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, ctx.getResources().getDisplayMetrics()));
					holder.imageView1.setMaxWidth((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, ctx.getResources().getDisplayMetrics()));
				}
				else { holder.imageView1.setImageDrawable(icon); }
			}else{
				holder.imageView1.setImageDrawable(iconFavorite);
				holder.imageView1.setAdjustViewBounds(true);
				holder.imageView1.setMaxHeight((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, ctx.getResources().getDisplayMetrics()));
				holder.imageView1.setMaxWidth((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, ctx.getResources().getDisplayMetrics()));
			}
		}
		
        return convertView;
	}

// INNER CLASS
	static class ViewHolder {
		ImageView imageView1;
        TextView favoriteName;
	}
}
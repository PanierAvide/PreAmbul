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

import net.line2soft.preambul.controllers.ExcursionListListener;
import net.line2soft.preambul.models.Excursion;
import net.line2soft.preambul.models.Locomotion;
import net.line2soft.preambul.utils.RWFile;
import android.R.color;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import net.line2soft.preambul.R;

/**
 * This adapter allows loading excursions in a list view.
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class ExcursionAdapter extends BaseAdapter {
// ATTRIBUTES
	/** The inflater **/
	private LayoutInflater inflater;
	/** The list of Excursions **/
	private Excursion[] items;
	/** An application context **/
	private Context ctx;
	/** The listener **/
	private ExcursionListListener listener;
	
// CONSTRUCTOR
	/**
	 * Class constructor
	 * @param context Application context
	 * @param items The list of Excursions to show
	 */
	public ExcursionAdapter(Context context, Excursion[] items, ExcursionListListener ls) {
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
		this.ctx = context;
		this.listener= ls;
	}

// ACCESSORS
	@Override
	public int getCount() {
		return items.length;
	}

	@Override
	public Object getItem(int position) {
		Object result = null;
		if(position >=0 && position < getCount()) {
			result = items[position];
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
			result = items[position].getId();
		}
		catch(ArrayIndexOutOfBoundsException aioobe){}
		return result;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = inflater.inflate(R.layout.excursion_item, null);
			ViewHolder holder = new ViewHolder();
			holder.excursionName = (TextView) convertView.findViewById(R.id.excursionName);
	       	holder.excursionLength = (TextView) convertView.findViewById(R.id.excursionLength);
	       	holder.locomotionsLayout = (LinearLayout) convertView.findViewById(R.id.locomotionsLayout);
	       	holder.excursionFavorite = (LinearLayout) convertView.findViewById(R.id.imageView2);
			convertView.setTag(holder);
		}

		Excursion item = (Excursion) getItem(position);
		if(item != null) {
			//Edit views
			ViewHolder holder = (ViewHolder)convertView.getTag();
			holder.excursionName.setText(item.getName());
			
			//Set logo, ie locomotion
			Locomotion[] locomotionsItems = item.getLocomotions();
			holder.locomotionsLayout.removeAllViews();
			for(int i=0; i < locomotionsItems.length; i++) {
				if(locomotionsItems[i].getIcon() == null) {
					//Define icon if undefined
					int imageResource = ctx.getResources().getIdentifier("locomotion_" + locomotionsItems[i].getKey(), "drawable", ctx.getPackageName());
					if(imageResource != 0) {
						int width=(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, ctx.getResources().getDisplayMetrics());
						int height=(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, ctx.getResources().getDisplayMetrics());
						Drawable ic =new BitmapDrawable(ctx.getResources(),RWFile.readImageResource(imageResource, width, height, ctx));
						//Drawable ic = ctx.getResources().getDrawable(imageResource);
						locomotionsItems[i].setIcon(ic);
					}
				}
				ImageView img = (ImageView) inflater.inflate(R.layout.locomotion_item, null);
				img.setImageDrawable(locomotionsItems[i].getIcon());
				holder.locomotionsLayout.addView(img);
			}

			//Set length
			Double length = Double.valueOf(item.getLength() / 1000);
			String lengthString = length.toString().substring(0, length.toString().lastIndexOf(".")+2);
			
			//Find the best way to display time
			String time = convertTime(item.getTime());
			//Set text
			holder.excursionLength.setText(lengthString+" km - "+time);
			//Set isFavorite
			ImageView img = new ImageButton(ctx);
			img.setBackgroundResource(color.transparent);
			img.setClickable(true);
			img.setFocusable(false);
			img.setAdjustViewBounds(true);
			img.setMaxHeight((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, ctx.getResources().getDisplayMetrics()));
			img.setMaxWidth((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, ctx.getResources().getDisplayMetrics()));

			holder.excursionFavorite.removeAllViews();
			holder.excursionFavorite.addView(img);
			img.setOnClickListener(listener);
			if(item.isFavorite()){
				img.setImageResource(R.drawable.icon_favorites);
			}else{
				img.setImageResource(R.drawable.icon_favorites_empty);
			}
		}
        
        return convertView;
	}
	
	/**
	 * Converts minutes into days, hours, minutes and seconds
	 * @param minutes The minutes to convert
	 * @return The converted string
	 */
	public static String convertTime(int minutes) {
		String time;
		if(minutes < 60) {
			time = minutes + " min";
		}
		else if(minutes < 24*60) {
			time = ((int) (minutes / 60))+ " h";
			if((minutes % 60)!=0){
				time=time+" "+(minutes % 60) + " min";
			}
		}
		else {
			int days = (int) (minutes / (24*60));
			int hours = (int) ((minutes % (24*60)) / 60);
			int min = minutes % (24*60) % 60;
			time = days + " j";
			if(hours > 0) {
				time += " "+hours+" h";
			}
			if(min > 0) {
				time += " "+min+" min";
			}
		}
		return time;
	}

// INNER CLASS
	static class ViewHolder {
		ImageView excursionLogo;
        TextView excursionName;
        TextView excursionLength;
        LinearLayout locomotionsLayout;
        LinearLayout excursionFavorite;
	}
}
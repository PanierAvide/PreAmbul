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

import net.line2soft.preambul.models.NavigationInstruction;
import net.line2soft.preambul.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * This class represents the instructions informations of an excursion
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class ExcursionInfoInstructionAdapter extends BaseAdapter {
// ATTRIBUTES
	/** The list of instructions **/
	private NavigationInstruction[] tab;
	/** The inflater **/
	private LayoutInflater inflater;
	
// CONSTRUCTOR
	/**
	 * Class constructor
	 * @param context Application context
	 * @param items The list of locations to show
	 */
	public ExcursionInfoInstructionAdapter(Context ctx, NavigationInstruction[] theTab){
		inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		tab=theTab;		
	}
	
	//Accessors
	@Override
	public int getCount() {
		return tab.length;
	}

	@Override
	public Object getItem(int position) {
		Object result = null;
		if(position >=0 && position < getCount()) {
			result = tab[position];
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
			result = tab[position].getId();
		}
		catch(ArrayIndexOutOfBoundsException e) {}
		return result;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = inflater.inflate(R.layout.instruction_item, null);
			ViewHolder holder = new ViewHolder();
			//Set views as objects
			holder.instruction = (TextView) convertView.findViewById(R.id.instruction);
	       	holder.instructionTime = (TextView) convertView.findViewById(R.id.instruction_time);
			convertView.setTag(holder);
		}

		NavigationInstruction item = (NavigationInstruction) getItem(position);
		if(item != null) {
			//Edit views
			ViewHolder holder = (ViewHolder)convertView.getTag();
			holder.instruction.setText(item.getInstruction());
			holder.instructionTime.setText(ExcursionAdapter.convertTime(item.getTime()));
		}
        
        return convertView;
	}
	
	//Inner Class
	static class ViewHolder {
		TextView instruction;
        TextView instructionTime;
	}
}
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

import net.line2soft.preambul.models.NavigationInstruction;
import net.line2soft.preambul.utils.RWFile;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import net.line2soft.preambul.R;

/**
 * This class is used to display {@link NavigationInstruction} in {@link SlippyMapActivity}
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class ImagePagerAdapter extends FragmentStatePagerAdapter {
// ATTRIBUTES
	/** The images to display **/
	private File[] images;
	private Context context;

// CONSTRUCTOR
	/**
	 * Class constructor
	 * @param fragmentManager 
	 * @param instructions The instructions to display
	 */
	public ImagePagerAdapter(FragmentManager fragmentManager, File[] images, Context ctx) {
		super(fragmentManager);
		this.images = images;
		this.context=ctx;
	}

// ACCESSORS
	@Override
    public int getCount() {
        return images.length;
    }

	@Override
	public Fragment getItem(int position) {
        return ImageDetailFragment.newInstance(position);
	}

// OTHER METHODS
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View)object;
        view.destroyDrawingCache();
        ((ViewPager) container).removeView(view);
        view = null;
	}
	@Override
    public Object instantiateItem(View collection, int position) {
        LayoutInflater inflater = (LayoutInflater) collection.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pager_images, null);
        //Set instruction in view
        ((ImageView)view.findViewById(R.id.imageViewPager)).setImageBitmap(RWFile.readImage(images[position], 100, 100, context));
        //Add view
        ((ViewPager) collection).addView(view, 0);
        return view;
    }

	/*
    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView((View) arg2);
    }
    
    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == ((View) arg1);
    }
    
    @Override
    public Parcelable saveState() {
        return null;
    }*/
}
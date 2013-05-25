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

import net.line2soft.preambul.utils.RWFile;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.ImageView;
import net.line2soft.preambul.R;

/**
 * The application's splash screen.
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class SplashscreenActivity extends Activity {
//ATTRIBUTES
	/** The thread to process splash screen events **/
    private Thread mSplashThread;

//OTHER METHODS
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splashscreen);
    }
	
	@Override
	protected void onResume() {
		super.onResume();
	    
	    DisplayMetrics metrics = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(metrics);

		((ImageView)findViewById(R.id.imageView0)).setImageBitmap(RWFile.readImageBackground(R.drawable.back_splash, this));
		final SplashscreenActivity splash = this;   
        
        //The thread to wait for splash screen events
        mSplashThread =  new Thread(){
            @Override
            public void run(){
                try {
                    synchronized(this){
                        //Wait given period of time or exit on touch
                        wait(3000);
                    }
                }
                catch(InterruptedException ex){;}
                finally{
	                splash.finish();
	                splash.overridePendingTransition(R.anim.appear,R.anim.disappear);
	                
	                //Run next activity
	                Intent intent = new Intent();
	                intent.setClass(splash, LocationSelectionActivity.class);
	                splash.startActivity(intent);
	                
	                interrupt();   
                }
            }
        };
        mSplashThread.start(); 
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		((BitmapDrawable)((ImageView)findViewById(R.id.imageView0)).getDrawable()).getBitmap().recycle();
		
	}
	@Override
	public void onStop(){
		super.onStop();
		mSplashThread.interrupt();
	}
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	}
        
    @Override
    public boolean onTouchEvent(MotionEvent evt) {
        if(evt.getAction() == MotionEvent.ACTION_DOWN)
        {
            synchronized(mSplashThread){
                mSplashThread.notifyAll();
            }
        }
        return true;
    }
    
    @Override
    public void onBackPressed(){}
}

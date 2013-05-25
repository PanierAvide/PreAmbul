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

import net.line2soft.preambul.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * This class represent the view of a compass
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class CompassView extends View{
// ATTRIBUTES
	/** The direction of the compass (from -180 to 180) **/
	private float direction = 0;
	/** Black paint **/
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	/** Grey paint **/
	private Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	/** Is the first draw of the view ? **/
	private boolean firstDraw;
	/** The image of the compass **/
	private Bitmap bitmap;
	/** the rotation matrix **/
	private Matrix matrix = new Matrix();

// CONSTRUCTORS
	/**
	 * Class construct
	 * @param context An application context
	 */
	public CompassView(Context context) {
		super(context);
		init();
	}

	/**
	 * Class construct
	 * @param context An application context
	 * @param attrs The set of attributes
	 */
	public CompassView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	/**
	 * Class construct
	 * @param context An application context
	 * @param attrs The set of attributes
	 * @param defStyle the default style
	 */
	public CompassView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

// OTHER METHODS
	/**
	 * Init the compass
	 */
	private void init(){
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);
		paint.setColor(Color.BLACK);
		paint.setTextSize(30);
		paint2.setStyle(Paint.Style.FILL);
		paint2.setColor(getResources().getColor(R.color.Black));
		paint2.setAlpha(80);
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.compass_icon);
		firstDraw = true;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		int cxCompass = getMeasuredWidth()/2;
		int cyCompass = getMeasuredHeight()/2;
		
		bitmap = Bitmap.createScaledBitmap(bitmap, getMeasuredWidth(), getMeasuredHeight(), true);
		canvas.drawCircle(cxCompass, cyCompass, (float)(Math.max(cxCompass, cyCompass)*0.8), paint2);
		canvas.drawCircle(cxCompass, cyCompass, (float)(Math.max(cxCompass, cyCompass)*0.8), paint);
		
		if(!firstDraw){
			matrix.reset();
			matrix.setRotate(360 - direction, cxCompass, cyCompass);
			canvas.drawBitmap(bitmap, matrix, null);
		}
	}
	
	/**
	 * Edits the direction of the compass
	 * @param dir The new direction
	 */
	public void updateDirection(float dir) {
		firstDraw = false;
		direction = dir;
		invalidate();
	}
}
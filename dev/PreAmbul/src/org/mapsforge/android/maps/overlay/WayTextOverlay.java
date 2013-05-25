package org.mapsforge.android.maps.overlay;

import org.mapsforge.android.maps.Projection;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;

/**
 * WayOverlay is an abstract base class to display {@link OverlayWayText OverlayWayTexts}. The class defines some methods to
 * access the backing data structure of deriving subclasses.
 * <p>
 * The overlay may be used to show additional ways such as calculated routes. Closed polygons, for example buildings or
 * areas, are also supported. A way node sequence is considered as a closed polygon if the first and the last way node
 * are equal.
 * 
 * @param <Way>
 *            the type of ways handled by this overlay.
 */
public abstract class WayTextOverlay<Way extends OverlayWayText> extends Overlay {
	private static final String THREAD_NAME = "WayOverlay";

	private final Paint defaultPaintFill;
	private final Paint defaultPaintOutline;
	private final Path path;

	/**
	 * @param defaultPaintFill
	 *            the default paint which will be used to fill the ways (may be null).
	 * @param defaultPaintOutline
	 *            the default paint which will be used to draw the way outlines (may be null).
	 */
	public WayTextOverlay(Paint defaultPaintFill, Paint defaultPaintOutline) {
		super();
		this.defaultPaintFill = defaultPaintFill;
		this.defaultPaintOutline = defaultPaintOutline;
		this.path = new Path();
		this.path.setFillType(Path.FillType.EVEN_ODD);
	}

	/**
	 * @return the numbers of ways in this overlay.
	 */
	public abstract int size();

	private void assemblePath(Point drawPosition, Way overlayWay) {
		this.path.reset();
		for (int i = 0; i < overlayWay.cachedWayPositions.length; ++i) {
			this.path.moveTo(overlayWay.cachedWayPositions[i][0].x - drawPosition.x,
					overlayWay.cachedWayPositions[i][0].y - drawPosition.y);
			for (int j = 1; j < overlayWay.cachedWayPositions[i].length; ++j) {
				this.path.lineTo(overlayWay.cachedWayPositions[i][j].x - drawPosition.x,
						overlayWay.cachedWayPositions[i][j].y - drawPosition.y);
			}
		}
	}

	private void drawPathOnCanvas(Canvas canvas, Way overlayWay) {
		if (overlayWay.hasPaint) {
			// use the paints from the current way
			if (overlayWay.paintOutline != null) {
				canvas.drawPath(this.path, overlayWay.paintOutline);
			}
			if (overlayWay.paintFill != null) {
				canvas.drawPath(this.path, overlayWay.paintFill);
			}
		} else {
			// use the default paint objects
			if (this.defaultPaintOutline != null) {
				canvas.drawPath(this.path, this.defaultPaintOutline);
			}
			if (this.defaultPaintFill != null) {
				canvas.drawPath(this.path, this.defaultPaintFill);
			}
		}
		//Draw text along path
		if(overlayWay.text != null && overlayWay.text.trim().length() > 0) {
			int length = (int) (new PathMeasure(this.path, false).getLength());
			Paint backTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			backTextPaint.setTextSize(18);
			backTextPaint.setColor(Color.WHITE);
			backTextPaint.setAlpha(255);
			backTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			backTextPaint.setStrokeWidth(6);
			backTextPaint.setStrokeJoin(Paint.Join.ROUND);
			backTextPaint.setStrokeCap(Paint.Cap.ROUND);
			
			Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			textPaint.setTextSize(18);
			textPaint.setColor(Color.BLACK);
			textPaint.setAlpha(255);
			textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			textPaint.setStrokeWidth(1);
			textPaint.setStrokeJoin(Paint.Join.ROUND);
			textPaint.setStrokeCap(Paint.Cap.ROUND);
			
			for(int offset = 20; offset < length; offset = offset+1000) {
				canvas.drawTextOnPath(overlayWay.text, this.path, offset, 0, backTextPaint);
				canvas.drawTextOnPath(overlayWay.text, this.path, offset, 0, textPaint);
			}
		}
	}

	/**
	 * Creates a way in this overlay.
	 * 
	 * @param index
	 *            the index of the way.
	 * @return the way.
	 */
	protected abstract Way createWay(int index);

	@Override
	protected void drawOverlayBitmap(Canvas canvas, Point drawPosition, Projection projection, byte drawZoomLevel) {
		int numberOfWays = size();
		for (int wayIndex = 0; wayIndex < numberOfWays; ++wayIndex) {
			if (isInterrupted() || sizeHasChanged()) {
				// stop working
				return;
			}

			// get the current way
			Way overlayWay = createWay(wayIndex);
			if (overlayWay == null) {
				continue;
			}

			synchronized (overlayWay) {
				// make sure that the current way has way nodes
				if (overlayWay.wayNodes == null || overlayWay.wayNodes.length == 0) {
					continue;
				}

				// make sure that the cached way node positions are valid
				if (drawZoomLevel != overlayWay.cachedZoomLevel) {
					for (int i = 0; i < overlayWay.cachedWayPositions.length; ++i) {
						for (int j = 0; j < overlayWay.cachedWayPositions[i].length; ++j) {
							overlayWay.cachedWayPositions[i][j] = projection.toPoint(overlayWay.wayNodes[i][j],
									overlayWay.cachedWayPositions[i][j], drawZoomLevel);
						}
					}
					overlayWay.cachedZoomLevel = drawZoomLevel;
				}

				assemblePath(drawPosition, overlayWay);
				drawPathOnCanvas(canvas, overlayWay);
			}
		}
	}

	@Override
	protected String getThreadName() {
		return THREAD_NAME;
	}

	/**
	 * This method should be called after ways have been added to the overlay.
	 */
	protected final void populate() {
		super.requestRedraw();
	}
}
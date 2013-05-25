package org.mapsforge.android.maps;

import org.mapsforge.core.GeoPoint;
import org.mapsforge.core.MapPosition;

import android.graphics.Point;
import android.util.Log;

/**
 * MapViewLimits.java
 * @author JMPergar
 * Created on August 28, 2012
 */

/**
 * A MapViewLimits stores the area boundaries that we can visualize.
 * Coding for version 0.3.0
 */
public class MapViewLimits {
    /**
     * Maximum top limit.
     */
    private static final double NO_LIMITS_TOP = 90;
    /**
     * Maximum bottom limit.
     */
    private static final double NO_LIMITS_BOTTOM = -90;
    /**
     * Maximum left limit.
     */
    private static final double NO_LIMITS_LEFT = -180;
    /**
     * Maximum right limit.
     */
    private static final double NO_LIMITS_RIGHT = 180;

    /**
     * Latitude of the top limit.
     */
    private double mTopLimit;
    /**
     * Latitude of the bottom limit.
     */
    private double mBottomLimit;
    /**
     * Longitude of the left limit.
     */
    private double mLeftLimit;
    /**
     * Longitude of the right limit.
     */
    private double mRightLimit;

    /**
     * MapView on which apply the limits.
     */
    private final MapView mMapView;

    /**
     * Default Constructor. No apply restrictions of area.
     *
     * @param pMapView
     *            MapView on which apply the limits.
     */
    public MapViewLimits(final MapView pMapView) {
        super();
        this.mTopLimit = NO_LIMITS_TOP;
        this.mBottomLimit = NO_LIMITS_BOTTOM;
        this.mLeftLimit = NO_LIMITS_LEFT;
        this.mRightLimit = NO_LIMITS_RIGHT;

        this.mMapView = pMapView;
    }

    /**
     * Sets the limits of visible map.
     *
     * @param pTopLimit
     *            The new latitude of the top limit.
     * @param pBottomLimit
     *            The new latitude of the bottom limit.
     * @param pLeftLimit
     *            The new longitude of the left limit.
     * @param pRightLimit
     *            The new longitude of the right limit.
     */
    public final void setLimits(
            final double pTopLimit,
            final double pBottomLimit,
            final double pLeftLimit,
            final double pRightLimit) {

        // TODO Check that the values ??are logical.
        this.mTopLimit = pTopLimit;
        this.mBottomLimit = pBottomLimit;
        this.mLeftLimit = pLeftLimit;
        this.mRightLimit = pRightLimit;
    }

    /**
     * @return the latitude of the top limit.
     */
    public final double getTopLimit() {
        return mTopLimit;
    }

    /**
     * @return the latitude of the bottom limit.
     */
    public final double getBottomLimit() {
        return mBottomLimit;
    }

    /**
     * @return the longitude of the left limit.
     */
    public final double getLeftLimit() {
        return mLeftLimit;
    }

    /**
     * @return the longitude of the right limit.
     */
    public final double getRightLimit() {
        return mRightLimit;
    }

    /**
     * Removes the restrictions of area.
     */
    public final void deleteLimits() {
        this.mTopLimit = NO_LIMITS_TOP;
        this.mBottomLimit = NO_LIMITS_BOTTOM;
        this.mLeftLimit = NO_LIMITS_LEFT;
        this.mRightLimit = NO_LIMITS_RIGHT;
    }

    /**
     * @param pMapPosition Object that include location and level zoom.
     * @return If the display area is valid for the position and current zoom level.
     */
    public final boolean isPositionValid(final MapPosition pMapPosition) {
        return isZoomValid(pMapPosition);
    }
   
    /**
     * @param pMapPosition Object that include location and level zoom.
     * @return If the display area is valid for the current position and zoom level.
     */
    public final boolean isZoomValid(final MapPosition pMapPosition) {
    	boolean result = false;
    	if(pMapPosition != null) {
    		if(mMapView.getWidth() > 0 && mMapView.getHeight() > 0) {
	    		GeoPoint position = (pMapPosition.geoPoint != null) ? pMapPosition.geoPoint : mMapView.getMapPosition().getMapCenter();
	    		MapViewProjection projection = new MapViewProjection(mMapView);
	    		Point p = projection.toPixelsWithZoom(position, null, pMapPosition.zoomLevel);
	    		
	    		//Get the north west corner
	    		int nwPixelX = (int) (p.x - mMapView.getWidth() / 2);
	    		int nwPixelY = (int) (p.y - mMapView.getHeight() / 2);
	    		GeoPoint nwPoint = projection.fromPixelsWithZoom(nwPixelX, nwPixelY, pMapPosition.zoomLevel);
	    		
	    		//Get the south east corner
	    		GeoPoint sePoint = projection.fromPixelsWithZoom(nwPixelX+mMapView.getWidth(), nwPixelY+mMapView.getHeight(), pMapPosition.zoomLevel);
	    		
	    		//Is the position valid ?
	    		if(nwPoint.getLatitude() <= mTopLimit) {
	    			if(nwPoint.getLongitude() >= mLeftLimit) {
	    				if(sePoint.getLatitude() >= mBottomLimit) {
	    					if(sePoint.getLongitude() <= mRightLimit) {
	    						result = true;
	    					} else { Log.d("Mapslimit", "Out of limit: right"); }
	    				} else { Log.d("Mapslimit", "Out of limit: bottom"); }
	    			} else { Log.d("Mapslimit", "Out of limit: left"); }
	    		} else { Log.d("Mapslimit", "Out of limit: top"); }
    		} else { result = true; }
    	}
    	return result;
//    	boolean result = true;
//        if (pMapPosition != null) {
//            GeoPoint geoPoint = (pMapPosition.geoPoint==null) ? mMapView.getMapPosition().getMapCenter() : pMapPosition.geoPoint;
//            MapViewProjection mapProyection = new MapViewProjection(mMapView);
//            Point p = mapProyection.toPixelsWithZoom(geoPoint, null, pMapPosition.zoomLevel);
//           
//            // FIXME Improve: Fix for a specific case.
//            if (p != null) {
//	            double pixelX = p.x;
//	            double pixelY = p.y;
//	
//	            /*Log.d("MapsLimits", "Position (GEO): Lon " + geoPoint.getLongitude()
//	                    + ", Lat " + geoPoint.getLatitude());
//	            Log.d("MapsLimits",
//	                    "Position (PIX): Lon " + pixelX + ", Lat " + pixelY);*/
//	
//	            // calculate the pixel coordinates of the top left corner
//	            pixelX -= this.mMapView.getWidth() >> 1;
//	            pixelY -= this.mMapView.getHeight() >> 1;
//	
//	            /*Log.d("MapsLimits",
//	                    "Corner (PIX): Lon " + pixelX + ", Lat " + pixelY);*/
//	
//	            GeoPoint cornerNW = mapProyection.fromPixelsWithZoom(
//	                    (int) pixelX,
//	                    (int) pixelY,
//	                    pMapPosition.zoomLevel);
//	            GeoPoint cornerSE = mapProyection.fromPixelsWithZoom(
//	                    (int) pixelX + this.mMapView.getWidth(),
//	                    (int) pixelY + this.mMapView.getHeight(),
//	                    pMapPosition.zoomLevel);
//	
//	            /*Log.d("MapsLimits", "Corner (GEO): Lon "
//	            + cornerNW.getLongitude() + ", Lat " + cornerNW.getLatitude());*/
//	
//	            /** TODO Analyze the case where the 180th meridian
//	             * crosses the visible area (rare case)
//	             */
//	
//	            // check if the latitude is correct
//	            if (cornerNW.getLatitude() > mTopLimit
//	                    || cornerSE.getLatitude() < mBottomLimit) {
//	                Log.d("MapsLimits", "Limite de latitud superado... "
//	                    + cornerNW.getLatitude() + ">" + mTopLimit + " "
//	                        + cornerSE.getLatitude() + "<" + mBottomLimit);
//	                result = false;
//	            }
//	            // check if the longitude is correct
//	            else if (cornerNW.getLongitude() < mLeftLimit
//	                    || cornerSE.getLongitude() > mRightLimit) {
//	                Log.d("MapsLimits", "Limite de longitud superado... "
//	                    + cornerNW.getLongitude() + ">" + mLeftLimit + " "
//	                        + cornerSE.getLongitude() + "<" + mRightLimit);
//	                result = false;
//	            } else { result = true; }
//            } else { result = true; }
//        } else { result = false; }
//        return result;
    }

}

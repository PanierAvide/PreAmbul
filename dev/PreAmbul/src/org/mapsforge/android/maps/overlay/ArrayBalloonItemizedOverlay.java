package org.mapsforge.android.maps.overlay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.mapsforge.android.maps.MapView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.Gravity;
import net.line2soft.preambul.R;
import net.line2soft.preambul.models.NamedPoint;
import net.line2soft.preambul.models.PointOfInterest;
import net.line2soft.preambul.utils.RWFile;
import net.line2soft.preambul.views.FavoriteListActivity;
import net.line2soft.preambul.views.PoiInfoActivity;
import net.line2soft.preambul.views.SlippyMapActivity;

/**
 * ArrayItemizedOverlay is a thread-safe implementation of the {@link ItemizedOverlay} class using an {@link ArrayList}
 * as internal data structure. A default marker for all {@link OverlayItem OverlayItems} without an individual marker
 * can be defined via the constructor.
 */
public class ArrayBalloonItemizedOverlay extends BalloonItemizedOverlay<OverlayItem> {
        private static final int INITIAL_CAPACITY = 8;
        private static final String THREAD_NAME = "ArrayBalloonItemizedOverlay";

        private final List<OverlayItem> overlayItems;
        private HashMap<OverlayItem,NamedPoint> pointsItems;
        private HashMap<Integer,OverlayItem> hiddenOverlays;
        private HashMap<OverlayItem,Drawable> iconStared;
        private Activity act;
        private BitmapDrawable star;

        /**
         * @param defaultMarker
         *            the default marker (may be null). This marker is aligned to the center of its bottom line to allow for
         *            a conical symbol such as a pin or a needle.
         */
        public ArrayBalloonItemizedOverlay(Drawable defaultMarker, MapView mv, Activity act) {
                this(defaultMarker, mv, true, act);
        }

        /**
         * @param defaultMarker
         *            the default marker (may be null).
         * @param alignMarker
         *            whether the default marker should be aligned or not. If true, the marker is aligned to the center of
         *            its bottom line to allow for a conical symbol such as a pin or a needle.
         */
        public ArrayBalloonItemizedOverlay(Drawable defaultMarker, MapView mv, boolean alignMarker, Activity act) {
                super(defaultMarker != null && alignMarker ? BalloonItemizedOverlay.boundCenterBottom(defaultMarker) : defaultMarker, mv);
                this.overlayItems = new ArrayList<OverlayItem>(INITIAL_CAPACITY);
                this.act = act;
                pointsItems = new HashMap<OverlayItem,NamedPoint>();
                hiddenOverlays = new HashMap<Integer,OverlayItem>();
                star = (BitmapDrawable) act.getResources().getDrawable(R.drawable.favorite_poi);
    			star.setGravity(Gravity.RIGHT);
    			iconStared = new HashMap<OverlayItem,Drawable>();
        }

        /**
         * Adds the given item to the overlay.
         *
         * @param overlayItem
         *            the item that should be added to the overlay.
         */
        public void addItem(OverlayItem overlayItem, NamedPoint point) {
                synchronized (this.overlayItems) {
                	if(point==null) {
                		overlayItem.setMarker(BalloonItemizedOverlay.boundCenterBottom(overlayItem.getMarker()));
                	} else {
                		Drawable icon = overlayItem.getMarker();
                		if(point.getClass().equals(PointOfInterest.class) && point.isFavorite() && !iconStared.containsKey(overlayItem)) {
                			Drawable[] arrayDrawable = { icon, star };
                			LayerDrawable layerDrawable = new LayerDrawable(arrayDrawable);
                			layerDrawable.setLayerInset(1, 14, 0, 0, 20);
                			layerDrawable.setLayerInset(0, 0, 20, 14, 0);
                			icon = layerDrawable;
                			iconStared.put(overlayItem, layerDrawable);
                		}
                		else if(point.getClass().equals(PointOfInterest.class) && point.isFavorite()) {
                			icon = iconStared.get(overlayItem);
                		}
                		overlayItem.setMarker(BalloonItemizedOverlay.boundLeftCenter(icon));
                	}
                    this.overlayItems.add(overlayItem);
                    this.pointsItems.put(overlayItem, point);
                }
                populate();
        }

        /**
         * Adds all items of the given collection to the overlay.
         *
         * @param c
         *            collection whose items should be added to the overlay.
         */
        public void addItems(Collection<? extends OverlayItem> c) {
                synchronized (this.overlayItems) {
                        this.overlayItems.addAll(c);
                        for(OverlayItem item : c) {
                        	this.pointsItems.put(item, null);
                        }
                }
                populate();
        }

        /**
         * Removes all items from the overlay.
         */
        public void clear() {
                synchronized (this.overlayItems) {
                	hideAllBalloons();
                	this.overlayItems.clear();
                	this.pointsItems.clear();
                }
                populate();
        }

        @Override
        public String getThreadName() {
                return THREAD_NAME;
        }

        /**
         * Removes the given item from the overlay.
         *
         * @param overlayItem
         *            the item that should be removed from the overlay.
         */
        public void removeItem(OverlayItem overlayItem) {
                synchronized (this.overlayItems) {
                        this.overlayItems.remove(overlayItem);
                        this.pointsItems.remove(overlayItem);
                        if(iconStared.containsKey(overlayItem)) { iconStared.remove(overlayItem); }
                }
                populate();
        }

        @Override
        public int size() {
                synchronized (this.overlayItems) {
                        return this.overlayItems.size();
                }
        }

        @Override
        protected OverlayItem createItem(int index) {
                synchronized (this.overlayItems) {
                        if (index >= this.overlayItems.size()) {
                                return null;
                        }
                        return this.overlayItems.get(index);
                }
        }

		@Override
		protected boolean onBalloonTap(int index, OverlayItem item) {
			//Open activity for advanced informations
			NamedPoint current = pointsItems.get(item);
			if(current != null) {
				Intent it = new Intent(act, PoiInfoActivity.class);
				if(pointsItems.get(item).getClass().equals(PointOfInterest.class)) {
					PointOfInterest currentPoi = (PointOfInterest) current;
					it.putExtra(SlippyMapActivity.POI_KEY, currentPoi.getId());
					it.putExtra(SlippyMapActivity.POI_TYPE, currentPoi.getType().getId());
					it.putExtra(SlippyMapActivity.POI_CATEG, currentPoi.getType().getCategory().getId());
				} else {
					it.putExtra(FavoriteListActivity.NAMEDPOINTFAVORITE_KEY, RWFile.formatCoordinates(current.getPoint().getLatitude(), current.getPoint().getLongitude()));
				}
				act.startActivity(it);
				onBalloonClosed(index);
			}
			return true;
		}

		@Override
		protected void onBalloonOpen(int index) {
			synchronized(overlayItems) {
				hiddenOverlays.put(index, overlayItems.get(index));
				overlayItems.remove(index);
			}
			populate();
		}

		@Override
		public void onBalloonClosed(int index) {
			synchronized(overlayItems) {
				if(hiddenOverlays.containsKey(index)) {
					addItem(hiddenOverlays.get(index), pointsItems.get(hiddenOverlays.get(index)));
					hiddenOverlays.remove(index);
				}
			}
			populate();
		}
		
		@Override
		public boolean onTap(int index) {
			boolean result = false;
			if(pointsItems.containsKey(overlayItems.get(index)) && pointsItems.get(overlayItems.get(index)) != null) {
				NamedPoint point = pointsItems.get(overlayItems.get(index));
				if(point != null && point.getClass().equals(PointOfInterest.class) && ((PointOfInterest) point).getSound()!=null) {
                	isPlayable = true;
                } else {
                	isPlayable = false;
                }
				result = super.onTap(index);
			}
			populate();
			return result;
		}
}
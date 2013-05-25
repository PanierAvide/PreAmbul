package org.mapsforge.android.maps.overlay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.graphics.Paint;

/**
 * ArrayWayOverlay is a thread-safe implementation of the {@link WayOverlay} class using an {@link ArrayList} as
 * internal data structure. Default paints for all {@link OverlayWayText OverlayWayTexts} without individual paints can be
 * defined via the constructor.
 */
public class ArrayWayTextOverlay extends WayTextOverlay<OverlayWayText> {
	private static final int INITIAL_CAPACITY = 8;
	private static final String THREAD_NAME = "ArrayWayOverlay";

	private final List<OverlayWayText> overlayWays;

	/**
	 * @param defaultPaintFill
	 *            the default paint which will be used to fill the ways (may be null).
	 * @param defaultPaintOutline
	 *            the default paint which will be used to draw the way outlines (may be null).
	 */
	public ArrayWayTextOverlay(Paint defaultPaintFill, Paint defaultPaintOutline) {
		super(defaultPaintFill, defaultPaintOutline);
		this.overlayWays = new ArrayList<OverlayWayText>(INITIAL_CAPACITY);
	}

	/**
	 * Adds the given way to the overlay.
	 * 
	 * @param overlayWay
	 *            the way that should be added to the overlay.
	 */
	public void addWay(OverlayWayText overlayWay) {
		synchronized (this.overlayWays) {
			this.overlayWays.add(overlayWay);
		}
		populate();
	}

	/**
	 * Adds all ways of the given collection to the overlay.
	 * 
	 * @param c
	 *            collection whose ways should be added to the overlay.
	 */
	public void addWays(Collection<? extends OverlayWayText> c) {
		synchronized (this.overlayWays) {
			this.overlayWays.addAll(c);
		}
		populate();
	}

	/**
	 * Removes all ways from the overlay.
	 */
	public void clear() {
		synchronized (this.overlayWays) {
			this.overlayWays.clear();
		}
		populate();
	}

	@Override
	public String getThreadName() {
		return THREAD_NAME;
	}

	/**
	 * Removes the given way from the overlay.
	 * 
	 * @param overlayWay
	 *            the way that should be removed from the overlay.
	 */
	public void removeWay(OverlayWayText overlayWay) {
		synchronized (this.overlayWays) {
			this.overlayWays.remove(overlayWay);
		}
		populate();
	}

	@Override
	public int size() {
		synchronized (this.overlayWays) {
			return this.overlayWays.size();
		}
	}

	@Override
	protected OverlayWayText createWay(int index) {
		synchronized (this.overlayWays) {
			if (index >= this.overlayWays.size()) {
				return null;
			}
			return this.overlayWays.get(index);
		}
	}
}

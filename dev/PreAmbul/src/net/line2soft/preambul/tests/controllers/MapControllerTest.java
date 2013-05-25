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

package net.line2soft.preambul.tests.controllers;

import net.line2soft.preambul.controllers.MapController;
import net.line2soft.preambul.models.Location;
import android.content.Context;
import android.test.AndroidTestCase;

/**
 * Test class for {@link MapController}
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class MapControllerTest extends AndroidTestCase {
// SETUP
	protected void setUp() throws Exception {
		super.setUp();
	}

// END
	protected void tearDown() throws Exception {
		super.tearDown();
		MapController.resetInstance();
	}

// TESTS
	/**
	 * getInstance(Context) test
	 */
	public void testGetInstanceContext() {
		MapController mc1 = MapController.getInstance(getContext());
		try {
			wait(100);
		} catch (Exception e) {;}
		MapController mc2 = MapController.getInstance(getContext());
		assertEquals(mc1, mc2);
	}

	/**
	 * getInstance() test
	 */
	public void testGetInstance() {
		MapController mc1 = MapController.getInstance();
		try {
			wait(100);
		} catch (Exception e) {;}
		MapController mc2 = MapController.getInstance();
		assertEquals(mc1, mc2);
	}

	/**
	 * getContext() test, when context has been initiated
	 */
	public void testGetContextInit() {
		Context ctx = getContext();
		MapController mc1 = MapController.getInstance(ctx);
		assertEquals(ctx, mc1.getContext());
	}
	
	/**
	 * getContext() test, when context hasn't been initiated
	 */
	public void testGetContextNotInit() {
		MapController mc1 = MapController.getInstance();
		assertNull(mc1.getContext());
	}

	/**
	 * getCurrentLocation() test, when initiated
	 */
	public void testGetCurrentLocationInit() {
		MapController mc1 = MapController.getInstance();
		Location l1 = new Location(0, "Vannes", "56000", "FR", null, null, 1, null,null);
		mc1.setCurrentLocation(l1);
		assertEquals(l1, mc1.getCurrentLocation());
	}
	
	/**
	 * getCurrentLocation() test, when not initiated
	 */
	public void testGetCurrentLocationNotInit() {
		MapController mc1 = MapController.getInstance();
		assertNull(mc1.getCurrentLocation());
	}

	/**
	 * getPathToDisplay() test, when not initiated
	 */
	public void testGetPathToDisplayNotInit() {
		MapController mc1 = MapController.getInstance();
		assertEquals(0, mc1.getPathToDisplay());
	}
	
	/**
	 * getPathToDisplay() test, when initiated
	 */
	public void testGetPathToDisplayInit() {
		MapController mc1 = MapController.getInstance();
		mc1.setPathToDisplay(42);
		assertEquals(42, mc1.getPathToDisplay());
	}

	/**
	 * setCurrentLocation() test
	 */
	public void testSetCurrentLocation() {
		MapController mc1 = MapController.getInstance();
		Location l1 = new Location(0, "Vannes", "56000", "FR", null, null, 1, null,null);
		mc1.setCurrentLocation(l1);
		assertEquals(l1, mc1.getCurrentLocation());
	}

	/**
	 * setPathToDisplay() test
	 */
	public void testSetPathToDisplay() {
		MapController mc1 = MapController.getInstance();
		mc1.setPathToDisplay(42);
		assertEquals(42, mc1.getPathToDisplay());
	}
}
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

import net.line2soft.preambul.controllers.LocationsController;
import net.line2soft.preambul.views.LocationInstallActivity;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Test class for {@link LocationsController}
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class LocationsControllerTest extends ActivityInstrumentationTestCase2<LocationInstallActivity> {
// ATTRIBUTES
	private LocationInstallActivity lia1;
	private LocationsController lc1;
	
// CONSTRUCTOR
	/**
	 * Test of the constructor
	 */
	public LocationsControllerTest() {
		super("net.line2soft.preambul.views", LocationInstallActivity.class);
	}

// OTHER METHODS
	protected void setUp() throws Exception {
		fail("Not yet implemented");
		super.setUp();
//		lia1 = getActivity();
//		lc1 = LocationsController.getInstance(lia1);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

// getCurrentActivity() test
	/**
	 * Test of the getCurrentActivity() method
	 */
	public void testGetCurrentActivity() {
		//assertSame(lia1, lc1.getCurrentActivity());
		fail("Not yet implemented");
	}
	
// getInstance() test
	/**
	 * Test of the getInstanceSame() method
	 */
	public void testGetInstanceSame() {
		assertSame(lc1, LocationsController.getInstance(lia1));
	}

// getAllLocations() test
	/**
	 * Test of the getAllLocationVoid() method
	 */
	public void testGetAllLocationsVoid() {
		assertEquals(lc1.getAllLocations(), null);
	}

// TODO Other tests
	public void testGetInstalledLocations() {
		fail("Not yet implemented");
	}

	public void testSetCurrentActivity() {
		fail("Not yet implemented");
	}

	public void testInstallLocationsPackages() {
		fail("Not yet implemented");
	}

	public void testUninstallLocationsPackages() {
		fail("Not yet implemented");
	}

	public void testUpdateLocationsPackages() {
		fail("Not yet implemented");
	}

	public void testCheckServerListUpdates() {
		fail("Not yet implemented");
	}

	public void testSearchLocationAndUpdateView() {
		fail("Not yet implemented");
	}

	public void testLaunchLocation() {
		fail("Not yet implemented");
	}
}
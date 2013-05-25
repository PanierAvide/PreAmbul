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

package net.line2soft.preambul.tests.models;

import net.line2soft.preambul.models.NavigationInstruction;

import org.mapsforge.android.maps.overlay.OverlayWay;
import org.mapsforge.core.GeoPoint;

import junit.framework.TestCase;

/**
 * Test class for {@link NavigationInstruction}
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class NavigationInstructionTest extends TestCase {
// ATTRIBUTES
	/** NavigationInstruction's attribute */
	private NavigationInstruction navIns1;
	private NavigationInstruction navIns2;
	private NavigationInstruction navIns3;
	private OverlayWay seg;
		
// OTHER METHODS
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		try {
			GeoPoint[][] pts = new GeoPoint[1][1];
			pts[0][0] = new GeoPoint(20.0, 22.0);
			seg = new OverlayWay(pts);
			navIns1 = new NavigationInstruction(1, "Tourner à droite", 90, seg);
			navIns2 = new NavigationInstruction(2, "Tourner à gauche", 60, null);
			navIns3 = new NavigationInstruction(3, "Continuer tout droit", 100, null);
		} catch(Exception e) {;}
	}
	
// CONSTRUCTOR TEST
	public void testNavigationInstruction() {
		try {
			GeoPoint[][] pts = new GeoPoint[1][1];
			pts[0][0] = new GeoPoint(20.0, 22.0);
			seg = new OverlayWay(pts);
			navIns1 = new NavigationInstruction(1, "Tourner à droite", 90, seg);
			navIns2 = new NavigationInstruction(2, "Tourner à gauche", 60, null);
			navIns3 = new NavigationInstruction(3, "Continuer tout droit", 100, null);
		} catch(Exception e) { fail("Constructor failed"); }
	}
	
// ACCESSORS TEST
	/**
	 * Test of the getId() method
	 */
	public void testGetId() {
		assertEquals(1, navIns1.getId());
	}
	
	/**
	 * Test of the getInstruction() method
	 */
	public void testGetInstruction() {
		assertEquals("Tourner à gauche", navIns2.getInstruction());
	}
	
	/**
	 * Test of the getTime() method
	 */
	public void testGetTime() {
		assertEquals(100, navIns3.getTime());
	}
	
	/**
	 * Test of the getSegment() method
	 */
	public void testGetSegment() {
		assertEquals(seg, navIns1.getSegment());
	}
}
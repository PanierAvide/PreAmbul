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

import java.net.MalformedURLException;
import java.util.ArrayList;

import net.line2soft.preambul.models.PointOfInterest;
import net.line2soft.preambul.models.PointOfInterestCategory;
import net.line2soft.preambul.models.PointOfInterestType;

import org.mapsforge.core.GeoPoint;

import android.graphics.drawable.Drawable;
import junit.framework.TestCase;

/**
 * Test class for {@link PointOfInterestType}
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class PointOfInterestTypeTest extends TestCase {
// ATTRIBUTES
	/** PointOfInterestType's attribute */
	private PointOfInterestType poiType1;
	
	/** PointOfInterestType's ID */
	private String id1;
	
	/** PointOfInterestType's name */
	private String name1;

	/** PointOfInterestType's icon */
	private Drawable icon1;
	
	/** POI */
	private PointOfInterest poi1;
	
	/** The list of POIs **/
	private ArrayList<PointOfInterest> pois1;
	
	/** A category of POI */
	private PointOfInterestCategory category1;
	
// OTHER METHODS
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		try {
			poiType1 = new PointOfInterestType(id1, name1, icon1, category1);
		} catch(Exception e) {;}
	}

// CONSTRUCTOR TEST
	@SuppressWarnings("unused")
	public void testPointOfInterestType() {
		try {
			PointOfInterestType tmp1 = new PointOfInterestType(id1, name1, icon1, category1);
		} catch(Exception e) { fail("Constructor failed"); }
	}
	
// ACCESSORS TESTS
	/**
	 * Test of the getId() method
	 */
	public void testGetId() {
		assertEquals(id1, poiType1.getId());
	}
	
	/**
	 * Test of the getName() method
	 */
	public void testGetName() {
		assertEquals(name1, poiType1.getName());
	}
	
	/**
	 * Test of the getIcon() method
	 */
	public void testGetIcon() {
		assertEquals(icon1, poiType1.getIcon());
	}
	
	/**
	 * Test of the getPoint() method
	 */
	public void testGetPois() {
		assertEquals(pois1, poiType1.getPois());
	}
	
	/**
	 * Test of the getPoint() method
	 */
	public void testGetCategory() {
		assertEquals(category1, poiType1.getCategory());
	}
	
// OTHER METHOD TEST
	/**
	 * Test of the addPoint() method
	 * @throws MalformedURLException 
	 */
	public void testAddPoint() {
		GeoPoint point1 = new GeoPoint(10, 10);
		poi1 = new PointOfInterest(point1, "name1", "description1", "comment1", null, poiType1, true);
		pois1.add(poi1);
		
		//assertEquals(poi1, pois1.get(poi1));
	}	
}
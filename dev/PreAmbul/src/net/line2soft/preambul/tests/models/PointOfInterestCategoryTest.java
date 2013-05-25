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

import java.util.HashMap;

import net.line2soft.preambul.models.PointOfInterestCategory;
import net.line2soft.preambul.models.PointOfInterestType;
import android.graphics.drawable.Drawable;
import junit.framework.TestCase;

/**
 * Test class for {@link PointOfInterestCategory}
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class PointOfInterestCategoryTest extends TestCase {
// ATTRIBUTES
	/** PointOfInterestCategory's attribute */
	private PointOfInterestCategory poiCat1;
	
	/** PointOfInterestCategory's ID */
	private String id1;
	
	/** PointOfInterestCategory's name */
	private String name1;

	/** PointOfInterestCategory's icon */
	private Drawable icon1;
	
	
// OTHER METHODS
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		try {
			poiCat1 = new PointOfInterestCategory(id1, name1, icon1);
		} catch(Exception e) {;}
	}

// CONSTRUCTOR TEST
	@SuppressWarnings("unused")
	public void testPointOfInterestCategory() {
		try {
			PointOfInterestCategory tmp1 = new PointOfInterestCategory(id1, name1, icon1);
		} catch(Exception e) { fail("Constructor failed"); }
	}
	
// ACCESSORS TESTS
	/**
	 * Test of the getId() method
	 */
	public void testGetId() {
		assertEquals(id1, poiCat1.getId());
	}
		
		/**
	 * Test of the getName() method
	 */
	public void testGetName() {
		assertEquals(name1, poiCat1.getName());
	}
		
	/**
	 * Test of the getIcon() method
	 */
	public void testGetIcon() {
		assertEquals(icon1, poiCat1.getIcon());
	}
	
	/**
	 * Test of the getType() method
	 */
	public void testGetType() {
		PointOfInterestType poiType1 = new PointOfInterestType("restaurant", "Restaurant", null, poiCat1);
		poiCat1.addType(poiType1);
		assertEquals(poiType1, poiCat1.getType("restaurant"));
	}
	
	/**
	 * Test of the getType() method
	 */
	public void testGetTypes() {
		PointOfInterestType poiType2 = new PointOfInterestType("restaurant", "Restaurant", null, poiCat1);
		PointOfInterestType poiType3 = new PointOfInterestType("bar", "Bar", null, poiCat1);
		PointOfInterestType poiType4 = new PointOfInterestType("cafe", "Café", null, poiCat1);
		poiCat1.addType(poiType2);
		poiCat1.addType(poiType3);
		poiCat1.addType(poiType4);
		PointOfInterestType POIArray[] = {poiType2, poiType3, poiType4};
		for (int i=0; i< POIArray.length; i++) {
			assertEquals(POIArray[i], poiCat1.getTypes()[i]);
		}
	}
	
//MODIFIERS TESTS
	/**
	 * Test of the addType() method
	 */
	public void testAddType() {
		PointOfInterestType poiType5 = new PointOfInterestType("restaurant", "Restaurant", null, poiCat1);
		HashMap<String, PointOfInterestType> types1 = new HashMap<String, PointOfInterestType>();
		types1.put("id1", poiType5);
		
		assertEquals(poiType5, types1.get(poiType5));
	}
}
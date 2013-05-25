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

import java.net.URL;


import net.line2soft.preambul.models.PointOfInterest;
import net.line2soft.preambul.models.PointOfInterestType;

import org.mapsforge.core.GeoPoint;
import junit.framework.TestCase;

/**
 * Test class for {@link PointOfInterest}
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class PointOfInterestTest extends TestCase {
// ATTRIBUTES
	/** PointOfInterest's attributes */
	private PointOfInterest poi1;
	private PointOfInterest poi2;

	/** PointOfInterest's GeoPoints */
	private GeoPoint point1;
	private GeoPoint point2;
	
	/** PointOfInterest's names */
	private String name1;
	private String name2;
	
	/** PointOfInterest's descriptions */
	private String desc1;
	private String desc2;
	
	/** PointOfInterest's comments */
	private String cmt1;
	private String cmt2;
	
	/** PointOfInterest's URLs */
	private URL link1;
	private URL link2;
	
	/** PointOfInterest's types */
	private PointOfInterestType type1;
	private PointOfInterestType type2;
	
	/** PointOfInterest's states (favorite or not) */
	private boolean isFav1;
	private boolean isFav2;
	
	
// OTHER METHODS
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		try {
			point1 = new GeoPoint(5, 5);
			point2 = new GeoPoint(10, 10);
			poi1 = new PointOfInterest(point1, name1, desc1, cmt1, link1, type1, isFav1);
			poi2 = new PointOfInterest(point2, name2, desc2, cmt2, link2, type2, isFav2);
		} catch(Exception e) {;}
	}
	
// CONSTRUCTOR TEST
	@SuppressWarnings("unused")
	public void testPointOfInterest() {
		try {
			PointOfInterest tmp1 = new PointOfInterest(point1, name1, desc1, cmt1, link1, type1, isFav1);
			PointOfInterest tmp2 = new PointOfInterest(point2, name2, desc2, cmt2, link2, type2, isFav2);
			
		} catch(Exception e) { fail("Constructor failed"); }
	}

// ACCESSORS TESTS
	/**
	 * Test of the getPoint() method
	 */
	public void testGetPoint() {
		assertEquals(point1, poi1.getPoint());
	}
	
	/**
	 * Test of the getName() method
	 */
	public void testGetName() {
		assertEquals(name1, poi1.getName());
	}
	
	/**
	 * Test of the getDescription() method
	 */
	public void testGetDescription() {
		assertEquals(desc1, poi1.getDescription());
	}
	
	/**
	 * Test of the getComment() method
	 */
	public void testGetComment() {
		assertEquals(cmt1, poi1.getComment());
	}
	
	/**
	 * Test of the getLink() method
	 */
	public void testGetLink() {
		assertEquals(link1, poi1.getLink());
	}

	/**
	 * Test of the getType() method
	 */
	public void testGetType() {
		assertEquals(type1, poi1.getType());
	}
	
// MODIFIERS TESTS
	/**
	 * Test of the setPoint() method
	 */
	public void testSetPoint(GeoPoint point2) {
		poi2.setPoint(point2);
		assertEquals(point2, poi2.getPoint());
	}
	
	/**
	 * Test of the setFavorite() method
	 */
	public void testSetFavorite(boolean isFav2) {
		poi2.setFavorite(isFav2);
		assertEquals(isFav2, poi2.isFavorite());
	}
	
	/**
	 * Test of the setType() method
	 */
	public void testSetType(PointOfInterestType type2) {
		poi2.setType(type2);
		assertEquals(type2, poi2.getType());
	}
}
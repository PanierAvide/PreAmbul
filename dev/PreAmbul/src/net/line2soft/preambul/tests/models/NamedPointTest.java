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

import net.line2soft.preambul.models.NamedPoint;

import org.mapsforge.core.GeoPoint;
import android.graphics.Bitmap;
import junit.framework.TestCase;

/**
 * Test class for {@link NamedPoint}
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class NamedPointTest extends TestCase {
// ATTRIBUTES
	/** NamedPoint's attributes */
	private NamedPoint namedPoint1;
	private NamedPoint namedPoint2;
	
	/** NamedPoint's points */
	private GeoPoint point1;
	private GeoPoint point2;
	
	/** NamedPoint's names */
	private String name1;
	private String name2;
	
	/** NamedPoint's comments */
	private String comment1;
	private String comment2;
	
	/** NamedPoint's photos */
	private Bitmap photo1;
	/** NamedPoint's attributes isFavorite */
	private boolean isFavorite1;
	private boolean isFavorite2;
	
// OTHER METHODS
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		try {
			point1 = new GeoPoint(5, 5);
			point2 = new GeoPoint(10, 10);
			namedPoint1 = new NamedPoint(point1, name1, comment1, isFavorite1);
			namedPoint2 = new NamedPoint(point2, name2, comment2, isFavorite2);
		} catch(Exception e) {;}
	}

// CONSTRUCTOR TEST
		@SuppressWarnings("unused")
		public void testNamedPoint() {
			try {
				NamedPoint tmp1 = new NamedPoint(point1, name1, comment1, isFavorite1);
				NamedPoint tmp2 = new NamedPoint(point2, name2, comment2, isFavorite2);
				
			} catch(Exception e) { fail("Constructor failed"); }
		}
		
// ACCESSORS TESTS
	/**
	 * Test of the getName() method
	 */
	public void testGetName() {
		assertEquals(name1, namedPoint1.getName());
	}
	
	/**
	 * Test of the getComment() method
	 */
	public void testGetComment() {
		assertEquals(comment1, namedPoint1.getName());
	}
	
	/**
	 * Test of the getPhoto() method
	 */
	public void testGetPhoto() {
		assertEquals(photo1, namedPoint1.getPhoto());
	}
	
	/**
	 * Test of the getPoint() method
	 */
	public void testGetPoint() {
		assertEquals(point1, namedPoint1.getPoint());
	}
	
// MODIFIERS TESTS
	/**
	 * Test of the setPhoto() method
	 */
	public void testSetPhoto(Bitmap photo2) {
		namedPoint2.setPhoto(photo2);
		assertEquals(photo2, namedPoint2.getPhoto());
	}
	
	/**
	 * Test of the setFavorite() method
	 */
	public void testSetFavorite(boolean isFavorite2) {
		namedPoint2.setFavorite(isFavorite2);
		assertEquals(isFavorite2, namedPoint2.isFavorite());
	}
}
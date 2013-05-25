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

import net.line2soft.preambul.models.Coordinate;
import junit.framework.TestCase;

/**
 * Test class for {@link Coordinate}
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class CoordinateTest extends TestCase {
// ATTRIBUTES
	private Coordinate c1;
	private Coordinate c2;
	private Coordinate c3;

// OTHER METHODS
	protected void setUp() throws Exception {
		super.setUp();
		try {
			c1 = new Coordinate(0,0);
			c2 = new Coordinate(15.3658, 48.1254);
			c3 = new Coordinate(-48.1254, -15.3658);
		} catch(Exception e) {;}
	}

// TESTS OF THE Coordinate() METHOD
	@SuppressWarnings("unused")
	public void testCoordinateZero() {
		try {
			Coordinate tmp1 = new Coordinate(0,0);
		}
		catch(Exception e) { fail("Constructor error"); }
	}
	
	@SuppressWarnings("unused")
	public void testCoordinatePositive() {
		try {
			Coordinate tmp2 = new Coordinate(15.3658, 48.1254);
		}
		catch(Exception e) { fail("Constructor error"); }
	}
	
	@SuppressWarnings("unused")
	public void testCoordinateNegative() {
		try {
			Coordinate tmp3 = new Coordinate(-48.1254, -15.3658);
		}
		catch(Exception e) { fail("Constructor error"); }
	}

// TESTS OF Equals() METHOD
	public void testEqualsTrue() {
		assertEquals(c2, new Coordinate(15.3658, 48.1254));
	}
	
	public void testEqualsFalse() {
		assertFalse(c3.equals(new Coordinate(5.1, 3.6)));
	}

// TESTS OF HashCode() METHOD
	public void testHashCodeEqualsZero() {
		assertEquals(c1.hashCode(), new Coordinate(0, 0).hashCode());
	}
	
	public void testHashCodeEqualsPositive() {
		assertEquals(c2.hashCode(), new Coordinate(15.3658, 48.1254).hashCode());
	}
	
	public void testHashCodeEqualsNegative() {
		assertEquals(c3.hashCode(), new Coordinate(-48.1254, -15.3658).hashCode());
	}

// TESTS OF Distance() METHOD
	public void testDistanceC1C2() {
		assertEquals(c1.distance(c2), 2552.1619348);
	}
	
	public void testDistanceC2C2() {
		assertEquals(c2.distance(c2), 0.0);
	}
	
	public void testDistanceC3C2() {
		assertEquals(c3.distance(c2), 8062.26495488);
	}

	public void testDistanceC3C2EqualsC2C3() {
		assertEquals(c3.distance(c2), c2.distance(c3));
	}

// TEST OF getX() METHOD
	public void testGetX() {
		Coordinate t1 = new Coordinate(15.2, 3.2);
		assertEquals(t1.getX(), 15.2);
	}

// TEST OF getY() METHOD
	public void testGetY() {
		Coordinate t1 = new Coordinate(15.2, 3.2);
		assertEquals(t1.getY(), 3.2);
	}
}
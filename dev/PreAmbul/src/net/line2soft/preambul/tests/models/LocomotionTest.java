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

import net.line2soft.preambul.models.Locomotion;
import net.line2soft.preambul.R;
import android.graphics.drawable.Drawable;
import android.test.AndroidTestCase;

/**
 * Test class for {@link Locomotion}
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class LocomotionTest extends AndroidTestCase {

// OTHER METHODS
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

// ACCESSORS TEST
	/**
	 * Test of the getIcon() method
	 */
	public void testGetIcon() {
		Locomotion feet = Locomotion.getLocomotion(getContext(), "feet");
		assertNull(feet.getIcon());
	}

	/**
	 * Test of the getName() method
	 */
	public void testGetName() {
		Locomotion feet = Locomotion.getLocomotion(getContext(), "feet");
		assertTrue(feet.getName().equals("À pied"));
	}

	/**
	 * Test of the getLocomotion() method
	 */
	public void testGetLocomotion() {
		Locomotion feet = Locomotion.getLocomotion(getContext(), "feet");
		assertTrue(feet.getName().equals("À pied"));
		assertNull(feet.getIcon());
	}
	
	/**
	 * Test of the getCount() method
	 */
	public void testGetCount() {
		assertEquals(17, Locomotion.getCount(getContext()));
	}
	
	/**
	 * Test of the getKey() method
	 */
	public void testGetKey() {
		assertEquals("feet", Locomotion.getLocomotion(getContext(), "feet").getKey());
	}

// MODIFIERS TESTS
	/**
	 * Test of the setIcon() method
	 */
	public void testSetIcon() {
		Locomotion feet = Locomotion.getLocomotion(getContext(), "feet");
		feet.setIcon(getContext().getResources().getDrawable(R.drawable.logo));
		Drawable d = feet.getIcon();
		assertEquals(d.getBounds(), getContext().getResources().getDrawable(R.drawable.logo).getBounds());
	}
	
	public void testExists() {
		assertTrue(Locomotion.exists(getContext(), "feet"));
		assertFalse(Locomotion.exists(getContext(), "not existing locomotion"));
	}
	
	/*//TODO
	public void testListToString() {
		fail("Not yet implemented");
	}*/
	
	public void testFindKey() {
		assertEquals("feet", Locomotion.findKey(getContext(), "À pied"));
	}
	
	//TODO
	public void testEquals() {
		fail("Not yet implemented");
	}
}

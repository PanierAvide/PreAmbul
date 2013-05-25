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

import net.line2soft.preambul.controllers.MapController;
import net.line2soft.preambul.models.Excursion;
import net.line2soft.preambul.models.Location;
import net.line2soft.preambul.models.Locomotion;
import net.line2soft.preambul.models.PointOfInterest;

import org.mapsforge.android.maps.overlay.OverlayWay;
import org.mapsforge.core.GeoPoint;

import android.test.AndroidTestCase;


/**
 * Test class for {@link Excursion}
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class ExcursionTest extends AndroidTestCase {
// ATTRIBUTES
	private Locomotion[] byFeet;

// INIT
	protected void setUp() throws Exception {
		byFeet = new Locomotion[1];
		byFeet[0]=Locomotion.getLocomotion(getContext(), "feet");
		super.setUp();
	}

// END
	protected void tearDown() throws Exception {
		super.tearDown();
	}

// TESTS
	/**
	 * Constructor test
	 */
	@SuppressWarnings("unused")
	public void testExcursion() {
		try {
			Excursion tmp1 = new Excursion(0, "Balade 1", byFeet, 150, Excursion.DIFFICULTY_EXPERT, "Balade au sommet de la montagne");
		}
		catch (Exception e) {
			fail("Exception during constructor call");
		}
	}

	/**
	 * Test of the getId() method
	 */
	public void testGetId() {
		int id = 42;
		Excursion tmp2 = new Excursion(id, "Balade 1", byFeet, 150, Excursion.DIFFICULTY_EXPERT, "Balade au sommet de la montagne");
		assertEquals(id, tmp2.getId());
	}

	/**
	 *  Test of the getTest() method
	 */
	public void testGetName() {
		String name = "Balade n°42";
		Excursion tmp3 = new Excursion(0, name, byFeet, 150, Excursion.DIFFICULTY_EXPERT, "Balade au sommet de la montagne");
		assertTrue(name.equals(tmp3.getName()));
	}

	/**
	 *  Test of the getLocomotion() method
	 */
	public void testGetLocomotion() {
		Excursion tmp4 = new Excursion(0, "Balade 1", byFeet, 150, Excursion.DIFFICULTY_EXPERT, "Balade au sommet de la montagne");
		assertEquals(byFeet, tmp4.getLocomotions());
	}

	/**
	 *  Test of the getTime() method
	 */
	public void testGetTime() {
		int time = 42;
		Excursion tmp5 = new Excursion(0, "Balade 1", byFeet, time, Excursion.DIFFICULTY_EXPERT, "Balade au sommet de la montagne");
		assertEquals(time, tmp5.getTime());
	}

	/**
	 *  Test of the getDifficulty() method
	 */
	public void testGetDifficulty() {
		int difficulty = Excursion.DIFFICULTY_HARD;
		Excursion tmp6 = new Excursion(0, "Balade 1", byFeet, 150, difficulty, "Balade au sommet de la montagne");
		assertEquals(difficulty, tmp6.getDifficulty());
	}

	/**
	 *  Test of the getDescription() method
	 */
	public void testGetDescription() {
		String description = "Randonnée au coeur du Lavandou";
		Excursion tmp7 = new Excursion(0, "Balade 1", byFeet, 150, Excursion.DIFFICULTY_EXPERT, description);
		assertTrue(description.equals(tmp7.getDescription()));
	}

	/**
	 *  Test of the getInstructions() method
	 */
	//TODO Write the test
	public void testGetInstructions() {
		fail("Not yet implemented");
	}

	/**
	 * Test of the setPath() method
	 */
	public void testSetPath() {
		//TODO
		fail("Not yet implemented");
	}
	/**
	 * Test of the getPath() method
	 */
	public void testGetPath() {
		//Data for test
		GeoPoint[][] points = { {
			new GeoPoint(47.65238368396062, -2.7860486921019922),
			new GeoPoint(47.652952880694755, -2.78396572353694),
			new GeoPoint(47.65286022118406, -2.783597975717504),
			new GeoPoint(47.65253118404208, -2.7834183126606797),
			new GeoPoint(47.651727492651496, -2.7833144449559533) } };
		OverlayWay ow1 = new OverlayWay(points);
		
		//Init
		Excursion exc1 = new Excursion(0, "Balade 1", byFeet, 150, Excursion.DIFFICULTY_EXPERT, "Balade 1");
		exc1.setPath(ow1);
		
		assertEquals(exc1.getPath(), ow1);
	}

	/**
	 * Test of the getSurroundingPois() method
	 */
	public void testGetSurroundingPois() {
		//Data for test
		GeoPoint[] resultPois = {
				new GeoPoint(47.652842836745265, -2.7835741249774606),
				new GeoPoint(47.652937823074275, -2.783967602570898),
				new GeoPoint(47.652540707635445, -2.7832859371633845) };
		PointOfInterest[] poisSet = {
				new PointOfInterest(new GeoPoint(47.652842836745265, -2.7835741249774606),null,null,null,null,null,false),
				new PointOfInterest(new GeoPoint(47.652937823074275, -2.783967602570898),null,null,null,null,null,false),
				new PointOfInterest(new GeoPoint(47.65299787452319, -2.786626877675366),null,null,null,null,null,false),
				new PointOfInterest(new GeoPoint(47.6513556712458, -2.7832413519483405),null,null,null,null,null,false),
				new PointOfInterest(new GeoPoint(47.652540707635445, -2.7832859371633845),null,null,null,null,null,false),
				new PointOfInterest(new GeoPoint(47.65254080149592, -2.7832849769740404),null,null,null,null,null,false)
		};
		GeoPoint[][] points = { {
			new GeoPoint(47.65238368396062, -2.7860486921019922),
			new GeoPoint(47.652952880694755, -2.78396572353694),
			new GeoPoint(47.65286022118406, -2.783597975717504),
			new GeoPoint(47.65253118404208, -2.7834183126606797),
			new GeoPoint(47.651727492651496, -2.7833144449559533) } };
		OverlayWay ow1 = new OverlayWay(points);
		Location loc = new Location(1,"Vannes","56000","FR",null,null,10,null,null);
		//TODO
		//loc.setPOIs(poisSet);
		MapController.getInstance(getContext()).setCurrentLocation(loc);
		
		//Init
		Excursion exc1 = new Excursion(0, "Balade 1", byFeet, 150, Excursion.DIFFICULTY_EXPERT, "Balade 1");
		exc1.setPath(ow1);
		PointOfInterest[] processedPois = exc1.getSurroundingPois(getContext());
		
		//Test if returned values are correct
		assertEquals(resultPois.length, processedPois.length);
		for(int i = 0; i < processedPois.length; i++) {
			boolean found = false;
			short j = 0;
			while(!found && j < resultPois.length){
				if(processedPois[i].getPoint().equals(resultPois[j])) { found = true; }
				j++;
			}
			assertTrue(found);
		}
	}
	
	/**
	 * Test of the getLength() method
	 */
	public void testGetLength() {
		//Data for test
		GeoPoint[][] points = { {
			new GeoPoint(47.65238368396062, -2.7860486921019922),
			new GeoPoint(47.652952880694755, -2.78396572353694),
			new GeoPoint(47.65286022118406, -2.783597975717504),
			new GeoPoint(47.65253118404208, -2.7834183126606797),
			new GeoPoint(47.651727492651496, -2.7833144449559533) } };
		OverlayWay ow1 = new OverlayWay(points);
		
		//Init
		Excursion exc1 = new Excursion(0, "Balade 1", byFeet, 150, Excursion.DIFFICULTY_EXPERT, "Balade 1");
		exc1.setPath(ow1);
		
		//Test
		assertEquals((int) exc1.getLength(), 326);
	}
	

	/**
	 * Test of the setFavorite() method
	 */
	public void testSetFavorite() {
		//TODO
		fail("Not yet implemented");
	}
	/**
	 * Test of the getFavorite() method
	 */
	public void testGetFavorite() {
		//TODO
		fail("Not yet implemented");
	}
}
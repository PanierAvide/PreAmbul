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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import net.line2soft.preambul.models.Area;
import net.line2soft.preambul.models.Coordinate;
import net.line2soft.preambul.models.Location;
import net.line2soft.preambul.models.PointOfInterest;
import net.line2soft.preambul.models.PointOfInterestCategory;
import net.line2soft.preambul.models.PointOfInterestType;

import org.mapsforge.core.GeoPoint;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import junit.framework.TestCase;

/**
 * Test class for {@link Location}
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class LocationTest extends TestCase {
// ATTRIBUTES
	/** LocationTest's attribute **/
	private Location loc1;
	private Location loc2;
	private Location loc3;
	
	/** LocationTest's logos **/
	private Uri logo1;
	private Uri logo2;
	private Uri logo3;
	
	/** LocationTest's URLs **/
	private URL url1;
	private URL url2;
	private URL url3;
	
	/** LocationTest's Areas **/
	private Area area1;
	private Area area2;
	private Area area3;
	
	/** The coordinate of the top left corner of the area **/
	private Coordinate topLeftCorner1;
	private Coordinate topLeftCorner2;
	private Coordinate topLeftCorner3;
	
	/** The coordinate of the bottom right corner of the area **/
	private Coordinate bottomRightCorner1;
	private Coordinate bottomRightCorner2;
	private Coordinate bottomRightCorner3;
	
// OTHER METHODS
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		try {
			topLeftCorner1 = new Coordinate(1.0, 1.0);
			topLeftCorner2 = new Coordinate(2.0, 2.0);
			topLeftCorner3 = new Coordinate(3.0, 3.0);
			
			bottomRightCorner1 = new Coordinate(10.0, 10.0);
			bottomRightCorner2 = new Coordinate(20.0, 20.0);
			bottomRightCorner3 = new Coordinate(30.0, 30.0);
			
			area1 = new Area(topLeftCorner1, bottomRightCorner1);
			area1 = new Area(topLeftCorner2, bottomRightCorner2);
			area1 = new Area(topLeftCorner3, bottomRightCorner3);
			
			logo1 = Uri.fromFile(new File("/res/drawable-hdpi/logo.png"));
			logo2 = Uri.fromFile(new File("/res/drawable-hdpi/logo.png"));
			logo3 = Uri.fromFile(new File("/res/drawable-hdpi/logo.png"));
			
			url1 = new URL("http://tonurl.com/");
			url2 = new URL("http://tonurl.com/");
			url3 = new URL("http://tonurl.com/");
			
			//Creation of locations
			loc1 = new Location(1,"Vannes","56000","FR",area1,logo1,10,url1,null);
			loc2 = new Location(2,"Arradon","56010","FR",area2,logo2,12,url2,null);
			loc3 = new Location(3,"Séné","56020","FR",area3,logo3,15,url3,null);
		} catch(Exception e) {;}
	}
	
// CONSTRUCTOR TEST
	@SuppressWarnings("unused")
	public void testLocation() {
		try {
			Location tmp1 = new Location(1,"Vannes","56000","FR",area1,logo1,10,url1,null);
			Location tmp2 = new Location(2,"Arradon","56010","FR",area2,logo2,12,url2,null);
			Location tmp3 = new Location(3,"Séné","56020","FR",area3,logo3,15,url3,null);
		} catch(Exception e) { fail("Constructor failed"); }
	}

// ACCESSORS TESTS
	/**
	 * Test of the getId() method
	 */
	public void testGetId() {
		assertEquals(1, loc1.getId());
	}
	
	/**
	 * Test of the getName() method
	 */
	public void testGetName() {
		assertEquals("Vannes", loc1.getName());
	}
	
	/**
	 * Test of the getLogo() method 
	 */
	public void testGetLogo() {
		assertEquals(logo1, loc1.getLogo());
	}	
	/**
	 * Test of the getPhotoUrl() method 
	 */
	public void testGetPhotoUrl() {
		//TODO
		fail("Not yet implemented");
	}	
	/**
	 * Test of the getBoundaryBox() method 
	 */
	public void testGetBoundaryBox() {
		//TODO
		fail("Not yet implemented");
	}	
	/**
	 * Test of the getVersion() method 
	 */
	public void testGetVersion() {
		assertEquals(10, loc1.getVersion());
	}
	
	/**
	 * Test of the getUrl() method 
	 */
	public void testGetUrl() {
		assertEquals(url1, loc1.getPackageUrl());
	}
	
	/**
	 * Test of the getUrl() method 
	 */
	public void testGetPostalCode() {
		assertEquals("56000", loc1.getPostalCode());
	}
	
	/**
	 * Test of the getCountry() method 
	 */
	public void testGetCountry() {
		assertEquals("FR", loc1.getCountry());
	}
	
	/**
	 * Test of the getPOIs() method 
	 */
	public void testGetPOIs() {
		//TODO
		fail("Not yet implemented");
		/*
		GeoPoint point = new GeoPoint(10, 10);
		URL lien = new URL("http://tonurl.com/")
		
		Drawable icon;
		Drawable icon2;
		PointOfInterestCategory category = new PointOfInterestCategory("id", "name", icon2);
		PointOfInterestType type = new PointOfInterestType("id", "name", icon, category);
		PointOfInterest poi = new PointOfInterest(point, "nom", "decription", "commentaire", lien, type, true);
		
		assertEquals(poi, type.getPois());
		*/
	}

	/**
	 * getExcursions() test
	 */
	public void testGetExcursions() {
		//TODO
		fail("Not yet implemented");
	}
	/**
	 * getNamedFavorites() test
	 */
	public void testGetNamedFavorites() {
		//TODO
		fail("Not yet implemented");
	}
	/**
	 * getFavorites() test
	 */
	public void testGetFavorites() {
		//TODO
		fail("Not yet implemented");
	}
	/**
	 * getFavorite() test
	 */
	public void testGetFavorite() {
		//TODO
		fail("Not yet implemented");
	}
	/**
	 * findFavorite() test
	 */
	public void testFindFavorite() {
		//TODO
		fail("Not yet implemented");
	}
	/**
	 * getPoiCategory() test
	 */
	public void testGetPoiCategory() {
		//TODO
		fail("Not yet implemented");
	}
	/**
	 * getAllPoiCategory() test
	 */
	public void testGetAllPoiCategory() {
		//TODO
		fail("Not yet implemented");
	}
	/**
	 * getAvailableLocomotion() test
	 */
	public void testGetAvailableLocomotions() {
		//TODO
		fail("Not yet implemented");
	}
	
// MODIFIERS TESTS
	/**
	 * Test of the setName()
	 */
	public void testSetName() {
		loc2.setName("Arradon, la commune");
		assertEquals("Arradon, la commune", loc2.getName());
	}
	
	/**
	 * Test of the setLogo()
	 */
	public void testSetLogo() {
		Uri newUri = Uri.fromFile(new File("/res/drawable-hdpi/logo.png"));
		loc1.setLogo(newUri);
		assertEquals(newUri, loc1.getLogo());
	}
	
	/**
	 * Test of the setVersion()
	 */
	public void testSetVersion() {
		loc1.setVersion(11);
		assertEquals(11, loc1.getVersion());
	}
	
	/**
	 * Test of the setUrl()
	 */
	public void testSetPackageUrl() {
		URL newUrl;
		try {
			newUrl = new URL("http://tonurl.com/");
			loc3.setPackageUrl(newUrl);
			assertEquals(newUrl, loc3.getPackageUrl());
		} catch (MalformedURLException e) {
			fail("Invalid URL");
		}
	}
	
	/**
	 * Test of the setPostalCode()
	 */
	public void testSetPostalCode() {
		loc3.setPostalCode("56010");
		assertEquals("56010", loc3.getPostalCode());
	}
	
	/**
	 * Test of the setCountry()
	 */
	public void testSetCountry() {
		loc2.setCountry("Canada");
		assertEquals("Canada", loc2.getCountry());
	}
	
	/**
	 * Test of the setPOIs() method 
	 */
	public void testSetPOIs() {
		//TODO
		fail("Not yet implemented");
	}	
	
	public void testFindExcursions() {
		//TODO
		fail("Not yet implemented");
	}
}
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

package net.line2soft.preambul.tests.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;

import net.line2soft.preambul.models.Area;
import net.line2soft.preambul.models.Coordinate;
import net.line2soft.preambul.models.Excursion;
import net.line2soft.preambul.models.Location;
import net.line2soft.preambul.models.Locomotion;
import net.line2soft.preambul.models.NamedPoint;
import net.line2soft.preambul.models.NavigationInstruction;
import net.line2soft.preambul.models.PointOfInterest;
import net.line2soft.preambul.models.PointOfInterestCategory;
import net.line2soft.preambul.utils.RWFile;

import org.mapsforge.android.maps.overlay.OverlayWay;
import org.mapsforge.core.GeoPoint;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.net.Uri;
import android.test.AndroidTestCase;

import net.line2soft.preambul.R;

import junit.framework.Assert;

/**
 * Test {@link RWFile}
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class RWFileTest extends AndroidTestCase {
// OTHER METHODS
	/**
	 * Test of the deleteDirectory() method
	 */
	public void testDeleteDirectory(){
		try{
			Context ctx= this.getContext();
			File file1=new File(ctx.getFilesDir().getAbsolutePath() + File.separator + "RandOSM"+ File.separator +"test");
			
			if(!file1.mkdirs()){Assert.fail();}
			RWFile.deleteDirectory(file1);
			assertFalse(file1.exists());
			
			if(!file1.mkdirs()){Assert.fail();}
			try{
				File.createTempFile("test1",null,file1);
			}catch(Exception e){
				System.err.println(e.getMessage());
				e.printStackTrace();
				Assert.fail();
			}
			RWFile.deleteDirectory(new File(ctx.getFilesDir().getAbsolutePath() + File.separator + "RandOSM"));
			assertFalse(file1.exists());
			
		}catch(Exception e){
			e.printStackTrace();
			Assert.fail();
		}		
	}

	/**
	 * Test of the writeLocationsXML() method
	 */
	public void testWriteLocationsXML(){
		try{
			Context ctx= this.getContext();
			File file1=new File(ctx.getFilesDir().getAbsolutePath() + File.separator + "Rando");
			HashMap<Integer,Location> testMap=new HashMap<Integer,Location>();
			
			int id=1;
			String name="ville";
			String postalCode="42";
			String country="FR";
			double topLeftX=0;
			double topLeftY=0;
			double botRightX=0;
			double botRightY=0;
			int version=-1;
			URL url=new URL("http://www.aaa.com");
			Uri logo=null;
			
			testMap.put(id, new Location(id, name, postalCode, country, new Area(new Coordinate(topLeftX,topLeftY),new Coordinate(botRightX,botRightY)),
				logo, version, url,null));
			
			RWFile.writeLocationsXML(file1, testMap);
			
			BufferedReader reader=new BufferedReader(new FileReader(file1));
			//first readLine() to not read the <?xml> tag
			String line=reader.readLine();
			boolean result=false;
			assertNotNull(line);
			if(line.indexOf("<locations>")>=0){
				if(line.indexOf("<location")>=0 && line.indexOf("name")>0 && line.indexOf("ville")>0){
					if(line.indexOf("</locations>")>=0){
						result=true;
					}
				}
			}
			file1.delete();
			reader.close();
			assertTrue(result);
		}catch(Exception e){
			e.printStackTrace();
			Assert.fail();
		}
	}

	/**
	 * Test of the readLocationXML() method
	 */
	public void testReadLocationsXML(){
		try{
			Context ctx= this.getContext();
			File file1=new File(ctx.getFilesDir().getAbsolutePath() + File.separator + "Rando");
			HashMap<Integer,Location> testMap=new HashMap<Integer,Location>();
			
			int id=1;
			String name="ville";
			String postalCode="42";
			String country="FR";
			double topLeftX=1.0;
			double topLeftY=2.0;
			double botRightX=3.0;
			double botRightY=4.0;
			int version=-1;
			URL url=new URL("http://www.aaa.com");
			Uri logo=null;
			URL url2 = new URL("http://www.dotcom.com/");
			
			testMap.put(id, new Location(id, name, postalCode, country, new Area(new Coordinate(topLeftX,topLeftY),new Coordinate(botRightX,botRightY)),
				logo, version, url,null));
			testMap.put(2, new Location(2, "Rennes", "35000", "FR", new Area(new Coordinate(5.0, 6.0),new Coordinate(7.0, 8.0)),
					null, 2, url2,null));
			
			RWFile.writeLocationsXML(file1, testMap);
			
			HashMap<Integer,Location> map2=RWFile.readLocationsXML(file1);
			Location loc=map2.get(1);
			Location loc2=map2.get(2);
			assertEquals(loc.getId(),id);
			assertEquals(loc.getName(),name);
			assertEquals(loc.getPostalCode(),postalCode);
			assertEquals(loc.getCountry(),country);
			assertEquals(loc.getBoundaryBox().getTopLeftCorner().getX(), topLeftX);
			assertEquals(loc.getBoundaryBox().getTopLeftCorner().getY(), topLeftY);
			assertEquals(loc.getBoundaryBox().getBottomRightCorner().getX(), botRightX);
			assertEquals(loc.getBoundaryBox().getBottomRightCorner().getY(), botRightY);
			assertEquals(loc.getVersion(), version);
			assertEquals(loc.getPackageUrl(), url);
			assertEquals(loc2.getId(),2);
			assertEquals(loc2.getName(),"Rennes");
			assertEquals(loc2.getPostalCode(),"35000");
			assertEquals(loc2.getCountry(),"FR");
			assertEquals(loc2.getBoundaryBox().getTopLeftCorner().getX(), 5.0);
			assertEquals(loc2.getBoundaryBox().getTopLeftCorner().getY(), 6.0);
			assertEquals(loc2.getBoundaryBox().getBottomRightCorner().getX(), 7.0);
			assertEquals(loc2.getBoundaryBox().getBottomRightCorner().getY(), 8.0);
			assertEquals(loc2.getVersion(), 2);
			assertEquals(loc2.getPackageUrl(), url2);
		}catch(Exception e){
			e.printStackTrace();
			Assert.fail();
		}		
	}

	/**
	 * Test of the copyRenderRulesIntoFiles() method
	 */
	public void testCopyRenderRulesIntoFiles() {
		File style = new File(getContext().getFilesDir()+File.separator+"map_style.xml");
		File result = RWFile.copyRenderRulesIntoFiles(getContext());
		assertTrue(style.getPath().equals(result.getPath()));
		assertTrue(style.length() == result.length());
	}
	
	/**
	 * Test of the readGPX() method
	 */
	public void testReadGPX() {
		//Write GPX file into cache dir
		File gpx = new File(getContext().getCacheDir().getPath()+File.separator+"test.gpx");
		StringWriter writer = new StringWriter();
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+"<gpx version=\"1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.topografix.com/GPX/1/0\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd\">"
			+"<time>2013-02-26T15:53:26Z</time>"
			+"<bounds minlat=\"47.606455161\" minlon=\"-2.861006360\" maxlat=\"47.649921327\" maxlon=\"-2.784788959\"/>"
			+"<trk>"
			+"  <name>Circuit kayak 1</name>"
			+"<trkseg>"
			+" <trkpt lat=\"47.615919984\" lon=\"-2.827286615\">"
			+" </trkpt>"
			+" <trkpt lat=\"47.615074932\" lon=\"-2.826008724\">"
			+" </trkpt>"
			+" <trkpt lat=\"47.613999722\" lon=\"-2.823495609\">"
			+" </trkpt>"
			+"</trkseg>"
			+"</trk>"
			+"</gpx>");
		//Write into filesystem
        FileOutputStream fos;
        try {
        	fos = new FileOutputStream(gpx);
			fos.write(writer.toString().getBytes());
			//Close
	        fos.close();
	        
	        //Try to read
	        try {
				OverlayWay ow = RWFile.readGPX(gpx);
				GeoPoint[][] nodes = ow.getWayNodes();
				//Test nodes length
				assertEquals(nodes.length, 1);
				assertEquals(nodes[0].length, 3);
				//Test returned nodes
				assertEquals(nodes[0][0].getLatitude(), 47.615919);
				assertEquals(nodes[0][0].getLongitude(), -2.827286);
				assertEquals(nodes[0][1].getLatitude(), 47.615074);
				assertEquals(nodes[0][1].getLongitude(), -2.826008);
				assertEquals(nodes[0][2].getLatitude(), 47.613999);
				assertEquals(nodes[0][2].getLongitude(), -2.823495);
			} catch (XmlPullParserException e) {
				e.printStackTrace();
				fail("Parsing error");
			} catch (IOException e) {
				e.printStackTrace();
				fail("Read error");
			}
		} catch (IOException e) {
			e.printStackTrace();
			fail("Writing error");
		}
	}
	/**
	 * Test of the readNavigationXML() method
	 */
	public void testReadNavigationXML() {
		//Write XML file into cache dir
		File nav = new File(getContext().getCacheDir().getPath()+File.separator+"navig.nax");
		StringWriter writer = new StringWriter();
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+"<navigation>"
			+" <instruction id=\"1\" value=\"Dirigez­ vous vers le nord\" time=\"5\" />"
			+" <instruction id=\"3\" value=\"À 500 mètres, tournez à gauche\" time=\"25\" />"
			+" <instruction id=\"2\" value=\"Suivez la Rue du Moulin\" time=\"20\" />"
			+" <instruction id=\"4\" value=\"Vous êtes arrivé\" time=\"45\" />"
			+"</navigation>");
		//Write GPX file into cache dir
		File gpx = new File(getContext().getCacheDir().getPath()+File.separator+"test.gpx");
		StringWriter writer2 = new StringWriter();
		writer2.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+"<gpx version=\"1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.topografix.com/GPX/1/0\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd\">"
			+"<time>2013-02-26T15:53:26Z</time>"
			+"<bounds minlat=\"47.606455161\" minlon=\"-2.861006360\" maxlat=\"47.649921327\" maxlon=\"-2.784788959\"/>"
			+"<trk>"
			+"  <name>Circuit kayak 1</name>"
			+"<trkseg>"
			+" <trkpt lat=\"47.615919984\" lon=\"-2.827286615\">"
			+" </trkpt>"
			+" <trkpt lat=\"47.615074932\" lon=\"-2.826008724\">"
			+" </trkpt>"
			+" <trkpt lat=\"47.613999722\" lon=\"-2.823495609\">"
			+"  <instruction />"
			+" </trkpt>"
			+" <trkpt lat=\"47.615919984\" lon=\"-2.827286615\">"
			+"  <instruction />"
			+" </trkpt>"
			+" <trkpt lat=\"47.615074932\" lon=\"-2.826008724\">"
			+" </trkpt>"
			+" <trkpt lat=\"47.613999722\" lon=\"-2.823495609\">"
			+"  <instruction />"
			+" </trkpt>"
			+" <trkpt lat=\"47.615919984\" lon=\"-2.827286615\">"
			+" </trkpt>"
			+"</trkseg>"
			+"</trk>"
			+"</gpx>");
		//Write into filesystem
        FileOutputStream fos;
        try {
        	fos = new FileOutputStream(nav);
			fos.write(writer.toString().getBytes());
			fos.close();
			fos = new FileOutputStream(gpx);
			fos.write(writer2.toString().getBytes());
	        fos.close();
	        
	        //Try to read
	        try {
				NavigationInstruction[] ni = RWFile.readNavigationXML(nav, gpx);
				//Test instructions length
				assertEquals(4, ni.length);
				//Test ID
				assertEquals(1, ni[0].getId());
				assertEquals(2, ni[1].getId());
				assertEquals(3, ni[2].getId());
				assertEquals(4, ni[3].getId());
				//Test returned instructions
				assertEquals("Dirigez­ vous vers le nord", ni[0].getInstruction());
				assertEquals("Suivez la Rue du Moulin", ni[1].getInstruction());
				assertEquals("À 500 mètres, tournez à gauche", ni[2].getInstruction());
				assertEquals("Vous êtes arrivé", ni[3].getInstruction());
				//Test time
				assertEquals(5, ni[0].getTime());
				assertEquals(20, ni[1].getTime());
				assertEquals(25, ni[2].getTime());
				assertEquals(45, ni[3].getTime());
				//Test segments
				assertEquals(3, ni[0].getSegment().getWayNodes()[0].length);
				assertEquals(2, ni[1].getSegment().getWayNodes()[0].length);
				assertEquals(3, ni[2].getSegment().getWayNodes()[0].length);
				assertEquals(2, ni[3].getSegment().getWayNodes()[0].length);
				//Instruction 1
				assertEquals(47.615919, ni[0].getSegment().getWayNodes()[0][0].getLatitude());
				assertEquals(-2.827286, ni[0].getSegment().getWayNodes()[0][0].getLongitude());
				assertEquals(47.615074, ni[0].getSegment().getWayNodes()[0][1].getLatitude());
				assertEquals(-2.826008, ni[0].getSegment().getWayNodes()[0][1].getLongitude());
				assertEquals(47.613999, ni[0].getSegment().getWayNodes()[0][2].getLatitude());
				assertEquals(-2.823495, ni[0].getSegment().getWayNodes()[0][2].getLongitude());
				//Instruction 2
				assertEquals(47.613999, ni[1].getSegment().getWayNodes()[0][0].getLatitude());
				assertEquals(-2.823495, ni[1].getSegment().getWayNodes()[0][0].getLongitude());
				assertEquals(47.615919, ni[1].getSegment().getWayNodes()[0][1].getLatitude());
				assertEquals(-2.827286, ni[1].getSegment().getWayNodes()[0][1].getLongitude());
				//Instruction 3
				assertEquals(47.615919, ni[2].getSegment().getWayNodes()[0][0].getLatitude());
				assertEquals(-2.827286, ni[2].getSegment().getWayNodes()[0][0].getLongitude());
				assertEquals(47.615074, ni[2].getSegment().getWayNodes()[0][1].getLatitude());
				assertEquals(-2.826008, ni[2].getSegment().getWayNodes()[0][1].getLongitude());
				assertEquals(47.613999, ni[2].getSegment().getWayNodes()[0][2].getLatitude());
				assertEquals(-2.823495, ni[2].getSegment().getWayNodes()[0][2].getLongitude());
				//instruction 4
				assertEquals(47.613999, ni[3].getSegment().getWayNodes()[0][0].getLatitude());
				assertEquals(-2.823495, ni[3].getSegment().getWayNodes()[0][0].getLongitude());
				assertEquals(47.615919, ni[3].getSegment().getWayNodes()[0][1].getLatitude());
				assertEquals(-2.827286, ni[3].getSegment().getWayNodes()[0][1].getLongitude());
				
			} catch (XmlPullParserException e) {
				e.printStackTrace();
				fail("Parsing error");
			} catch (IOException e) {
				e.printStackTrace();
				fail("Read error");
			}
		} catch (IOException e) {
			e.printStackTrace();
			fail("Writing error");
		}
	}
	
	/**
	 * Test of the readExcursionsXML() method
	 */
	public void testReadExcursionsXML() {
		//Write XML file into cache dir
		File exc = new File(getContext().getCacheDir().getPath()+File.separator+"excursions.xml");
		StringWriter writer = new StringWriter();
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+"<excursions>"
				+"	<excursion id=\"1\" name=\"Îles Logoden\" locomotion=\"kayak\" time=\"120\" difficulty=\"2\""
				+"		description=\"Cette balade en kayak vous mènera jusqu'aux îles Logoden, au sud de la commune.\" />"
				+"	<excursion id=\"2\" name=\"Parcours vélo 1\" locomotion=\"bicycle\" time=\"150\" difficulty=\"1\""
				+"		description=\"Parcours vélo 1\" />"
				+"	<excursion id=\"3\" name=\"Parcours vélo 5\" locomotion=\"bicycle;feet\" time=\"42\" difficulty=\"5\""
				+"		description=\"Parcours vélo 5\" />"
				+"</excursions>");
		
		//Create XML file for bookmarks
		File favorites = new File(getContext().getCacheDir().getPath()+File.separator+"fav_excursions.xml");
		StringWriter writerFav = new StringWriter();
		writerFav.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?>"
				+"<excursions-bookmarks>"
				+" <bookmark id=\"2\" />"
				+" <bookmark id=\"1\" />"
				+"</excursions-bookmarks>");
		
		//Write into filesystem
        FileOutputStream fos;
        try {
        	fos = new FileOutputStream(exc);
			fos.write(writer.toString().getBytes());
	        fos.close();
	        fos = new FileOutputStream(favorites);
			fos.write(writerFav.toString().getBytes());
	        fos.close();
	        
	        //Try to read
	        try {
				HashMap<Integer,Excursion> excursions = RWFile.readExcursionsXML(getContext(), exc, favorites);
				//Test size
				assertEquals(3, excursions.size());
				//Test excursions ID
				assertEquals(1, excursions.get(1).getId());
				assertEquals(2, excursions.get(2).getId());
				assertEquals(3, excursions.get(3).getId());
				//Test names
				assertEquals("Îles Logoden", excursions.get(1).getName());
				assertEquals("Parcours vélo 1", excursions.get(2).getName());
				assertEquals("Parcours vélo 5", excursions.get(3).getName());
				//Test locomotions
				assertEquals(Locomotion.getLocomotion(getContext(), "kayak"), excursions.get(1).getLocomotions()[0]);
				assertEquals(Locomotion.getLocomotion(getContext(), "bicycle"), excursions.get(2).getLocomotions()[0]);
				boolean foundFeet = false;
				boolean foundBicycle = false;
				if(excursions.get(3).getLocomotions()[0].equals(Locomotion.getLocomotion(getContext(), "feet"))) { foundFeet = true; }
				else if(excursions.get(3).getLocomotions()[0].equals(Locomotion.getLocomotion(getContext(), "bicycle"))) { foundBicycle = true; }
				if(excursions.get(3).getLocomotions()[1].equals(Locomotion.getLocomotion(getContext(), "feet"))) { foundFeet = true; }
				else if(excursions.get(3).getLocomotions()[1].equals(Locomotion.getLocomotion(getContext(), "bicycle"))) { foundBicycle = true; }
				assertTrue(foundFeet);
				assertTrue(foundBicycle);
				//Test times
				assertEquals(120, excursions.get(1).getTime());
				assertEquals(150, excursions.get(2).getTime());
				assertEquals(42, excursions.get(3).getTime());
				//Test difficulties
				assertEquals(Excursion.DIFFICULTY_EASY, excursions.get(1).getDifficulty());
				assertEquals(Excursion.DIFFICULTY_NONE, excursions.get(2).getDifficulty());
				assertEquals(Excursion.DIFFICULTY_EXPERT, excursions.get(3).getDifficulty());
				//Test descriptions
				assertEquals("Cette balade en kayak vous mènera jusqu'aux îles Logoden, au sud de la commune.", excursions.get(1).getDescription());
				assertEquals("Parcours vélo 1", excursions.get(2).getDescription());
				assertEquals("Parcours vélo 5", excursions.get(3).getDescription());
				//Test isFavorite
				assertTrue(excursions.get(1).isFavorite());
				assertTrue(excursions.get(2).isFavorite());
				assertFalse(excursions.get(3).isFavorite());
			} catch (XmlPullParserException e) {
				e.printStackTrace();
				fail("Parsing error");
			} catch (IOException e) {
				e.printStackTrace();
				fail("Read error");
			}
		} catch (IOException e) {
			e.printStackTrace();
			fail("Writing error");
		}
	}
	
	/**
	 * Test of the formatCoodinates() method
	 */
	public void testFormatCoordinates() {
		assertEquals("+047.123456-002.365478", RWFile.formatCoordinates(47.123456789, -2.3654789));
		assertEquals("-147.123456+002.365000", RWFile.formatCoordinates(-147.123456789, 2.365));
	}
	
	/**
	 * Test of the readPoiGPX() method
	 */
	public void testReadPoiGPX() {
		//Write XML file for categories into cache dir
		File cat = new File(getContext().getCacheDir().getPath()+File.separator+"poi_categories_fr.xml");
		StringWriter writer = new StringWriter();
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+"<poi-categories>"
				+"<category id=\"hosting\" name=\"Hébergement\">"
				+" <type id=\"hostel\" name=\"Hôtel\" />"
				+" <type id=\"campsite\" name=\"Camping\" />"
				+"</category>"
				+"<category id=\"dining\" name=\"Restauration\">"
				+" <type id=\"restaurant\" name=\"Restaurant\" />"
				+"</category>"
				+"</poi-categories>");
		
		//Create XML file for POIs
		File poi = new File(getContext().getCacheDir().getPath()+File.separator+"pois.gpx");
		StringWriter writerPoi = new StringWriter();
		writerPoi.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>"
				+"<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"byHand\" version=\"1.1\""
				+" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema­instance\""
				+" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1" 
				+" http://www.topografix.com/GPX/1/1/gpx.xsd\">"
				+" <wpt lat=\"39.921055008\" lon=\"3.054223107\">"
				+"  <name>Camping d'Arradon</name>"
				+"  <cmt>Le camping vous accueille du lundi au vendredi.</cmt>"
				+"  <link>http://www.camping-arradon.fr/</link>"
				+"  <type>campsite</type>"
				+" </wpt>"
				+" <wpt lat=\"38.921055008\" lon=\"4.054223107\">"
				+"  <name>Restaurant du Coin</name>"
				+"  <cmt>Le resto sympathique du centre.</cmt>"
				+"  <desc>1 Rue du Centre, 56000 Vannes. 0312345678</desc>"
				+"  <type>restaurant</type>"
				+" </wpt>"
				+"</gpx>");
		
		//Create XML file for bookmarks
		File favorites = new File(getContext().getCacheDir().getPath()+File.separator+"fav_pois.xml");
		StringWriter writerFav = new StringWriter();
		writerFav.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
				+"<pois-bookmarks>"
				+" <bookmark lat=\"39.921055008\" lon=\"3.054223107\" />"
				+"</pois-bookmarks>");
		//Write into filesystem
        FileOutputStream fos;
        try {
        	fos = new FileOutputStream(cat);
			fos.write(writer.toString().getBytes());
			fos.close();
			fos = new FileOutputStream(poi);
			fos.write(writerPoi.toString().getBytes());
	        fos.close();
	        fos = new FileOutputStream(favorites);
			fos.write(writerFav.toString().getBytes());
	        fos.close();
	        
	        //Try to read
	        try {
				HashMap<String,PointOfInterestCategory> pois = RWFile.readPoiGPX(poi, getContext(), R.xml.poi_categories_fr, favorites,0);
				//Test size
				assertEquals(6, pois.size());
				assertEquals(3, pois.get("hosting").getTypes().length);
				assertEquals(5, pois.get("dining").getTypes().length);
				assertEquals(8, pois.get("shop").getTypes().length);
				assertEquals(10, pois.get("amenity").getTypes().length);
				assertEquals(9, pois.get("places").getTypes().length);
				assertEquals(3, pois.get("natural").getTypes().length);
				assertEquals(0, pois.get("hosting").getType("hostel").getPois().length);
				assertEquals(1, pois.get("hosting").getType("campsite").getPois().length);
				assertEquals(1, pois.get("dining").getType("restaurant").getPois().length);
				//Test categories
				assertEquals("Hébergement", pois.get("hosting").getName());
				assertEquals("Restauration", pois.get("dining").getName());
				assertEquals("hosting", pois.get("hosting").getId());
				assertEquals("dining", pois.get("dining").getId());
				//Test types
				assertEquals("Hôtel", pois.get("hosting").getType("hostel").getName());
				assertEquals("Camping", pois.get("hosting").getType("campsite").getName());
				assertEquals("Restaurant", pois.get("dining").getType("restaurant").getName());
				//Test POIs
				PointOfInterest camping = pois.get("hosting").getType("campsite").getPois()[0];
				PointOfInterest resto = pois.get("dining").getType("restaurant").getPois()[0];
				assertEquals(39.921055, camping.getPoint().getLatitude());
				assertEquals(3.054223, camping.getPoint().getLongitude());
				assertEquals(38.921055, resto.getPoint().getLatitude());
				assertEquals(4.054223, resto.getPoint().getLongitude());
				assertEquals("Camping d'Arradon", camping.getName());
				assertEquals("Restaurant du Coin", resto.getName());
				assertEquals("Le camping vous accueille du lundi au vendredi.", camping.getComment());
				assertEquals("Le resto sympathique du centre.", resto.getComment());
				assertEquals(null, camping.getDescription());
				assertEquals("1 Rue du Centre, 56000 Vannes. 0312345678", resto.getDescription());
				assertEquals("http://www.camping-arradon.fr/", camping.getLink().toString());
				assertEquals(null, resto.getLink());
			} catch (XmlPullParserException e) {
				e.printStackTrace();
				fail("Parsing error");
			} catch (IOException e) {
				e.printStackTrace();
				fail("Read error");
			}
		} catch (IOException e) {
			e.printStackTrace();
			fail("Writing error");
		}
	}
	
	/**
	 * Test of the readfavoritesGPX() method
	 */
	public void testReadFavoritesGPX() {
		//Create XML file for POIs
		File xml = new File(getContext().getCacheDir().getPath()+File.separator+"favorites.gpx");
		StringWriter writer = new StringWriter();
		writer.write("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\" ?>"
				+"<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"byHand\" version=\"1.1\""
				+" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema­instance\""
				+" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1" 
				+" http://www.topografix.com/GPX/1/1/gpx.xsd\">"
				+" <wpt lat=\"39.921055008\" lon=\"3.054223107\">"
				+"  <name>Camping d'Arradon</name>"
				+"  <cmt>Le camping vous accueille du lundi au vendredi.</cmt>"
				+" </wpt>"
				+" <wpt lat=\"38.921055008\" lon=\"4.054223107\">"
				+"  <name>Restaurant du Coin</name>"
				+"  <cmt>Le resto sympathique du centre.</cmt>"
				+" </wpt>"
				+"</gpx>");
		//Write into filesystem
        FileOutputStream fos;
        try {
			fos = new FileOutputStream(xml);
			fos.write(writer.toString().getBytes());
	        fos.close();
	        
	        //Try to read
	        try {
	        	HashMap<String,NamedPoint> favorites = RWFile.readFavoritesGPX(xml);
	        	assertTrue(favorites != null);
				//Test size
				assertEquals(2, favorites.size());
				//Test points
				NamedPoint camping = favorites.get(RWFile.formatCoordinates(39.921055008, 3.054223107));
				NamedPoint resto = favorites.get(RWFile.formatCoordinates(38.921055008, 4.054223107));
				assertEquals(39.921055, camping.getPoint().getLatitude());
				assertEquals(3.054223, camping.getPoint().getLongitude());
				assertEquals(38.921055, resto.getPoint().getLatitude());
				assertEquals(4.054223, resto.getPoint().getLongitude());
				assertEquals("Camping d'Arradon", camping.getName());
				assertEquals("Restaurant du Coin", resto.getName());
				assertEquals("Le camping vous accueille du lundi au vendredi.", camping.getComment());
				assertEquals("Le resto sympathique du centre.", resto.getComment());
			} catch (XmlPullParserException e) {
				e.printStackTrace();
				fail("Parsing error");
			} catch (IOException e) {
				e.printStackTrace();
				fail("Read error");
			}
		} catch (IOException e) {
			e.printStackTrace();
			fail("Writing error");
		}
	}
	
	/**
	 * Test of the saveFavoritesGPX() method
	 */
	public void testSaveFavoritesGPX() {
		//Init objects
		double coordA = 2.256545;
		double coordB = 47.589632;
		String name1 = "Camping des Flots Rouges";
		String name2 = "Chez John";
		String comment1 = "Le camping de mes vacances";
		String comment2 = "La maison de chez John";
		NamedPoint p1 = new NamedPoint(new GeoPoint(coordA, coordB), name1, comment1, true);
		NamedPoint p2 = new NamedPoint(new GeoPoint(coordB, coordA), name2, comment2, true);
		NamedPoint[] fav = { p1, p2 };
		File xml = new File(getContext().getCacheDir().getPath()+File.separator+"favorites.gpx");
		
		//Launch method
		try {
			RWFile.saveFavoritesGPX(xml, fav);
			
			//Try to read
			HashMap<String,NamedPoint> resultH = RWFile.readFavoritesGPX(xml);
			
			//Test
			NamedPoint[] result = { resultH.get(RWFile.formatCoordinates(coordA, coordB)), resultH.get(RWFile.formatCoordinates(coordB, coordA)) }; 
			assertEquals(name1, result[0].getName());
			assertEquals(name2, result[1].getName());
			assertEquals(comment1, result[0].getComment());
			assertEquals(comment2, result[1].getComment());
			assertTrue(result[0].isFavorite());
			assertTrue(result[1].isFavorite());
			assertEquals(coordA, result[0].getPoint().getLatitude());
			assertEquals(coordB, result[0].getPoint().getLongitude());
			assertEquals(coordB, result[1].getPoint().getLatitude());
			assertEquals(coordA, result[1].getPoint().getLongitude());
		} catch(IOException e) {
			e.printStackTrace();
			fail("IOException");
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			fail("Parsing error");
		}
	}
	
	/**
	 * Test of the saveFavorites() method
	 */
	public void testSaveFavoritesPoi() {
		//Init POIs
		double coordA = 2.25654587;
		double coordB = 47.58963214;
		double coordC = -47.58963214;
		PointOfInterest p1 = new PointOfInterest(new GeoPoint(coordA, coordB), null, null, null, null,null, true);
		PointOfInterest p2 = new PointOfInterest(new GeoPoint(coordB, coordA), null, null, null, null,null, false);
		PointOfInterest p3 = new PointOfInterest(new GeoPoint(coordC, coordA), null, null, null, null,null, true);
		PointOfInterest[] pois = {p1,p2,p3};
		File xml = new File(getContext().getCacheDir().getPath()+File.separator+"favoritesPoi.xml");
		
		//Try to save
		try {
			RWFile.saveFavoritesPoi(xml, pois);
		} catch (NullPointerException e) {
			e.printStackTrace();
			fail("FileNotFound");
		} catch (IOException e) {
			e.printStackTrace();
			fail("IOException");
		}
		
		//Create other files
		//Write XML file for categories into cache dir
		File cat = new File(getContext().getCacheDir().getPath()+File.separator+"poi_categories_fr.xml");
		StringWriter writer = new StringWriter();
		writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+"<poi-categories>"
				+"<category id=\"hosting\" name=\"Hébergement\">"
				+" <type id=\"hostel\" name=\"Hôtel\" />"
				+" <type id=\"campsite\" name=\"Camping\" />"
				+"</category>"
				+"<category id=\"dining\" name=\"Restauration\">"
				+" <type id=\"restaurant\" name=\"Restaurant\" />"
				+"</category>"
				+"</poi-categories>");
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(cat);
			fos.write(writer.toString().getBytes());
	        fos.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			fail("FileNotFound for categories writing");
		} catch (IOException e) {
			e.printStackTrace();
			fail("IOException for categories writing");
		}
		
		//Create XML file for POIs
		File poi = new File(getContext().getCacheDir().getPath()+File.separator+"pois.gpx");
		StringWriter writerPoi = new StringWriter();
		writerPoi.write("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\" ?>"
				+"<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"byHand\" version=\"1.1\""
				+" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema­instance\""
				+" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1" 
				+" http://www.topografix.com/GPX/1/1/gpx.xsd\">"
				+" <wpt lat=\""+coordA+"\" lon=\""+coordB+"\">"
				+"  <name>Camping d'Arradon</name>"
				+"  <cmt>Le camping vous accueille du lundi au vendredi.</cmt>"
				+"  <link>http://www.camping-arradon.fr/</link>"
				+"  <type>campsite</type>"
				+" </wpt>"
				+" <wpt lat=\""+coordB+"\" lon=\""+coordA+"\">"
				+"  <name>Restaurant du Coin</name>"
				+"  <cmt>Le resto sympathique du centre.</cmt>"
				+"  <desc>1 Rue du Centre, 56000 Vannes. 0312345678</desc>"
				+"  <type>restaurant</type>"
				+" </wpt>"
				+" <wpt lat=\""+coordC+"\" lon=\""+coordA+"\">"
				+"  <name>Restaurant du Coin</name>"
				+"  <cmt>Le resto sympathique du centre.</cmt>"
				+"  <desc>1 Rue du Centre, 56000 Vannes. 0312345678</desc>"
				+"  <type>hostel</type>"
				+" </wpt>"
				+"</gpx>");
		try {
			fos = new FileOutputStream(poi);
			fos.write(writerPoi.toString().getBytes());
	        fos.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			fail("FileNotFound for POI writing");
		} catch (IOException e) {
			e.printStackTrace();
			fail("IOException for POI writing");
		}
		
		//Read written file
		/*try {
			PointOfInterestCategory[] result = RWFile.readPoiGPX(poi, cat, xml);
			//Test size
			assertEquals(2, result.length);
			assertEquals(2, result[0].getTypes().length);
			assertEquals(1, result[1].getTypes().length);
			assertEquals(1, result[0].getType("hostel").getPois().length);
			assertEquals(1, result[0].getType("campsite").getPois().length);
			assertEquals(1, result[1].getType("restaurant").getPois().length);
			//Test if favorites are correctly loaded
			assertEquals(true, result[0].getType("campsite").getPois()[0].isFavorite());
			assertEquals(false, result[1].getType("restaurant").getPois()[0].isFavorite());
			assertEquals(true, result[0].getType("hostel").getPois()[0].isFavorite());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail("FileNotFound when reading");
		} catch (IOException e) {
			e.printStackTrace();
			fail("IOException when reading");
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			fail("Parsing error when reading");
		}*/
	}
	
	/**
	 * Test of the decodeCoordinates() method
	 */
	public void testDecodeCoordinates() {
		//Test 1
		GeoPoint result1 = RWFile.decodeCoordinates("+044.123456-002.654789");
		assertNotNull(result1);
		assertEquals(44.123456, result1.getLatitude());
		assertEquals(-2.654789, result1.getLongitude());
		//Test 2
		GeoPoint result2 = RWFile.decodeCoordinates("-044.129000+123.654780");
		assertNotNull(result2);
		assertEquals(-44.129, result2.getLatitude());
		assertEquals(123.65478, result2.getLongitude());
	}
}
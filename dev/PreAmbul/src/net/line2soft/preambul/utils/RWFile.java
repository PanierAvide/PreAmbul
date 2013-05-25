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

package net.line2soft.preambul.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.line2soft.preambul.models.Area;
import net.line2soft.preambul.models.Coordinate;
import net.line2soft.preambul.models.Excursion;
import net.line2soft.preambul.models.Location;
import net.line2soft.preambul.models.Locomotion;
import net.line2soft.preambul.models.NamedPoint;
import net.line2soft.preambul.models.NavigationInstruction;
import net.line2soft.preambul.models.NavigationInstructionComparator;
import net.line2soft.preambul.models.PointOfInterest;
import net.line2soft.preambul.models.PointOfInterestCategory;
import net.line2soft.preambul.models.PointOfInterestType;

import org.mapsforge.android.maps.overlay.OverlayWay;
import org.mapsforge.core.GeoPoint;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Xml;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import net.line2soft.preambul.R;

/**
 * This class contains utilitary methods for reading and writing file.
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class RWFile {
// OTHER METHODS
	/**
	 * Deletes a directory and its content (a directory can't be deleted if it's not empty)
	 * @param path The directory to delete
	 * @return True if it was deleted
	 */
	public static boolean deleteDirectory(File path) {
		boolean result = false;
		if( path.exists() ) {
			File[] files = path.listFiles();
			if (files == null) {
				result = true;
			}
			for(int i=0; i<files.length; i++) {
				if(files[i].isDirectory()) {
					deleteDirectory(files[i]);
				}
				else {
					files[i].delete();
				}
			}
		    result = path.delete();
		}
		return result;
	}
	
	/**
	 * Reads a XML file and returns a {@link Location} hashmap.
	 * Example of XML file<br />
	 * <pre><?xml version="1.0" encoding="UTF-8"?>
	 * <locations>
	 * <location id="1"
	 * name="City"
	 * version="25"
	 * url="http://download.net/adress.zip"
	 * postalCode="12345"
	 * country="FR"
	 * boundaryBoxTopLeftX="0.0"
	 * boundaryBoxTopLeftY="0.0"
	 * boundaryBoxBottomRightX="0.0"
	 * boundaryBoxBottomRightY="0.0" />
	 * </locations></pre>
	 * @param xml The XML file to read
	 * @return The read location hashmap.
	 * @throws FileNotFoundException If the specified file doesn't exist.
	 * @throws IOException
	 * @throws XmlPullParserException If the specified file isn't a XML file.
	 */
	public static HashMap<Integer,Location> readLocationsXML(File xml) throws FileNotFoundException, IOException, XmlPullParserException {
		HashMap<Integer,Location> result = null;
		if(xml.exists() && xml.isFile()){
			if(xml.canRead()){
				//Create parser and set input stream
	            XmlPullParser parser = Xml.newPullParser();
	            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
	            parser.setInput(new FileInputStream(xml), null);
	            int eventType = parser.getEventType();
    			result=new HashMap<Integer,Location>();
    			//Parse XML
	            while(eventType != XmlPullParser.END_DOCUMENT){
	            	if(eventType==XmlPullParser.START_TAG && parser.getName().equals("location")){
            			int i=0;
            			int count=parser.getAttributeCount();
            			
            			//Current location attributes
            			int id=-1;
            			String name="";
            			String postalCode="";
            			String country="";
            			double topLeftX=0;
            			double topLeftY=0;
            			double botRightX=0;
            			double botRightY=0;
            			int version=-1;
            			URL url=null;
            			URL photoUrl=null;
            			
            			//Parse location attributes
            			while(i<count){
            				if(parser.getAttributeName(i).equals("id")){
            					id=Integer.parseInt(parser.getAttributeValue(i));	            					
            				}
            				else if(parser.getAttributeName(i).equals("name")){
            					name=parser.getAttributeValue(i);
            				}
            				else if(parser.getAttributeName(i).equals("version")){
            					version=Integer.parseInt(parser.getAttributeValue(i));	            					
            				}
            				else if(parser.getAttributeName(i).equals("url")){
            					url=new URL(parser.getAttributeValue(i));
            				}
            				else if(parser.getAttributeName(i).equals("postalCode")){
            					postalCode=parser.getAttributeValue(i);	            					
            				}
            				else if(parser.getAttributeName(i).equals("country")){
            					country=parser.getAttributeValue(i);	            					
            				}
            				else if(parser.getAttributeName(i).equals("boundaryBoxTopLeftX")){
            					topLeftX=Double.parseDouble(parser.getAttributeValue(i));	 	            					
            				}
            				else if(parser.getAttributeName(i).equals("boundaryBoxTopLeftY")){
            					topLeftY=Double.parseDouble(parser.getAttributeValue(i));	  	            					
            				}
            				else if(parser.getAttributeName(i).equals("boundaryBoxBottomRightX")){
            					botRightX=Double.parseDouble(parser.getAttributeValue(i));	  	            					
            				}
            				else if(parser.getAttributeName(i).equals("boundaryBoxBottomRightY")){
            					botRightY=Double.parseDouble(parser.getAttributeValue(i));	            					
            				}
            				else if(parser.getAttributeName(i).equals("photos") && parser.getAttributeValue(i).length() > 0){
            					photoUrl=new URL(parser.getAttributeValue(i));	            					
            				}
	            			if(id!=-1){
	            				String folder = xml.getAbsolutePath().substring(0, xml.getAbsolutePath().lastIndexOf(File.separator));
	            				Uri logo=Uri.fromFile(new File(folder+File.separator+"logos"+File.separator+id+".png"));
	            				if(!name.equals("")){
	            					result.put(id, new Location(id, name, postalCode, country, new Area(new Coordinate(topLeftX,topLeftY),new Coordinate(botRightX,botRightY)),
	            							logo, version, url, photoUrl));
	            				}
	            			}
	            			i++;
            			}	            			
	            	}
	            	eventType=parser.next();
	            }
			}else throw new IOException();
		}else throw new FileNotFoundException();
		return result;
	}
	
	/**
	 * Writes a XML file (on a single line) 
	 * @param xml The file to write
	 * @param locations The locations data
	 * @throws IOException If there was an error during writing.
	 */
	public static void writeLocationsXML(File xml, HashMap<Integer,Location> locations) throws IOException {
		ArrayList<Location> locationsList = new ArrayList<Location>(locations.values());
	    XmlSerializer serializer = Xml.newSerializer();
	    StringWriter writer = new StringWriter();
        serializer.setOutput(writer);
        //Create XML file
        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "locations");
        for (Location loc : locationsList){
            serializer.startTag("", "location");
            serializer.attribute("", "id", ""+loc.getId());
            serializer.attribute("", "name", loc.getName());
            serializer.attribute("", "version", ""+loc.getVersion());
            serializer.attribute("", "url", loc.getPackageUrl().toExternalForm());
            String photosUrl = (loc.getPhotoUrl() != null) ? loc.getPhotoUrl().toExternalForm() : "";
            serializer.attribute("", "photos", photosUrl);
            serializer.attribute("", "postalCode", loc.getPostalCode());
            serializer.attribute("", "country", loc.getCountry());
            Area area=loc.getBoundaryBox();
            Coordinate topLeftCorner = area.getTopLeftCorner();
            Coordinate bottomRightCorner = area.getBottomRightCorner();
            serializer.attribute("", "boundaryBoxTopLeftX", String.valueOf(topLeftCorner.getX()));
            serializer.attribute("", "boundaryBoxTopLeftY", String.valueOf(topLeftCorner.getY()));
            serializer.attribute("", "boundaryBoxBottomRightX", String.valueOf(bottomRightCorner.getX()));
            serializer.attribute("", "boundaryBoxBottomRightY", String.valueOf(bottomRightCorner.getY()));
            serializer.endTag("", "location");
        }
        serializer.endTag("", "locations");
        serializer.endDocument();
        //Write into filesystem
        FileOutputStream fos = new FileOutputStream(xml);
        fos.write(writer.toString().getBytes());
        //Close
        fos.close();
	}
	
	/**
	 * Checks if external storage is available for writing operations
	 * @return True if available
	 */
	public static boolean isExternalStorageWritable() {
		boolean result = false;
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        result = true;
	    }
	    return result;
	}

	/**
	 * Checks if external storage is available for reading operations
	 * @return
	 */
	public static boolean isExternalStorageReadable() {
		boolean result = false;
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        result = true;
	    }
	    return result;
	}
	
	/**
	 * Copies the render rules XML file into files dir.
	 * @param ctx An application context
	 */
	public static File copyRenderRulesIntoFiles(Context ctx) {
		File result = new File(ctx.getFilesDir()+File.separator+"map_style.xml");
		try {
			//Open streams
		    InputStream is = ctx.getResources().openRawResource(R.raw.map_style);
		    FileOutputStream fos = new FileOutputStream(result);
		    
		    //Read file
		    StringBuilder text = new StringBuilder();
		    Scanner sc = new Scanner(is);
		    while(sc.hasNextLine()) {
		    	text.append(sc.nextLine()+System.getProperty("line.separator"));
		    }
		    
		    //Write text
		    Writer out = new OutputStreamWriter(fos);
		    out.write(text.toString());
	
		    //Close streams
		    is.close();
		    out.close();
		    sc.close();
		    fos.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		return result;
	}
	
	/**
	 * Copies the render rules XML file into files dir.
	 * @param ctx An application context
	 */
	public static File copyPoiCategoriesXmlIntoFiles(Context ctx) {
		File result = new File(ctx.getFilesDir()+File.separator+"poi_categories.xml");
		try {
			//Open streams
		    InputStream is = ctx.getResources().openRawResource(R.raw.map_style);
		    FileOutputStream fos = new FileOutputStream(result);
		    
		    //Read file
		    StringBuilder text = new StringBuilder();
		    Scanner sc = new Scanner(is);
		    while(sc.hasNextLine()) {
		    	text.append(sc.nextLine()+System.getProperty("line.separator"));
		    }
		    
		    //Write text
		    Writer out = new OutputStreamWriter(fos);
		    out.write(text.toString());
	
		    //Close streams
		    is.close();
		    out.close();
		    sc.close();
		    fos.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		return result;
	}

	/**
	 * Reads a GPX file and returns a path.
	 * @param xml The file to read
	 * @return The read path
	 */
	public static OverlayWay readGPX(File xml) throws XmlPullParserException, IOException, FileNotFoundException {
		OverlayWay result = null;
		GeoPoint[][] points = null;
		if(xml.exists() && xml.isFile()){
			if(xml.canRead()){
				//Init attributes
				ArrayList<GeoPoint> currentTrkSeg = new ArrayList<GeoPoint>();
				ArrayList<GeoPoint[]> listTrkSeg = new ArrayList<GeoPoint[]>();
				//Create parser and set input stream
	            XmlPullParser parser = Xml.newPullParser();
	            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
	            parser.setInput(new FileInputStream(xml), null);
	            int eventType = parser.getEventType();
    			//Parse XML
	            while(eventType != XmlPullParser.END_DOCUMENT){
	            	//New segment
	            	if(eventType == XmlPullParser.START_TAG && parser.getName().equals("trkseg")) {
	            		currentTrkSeg = new ArrayList<GeoPoint>();
	            	}
	            	//New point
	            	else if(eventType == XmlPullParser.START_TAG && parser.getName().equals("trkpt")) {
	            		//Init lat and lon
	            		double lat = 0;
	            		double lon = 0;
	            		short latAndLon = 0;
            			int count=parser.getAttributeCount();
	            		
	            		//Parse attributes
            			for(int i=0; i < count; i++) {
            				if(parser.getAttributeName(i).equals("lat")){
            					lat = Double.parseDouble(parser.getAttributeValue(i));
            					latAndLon++;
            				}
            				if(parser.getAttributeName(i).equals("lon")){
            					lon = Double.parseDouble(parser.getAttributeValue(i));
            					latAndLon++;
            				}
            			}
            			
            			if(latAndLon == 2) {
            				currentTrkSeg.add(new GeoPoint(lat, lon));
            			}
	            	}
	            	//End of segment
	            	else if(eventType == XmlPullParser.END_TAG && parser.getName().equals("trkseg")) {
	            		//Create a Geopoint array and list it
	            		GeoPoint[] currentSeg = new GeoPoint[currentTrkSeg.size()];
	            		for(int i=0; i < currentSeg.length; i++) {
	            			currentSeg[i] = currentTrkSeg.get(i);
	            		}
	            		listTrkSeg.add(currentSeg);
	            	}
	            	eventType=parser.next();
	            }
	            //Create array
        		points = new GeoPoint[listTrkSeg.size()][];
        		for(int i=0; i < listTrkSeg.size(); i++) {
        			points[i] = listTrkSeg.get(i);
        		}
        		
        		//Create way
        		result = new OverlayWay(points);
			}else throw new IOException();
		}else throw new FileNotFoundException();
		return result;
	}
	
	/**
	 * Reads a GPX file and returns segments
	 * @param xml The file to read
	 * @return An array of read segments
	 */
	public static OverlayWay[] readGPXAsSegments(File xml) throws XmlPullParserException, IOException, FileNotFoundException {
		OverlayWay[] result = null;
		if(xml.exists() && xml.isFile()){
			if(xml.canRead()){
				//Init attributes
				ArrayList<OverlayWay> segmentsList = new ArrayList<OverlayWay>();
				ArrayList<GeoPoint> currentSegment = new ArrayList<GeoPoint>();
				//Create parser and set input stream
	            XmlPullParser parser = Xml.newPullParser();
	            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
	            parser.setInput(new FileInputStream(xml), null);
	            int eventType = parser.getEventType();
    			//Parse XML
	            while(eventType != XmlPullParser.END_DOCUMENT){
	            	//New point
	            	if(eventType == XmlPullParser.START_TAG && parser.getName().equals("trkpt")) {
	            		//Init lat and lon
	            		double lat = 0;
	            		double lon = 0;
	            		short latAndLon = 0;
            			int count=parser.getAttributeCount();
	            		
	            		//Parse attributes
            			for(int i=0; i < count; i++) {
            				if(parser.getAttributeName(i).equals("lat")){
            					lat = Double.parseDouble(parser.getAttributeValue(i));
            					latAndLon++;
            				}
            				if(parser.getAttributeName(i).equals("lon")){
            					lon = Double.parseDouble(parser.getAttributeValue(i));
            					latAndLon++;
            				}
            			}
            			
            			if(latAndLon == 2) {
            				currentSegment.add(new GeoPoint(lat, lon));
            			}
	            	}
	            	//New instruction
	            	else if(eventType == XmlPullParser.START_TAG && parser.getName().equals("instruction")) {
	            		//Save last segment (if it exists)
	            		GeoPoint[][] currentSeg = new GeoPoint[1][currentSegment.size()];
	            		for(int i=0; i < currentSeg[0].length; i++) {
	            			currentSeg[0][i] = currentSegment.get(i);
	            		}
	            		//Add it to the list
	            		segmentsList.add(new OverlayWay(currentSeg));
	            		//Reset current segment
	            		GeoPoint newPt = currentSegment.get(currentSegment.size() - 1);
	            		currentSegment.clear();
	            		currentSegment.add(newPt);
	            	}
	            	//End of segment
	            	else if(eventType == XmlPullParser.END_TAG && parser.getName().equals("trkseg")) {
	            		//Save last segment (if it exists)
	            		GeoPoint[][] currentSeg = new GeoPoint[1][currentSegment.size()];
	            		for(int i=0; i < currentSeg[0].length; i++) {
	            			currentSeg[0][i] = currentSegment.get(i);
	            		}
	            		//Add it to the list
	            		segmentsList.add(new OverlayWay(currentSeg));
	            		//Reset current segment
	            		GeoPoint newPt = currentSegment.get(currentSegment.size() - 1);
	            		currentSegment.clear();
	            		currentSegment.add(newPt);
	            	}
	            	eventType=parser.next();
	            }
	            //Create array
	            result = new OverlayWay[segmentsList.size()];
        		for(int i=0; i < result.length; i++) {
        			result[i] = segmentsList.get(i);
        		}
			}else throw new IOException();
		}else throw new FileNotFoundException();
		return result;
	}

	/**
	 * Reads a navigation XML (NAX) and returns the read instructions of navigation
	 * @param xml The XML to read
	 * @return The read instructions
	 */
	public static NavigationInstruction[] readNavigationXML(File xml, File gpx) throws XmlPullParserException, IOException, FileNotFoundException {
		ArrayList<NavigationInstruction> result = null;
		if(xml.exists() && xml.isFile()){
			if(xml.canRead()){
				OverlayWay[] segments = readGPXAsSegments(gpx);
				//Create parser and set input stream
	            XmlPullParser parser = Xml.newPullParser();
	            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
	            parser.setInput(new FileInputStream(xml), null);
	            int eventType = parser.getEventType();
    			result=new ArrayList<NavigationInstruction>();
    			
    			//Parse XML
	            while(eventType != XmlPullParser.END_DOCUMENT){
	            	if(eventType==XmlPullParser.START_TAG && parser.getName().equals("instruction")){
            			int i=0;
            			int count=parser.getAttributeCount();
            			
            			//Current excursion attributes
            			int id=-1;
            			String value="";
            			int time = -1;
            			
            			//Parse location attributes
            			while(i<count){
            				if(parser.getAttributeName(i).equals("id")){
            					id=Integer.parseInt(parser.getAttributeValue(i));
            				}
            				else if(parser.getAttributeName(i).equals("value")){
            					value=parser.getAttributeValue(i);
            				}
            				else if(parser.getAttributeName(i).equals("time")){
            					time=Integer.parseInt(parser.getAttributeValue(i));
            				}
	            			i++;
            			}
            			//Insert value into results
            			if(id > -1 && !value.equals("") && time >= 0) {
            				OverlayWay way = null;
            				if(id <= segments.length) { way = segments[id-1]; }
            				result.add(new NavigationInstruction(id, value, time, way));
            			}
	            	}
	            	eventType=parser.next();
	            }
			}else throw new IOException();
		}else throw new FileNotFoundException();
		
		//Convert arraylist to array if parsing worked
		NavigationInstruction[] resultArray = null;
		if(result != null) {
			//Sort instructions by ID
			Collections.sort(result, new NavigationInstructionComparator());
			Object[] sR = result.toArray();
			resultArray = Arrays.copyOf(sR, sR.length, NavigationInstruction[].class);
		}
		return resultArray;
	}
	
	/**
	 * Reads the excursions.xml file and loads the read excursions
	 * @param xml The file to read
	 * @param favorites The XML file which contains the list of favorites excursions
	 * @return The read excursions
	 */
	public static HashMap<Integer,Excursion> readExcursionsXML(Context ctx, File xml, File favorites) throws XmlPullParserException, IOException, FileNotFoundException {
		HashMap<Integer,Excursion> result = null;
		if(xml.exists() && xml.isFile()){
			if(xml.canRead()){
				//Read favorites excursions
				XmlPullParser parser;
				ArrayList<Integer> favoritesExcursionsId = new ArrayList<Integer>();
				if(favorites.exists() && favorites.isFile()){
					if(favorites.canRead()){
						//Create parser and set input stream
			            parser = Xml.newPullParser();
			            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			            parser.setInput(new FileInputStream(favorites), null);
			            int eventType = parser.getEventType();
		    			//Parse XML
			            while(eventType != XmlPullParser.END_DOCUMENT){
			            	if(eventType==XmlPullParser.START_TAG && parser.getName().equals("bookmark")){
		            			//Current attributes
		            			int id = 0;
		            			
		            			//Parse attributes
		            			int count=parser.getAttributeCount();
		            			for(int i=0; i < count; i++) {
		            				if(parser.getAttributeName(i).equals("id")){
		            					id=Integer.parseInt(parser.getAttributeValue(i));
		            				}
		            			}
		            			
		            			//Save values if correct
		            			if(id > 0) {
		            				favoritesExcursionsId.add(id);
		            			}
			            	}
			            	eventType=parser.next();
			            }
					}else throw new IOException();
				}
				
				//Read all excursions
				//Create parser and set input stream
	            parser = Xml.newPullParser();
	            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
	            parser.setInput(new FileInputStream(xml), null);
	            int eventType = parser.getEventType();
    			result=new HashMap<Integer,Excursion>();
    			//Parse XML
	            while(eventType != XmlPullParser.END_DOCUMENT){
	            	if(eventType==XmlPullParser.START_TAG && parser.getName().equals("excursion")){
            			int i=0;
            			int count=parser.getAttributeCount();
            			
            			//Current excursion attributes
            			int id=-1;
            			String name="";
            			ArrayList<Locomotion> locomotions = new ArrayList<Locomotion>();
            			int time = 0;
            			int difficulty = -1;
            			String description = "";
            			
            			//Parse location attributes
            			while(i<count){
            				if(parser.getAttributeName(i).equals("id")){
            					id=Integer.parseInt(parser.getAttributeValue(i));
            				}
            				else if(parser.getAttributeName(i).equals("name")){
            					name=parser.getAttributeValue(i);
            				}
            				else if(parser.getAttributeName(i).equals("locomotion")){
            					String locomotionString=parser.getAttributeValue(i);
            					//Parse values in locomotionString
            					String[] values = locomotionString.split(";");
            					for(String str : values) {
            						if(Locomotion.getLocomotion(ctx, str) != null) {
            							locomotions.add(Locomotion.getLocomotion(ctx, str));
            						} else {
            							locomotions.add(Locomotion.getLocomotion(ctx, "other"));
            						}
            					}
            				}
            				else if(parser.getAttributeName(i).equals("time")){
            					time=Integer.parseInt(parser.getAttributeValue(i));
            				}
            				else if(parser.getAttributeName(i).equals("difficulty")){
            					difficulty=Integer.parseInt(parser.getAttributeValue(i));
            				}
            				else if(parser.getAttributeName(i).equals("description")){
            					description=parser.getAttributeValue(i);	            					
            				}
	            			if(id!=-1 && !name.equals("") && locomotions.size() > 0 && time > 0 && difficulty >= 1 && !description.equals("")) {
	            				Locomotion[] locomotion = Arrays.copyOf(locomotions.toArray(), locomotions.size(), Locomotion[].class);
	            				boolean isFavorite = favoritesExcursionsId.contains(id);
	            				result.put(id, new Excursion(id, name, locomotion, time, difficulty, description, isFavorite));
	            			}
	            			i++;
            			}	            			
	            	}
	            	eventType=parser.next();
	            }
			}else throw new IOException();
		}else throw new FileNotFoundException();
		return result;
	}
	
	/**
	 * Reads the list of point of interests saved in the file system, and loads it categorized. 
	 * @param xml The file which contains the list of POI
	 * @param categories The file which contains the list of POI categories
	 * @param favorites The file which contains the list of favorites POI
	 * @return The list of POI, categorized.
	 * @throws IOException 
	 * @throws XmlPullParserException 
	 * @throws FileNotFoundException 
	 */
	public static HashMap<String, PointOfInterestCategory> readPoiGPX(File xml, Context ctx, int resourceId, File favorites, int idLocation) throws IOException, XmlPullParserException, FileNotFoundException {
		//Init vars
		ArrayList<String> favoritesId = new ArrayList<String>();
		HashMap<String, PointOfInterestCategory> result = readPoiCategoryXML(ctx, resourceId);
		XmlPullParser parser;
		
		//Map the ID of a category to its index in the array
		HashMap<String,PointOfInterestType> poiTypeIdToType = new HashMap<String,PointOfInterestType>();
		for(PointOfInterestCategory poic : result.values()) {
			for(PointOfInterestType poit : poic.getTypes()) {
				poiTypeIdToType.put(poit.getId(), poit);
			}
		}
		
		//Check if files exists and can be read
		if(xml.exists() && xml.isFile()){
			if(xml.canRead()){
				//Read the list of favorites POI
				if(favorites.exists() && favorites.isFile()){
					if(favorites.canRead()){
						//Create parser and set input stream
			            parser = Xml.newPullParser();
			            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			            parser.setInput(new FileInputStream(favorites), null);
			            int eventType = parser.getEventType();
		    			//Parse XML
			            while(eventType != XmlPullParser.END_DOCUMENT){
			            	if(eventType==XmlPullParser.START_TAG && parser.getName().equals("bookmark")){
		            			//Current attributes
		            			double lat = 0;
		            			double lon = 0;
		            			
		            			//Parse attributes
		            			int count=parser.getAttributeCount();
		            			for(int i=0; i < count; i++) {
		            				if(parser.getAttributeName(i).equals("lat")){
		            					lat=Double.parseDouble(parser.getAttributeValue(i));
		            				}
		            				else if(parser.getAttributeName(i).equals("lon")){
		            					lon=Double.parseDouble(parser.getAttributeValue(i));
		            				}
		            			}
		            			
		            			//Save values if correct
		            			if(lat != 0 && lon != 0) {
		            				favoritesId.add(formatCoordinates(lat, lon));
		            			}
			            	}
			            	eventType=parser.next();
			            }
					}else throw new IOException();
				}
				
				//Read the list of POI
				//Create parser and set input stream
	            parser = Xml.newPullParser();
	            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
	            parser.setInput(new FileInputStream(xml), "utf-8");
	            int eventType = parser.getEventType();
    			//Parse XML
	            double lat = 0; double lon = 0; String name = null; String desc = null;
        		String cmt = null; String type = null; URL link = null;
        		String lastName = "";
        		PointOfInterestType lastPoiT=null;
	            while(eventType != XmlPullParser.END_DOCUMENT){
	            	lastName = (parser.getName() != null) ? parser.getName() : lastName;
	            	//New point
	            	if(eventType == XmlPullParser.START_TAG && parser.getName().equals("wpt")) {
            			int count=parser.getAttributeCount();
	            		
	            		//Parse attributes
            			for(int i=0; i < count; i++) {
            				if(parser.getAttributeName(i).equals("lat")){
            					lat = Double.parseDouble(parser.getAttributeValue(i));
            				}
            				if(parser.getAttributeName(i).equals("lon")){
            					lon = Double.parseDouble(parser.getAttributeValue(i));
            				}
            			}
	            	}
	            	else if(eventType == XmlPullParser.TEXT && lastName.equals("name") && parser.getText().trim().length()>0) {
	            		name = parser.getText().trim();
	            	}
	            	else if(eventType == XmlPullParser.TEXT && lastName.equals("desc") && parser.getText().trim().length()>0) {
	            		desc = parser.getText().trim();
	            	}
	            	else if(eventType == XmlPullParser.TEXT && lastName.equals("cmt") && parser.getText().trim().length()>0) {
	            		cmt = parser.getText().trim();
	            	}
	            	else if(eventType == XmlPullParser.TEXT && lastName.equals("link") && parser.getText().trim().length()>0) {
	            		link = new URL(parser.getText().trim());
	            	}
	            	else if(eventType == XmlPullParser.TEXT && lastName.equals("type") && parser.getText().trim().length()>0) {
	            		type = parser.getText().trim();
	            	}
	            	//End of point
	            	else if(eventType == XmlPullParser.END_TAG && parser.getName().equals("wpt")) {
	            		if(name != null && name.length() > 0 && type != null && type.length() > 0 && lat != 0 && lon != 0) {
	            			//Create POI
	            			boolean isFavorite = favoritesId.contains(formatCoordinates(lat, lon));
		            		//Add to right type
		            		PointOfInterestType poit = lastPoiT = (poiTypeIdToType.get(type) != null) ? poiTypeIdToType.get(type) : poiTypeIdToType.get("other");
		            		PointOfInterest poi = new PointOfInterest(new GeoPoint(lat, lon), name, desc, cmt, link, lastPoiT, isFavorite);
		            		File soundFile=new File((Environment.getExternalStorageDirectory()).getAbsolutePath()+File.separator+"Android"+File.separator+"data"+File.separator+"net.line2soft.preambul"+File.separator+"files"
		            							+File.separator+idLocation+File.separator+"pois"+File.separator+poi.getId()+File.separator+"sound.mp3");
		            		if(soundFile.exists()){
		            			poi.setSound(soundFile);	
		            		}
		            		File photoFile=new File((Environment.getExternalStorageDirectory()).getAbsolutePath()+File.separator+"Android"+File.separator+"data"+File.separator+"net.line2soft.preambul"+File.separator+"files"
        							+File.separator+idLocation+File.separator+"pois"+File.separator+poi.getId()+File.separator+"picture.jpg");
			        		if(photoFile.exists()){
			        			
			        			Bitmap bmp=null;
			        		    // Get the dimensions for the view
			        		    Display display = (Display)((WindowManager)ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			        		    int targetW = display.getWidth();
			        		    int targetH = display.getHeight();
			        		  
			        		    // Get the dimensions of the bitmap
			        		    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			        		    bmOptions.inJustDecodeBounds = true;
			        		    BitmapFactory.decodeFile(photoFile.getAbsolutePath(), bmOptions);
			        		    int photoW = bmOptions.outWidth;
			        		    int photoH = bmOptions.outHeight;
			        		  
			        		    // Determine how much to scale down the image
			        		    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
			        		  
			        		    // Decode the image file into a Bitmap sized to fill the View
			        		    bmOptions.inJustDecodeBounds = false;
			        		    bmOptions.inSampleSize = scaleFactor;
			        		    bmOptions.inPurgeable = true;
			        		  
			        		    bmp = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), bmOptions);
			        			
			        			poi.setPhoto(bmp);	
			        		}
		            		poit.addPoint(poi);
		            		//Delete found values
		            		lat = 0; lon = 0; name = null; desc = null;
		            		cmt = null; type = null; link = null; lastName = "";
	            		}
	            	}
	            	eventType=parser.next();
	            }

			}else throw new IOException();
		}else throw new FileNotFoundException();
		
		return result;
	}
	
	/**
	 * Reads the list of POI categories from the specified XML file, and returns categories and types (but without any {@link PointOfInterest} object).
	 * @param ctx An application context
	 * @param ressourceId The ID of the XML file
	 * @return The list of PointOfInterestCategories
	 * @throws IOException Reading error
	 * @throws XmlPullParserException Parsing error
	 */
	public static HashMap<String, PointOfInterestCategory> readPoiCategoryXML(Context ctx, int ressourceId) throws XmlPullParserException, IOException {
		HashMap<String,PointOfInterestCategory> result = new HashMap<String,PointOfInterestCategory>();
		//Check if files exists and can be read
		try {
			//Create parser and set input stream
            XmlPullParser parser = ctx.getResources().getXml(ressourceId);
            int eventType = parser.getEventType();
			//Parse XML
            PointOfInterestCategory currentCat = null;
            Drawable icon = null;
            while(eventType != XmlPullParser.END_DOCUMENT){
            	//New category
            	if(eventType == XmlPullParser.START_TAG && parser.getName().equals("category")) {
        			int count=parser.getAttributeCount();
        			String id = null;
        			String name = null;
            		
            		//Parse attributes
        			for(int i=0; i < count; i++) {
        				if(parser.getAttributeName(i).equals("id")){
        					id = parser.getAttributeValue(i);
        				}
        				if(parser.getAttributeName(i).equals("name")){
        					name = parser.getAttributeValue(i);
        				}
        			}
        			
        			//Create category
        			if(name != null && name.length() > 0 && id != null && id.length() > 0) {
        				int iconId = ctx.getResources().getIdentifier("marker_poi_" + id, "drawable", ctx.getPackageName());
        				icon = null;
        				if(iconId != 0) { icon = ctx.getResources().getDrawable(iconId); }
        				currentCat = new PointOfInterestCategory(id, name, icon);
        				result.put(id, currentCat);
        			}
            	}
            	else if(eventType == XmlPullParser.START_TAG && parser.getName().equals("type")) {
            		int count=parser.getAttributeCount();
        			String id = null;
        			String name = null;
            		
            		//Parse attributes
        			for(int i=0; i < count; i++) {
        				if(parser.getAttributeName(i).equals("id")){
        					id = parser.getAttributeValue(i);
        				}
        				if(parser.getAttributeName(i).equals("name")){
        					name = parser.getAttributeValue(i);
        				}
        			}
        			
        			//Create type
        			if(name != null && name.length() > 0 && id != null && id.length() > 0) {
        				int iconId = ctx.getResources().getIdentifier("marker_poi_" + id, "drawable", ctx.getPackageName());
        				icon = null;
        				if(iconId != 0) { icon = ctx.getResources().getDrawable(iconId); }
        				PointOfInterestType currentType = new PointOfInterestType(id, name, icon, currentCat);
        				currentCat.addType(currentType);
        			}
            	}
            	eventType=parser.next();
            }
		} catch(Resources.NotFoundException e) {
			throw new FileNotFoundException(e.getMessage());
		}
		if(result.size() == 0) { result = null; }
		return result;
	}
	
	/**
	 * Reads the XML file which contains the list of favorites points, defined by user.
	 * @param xml The file to read
	 * @return An array which contains read points.
	 * @throws XmlPullParserException 
	 * @throws IOException
	 */
	public static HashMap<String,NamedPoint> readFavoritesGPX(File xml) throws XmlPullParserException, IOException {
		HashMap<String,NamedPoint> result = new HashMap<String,NamedPoint>();
		if(xml.exists() && xml.isFile()){
			if(xml.canRead()){
				//Create parser and set input stream
	            XmlPullParser parser = Xml.newPullParser();
	            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
	            parser.setInput(new FileInputStream(xml), null);
	            int eventType = parser.getEventType();
	   			//Parse XML
	            double lat = 0; double lon = 0; String name = null; String cmt = null;
        		String lastName = "";
	            while(eventType != XmlPullParser.END_DOCUMENT){
	            	lastName = (parser.getName() != null) ? parser.getName() : lastName;
	            	//New point
	            	if(eventType == XmlPullParser.START_TAG && parser.getName().equals("wpt")) {
            			int count=parser.getAttributeCount();
	            		
	            		//Parse attributes
            			for(int i=0; i < count; i++) {
            				if(parser.getAttributeName(i).equals("lat")){
            					lat = Double.parseDouble(parser.getAttributeValue(i));
            				}
            				if(parser.getAttributeName(i).equals("lon")){
            					lon = Double.parseDouble(parser.getAttributeValue(i));
            				}
            			}
	            	}
	            	else if(eventType == XmlPullParser.TEXT && lastName.equals("name") && parser.getText() != null && parser.getText().trim().length() > 0) {
	            		name = parser.getText();
	            	}
	            	else if(eventType == XmlPullParser.TEXT && lastName.equals("cmt") && parser.getText() != null && parser.getText().trim().length() > 0) {
	            		cmt = parser.getText();
	            	}
	            	//End of point
	            	else if(eventType == XmlPullParser.END_TAG && parser.getName().equals("wpt")) {
	            		if(name != null && name.trim().length() > 0 && lat != 0 && lon != 0) {
	            			result.put(formatCoordinates(lat, lon), new NamedPoint(new GeoPoint(lat, lon), name.trim(), cmt, true));
		            		//Delete found values
		            		lat = 0; lon = 0; name = null; cmt = null; lastName = "";
	            		}
	            	}
	            	eventType=parser.next();
	            }
			}else throw new IOException();
		}
		
		return result;
	}
	
	/**
	 * Saves user's favorites points into filesystem.
	 * @param xml The XML to write into
	 * @param favorites The array which contains user's favorites
	 * @throws IOException Writing error
	 */
	public static void saveFavoritesGPX(File xml, NamedPoint[] favorites) throws IOException {
	    XmlSerializer serializer = Xml.newSerializer();
	    StringWriter writer = new StringWriter();
        serializer.setOutput(writer);

        if(!xml.exists()){
        	xml.getParentFile().mkdirs();
        }
        
        //Create XML file
        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "gpx");
        serializer.attribute("", "creator", "PreAmbul");
        for (NamedPoint np : favorites){
        	//wpt
            serializer.startTag("", "wpt");
            serializer.attribute("", "lat", Double.toString(np.getPoint().getLatitude()));
            serializer.attribute("", "lon", Double.toString(np.getPoint().getLongitude()));
            //name
            serializer.startTag("", "name");
            serializer.text(np.getName());
            serializer.endTag("", "name");
            //cmt
            if(np.getComment() != null) {
	            serializer.startTag("", "cmt");
	            serializer.text(np.getComment());
	            serializer.endTag("", "cmt");
            }
            //End wpt
            serializer.endTag("", "wpt");
        }
        serializer.endTag("", "gpx");
        serializer.endDocument();
        //Write into filesystem
        FileOutputStream fos = new FileOutputStream(xml);
        fos.write(writer.toString().getBytes());
        //Close
        fos.close();
	}
	
	/**
	 * Saves the favorites {@link PointOfInterest} into filesystem.
	 * @param xml The XML file to write into
	 * @param allPois The set of POIs, not only favorites
	 * @throws IOException Writing error
	 */
	public static void saveFavoritesPoi(File xml, PointOfInterest[] allPois) throws IOException, NullPointerException {
		if(!xml.exists()){
			xml.getParentFile().mkdirs();
		}
		//Init serializer
	    XmlSerializer serializer = Xml.newSerializer();
	    StringWriter writer = new StringWriter();
        serializer.setOutput(writer);
        
        //Create XML file
        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "poi-bookmarks");
        for (PointOfInterest poi : allPois){
        	if(poi.isFavorite()) {
	            serializer.startTag("", "bookmark");
	            serializer.attribute("", "lat", Double.toString(poi.getPoint().getLatitude()));
	            serializer.attribute("", "lon", Double.toString(poi.getPoint().getLongitude()));
	            serializer.endTag("", "bookmark");
        	}
        }
        serializer.endTag("", "poi-bookmarks");
        serializer.endDocument();
        //Write into filesystem
        FileOutputStream fos = new FileOutputStream(xml);
        fos.write(writer.toString().getBytes());
        //Close
        fos.close();
	}
	
	/**
	 * Saves favorites excursions into filesystem.
	 * @param xml The XML file to write into
	 * @param allExcursions The array which contains all the excursions, favorites and not favorites.
	 * @throws IOException Writing error
	 */
	public static void saveFavoritesExcursions(File xml, Excursion[] allExcursions) throws IOException {
		//Init serializer
	    XmlSerializer serializer = Xml.newSerializer();
	    StringWriter writer = new StringWriter();
        serializer.setOutput(writer);
        
        //Create XML file
        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "excursions-bookmarks");
        for (Excursion exc : allExcursions){
        	if(exc.isFavorite()) {
	            serializer.startTag("", "bookmark");
	            serializer.attribute("", "id", Integer.toString(exc.getId()));
	            serializer.endTag("", "bookmark");
        	}
        }
        serializer.endTag("", "excursions-bookmarks");
        serializer.endDocument();
        //Write into filesystem
        if(!xml.exists()){
        	xml.getParentFile().mkdirs();
        }
        FileOutputStream fos = new FileOutputStream(xml);
        fos.write(writer.toString().getBytes());
        //Close
        fos.close();
	}
	
	/**
	 * Formats coordinates to create a string representation.
	 * For example, a point which has for latitude 47.123456789 and for longitude -2.123 will have this representation : "+047.123457-002.123000".
	 * When a coordinate is longer than the representation, its value is rounded.
	 * @param lat The latitude
	 * @param lon The longitude
	 * @return The string representation, latitude followed by longitude, with a 6 digits precision after dot.
	 */
	public static String formatCoordinates(double lat, double lon) {
		DecimalFormatSymbols decimalSymbol = new DecimalFormatSymbols(Locale.getDefault());
		decimalSymbol.setDecimalSeparator('.');
		DecimalFormat f = new DecimalFormat("000.000000");
		f.setRoundingMode(RoundingMode.DOWN);
		f.setDecimalFormatSymbols(decimalSymbol);
		String latStr = f.format(lat);
		String lonStr = f.format(lon);
		//Add a + if needed
		if(lat >= 0) { latStr = "+" + latStr; }
		if(lon >= 0) { lonStr = "+" + lonStr; }
		return latStr+lonStr;
	}
	
	/**
	 * Calculate the size of the bitmap for bitmapfactories
	 * @param options
	 * @param reqWidth required width
	 * @param reqHeight required width
	 * @return the computed size
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	
	        // Calculate ratios of height and width to requested height and width
	        final int heightRatio = Math.round((float) height / (float) reqHeight);
	        final int widthRatio = Math.round((float) width / (float) reqWidth);
	
	        // Choose the smallest ratio as inSampleSize value, this will guarantee
	        // a final image with both dimensions larger than or equal to the
	        // requested height and width.
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }
	
	    return inSampleSize;
	}
	
	/**
	 * Unbind a drawable
	 * @param view The view which drawables have to be unbind
	 */
	public static void unbindDrawables(View view) {
	    if (view.getBackground() != null) {
	        view.getBackground().setCallback(null);
	    }
	    if (view instanceof ViewGroup) {
	        for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
	            unbindDrawables(((ViewGroup) view).getChildAt(i));
	        }
	        ((ViewGroup) view).removeAllViews();
	    }
	}
	
	/**
	 * Unzip a zip archive
	 * @param zipFile The file to unzip
	 * @param destinationFolder The folder where the zip content will be saved
	 * @return True if job is done
	 */
	public static boolean unzip(File zipFile, File destinationFolder) {
	     InputStream is;
	     ZipInputStream zis;
	     boolean result = true;
	     try {
	         String filename;
	         is = new FileInputStream(zipFile.getPath());
	         zis = new ZipInputStream(new BufferedInputStream(is));          
	         ZipEntry ze;
	         byte[] buffer = new byte[1024];
	         int count;

	         while ((ze = zis.getNextEntry()) != null) {
	             filename = ze.getName();

	             // Need to create directories if not exists, or
	             // it will generate an Exception...
	             if (ze.isDirectory()) {
	                File fmd = new File(destinationFolder.getPath() + File.separator + filename);
	                fmd.mkdirs();
	                continue;
	             }

	             FileOutputStream fout = new FileOutputStream(destinationFolder.getPath() + File.separator + filename);

	             while ((count = zis.read(buffer)) != -1) {
	                 fout.write(buffer, 0, count);             
	             }

	             fout.close();               
	             zis.closeEntry();
	         }

	         zis.close();
	     } catch(IOException e) {
	         e.printStackTrace();
	         result = false;
	     }
	     return result;
	}

	/**
	 * Decode a formated string which contains Coordinates
	 * @param formated The formated string
	 * @return The GeoPoint with the decode coordinates
	 */
	public static GeoPoint decodeCoordinates(String formated) {
		GeoPoint result = null;
		if(formated.length() == 22) {
			try {
				double lat = Double.parseDouble(formated.substring(0, 11));
				double lon = Double.parseDouble(formated.substring(11, 22));
				result = new GeoPoint(lat,lon);
			} catch(NumberFormatException e) {e.printStackTrace();}
		}
		return result;
	}
	
	/**
	 * 
	 */
	public static Bitmap readImage(File image, int widthPixels, int heightPixels, Context ctx){
		BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeResource(ctx.getResources(), R.drawable.back_excursion_info, options);
	    
	    // Calculate inSampleSize
	    options.inSampleSize = RWFile.calculateInSampleSize(options, widthPixels, heightPixels);
	    options.inPurgeable = true;
	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    Bitmap result=BitmapFactory.decodeFile(image.getAbsolutePath(), options);
		return result;
	}
	public static Bitmap readImageResource(int resource, int widthPixels, int heightPixels, Context ctx){
		BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeResource(ctx.getResources(), R.drawable.back_excursion_info, options);
	    
	    // Calculate inSampleSize
	    options.inSampleSize = RWFile.calculateInSampleSize(options, widthPixels, heightPixels);
	    options.inPurgeable = true;

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    Bitmap result=BitmapFactory.decodeResource(ctx.getResources(), resource, options);
		return result;
		
	}
	public static Bitmap readImageBackground(int resource, Context ctx){
		
		DisplayMetrics metrics = new DisplayMetrics();
		((Activity)ctx).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
	    return readImageResource(resource, metrics.widthPixels, metrics.heightPixels, ctx);
	}
	 
}
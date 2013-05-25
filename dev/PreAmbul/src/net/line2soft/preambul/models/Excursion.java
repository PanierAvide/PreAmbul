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

package net.line2soft.preambul.models;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import net.line2soft.preambul.controllers.MapController;
import net.line2soft.preambul.utils.RWFile;

import org.mapsforge.android.maps.overlay.OverlayWay;
import org.mapsforge.core.GeoPoint;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.preference.PreferenceManager;


/**
 * An excursion is a trip in a location.
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class Excursion{
// ATTRIBUTES
	//Difficulties
	/** Difficulty level: the simplest. A trip on a flat ground **/
	public static final int DIFFICULTY_NONE = 1;
	/** Difficulty level: easy. An excursion for people without experience **/
	public static final int DIFFICULTY_EASY = 2;
	/** Difficulty level: medium. An excursion for people with a bit of experience. The ground can be hilly. **/
	public static final int DIFFICULTY_MEDIUM = 3;
	/** Difficulty level: hard. An excursion in medium mountain. For experimented hikers. **/
	public static final int DIFFICULTY_HARD = 4;
	/** Difficulty level: expert. An excursion in high mountain. For very experimented hikers. **/
	public static final int DIFFICULTY_EXPERT = 5;
	
	//Object attributes
	/** The ID of the excursion **/
	private int id;
	/** The name of the excursion **/
	private String name;
	/** The possible locomotion for the excursion **/
	private Locomotion[] locomotions;
	/** The time needed to do this excursion **/
	private int time;
	/** The difficulty level of the excursion **/
	private int difficulty;
	/** The description of the excursion **/
	private String description;
	/** The surrounding points of interests **/
	private PointOfInterest[] surroundingPois;
	/** The path of the excursion **/
	private OverlayWay path;
	/** The instructions of navigation of the excursion **/
	private NavigationInstruction[] instructions;
	/** The length of the excursion (in meters) **/
	private double length = -1.0;
	/** Is this excursion favorite **/
	private boolean isFavorite;
	/** Last distance used to find surrounding pois **/
	private int lastDistance = 0;
	
// CONSTRUCTOR
	/**
	 * Class constructor
	 * @param id The ID of the excursion
	 * @param name The name of the excursion
	 * @param locomotion The medium of locomotion of the excursion
	 * @param time The time needed to do the excursion
	 * @param difficulty The difficulty level of the excursion
	 * @param description The description of the excursion
	 * @param favorite Is this excursion favorite
	 */
	public Excursion(int id, String name, Locomotion[] locomotions, int time, int difficulty, String description, boolean favorite) {
		this.id = id;
		this.name = name;
		this.locomotions = locomotions;
		this.time = time;
		this.difficulty = difficulty;
		this.description = description;
		this.isFavorite = favorite;
	}
	
	/**
	 * Class constructor
	 * @param id The ID of the excursion
	 * @param name The name of the excursion
	 * @param locomotion The medium of locomotion of the excursion
	 * @param time The time needed to do the excursion
	 * @param difficulty The difficulty level of the excursion
	 * @param description The description of the excursion
	 */
	public Excursion(int id, String name, Locomotion[] locomotions, int time, int difficulty, String description) {
		this(id, name, locomotions, time, difficulty, description, false);
	}

// ACCESSORS
	/**
	 * Returns the ID
	 * @return The ID
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Returns the name
	 * @return The name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the locomotion
	 * @return The locomotion
	 */
	public Locomotion[] getLocomotions() {
		return locomotions;
	}
	
	/**
	 * Returns the time of the excursion
	 * @return The time
	 */
	public int getTime() {
		return time;
	}
	
	/**
	 * Returns the difficulty
	 * @return The difficulty
	 */
	public int getDifficulty() {
		return difficulty;
	}
	
	/**
	 * Returns the description
	 * @return The description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Returns the instructions
	 * @return The instructions (as an array)
	 * @throws IOException 
	 * @throws XmlPullParserException 
	 * @throws FileNotFoundException 
	 */
	public NavigationInstruction[] getInstructions() throws FileNotFoundException, XmlPullParserException, IOException {
		//Load instructions if needed
		if(instructions==null) {
			loadInstructions();
		}
		return instructions;
	}
	
	/**
	 * Returns the path of the excursion
	 * @return The path
	 */
	public OverlayWay getPath() {
		//Load path if needed
		if(path==null) {
			loadPath();
		}
		return path;
	}
	
	/**
	 * Returns the points of interest which are next to the path
	 * @return The surrounding POIs
	 */
	public PointOfInterest[] getSurroundingPois(Context context) {
		String distanceStr = PreferenceManager.getDefaultSharedPreferences(context).getString("prefs_item_excursions_distance_poi", "50");
		int distance = Integer.parseInt(distanceStr);
		if(surroundingPois==null || distance != lastDistance) {
			lastDistance = distance;
			findSurroundingPois(context, distance);
		}
		return surroundingPois;
	}
	
	/**
	 * Returns the length of the path
	 * @return The length
	 */
	public double getLength() {
		if(length == -1.0) {
			calculateLength();
		}
		return length;
	}
	
	/**
	 * Is this excursion favorite
	 * @return True if it's favorite, false else.
	 */
	public boolean isFavorite() {
		return isFavorite;
	}
	
// MODIFIERS
	/**
	 * Set the path of the excursion
	 * @param newPath The new path
	 */
	public void setPath(OverlayWay newPath) {
		path = newPath;
	}
	
	/**
	 * Set if the excursion is favorite.
	 * @param f The new value
	 */
	public void setFavorite(boolean f) {
		isFavorite = f;
	}
	
// OTHER METHODS
	/**
	 * This method calls a XML reader to get the path of the excursion.
	 */
	private void loadPath() {
		File xml = new File(MapController.getInstance().getContext().getFilesDir().getPath()+File.separator+"locations"+File.separator+MapController.getInstance().getCurrentLocation().getId()+File.separator+"excursions"+File.separator+id+".gpx");
		try {
			path = RWFile.readGPX(xml);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method calls a XML reader to get the instructions of navigation
	 * @throws IOException 
	 * @throws XmlPullParserException 
	 * @throws FileNotFoundException 
	 */
	private void loadInstructions() throws FileNotFoundException, XmlPullParserException, IOException {
		File xml = new File(MapController.getInstance().getContext().getFilesDir().getPath()+File.separator+"locations"+File.separator+MapController.getInstance().getCurrentLocation().getId()+File.separator+"excursions"+File.separator+"fr"+File.separator+id+".nax");
		File gpx = new File(MapController.getInstance().getContext().getFilesDir().getPath()+File.separator+"locations"+File.separator+MapController.getInstance().getCurrentLocation().getId()+File.separator+"excursions"+File.separator+id+".gpx");
		instructions = RWFile.readNavigationXML(xml, gpx);
	}
	
	/**
	 * This method searches the POIs next to the path.
	 * @param distance The distance in meters
	 */
	private void findSurroundingPois(Context context, int distance) {
		PointOfInterest[] pois;
		try {
			pois = MapController.getInstance().getCurrentLocation().getPOIs(context);
		
			ArrayList<PointOfInterest> validPois = new ArrayList<PointOfInterest>();
			GeoPoint[][] pathPoints = path.getWayNodes();
			//Launch search
			for(int i=0; i < pois.length; i++) {
				boolean isSurrounding = false;
				int j = 0;
				int k = 0;
				while(j < pathPoints.length && !isSurrounding) {
					while(k < pathPoints[j].length-1 && !isSurrounding) {
						isSurrounding = isPointWithinDistanceToLineSegment(pathPoints[j][k], pathPoints[j][k+1], pois[i].getPoint(), distance);
						if(isSurrounding) { validPois.add(pois[i]); }
						k++;
					}
					j++;
				}
			}		
			//Put valid POIs into an array
			surroundingPois = new PointOfInterest[validPois.size()];
			
			for(int l = 0; l < surroundingPois.length; l++) {
				surroundingPois[l] = validPois.get(l);
			}
		}catch (Exception e){e.printStackTrace();} 
	}
	
	/**
	 * Get the distance between to geodesic points
	 * @param segmentStart The first point
	 * @param segmentEnd The second point
	 * @return The distance between the two points, in meters
	 */
	private double getDistanceBetweenTwoPoints(GeoPoint segmentStart, GeoPoint segmentEnd) {
		double earthRadius = 6363;
	    double dLat = Math.toRadians(segmentEnd.getLatitude()-segmentStart.getLatitude());
	    double dLng = Math.toRadians(segmentEnd.getLongitude()-segmentStart.getLongitude());
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(segmentStart.getLatitude())) * Math.cos(Math.toRadians(segmentEnd.getLatitude())) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;
	    return dist * 1000;
    }

	/**
	 * Checks if the point "test" is separated from segment of a distance less than testDistance
	 * @param segmentStart The first point of the segment
	 * @param segmentEnd The second point of the segment
	 * @param testPoint The point to test
	 * @param testDistance The maximal distance
	 * @return True if the distance is less than testDistance, false else
	 */
    private boolean isPointWithinDistanceToLineSegment(GeoPoint segmentStart, GeoPoint segmentEnd, GeoPoint testPoint, double testDistance) {
    	boolean result;
        if (getDistanceBetweenTwoPoints(segmentStart,testPoint) <= testDistance || getDistanceBetweenTwoPoints(segmentEnd,testPoint) <= testDistance) {
            result = true;
        }
        else {
	        //Side of a triangle
	        double baseLength = getDistanceBetweenTwoPoints(segmentStart, segmentEnd);
	        double startTest = getDistanceBetweenTwoPoints(segmentStart, testPoint);
	        double endTest = getDistanceBetweenTwoPoints(testPoint, segmentEnd);
	        //Area
	        double term1 = Math.pow((Math.pow(baseLength, 2) + Math.pow(startTest, 2) + Math.pow(endTest, 2)), 2);
	        double term2 = 2 * (Math.pow(baseLength, 4) + Math.pow(startTest, 4) + Math.pow(endTest, 4));
	        double area = .25 * Math.sqrt(term1 - term2);
	        //height of the triangle
	        double height = 2 * area / baseLength;
	        //Result
	        result = baseLength >= endTest && baseLength >= startTest && height <= testDistance;
        }
        return result;
    }
    
    /**
     * This method calculates length of the path
     */
    private void calculateLength() {
    	GeoPoint[][] nodes = getPath().getWayNodes();
    	length = 0.0;
    	for(int i=0; i < nodes.length; i++) {
    		for(int j=0; j < nodes[i].length -1; j++) {
    			float[] results = new float[3];
    			android.location.Location.distanceBetween(nodes[i][j].getLatitude(), nodes[i][j].getLongitude(), nodes[i][j+1].getLatitude(), nodes[i][j+1].getLongitude(), results);
    			length += results[0];
    		}
    	}
    }
}
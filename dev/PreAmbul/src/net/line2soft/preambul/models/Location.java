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
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import net.line2soft.preambul.controllers.MapController;
import net.line2soft.preambul.utils.RWFile;

import org.xmlpull.v1.XmlPullParserException;

import net.line2soft.preambul.R;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

/**
 * This class represents a location.
 * It contains the necessary informations (name, logo, postal code...).
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class Location {
// ATTRIBUTES
	/** Location's ID: is unique and doesn't change when a location is updated **/
	private int id;
	/** Location's name **/
	private String name;
	/** Location's logo **/
	private Uri logo;
	/** Location's version of package **/
	private int version;
	/** Location's URL **/
	private URL packageUrl;
	/** Photos URL **/
	private URL photoUrl;
	/** Location's postal code **/
	private String postalCode;
	/** Location's country **/
	private String country;
	/** Location's Boundary Box **/
	private Area boundaryBox;
	/** Location's points of interest **/
	private HashMap<String,PointOfInterestCategory> poiCategories;
	/** Location's favorites **/
	private HashMap<String, NamedPoint> namedPointFavorites;
	/** Location's excursions **/
	private HashMap<Integer,Excursion> excursions;
	
// CONSTRUCTOR
	/**
	 * Class constructor
	 * @param id The location's ID (unique, ID in the server list)
	 * @param name The location's name
	 * @param postalCode The location's postal code (if there is any)
	 * @param country The location's country in the <a href="https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2">ISO 3166-1 alpha-2</a> code (for example, the code for France is <b>FR</b>)
	 * @param zone The location's {@link Area}
	 * @param logo The location's logo's {@link URI}
	 * @param version The version of this location (installed or available, depends of the use of this object)
	 * @param packageUrl The download link for this version of this location
	 */
	public Location(int id, String name, String postalCode, String country, Area zone, Uri logo, int version, URL packageUrl, URL photoURL) {
		this.id = id;
		this.name = name;
		this.logo = logo;
		this.version = version;
		this.boundaryBox=zone;
		this.packageUrl = packageUrl;
		this.photoUrl = photoURL;
		this.postalCode = postalCode;
		this.country = country;
	}

// ACCESSORS
	/**
	 * Get the ID of the selected location, it is unique
	 * @return id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Get the name of the selected location
	 * @return name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the version of the selected location
	 * @return version
	 */
	public int getVersion() {
		return version;
	}
	
	/**
	 * Get the logo of the selected location
	 * @return logo
	 */
	public Uri getLogo() {
		return logo;
	}

	/**
	 * Get the URL of the selected location
	 * @return The URL
	 */
	public URL getPackageUrl() {
		return packageUrl;
	}
	/**
	 * Get the URL of the selected locations photos
	 * @return The URL
	 */
	public URL getPhotoUrl() {
		return photoUrl;
	}
	
	/**
	 * Get the boundary box of the selected location
	 * @return boundaryBox
	 */
	public Area getBoundaryBox() {
		return boundaryBox;
	}

	/**
	 * Get the postal code of the selected location
	 * @return postal code
	 */
	public String getPostalCode() {
		return postalCode;
	}
	
	/**
	 * Get the country of the selected location
	 * The location's country in the <a href="https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2">ISO 3166-1 alpha-2</a> code (for example, the code for France is <b>FR</b>)
	 * @return country
	 */
	public String getCountry() {
		return country;
	}
	
	/**
	 * returns all the favorites POI and user created points
	 * @return Hashmap containing the namedPoints favorites
	 * @throws IOException 
	 */
	public HashMap<String, NamedPoint> getNamedFavorites(Context ctx) throws IOException {
		if(namedPointFavorites == null) {
			loadNamedFavorites(ctx);
		}
		return namedPointFavorites;
	}
	
	/**
	 * Loads the user's favorites
	 * @param ctx An application context
	 * @throws IOException
	 */
	public void loadNamedFavorites(Context ctx) throws IOException {
		try {
			namedPointFavorites = RWFile.readFavoritesGPX(new File(ctx.getFilesDir().getPath()+File.separator+"locations"+File.separator+this.id+File.separator+"bookmarks"+File.separator+"favorites.gpx"));
		} catch (XmlPullParserException e) {
			throw new IOException(e.getMessage());
		}
	}
	

	/**
	 * Get the all the favorites
	 * @return The points of interest
	 * @throws IOException 
	 */
	public HashMap<String, NamedPoint> getFavorites(Activity ctx) throws IOException {
		HashMap<String, NamedPoint> coll;
		/*StackTraceElement[] stacktrace1=new StackTraceElement[0];
		StackTraceElement[] stacktrace2=new StackTraceElement[0];*/
		try{
			if(this.namedPointFavorites==null) {
				loadNamedFavorites(ctx);
			}
		}catch(Exception e){
			/*stacktrace1=e.getStackTrace();*/
		}finally{			
			coll = (namedPointFavorites != null) ? (HashMap<String, NamedPoint>)(namedPointFavorites.clone()) : new HashMap<String,NamedPoint>();
			
			PointOfInterest[] pois;
			try {
				pois = MapController.getInstance().getCurrentLocation().getPOIs(ctx);
				for(int i=0;i<pois.length;i++){
					if(pois[i].isFavorite()){
						coll.put(pois[i].getId(), pois[i]);
					}
				}
			} catch (XmlPullParserException e) {
				/*stacktrace2=e.getStackTrace();*/
			}
		}
		if(coll==null /*|| stacktrace1!=stacktrace2*/){
			/*StackTraceElement[] totalStackTrace = new StackTraceElement[stacktrace1.length + stacktrace2.length];
			System.arraycopy(stacktrace1, 0, stacktrace2, 0, stacktrace1.length);
			System.arraycopy(stacktrace2, 0, stacktrace2, stacktrace1.length, stacktrace2.length);
			IOException e = ;
			e.setStackTrace(totalStackTrace);*/
			throw new IOException();
		}
		return coll;
	}

	/**
	 * Get the favorite with the given id
	 * @return The points of interest
	 * @throws IOException 
	 */
	public NamedPoint getFavorite(Activity ctx, String key) throws IOException {
		HashMap<String, NamedPoint> coll=getFavorites(ctx);
		return coll.get(key);
	}
	
	/**
	 * Find favorites searching by name
	 * @param ctx An application context
	 * @param query The requested name
	 * @return The results of the search
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public ArrayList<NamedPoint> findFavorites(Activity ctx, String query) throws XmlPullParserException, IOException {
		ArrayList<NamedPoint> result = new ArrayList<NamedPoint>();
		for(NamedPoint np : getFavorites(ctx).values()) {
			if(query.trim().equals("") || np.getName().toLowerCase(Locale.FRENCH).contains(query.toLowerCase())) {
				result.add(np);
			}
		}
		return result;
	}
	
	/**
	 * Get the list of points of interest of this location
	 * @return The points of interest
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public PointOfInterest[] getPOIs(Context context) throws FileNotFoundException, IOException, XmlPullParserException {
		ArrayList<PointOfInterest> pois = new ArrayList<PointOfInterest>();
		for(PointOfInterestCategory cat : getAllPoiCategory(context).values()) {
			for(PointOfInterestType type : cat.getTypes()) {
				pois.addAll(Arrays.asList(type.getPois()));
			}
		}
		return Arrays.copyOf(pois.toArray(), pois.size(), PointOfInterest[].class);
	}
	
	/**
	 * Get the list of excursions for this location
	 * @return The excursions
	 */
	public HashMap<Integer, Excursion> getExcursions(Context ctx) throws FileNotFoundException, XmlPullParserException, IOException {
		if(excursions == null) {
			excursions = RWFile.readExcursionsXML(ctx,
					new File(MapController.getInstance().getContext().getFilesDir().getPath()+File.separator+"locations"+File.separator+id+File.separator+"excursions"+File.separator+"fr"+File.separator+"excursions.xml"),
					new File(MapController.getInstance().getContext().getFilesDir().getPath()+File.separator+"locations"+File.separator+id+File.separator+"bookmarks"+File.separator+"excursions.xml"));
		}
		return excursions;
	}
	

	
	/**
	 * Get the map of PoiCategories for this location
	 * @return The PoiCategories
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public HashMap<String, PointOfInterestCategory> getAllPoiCategory(Context context) throws FileNotFoundException, IOException, XmlPullParserException {
		HashMap<String,PointOfInterestCategory> result = null;
		if(poiCategories==null) {
			File xml = new File(context.getFilesDir().getPath()+File.separator+"locations"+File.separator+this.id+File.separator+"pois"+File.separator+"pois.gpx");
			File favorites = new File(context.getFilesDir().getPath()+File.separator+"locations"+File.separator+this.id+File.separator+"bookmarks"+File.separator+"pois.xml");
			poiCategories = RWFile.readPoiGPX(xml, context, R.xml.poi_categories_fr, favorites, this.id);
			result = poiCategories;
		} else {
			result = poiCategories;
		}
		return result;
	}
	
	/**
	 * Search excursions with a queried text
	 * @param query The string pattern
	 * @return The found excursions
	 * @throws FileNotFoundException The list of excursions wasn't found
	 * @throws XmlPullParserException Parser error
	 * @throws IOException Error during reading
	 */
	public ArrayList<Excursion> findExcursions(Context ctx, String query) throws FileNotFoundException, XmlPullParserException, IOException {
		return findExcursions(ctx, query, null);
	}
	
	/**
	 * Search excursions with a queried text and a restriction for locomotion
	 * @param query The string pattern
	 * @param locomotions The usable locomotions
	 * @return The found excursions
	 * @throws FileNotFoundException The list of excursions wasn't found
	 * @throws XmlPullParserException Parser error
	 * @throws IOException Error during reading
	 */
	public ArrayList<Excursion> findExcursions(Context ctx, String query, Locomotion[] locomotions) throws FileNotFoundException, XmlPullParserException, IOException {
		HashMap<Integer,Excursion> exc = getExcursions(ctx);
		ArrayList<Excursion> result = new ArrayList<Excursion>();
		//Search in hashmap
		for(int id : exc.keySet()) {
			boolean addToResult = false;
			//Search for name
			if(query.trim().equals("") || exc.get(id).getName().toLowerCase(Locale.FRENCH).contains(query.toLowerCase())) {
				//Search for locomotion
				if(locomotions == null) {
					addToResult = true;
				} else {
					//search for locomotions
					int i=0;
					int j=0;
					while(!addToResult && i < exc.get(id).getLocomotions().length) {
						while(!addToResult && j < locomotions.length) {
							if(exc.get(id).getLocomotions()[i].equals(locomotions[j])) {
								addToResult = true;
							}
							j++;
						}
						i++;
					}
				}
			}
			//Search by POI (with a minimal query to get pertinent results)
			if(!addToResult && query.length() >= 4) {
				PointOfInterest[] poisArray = exc.get(id).getSurroundingPois(ctx);
				int i=0;
				while(!addToResult && i < poisArray.length) {
					if(poisArray[i].getName().toLowerCase(Locale.FRENCH).contains(query.toLowerCase())) {
						addToResult = true;
					}
					i++;
				}
			}
			if(addToResult) {
				result.add(exc.get(id));
			}
		}
		return result;
	}
	
	
	
	/**
	 * Get the list of available locomotions for this location
	 * @param ctx An application Context
	 * @return The array of available locomotions
	 */
	public Locomotion[] getAvailableLocomotions(Context ctx) {
		ArrayList<Locomotion> resultAl = new ArrayList<Locomotion>();
		Locomotion[] result = null;
		try {
			HashMap<Integer, Excursion> excursions = getExcursions(ctx);
			int locomotionsListSize = Locomotion.getCount(ctx);
			for(int excId : excursions.keySet()) {
				Locomotion[] excLocomotions = excursions.get(excId).getLocomotions();
				for(Locomotion l : excLocomotions) {
					if(!resultAl.contains(l)) { resultAl.add(l); }
					if(resultAl.size()==locomotionsListSize) { break; }
				}
			}
			result = Arrays.copyOf(resultAl.toArray(), resultAl.size(), Locomotion[].class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(result == null) { result = new Locomotion[0]; }
		return result;
	}

	/**
	 * Returns the wanted POI category
	 * @param id The ID of the category
	 * @return The category
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public PointOfInterestCategory getPoiCategory(Activity ctx, String id) throws FileNotFoundException, IOException, XmlPullParserException {
		return getAllPoiCategory(ctx).get(id);
	}
	
// MODIFIERS
	/**
	 * Set the name of the selected location
	 * @param theName
	 */
	public void setName(String theName) {
		name = theName;
	}
	
	/**
	 * Set the logo of the selected location
	 * @param theLogo
	 */
	public void setLogo(Uri theLogo) {
		logo = theLogo;
	}
	
	/**
	 * Set the version of the selected location
	 * @param theVersion
	 */
	public void setVersion(int theVersion){
		version = theVersion;
	}
	
	/**
	 * Set the URL of the selected location
	 * @param The current URL
	 */
	public void setPackageUrl(URL theUrl){
		packageUrl = theUrl;
	}
	
	/**
	 * Set the URL of the selected location's photos
	 * @param The current URL
	 */
	public void setPhotosUrl(URL theUrl){
		packageUrl = theUrl;
	}
	
	/**
	 * Set the boundary box of the selected location
	 * @param theBbox
	 */
	public void setBoundaryBox(Area theBbox) {
		boundaryBox = theBbox;
	}
	
	/**
	 * Set the postal code of the selected location
	 * @param thePostalCode
	 */
	public void setPostalCode(String thePostalCode) {
		postalCode = thePostalCode;
	}
	
	/**
	 * Set the country of the selected location
	 * The location's country in the <a href="https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2">ISO 3166-1 alpha-2</a> code (for example, the code for France is <b>FR</b>)
	 * @param theCountry
	 */
	public void setCountry(String theCountry) {
		country = theCountry;
	}
}
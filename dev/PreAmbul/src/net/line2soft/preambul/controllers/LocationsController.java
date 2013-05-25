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

package net.line2soft.preambul.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import net.line2soft.preambul.models.Location;
import net.line2soft.preambul.utils.Network;
import net.line2soft.preambul.utils.RWFile;
import net.line2soft.preambul.views.ExcursionListActivity;
import net.line2soft.preambul.views.LocationActivity;
import net.line2soft.preambul.views.LocationSelectionActivity;
import net.line2soft.preambul.views.SlippyMapActivity;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.TextView;
import net.line2soft.preambul.R;

/**
 * This class is the controller for the locations management.
 * It allows to install, update, uninstall and select a location.
 * It also connects to locations server in order to get locations list and retrieve packages. 
 * The singleton pattern is used, in order to be able to access to the controller from any activity.
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public final class LocationsController {
// ATTRIBUTES
	/** Singleton instance **/
	private static volatile LocationsController instance = null;
	//Locations lists
	/** List of installed locations **/
	private HashMap<Integer,Location> installedLocations;
	/** List of all the available locations **/
	private HashMap<Integer,Location> allLocations;
	//Activity
	/** The currently running activity **/
	private LocationActivity currentActivity;
	/** Refers to installation activity **/
	public static final int INSTALL_VIEW = 1;
	/** Refers to selection activity **/
	public static final int SELECTION_VIEW = 2;
	/** Refers to update activity **/
	public static final int UPDATE_VIEW = 3;
	/** Refers to uninstall activity **/
	public static final int UNINSTALL_VIEW = 4;
	//Server URL
	/** URL for the location list **/
	public final URL ONLINE_LIST_URL;
	/** URL for the server which contains the location list **/
	public final URL SERVER_URL;
	//Locations lists XML
	/** The file which contains the list of available locations **/
	private File availableXml;
	/** The file which contains the list of installed locations **/
	private File installedXml;
	/** The download locations packages task **/
	private static DownloadInstallLocationPackageTask dlpt;
	/** Is download running **/
	private boolean isDownloadRunning = false;
	/** Is install running **/
	private boolean isInstallRunning = false;

// CONSTRUCTOR
	/**
	 * Class constructor.
	 * @param current The currently running activity
	 */
	private LocationsController(LocationActivity current) {
		//Set attributes
		currentActivity = current;
		availableXml = new File(currentActivity.getFilesDir().getPath()+File.separator+"available.xml");
		installedXml = new File(currentActivity.getFilesDir().getPath()+File.separator+"installed.xml");
		URL tmp;
		URL tmp2;
		try {
			tmp = new URL(Network.getServerUrl(currentActivity));
			tmp2 = new URL(tmp.toExternalForm()+"/list.xml");
		}
		catch(MalformedURLException mue) { tmp = tmp2 = null; }
		SERVER_URL = tmp;
		ONLINE_LIST_URL = tmp2;
		
		//Load lists
		try {
			loadLocationsListsFromXML();
		} catch (LocationManagementException e) {
			currentActivity.displayInfo(e.getMessage());
		}

		//Automatic updates research
		boolean checkUpdatesOnStart = PreferenceManager.getDefaultSharedPreferences(currentActivity).getBoolean("checkUpdatesOnStart", true);
		if(checkUpdatesOnStart) {
			//If there's an active Internet connection, let's check updates
			Thread connexion =  new Thread(){
	            @Override
	            public void run(){
        			if(Network.checkAvailableNetwork(currentActivity)) {
        				try {
        					if(checkServerListUpdates()==true) {
        						//Check if there are available updates for installed locations
        						Iterator<Integer> itr = installedLocations.keySet().iterator();
        						boolean availableInstalledUpdates = false;
        						while(itr.hasNext() && availableInstalledUpdates==false) {
        							int id = itr.next(); 
        							if(allLocations.containsKey(id) && allLocations.get(id).getVersion() > installedLocations.get(id).getVersion()) {
        								availableInstalledUpdates = true;
        								currentActivity.displayInfo(currentActivity.getString(R.string.message_available_update));
        							}
        						}
        					}
        				} catch (LocationManagementException e) {
        					//If there's an error, update checking is cancelled
        					e.printStackTrace();
        				}
        			}	                
	            }
	        };
	       connexion.start();
		}
	}

	/**
	 * Class constructor.
	 * @param current The currently running activity
	 */
	private LocationsController(Activity act) {
		availableXml = new File(act.getFilesDir().getPath()+File.separator+"available.xml");
		installedXml = new File(act.getFilesDir().getPath()+File.separator+"installed.xml");
		URL tmp;
		URL tmp2;
		try {
			tmp = new URL(Network.getServerUrl(act));
			tmp2 = new URL(tmp.toExternalForm()+"/list.xml");
		}
		catch(MalformedURLException mue) { tmp = tmp2 = null; }
		SERVER_URL = tmp;
		ONLINE_LIST_URL = tmp2;
		
		//Load lists
		try {
			loadLocationsListsFromXML();
		} catch (LocationManagementException e) {
			currentActivity.displayInfo(e.getMessage());
		}
	}

// ACCESSORS
	/**
	 * This methods returns the only instance of {@link LocationsController}
	 * @return The current instance of {@link LocationsController}
	 */
	public final static LocationsController getInstance(LocationActivity current) {
		synchronized(LocationsController.class){
			//Returns the current instance or create one if no one exists
			if(LocationsController.instance == null) {
				LocationsController.instance = new LocationsController(current);
			}
			else {
				LocationsController.instance.setCurrentActivity(current);
			}
		}
		return LocationsController.instance;
	}
	
	/**
	 * This methods returns the only instance of {@link LocationsController}
	 * @return The current instance of {@link LocationsController}
	 */
	public final static LocationsController getInstance(Activity current) {
		synchronized(LocationsController.class){
			//Returns the current instance or create one if no one exists
			if(LocationsController.instance == null) {
				LocationsController.instance = new LocationsController(current);
			}
		}
		return LocationsController.instance;
	}
	
	/**
	 * Returns the HashMap of installed locations
	 * @return The HashMap of installed locations
	 */
	public HashMap<Integer,Location> getInstalledLocations() {
		return installedLocations;
	}
	
	/**
	 * Returns the HashMap of all available locations
	 * @return The HashMap of available locations
	 */
	public HashMap<Integer,Location> getAllLocations() {
		return allLocations;
	}
	
	/**
	 * Returns the currently running activity
	 * @return The current activity
	 */
	public LocationActivity getCurrentActivity() {
		return currentActivity;
	}
	
	/**
	 * Returns the list of updatable locations
	 * @return A {@link HashMap} which contains all the updatable {@link Location}s
	 * @throws LocationManagementException Error while trying to get server list
	 */
	public HashMap<Integer,Location> getUpdatableLocations() {
		try {
			checkServerListUpdates();
		}
		catch(LocationManagementException e) {;}
		return getUpdatableLocationsWithoutNetwork();
	}
	
	/**
	 * Returns the list of updatable locations without checking server list updates
	 * @return A {@link HashMap} which contains all the updatable {@link Location}s
	 */
	public HashMap<Integer,Location> getUpdatableLocationsWithoutNetwork() {
		//Returns the locations which available version is superior to installed version
		HashMap<Integer,Location> result = new HashMap<Integer,Location>();
		for(int id : installedLocations.keySet()) {
			if(allLocations.containsKey(id) && allLocations.get(id).getVersion() > installedLocations.get(id).getVersion()) {
				result.put(id, allLocations.get(id));
			}
		}
		return result;
	}

	/**
	 * Get the list of installable locations
	 * @return The {@link HashMap} of installable {@link Location}s
	 * @throws LocationManagementException Error while trying to get server list
	 */
	public HashMap<Integer,Location> getInstallableLocations() {
		try {
			checkServerListUpdates();
		}
		catch(LocationManagementException e) {;}
		@SuppressWarnings("unchecked")
		HashMap<Integer,Location> result = (HashMap<Integer, Location>) allLocations.clone();
		//To get installable locations, we remove installed locations from all the available locations
		for(int id : installedLocations.keySet()) {
			result.remove(id);
		}
		return result;
	}
	
	/**
	 * Is install running ?
	 * @return True if yes, false else
	 */
	public boolean isInstallRunning() {
		return isInstallRunning;
	}
	
	/**
	 * Is download running ?
	 * @return True if yes, false else
	 */
	public boolean isDownloadRunning() {
		return isDownloadRunning;
	}

// MODIFIERS
	/**
	 * Edits the currently running activity
	 * @param current The current activity
	 */
	public void setCurrentActivity(LocationActivity current) {
		currentActivity = current;
	}

// OTHER METHODS
	/**
	 * Loads the locations lists from XML files saved in the file system.
	 * @throws LocationManagementException If an error occurs during reading or writing lists.
	 */
	private void loadLocationsListsFromXML() throws LocationManagementException {
		try {
			allLocations = RWFile.readLocationsXML(availableXml);
			installedLocations = RWFile.readLocationsXML(installedXml);
		} catch (FileNotFoundException e) {
			//Create files, probably first launch
			try {
				allLocations = new HashMap<Integer,Location>();
				installedLocations = new HashMap<Integer,Location>();
				RWFile.writeLocationsXML(availableXml, allLocations);
				RWFile.writeLocationsXML(installedXml, installedLocations);
			} catch (IOException e1) {
				throw new LocationManagementException(currentActivity.getString(R.string.message_location_creation_error));
			}
		} catch (IOException e) {
			throw new LocationManagementException(currentActivity.getString(R.string.message_reading_location_error));
		} catch (XmlPullParserException e) {
			throw new LocationManagementException(currentActivity.getString(R.string.message_not_xml_error));
		}
		finally {
			if(allLocations==null) {
				allLocations = new HashMap<Integer,Location>();
			}
			if(installedLocations==null) {
				installedLocations = new HashMap<Integer,Location>();
			}
		}
	}
	
	/**
	 * Launch locations' install.
	 * @param idLocations The IDs of locations to install
	 * @throws LocationManagementException If an error during install occurs
	 */
	boolean installLocationsPackages(int[] idLocations) throws LocationManagementException {
		checkServerListUpdates();
		dlpt = new DownloadInstallLocationPackageTask();
		dlpt.execute(idLocations);
		return true;
	}
	
	/**
	 * Launch locations' install with multimedia packages
	 * @param idLocations An array which contains the ID of locations to install
	 * @return True if it was launched
	 * @throws LocationManagementException
	 */
	boolean installLocationsPhotosPackages(int[] idLocations) throws LocationManagementException {
		checkServerListUpdates();
		DownloadInstallLocationPhotosPackageTask dlppt = new DownloadInstallLocationPhotosPackageTask();
		dlppt.execute(idLocations);
		return true;
	}
	
	/**
	 * Deletes the queried locations.
	 * @param idLocations The IDs of locations to uninstall
	 * @throws LocationManagementException If an error during uninstall occurs
	 */
	boolean uninstallLocationsPackages(int[] idLocations) throws LocationManagementException {
		File folderToDelete;
		boolean result = true;
		//Uninstall wanted locations
		for(int index : idLocations) {
			//Simply delete the folder which contains the location's data
			folderToDelete = new File(currentActivity.getFilesDir().getPath()+File.separator+"locations"+File.separator+index);
			boolean deleted;
			if(folderToDelete.exists()) {
				deleted = RWFile.deleteDirectory(folderToDelete);
			}else{
				deleted = true;
			}

			File photos=new File((Environment.getExternalStorageDirectory()).getAbsolutePath()+File.separator+"Android"+File.separator+"data"+File.separator+"net.line2soft.preambul"+File.separator+"files"+File.separator+"photos"+File.separator+index);
			if(photos.exists()){
				RWFile.deleteDirectory(photos);
			}
			
			if(!deleted) {
				result = false;
				throw new LocationManagementException(currentActivity.getString(R.string.message_location_uninstall_error)+" "+installedLocations.get(index).getName());
			}
			else {
				installedLocations.remove(index);
			}
		}
		try {
			RWFile.writeLocationsXML(installedXml, installedLocations);
		} catch (IOException e) {
			throw new LocationManagementException(currentActivity.getString(R.string.message_location_save_error));
		}
		return result;
	}
	
	/**
	 * Updates the queried locations
	 * @param idLocations The IDs of locations to update
	 * @throws LocationManagementException If an error during update occurs
	 */
	boolean updateLocationsPackages(int[] idLocations) throws LocationManagementException {
		//uninstalls and installs wanted packages
		boolean result=false;
		int[] current=new int[1];
		for(int index:idLocations){
			current[0]=index;
			
			//verifies if this location has photos installed on the disk, if so redownload them (they may have been updated)
			boolean photos=false;
			if(new File((Environment.getExternalStorageDirectory()).getAbsolutePath()+File.separator+"Android"+File.separator+"data"+File.separator+"net.line2soft.preambul"+File.separator+"files"+File.separator+"photos"+File.separator+current[0]).exists()){
				photos=true;
			}
			
			result = uninstallLocationsPackages(current);			
			
			if(result && photos ) {
				result = installLocationsPhotosPackages(idLocations);
			}else if(result){
				result = installLocationsPackages(idLocations);				
			}else{
				result=false;
			}
		}
		return result;
	}
	
	/**
	 * Checks if locations list was updated, and then updates the application list.
	 * It also saves the updated list into the file system.
	 * @return True if available locations list was updated, false else
	 * @throws LocationManagementException If an error occurs when downloading the locations list
	 */
	boolean checkServerListUpdates() throws LocationManagementException {
		boolean availableUpdate = false;
		//Download location list
		File downloadedLocationsXML = null;
		try {
			if(Network.checkAvailableNetwork(currentActivity) && Network.checkConnection(ONLINE_LIST_URL)) {
				downloadedLocationsXML = Network.download(ONLINE_LIST_URL, currentActivity.getFilesDir());
			}
		} catch (IOException e1) {;}
		if(downloadedLocationsXML != null) {
			//Compare new and old location lists
			if(downloadedLocationsXML.hashCode() != availableXml.hashCode()) {
				try {
					allLocations = RWFile.readLocationsXML(downloadedLocationsXML);
					saveLocationsListsIntoXML();
					availableUpdate = true;
					//Download logos
					File logoPath = new File(currentActivity.getFilesDir().getPath()+File.separator+"logos");
					if(!logoPath.exists()) { logoPath.mkdirs(); }
					for(int id : allLocations.keySet()) {
						try {
							Network.download(new URL(SERVER_URL.toExternalForm()+"/logos/"+id+".png"), logoPath);
						}
						catch(IOException e) {;}
					}
				} catch (Exception e) {
					throw new LocationManagementException(currentActivity.getString(R.string.message_unreachable_server));
				}
			}
		}
		return availableUpdate;
	}
	
	/**
	 * Saves the locations lists into a XML file into the file system.
	 * @throws LocationManagementException If an error occurs during writing.
	 */
	private void saveLocationsListsIntoXML() throws LocationManagementException {
		try {
			RWFile.writeLocationsXML(availableXml, allLocations);
			RWFile.writeLocationsXML(installedXml, installedLocations);
		} catch (IOException e) {
			throw new LocationManagementException(currentActivity.getString(R.string.message_location_save_error));
		}
	}
	
	/**
	 * Searches a location with str and updates the list in the designated view.
	 * @param str The search string
	 * @param view The view which contains the list to update, use {@link #SELECTION_VIEW}, {@link #INSTALL_VIEW}, {@link #UPDATE_VIEW} or {@link #UNINSTALL_VIEW}.
	 */
	void searchLocationAndUpdateView(String str, int view) {
		if(view==SELECTION_VIEW || view==INSTALL_VIEW || view==UPDATE_VIEW || view==UNINSTALL_VIEW) {
			if(!str.trim().equals("") && str != null) {
				str = str.trim();
				int initialSize = (view==INSTALL_VIEW) ? (int) (allLocations.size() * 0.05 + 1) : (int) (installedLocations.size() * 0.05 + 1); //Size is initialised to 5% +1 of corresponding locations
				ArrayList<Location> searchResult = new ArrayList<Location>(initialSize); 
				//Search in locations
				HashMap<Integer,Location> searchData = allLocations;
				switch(view) {
					case INSTALL_VIEW:
						searchData = getInstallableLocations();
						break;
					case UPDATE_VIEW:
						searchData = getUpdatableLocations();
						break;
					case UNINSTALL_VIEW:
						searchData = installedLocations;
						break;
					case SELECTION_VIEW:
						searchData = installedLocations;
						break;
				}
				//Find location which name contains the search string
				for(int locationId : searchData.keySet()) {
					boolean addToResult = false;
					//Search in name
					if(searchData.get(locationId).getName().toLowerCase(Locale.FRENCH).contains(str.toLowerCase())) {
						addToResult = true;
					}
					//Search in postal code
					else if(searchData.get(locationId).getPostalCode().toLowerCase(Locale.FRENCH).trim().equals(str.toLowerCase().trim())
							|| (str.trim().length() < searchData.get(locationId).getPostalCode().trim().length()
									&& searchData.get(locationId).getPostalCode().toLowerCase(Locale.FRENCH).trim().substring(0, str.trim().length()).equals(str.trim().toLowerCase(Locale.FRENCH)))) {
						addToResult = true;
					}
					if(addToResult) {
						searchResult.add(searchData.get(locationId));
					}
				}
				//Update view
				Object[] sR = searchResult.toArray();
				Location[] newList = Arrays.copyOf(sR, sR.length, Location[].class);
				currentActivity.setLocationList(newList);
			}
			//Reset list if void search
			else {
				HashMap<Integer,Location> list = null;
				switch(view) {
					case SELECTION_VIEW:
						list = installedLocations;
						break;
					case INSTALL_VIEW:
						list = getInstallableLocations();
						break;
					case UPDATE_VIEW:
						list = getUpdatableLocations();
						break;
					case UNINSTALL_VIEW:
						list = installedLocations;
						break;
				}
				//Update view
				Object[] tmpList = list.values().toArray();
				Location[] newList = Arrays.copyOf(tmpList, tmpList.length, Location[].class);
				currentActivity.setLocationList(newList);
				((TextView) currentActivity.findViewById(R.id.autoCompleteTextView1)).setText("");
			}
		}
	}
	
	/**
	 * Launches the wanted location: makes the slippy map appear with the queried location.
	 */
	void launchLocation(int id) {
		boolean excursionsBeforeMap = PreferenceManager.getDefaultSharedPreferences(currentActivity).getBoolean("displayExcursionsBeforeMap", false);
		Intent loc = (excursionsBeforeMap) ? new Intent(currentActivity, ExcursionListActivity.class) : new Intent(currentActivity, SlippyMapActivity.class);
		loc.putExtra(LocationSelectionActivity.LOCATION_ID, id);
		MapController.getInstance(currentActivity).resetValues();
		MapController.getInstance(currentActivity).setCurrentLocation(installedLocations.get(id));
		currentActivity.startActivity(loc);
	}
	
// INNER CLASS
	/**
	 * This class downloads and installs location packages in separate task
	 */
	private class DownloadInstallLocationPackageTask extends AsyncTask<int[],Void,HashMap<Integer,File>> {
		/** The message to display at the end **/
		private String finalMessage ="";
		
		@Override
		protected HashMap<Integer, File> doInBackground(int[]... arg0) {
			isDownloadRunning = true;
			int[] toDownload = arg0[0];
			HashMap<Integer,File> packages = new HashMap<Integer,File>(toDownload.length);
			//Download each package
			for(int index : toDownload) {
				try {
					packages.put(index, Network.download(allLocations.get(index).getPackageUrl(), currentActivity.getCacheDir()));
				} catch (IOException e) {
					packages.put(index, null);
				}
			}
			isDownloadRunning = false;
			isInstallRunning = true;
			try {
				//Change dialog text
				currentActivity.runOnUiThread(new Runnable() {
					  public void run() {
						  currentActivity.hideWaitDialog();
						  currentActivity.showWaitDialog(currentActivity.getString(R.string.message_wait), currentActivity.getString(R.string.message_install_running));
					  }
					});
				//Launch install
				installLocationsPackagesOnSystem(packages);
				finalMessage = currentActivity.getString(R.string.message_location_install_finished);
			} catch (LocationManagementException e) {
				finalMessage = e.getMessage();
			}
			isInstallRunning = false;
			return packages;
		}
		
		@Override
		protected void onPostExecute(HashMap<Integer,File> result) {
			super.onPostExecute(result);
			currentActivity.hideWaitDialog();
			currentActivity.displayInfo(finalMessage);
			currentActivity.onBackPressed();
			currentActivity.finish();
		}
		
		/**
		 * Installs the queried locations packages into the file system.
		 * @throws LocationManagementException If an error during install occurs
		 */
		private void installLocationsPackagesOnSystem(HashMap<Integer,File> locationsPackages) throws LocationManagementException {
			if(locationsPackages != null && !locationsPackages.isEmpty()) {
				//Install wanted locations
				File cachePackage = null;
				for(int index : locationsPackages.keySet()) {
					cachePackage = locationsPackages.get(index);
					if(cachePackage != null) {
						//Extract file
						File locationFolder = new File(currentActivity.getFilesDir().getPath()+File.separator+"locations"+File.separator+index);
						if(!RWFile.unzip(cachePackage, locationFolder)) {
							locationFolder.delete();
							throw new LocationManagementException(currentActivity.getString(R.string.message_location_install_error)+" "+allLocations.get(index).getName());
						} else {
							cachePackage.delete();
							//Add location to installed list
					    	installedLocations.put(index, allLocations.get(index));
						}
					}
					else {
						throw new LocationManagementException(currentActivity.getString(R.string.message_location_download_error)+" "+allLocations.get(index).getName());
					}
				}
				try {
					RWFile.writeLocationsXML(installedXml, installedLocations);
				} catch (IOException e) {
					throw new LocationManagementException(currentActivity.getString(R.string.message_location_save_error));
				}
			}
		}
	}
	
	/**
	 * This class downloads and installs location packages and media packages in separate task
	 */
	private class DownloadInstallLocationPhotosPackageTask extends AsyncTask<int[],Void,HashMap<Integer,File>> {
		private String finalMessage ="";
		
		@Override
		protected HashMap<Integer, File> doInBackground(int[]... arg0) {
			isDownloadRunning = true;
			int[] toDownload = arg0[0];
			HashMap<Integer,File> packages = new HashMap<Integer,File>(toDownload.length);
			HashMap<Integer,File> photos = new HashMap<Integer,File>(toDownload.length);
			//Download each package
			for(int index : toDownload) {
				try {
					packages.put(index, Network.download(allLocations.get(index).getPackageUrl(), currentActivity.getCacheDir()));
					if(allLocations.get(index).getPhotoUrl() != null) {
						photos.put(index, Network.download(allLocations.get(index).getPhotoUrl(), currentActivity.getCacheDir()));
					}
				} catch (IOException e) {
					packages.put(index, null);
				}
			}
			isDownloadRunning = false;
			isInstallRunning = true;
			try {
				//Change dialog text
				currentActivity.runOnUiThread(new Runnable() {
					  public void run() {
						  currentActivity.hideWaitDialog();
						  currentActivity.showWaitDialog(currentActivity.getString(R.string.message_wait), currentActivity.getString(R.string.message_install_running));
					  }
					});
				//Launch install
				installLocationsPackagesOnSystem(packages,photos);
				finalMessage = currentActivity.getString(R.string.message_location_install_finished);
			} catch (LocationManagementException e) {
				e.printStackTrace();
				finalMessage = e.getMessage();
			}
			isInstallRunning = false;
			return packages;
		}
		
		@Override
		protected void onPostExecute(HashMap<Integer,File> result) {
			super.onPostExecute(result);
			currentActivity.hideWaitDialog();
			currentActivity.displayInfo(finalMessage);
			currentActivity.onBackPressed();
			currentActivity.finish();
		}
		/**
		 * Installs the queried locations packages into the file system.
		 * @throws LocationManagementException If an error during install occurs
		 */
		private void installLocationsPackagesOnSystem(HashMap<Integer,File> locationsPackages,HashMap<Integer,File> locationsPhotos) throws LocationManagementException {
			if(locationsPackages != null && !locationsPackages.isEmpty()) {
				//Install wanted locations
				File cachePackage = null;
				for(int index : locationsPackages.keySet()) {
					cachePackage = locationsPackages.get(index);
					if(cachePackage != null) {
						//Extract file
						File locationFolder = new File(currentActivity.getFilesDir().getPath()+File.separator+"locations"+File.separator+index);
						if(!RWFile.unzip(cachePackage, locationFolder)) {
							locationFolder.delete();
							throw new LocationManagementException(currentActivity.getString(R.string.message_location_install_error)+" "+allLocations.get(index).getName());
						} else {
							cachePackage.delete();
							//Add location to installed list
					    	installedLocations.put(index, allLocations.get(index));
						}
					}
					else {
						throw new LocationManagementException(currentActivity.getString(R.string.message_location_download_error)+" "+allLocations.get(index).getName());
					}
				}
				for(int index : locationsPhotos.keySet()) {
					cachePackage = locationsPhotos.get(index);
					if(cachePackage != null) {
						//Extract file
						File contentFolder = new File((Environment.getExternalStorageDirectory()).getAbsolutePath()+File.separator+"Android"+File.separator+"data"+File.separator+"net.line2soft.preambul"+File.separator
								+"files"+File.separator+index);
						if(!RWFile.unzip(cachePackage, contentFolder)) {
							contentFolder.delete();
							throw new LocationManagementException(currentActivity.getString(R.string.message_location_install_error)+" "+allLocations.get(index).getName());
						} else {
							cachePackage.delete();
						}
					}
					else {
						throw new LocationManagementException(currentActivity.getString(R.string.message_location_download_error)+" "+allLocations.get(index).getName());
					}
				}
				try {
					RWFile.writeLocationsXML(installedXml, installedLocations);
				} catch (IOException e) {
					throw new LocationManagementException(currentActivity.getString(R.string.message_location_save_error));
				}
			}
		}
	}
}
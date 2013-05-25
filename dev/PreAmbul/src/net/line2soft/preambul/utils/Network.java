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
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.xmlpull.v1.XmlPullParser;

import net.line2soft.preambul.R;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * This class contains utilitary methods related to network.
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class Network {
// OTHER METHODS
	/**
	 * Downloads a file from the Internet
	 * @param address The URL of the file
	 * @param dest The destination directory
	 * @return The queried file
	 * @throws IOException HTTP connection error, or writing error
	 */
	public static File download(URL address, File dest) throws IOException {
		File result = null;
		InputStream in = null;
		BufferedOutputStream out = null;
		//Open streams
		try {			
			HttpGet httpGet = new HttpGet(address.toExternalForm());
			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is established.
			int timeoutConnection = 4000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT)
			int timeoutSocket = 5000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
			httpClient.setParams(httpParameters);
			HttpResponse response = httpClient.execute(httpGet);
			
			//Launch streams for download
			in = new BufferedInputStream(response.getEntity().getContent());
			String filename = address.getFile().substring(address.getFile().lastIndexOf("/")+1);
			File tmp = new File(dest.getPath()+File.separator+filename);
			out = new BufferedOutputStream(new FileOutputStream(tmp));
			
			//Test if connection is OK
			if(response.getStatusLine().getStatusCode() / 100 == 2) {
				//Download and write
				try {
					int byteRead = in.read();
					while(byteRead >= 0) {
						out.write(byteRead);
						byteRead = in.read();
					}
					result = tmp;
				}
				catch(IOException e) {
					throw new IOException("Error while writing file: "+e.getMessage());
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new IOException("Error while downloading: "+e.getMessage());
		}
		finally {
			//Close streams
			if(out!=null) { out.close(); }
			if(in!=null) { in.close(); }
		}
		return result;
	}
	
	/**
	 * Returns the server's URL read from the XML file
	 * @param ctx An application context
	 * @return The server URL
	 */
	public static String getServerUrl(Context ctx) {
		String serverUrl = null;
		XmlPullParser parser = ctx.getResources().getXml(R.xml.server);
		int eventType;
		try {
			eventType = parser.getEventType();
			while(eventType != XmlPullParser.END_DOCUMENT){
	        	if(eventType==XmlPullParser.START_TAG && parser.getName().equals("server")) {
	        		for(int i = 0; i < parser.getAttributeCount(); i++) {
		        		if(parser.getAttributeName(i).equals("url")){
		        			serverUrl = parser.getAttributeValue(i);
		        		}
	        		}
	        	}
	        	eventType = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverUrl;
	}
	
	/**
	 * Checks if a connection is possible between device and server.
	 * @param address The URL to request
	 * @return True if the connection is possible, false else
	 */
	public static boolean checkConnection(URL address) {
		boolean result = false;
		try {
			HttpURLConnection urlc = (HttpURLConnection) address.openConnection();
		    //urlc.setRequestProperty("User-Agent", "Android Application:"+Z.APP_VERSION);
		    urlc.setRequestProperty("Connection", "close");
		    urlc.setConnectTimeout(1000);
		    urlc.connect();
		    if (urlc.getResponseCode() == 200) {
		        result = true;
		    }
		} catch (Exception e) {;}
		return result;
	}
	
	/**
	 * Checks if a network is available.
	 * @param ct An application context
	 * @return True if a network is available, false else
	 */
	public static boolean checkAvailableNetwork(Context ct) {
		boolean result = false;
		ConnectivityManager cm = (ConnectivityManager) ct.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if(activeNetwork!=null && activeNetwork.isConnected()) { result = true; }
		return result;
	}
}
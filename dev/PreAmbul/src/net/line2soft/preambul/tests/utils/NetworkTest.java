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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import net.line2soft.preambul.utils.Network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.test.AndroidTestCase;


/**
 * Test class for {@link Network}
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class NetworkTest extends AndroidTestCase {
// OTHER METHODS
// checkConnection() tests
	public void testCheckConnectionExistingURL() {
		try {
			assertTrue(Network.checkConnection(new URL("http://www.google.fr/")));
		} catch (MalformedURLException e) {
			fail("Invalid test URL");
		}
	}

	public void testCheckConnectionNotExistingURL() {
		try {
			assertFalse(Network.checkConnection(new URL("http://localhost/non/existing/url/")));
		} catch (MalformedURLException e) {
			fail("Invalid test URL");
		}
	}

// download() tests
	public void testDownload() {
		try {
			File result = Network.download(new URL(Network.getServerUrl(getContext())+"/list.xml"), getContext().getFilesDir());
			assertTrue(result.exists());
			assertEquals(result.toString(), getContext().getFilesDir().toString()+File.separator+"list.xml");
			assertTrue(result.isFile());
			//assertEquals(result.length(), 1092);
			//Clean
			result.delete();
		}
		catch (MalformedURLException e1) {
			e1.printStackTrace();
			fail("URL invalid");
		}
		catch (IOException e2) {
			e2.printStackTrace();
			fail("IOException");
		}
	}

// checkAvailableNetwork() tests
	public void testCheckAvailableNetwork() {
			assertEquals(Network.checkAvailableNetwork(getContext()), ((ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo().isConnected());
	}

// getServerUrl() tests
	public void testGetServerUrl() {
		assertEquals(Network.getServerUrl(getContext()), "http://line2soft.monespace.net/autres/randosm");
	}
}
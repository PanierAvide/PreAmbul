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

package net.line2soft.preambul.tests;

import net.line2soft.preambul.tests.controllers.LocationsControllerTest;
import net.line2soft.preambul.tests.controllers.MapControllerTest;
import net.line2soft.preambul.tests.models.AreaTest;
import net.line2soft.preambul.tests.models.CoordinateTest;
import net.line2soft.preambul.tests.models.ExcursionTest;
import net.line2soft.preambul.tests.models.LocationTest;
import net.line2soft.preambul.tests.models.LocomotionTest;
import net.line2soft.preambul.tests.models.NamedPointTest;
import net.line2soft.preambul.tests.models.NavigationInstructionTest;
import net.line2soft.preambul.tests.models.PointOfInterestCategoryTest;
import net.line2soft.preambul.tests.models.PointOfInterestTest;
import net.line2soft.preambul.tests.models.PointOfInterestTypeTest;
import net.line2soft.preambul.tests.utils.NetworkTest;
import net.line2soft.preambul.tests.utils.RWFileTest;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Class containing all tests (Excursion tests, Locations tests, Map tests and PoiFavorites tests)
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class AllTests extends TestSuite {

	public static Test suite() {
	TestSuite suite = new TestSuite(
					"Testing all the application");
	//$JUnit-BEGIN$
	//Location
	//model
	suite.addTestSuite(AreaTest.class);
	suite.addTestSuite(CoordinateTest.class);
	suite.addTestSuite(LocationTest.class);
	//utils
	suite.addTestSuite(NetworkTest.class);
	suite.addTestSuite(RWFileTest.class);
	//controller
	suite.addTestSuite(LocationsControllerTest.class);
	
	//Map
	suite.addTestSuite(MapControllerTest.class);
	
	//Excursions	
	//model
	suite.addTestSuite(ExcursionTest.class);
	suite.addTestSuite(LocomotionTest.class);	
	suite.addTestSuite(NavigationInstructionTest.class);

	//PoiFavorites				
	//model
	suite.addTestSuite(NamedPointTest.class);
	suite.addTestSuite(PointOfInterestTest.class);	
	suite.addTestSuite(PointOfInterestTypeTest.class);	
	suite.addTestSuite(PointOfInterestCategoryTest.class);

	//$JUnit-END$	
	return suite;
	}
}
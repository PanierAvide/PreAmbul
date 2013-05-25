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

/**
 * An area is a bounding box defined by two points: the top left corner, and the bottom right corner.
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class Area {
// ATTRIBUTES
	/** The coordinate of the top left corner of the area **/
	private Coordinate topLeftCorner;
	/** The coordinate of the bottom right corner of the area **/
	private Coordinate bottomRightCorner;

// CONSTRUCTOR
	/**
	 * Class constructor
	 * @param topLeft The top left corner of the area
	 * @param bottomRight The bottom right corner of the area
	 */
	public Area(Coordinate topLeft, Coordinate bottomRight) {
		topLeftCorner = topLeft;
		bottomRightCorner = bottomRight;
	}
	
	/**
	 * Get the top left corner of the area
	 * @return The top left corner coordinate
	 */
	public Coordinate getTopLeftCorner() {
		return topLeftCorner;
	}

	/**
	 * Get the bottom right corner of the area
	 * @return The bottom right corner coordinate
	 */
	public Coordinate getBottomRightCorner() {
		return bottomRightCorner;
	}
}
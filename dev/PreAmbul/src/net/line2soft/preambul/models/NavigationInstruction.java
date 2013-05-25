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

import org.mapsforge.android.maps.overlay.OverlayWay;

/**
 * This class represents the instruction for navigation during an excursion.
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public class NavigationInstruction {
// ATTRIBUTES
	/** Id of an instruction */
	private int id;
	/** Instruction of navigation */
	private String instruction;
	/** Time of the course */
	private int time;
	/** The segment of way to display **/
	private OverlayWay segment;
	
// CONSTRUCTOR
	/**
	 * Class constructor
	 * @param id
	 * @param instruction
	 * @param time
	 */
	public NavigationInstruction(int id, String instruction, int time, OverlayWay segment){
		this.id = id;
		this.instruction = instruction;
		this.time = time;
		this.segment = segment;
	}

// ACCESSORS
	/**
	 * Get the id of the instruction
	 * @return The id
	 */
	public int getId(){
		return id;
	}
	
	/**
	 * Get the instruction for the navigation
	 * @return The instruction
	 */
	public String getInstruction(){
		return instruction;
	}
	
	/**
	 * Get the time of the course
	 * @return The time
	 */
	public int getTime(){
		return time;
	}
	
	/**
	 * Get the segment for the instruction
	 * @return The segment
	 */
	public OverlayWay getSegment() {
		return segment;
	}
}
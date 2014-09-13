package objects;
/**
 * Representation of any Celestial object within the system.
 * This class is extended to create specific types of objects
 * @author Simon Fleming (sf58@sussex.ac.uk)
 */
import resources.Calculations;
import system.Application;
import javax.microedition.lcdui.Graphics;

public abstract class CelestialObject {
	
	// Object Identifier
	protected int ID;
	// Object name
	protected String name;
	// X & Y position
	protected int xPos,yPos;
	// altitude & azimuth
	protected double alt,azimuth;
	// right ascension & declination
	protected double RA, DEC;
	// colour of object
	protected int colour;
	// size of object
	protected int size;
	// detailed information
	protected String details;
	// brightness - default as the brightness
	protected int magnitude=0;	
	
	// is this star visible?
	protected boolean visible  = false; // in the sky?
	protected boolean onscreen = false;	// on the screen?
	
	// is this star highlighted?
	protected boolean highlighted = false;	
	
	/**
	 * The celestial object constructor
	 */
	public CelestialObject() {
		// objects are hidden as default
		this.xPos 		 = -1;
		this.yPos 	     = -1;
		this.visible     = false;
		this.onscreen 	 = false;
		this.highlighted = false;
	}
	
	/**
	 * Is visible?
	 * @return if the star is current visible.
	 */
	public boolean isVisible() {
		return this.visible;
	}
	
	/**
	 * Set the visibility of this object
	 * @param b
	 * @return
	 */
	public void setVisible(boolean b) {
		this.visible = b;
	}
	
	
	/**
	 * Is this currently highlighted?
	 * @return if the star is highlighted
	 */
	public boolean isHighlighted() {
		return this.highlighted;
	}
	
	/**
	 * Make this highlighted or un-highlighted
	 * @param b new value of highlighted
	 */
	public void highlight(boolean b) {
		this.highlighted = b;
	}	
	
	/**
	 * Is this object currently on screen?
	 * @return
	 */
	public boolean isOnScreen() {
		return this.onscreen;
	}
	
	/**
	 * make this on screen/not on screen
	 * @param b
	 */
	public void onScreen(boolean b) {
		this.onscreen = b;
	}
	
	/** 
	 * Access the ID variable 
	 * @return the objects identification variable
	 */
	public int getID() {
		return this.ID;
	}
	
	/** 
	 * Access the name of this object
	 * @return the objects name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Set the object name
	 * @param name - objects name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Set the objects ID
	 * @param ID - objects ID
	 */
	public void setID(int ID) {
		this.ID = ID;
	}
	
	/**
	 * Set the current X,Y position of this star.
	 * @param x
	 * @param y
	 */
	public void setXY(int x, int y) {
		this.xPos = x;
		this.yPos = y;
	}
	
	/**
	 * Get X position
	 * @return x position
	 */
	public int getX() {
		return this.xPos;
	}
	
	/**
	 * Get Y position
	 * @return y position
	 */
	public int getY() {
		return this.yPos;
	}	
	
	/**
	 * Calculate the altitude and azimuth position of this object
	 */
	public abstract void calculatePosition(Application app, Calculations c);
	
	/**
	 * Set the details of this object
	 */
	public void setDetails(String details) {
		this.details = details;
	}
	
	/**
	 * Get the Right Ascension of this star
	 * @return stars right ascension
	 */
	public double getRA() {
		return this.RA;
	}
	
	/**
	 * Get the Declination of this star
	 * @return stars declination
	 */
	public double getDEC() {
		return this.DEC;
	}		
	
	/**
	 * Access the altitude of this object
	 * @return altitude of this object
	 */
	public double getAltitude() {
		return this.alt;
	}
	
	/**
	 * Access the azimuth of this object
	 * @return azimuth of this object
	 */
	public double getAzimuth() {
		return this.azimuth;
	}	
	
	/**
	 * Get the colour of this object
	 * @return colour
	 */
	public int getColour() {
		return this.colour;
	}
	
	/**
	 * Set the colour of this object
	 * @param c - new colour
	 */
	public void setColour(int c) {
		this.colour = c;
	}
	
	/**
	 * Get the size of this object
	 * @return colour
	 */
	public int getSize() {
		return this.size;
	}
	
	/**
	 * Set the size of this object
	 * @param s - new size
	 */
	public void setSize(int s) {
		this.size = s;
	}
	
	/**
	 * Return the stars magnitude 1-6
	 * @return magnitude
	 */
	public int getMag() {
		return this.magnitude;
	}
	
	
	/**
	 * Draw this object!
	 * @param Graphics g
	 */
	public abstract void draw(Graphics g, int circle, int colour);
}

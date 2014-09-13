package objects;
/**
 * This class represents a Satellite (Celestial Object).
 * @author Simon Fleming (sf58@sussex.ac.uk)  
 */
import javax.microedition.lcdui.Graphics;
import resources.Calculations;
import system.Application;

public class Satellite extends CelestialObject {
	
	/**
	 * Calculate the altitude and azimuth position of this object
	 */
	public void calculatePosition(Application app, Calculations c) {
		// Satellite positions are precalculated
	}	
	
	/**
	 * Create a new Satellite!
	 */
	public Satellite(String name, int alt, int az) {
		this.colour = 0xF34D43;
		this.size = 6;
		this.alt = alt;
		this.azimuth = az;
	}
	
	/**
	 * Draw this Satellite!
	 */
	public void draw(Graphics g, int circle, int colour) {
		int modifier = this.size/2;
		g.setColor(this.colour);
		g.fillArc(this.xPos-modifier, this.yPos-modifier, this.size, this.size, 0, 360);
	}
}

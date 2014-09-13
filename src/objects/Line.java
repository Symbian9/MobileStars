package objects;
/**
 * This class represents a constellation line from point x1,y1 to point x2,y2
 * @author Simon Fleming (sf58@sussex.ac.uk)  
 */
public class Line {

	// alt/azim
	public int al1,az1,al2,az2;
	// x,y's
	public int X1, Y1, X2, Y2;
	
	/**
	 * Constructor, saving point
	 */
	public Line(int al1, int az1, int al2, int az2) {
		this.al1 = al1;
		this.az1 = az1;
		this.al2 = al2;
		this.az2 = az2;
	}
	
}

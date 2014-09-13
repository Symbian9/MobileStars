package ui;
/**
 * Abstract class used to represent all screens.
 */
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import system.Application;

public abstract class Screen extends Canvas implements CommandListener {
	
	// Which application called this screen?
	protected Application app;
	
	/**
	 * Constructor of screen
	 * @param application that called this class
	 */
	public Screen(Application app) {
		this.app=app;
	}
	
	/**
	 * Request that the application updates its canvas to the provided index
	 * @param screenIndex of new screen
	 */
	public void updateApplication(int screenIndex) {
		app.updateScreen(screenIndex);
	}
	
	/**
	 * Handle user key presses
	 * @param command c
	 * @param displayable s
	 */
	public abstract void commandAction(Command c, Displayable s);

}
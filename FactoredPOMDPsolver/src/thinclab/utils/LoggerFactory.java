/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.utils;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/*
 * @author adityas
 *
 */
public class LoggerFactory {
	/*
	 * Creates loggers
	 */
	
	public static Logger getNewLogger(String name) {
		Logger logger = Logger.getLogger(name);
		logger.setLevel(Level.ALL);
		
		return logger;
	}
	
	public static Logger getConsoleLogger(String name) {
		/*
		 * Creates logger which prints to console
		 */
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(new SimpleFormatter());
		handler.setLevel(Level.ALL);
		
		Logger logger = LoggerFactory.getNewLogger(name);
		logger.addHandler(handler);
		
		return logger;
	}
}

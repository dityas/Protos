/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.utils;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
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
//		logger.setLevel(Level.OFF);
		
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
	
	public static void stopLogging() {
		/*
		 * Sets all log levels to OFF
		 */
		LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.OFF);
	}
	
	public static void startFineLogging() {
		/*
		 * Sets global log level to FINE
		 */
		LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.FINE);
	}
	
	public static void startInfoLogging() {
		/*
		 * Sets global log level to INFO
		 */
		LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.INFO);
	}
}

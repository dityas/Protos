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
	
	private static Level loggerLevel;
	
	public static Logger getNewLogger(String name) {
		Logger logger = Logger.getLogger(name);
		logger.setLevel(LoggerFactory.loggerLevel);
		
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
		LoggerFactory.loggerLevel = Level.OFF;
//		LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.OFF);
	}
	
	public static void startFineLogging() {
		/*
		 * Sets global log level to FINE
		 */
		LoggerFactory.loggerLevel = Level.ALL;
//		LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.FINE);
	}
	
	public static void startInfoLogging() {
		/*
		 * Sets global log level to INFO
		 */
		LoggerFactory.loggerLevel = Level.INFO;
//		LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.INFO);
	}
}

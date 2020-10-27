/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.utils;

import java.net.URI;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Order;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.plugins.Plugin;

/*
 * @author adityas
 *
 */
@Plugin(name = "HumanAgentInteractionLoggingConfigurationFactory", category = ConfigurationFactory.CATEGORY)
@Order(50)
public class HumanAgentInteractionLoggingConfigurationFactory extends ConfigurationFactory {
	
	/*
	 * An attempt to configure logging for log4j2
	 * 
	 * Most of the code is taken from the official log4j docs
	 */
	
	private static String fileName;
	
	// --------------------------------------------------------------------------------------------
	
    static Configuration createConfiguration(
    		final String name, 
    		ConfigurationBuilder<BuiltConfiguration> builder) {
    	
        builder.setConfigurationName(name);
        builder.setStatusLevel(Level.ERROR);
        builder.add(
        		builder.newFilter(
        				"ThresholdFilter", 
        				Filter.Result.ACCEPT, 
        				Filter.Result.NEUTRAL)
        		.addAttribute("level", Level.DEBUG));
        
        /* Make console logger */
        AppenderComponentBuilder appenderBuilder = 
        		builder.newAppender("Stdout", "CONSOLE")
        			.addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
        appenderBuilder.add(builder.newLayout("PatternLayout")
        		.addAttribute("pattern", "%d{dd MMM yy HH:mm:ss} %c{1} [%-5level]: %msg%n"));
        appenderBuilder.add(
        		builder.newFilter(
        				"MarkerFilter", 
        				Filter.Result.DENY, 
        				Filter.Result.NEUTRAL)
        		.addAttribute("marker", "FLOW"));
        
        /* Make file logger */
        String logFileName = "/dev/null";
        
        if (HumanAgentInteractionLoggingConfigurationFactory.fileName != null)
        	logFileName = HumanAgentInteractionLoggingConfigurationFactory.fileName;
        	
        AppenderComponentBuilder fileAppenderBuilder = 
        		builder.newAppender("log", "File")
        			.addAttribute("fileName", logFileName)
        			.addAttribute("immediateFlush", "true");
        fileAppenderBuilder.add(
        		builder.newLayout("PatternLayout")
        			.addAttribute("pattern", "%d{dd MMM yy HH:mm:ss} %c{1} [%-5level]: %msg%n"));
        fileAppenderBuilder.add(
        		builder.newFilter(
        				"MarkerFilter", 
        				Filter.Result.DENY, 
        				Filter.Result.NEUTRAL)
        		.addAttribute("marker", "FLOW"));
        
        /* Add both appenders */
//        builder.add(appenderBuilder);
        builder.add(fileAppenderBuilder);

        /*
         * Add everything to root logger cos i don't really know how log4j works and I have called
         * every logger instance using just the class name.
         */
        builder.add(
        		builder.newRootLogger(Level.ALL)
//        			.add(builder.newAppenderRef("Stdout"))
        			.add(builder.newAppenderRef("log")));
        
        return builder.build();
    }
    
    // -----------------------------------------------------------------------------------------------

    @Override
    public Configuration getConfiguration(
    		final LoggerContext loggerContext, 
    		final ConfigurationSource source) {
        return getConfiguration(loggerContext, source.toString(), null);
    }

    @Override
    public Configuration getConfiguration(
    		final LoggerContext loggerContext, 
    		final String name, final URI configLocation) {
        ConfigurationBuilder<BuiltConfiguration> builder = newConfigurationBuilder();
        return createConfiguration(name, builder);
    }

    @Override
    protected String[] getSupportedTypes() {
        return new String[] {"*"};
    }
    
    // ---------------------------------------------------------------------------------------------
    
    public static void initializeLogging() {
    	System.setProperty(
    			"log4j.configurationFactory", 
    			HumanAgentInteractionLoggingConfigurationFactory.class.getName());
    }
    
    public static void setLogFileName(String fileName) {
    	
    	HumanAgentInteractionLoggingConfigurationFactory.fileName = fileName;
    }
}

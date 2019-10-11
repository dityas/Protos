/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.ipomdpsolver;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import thinclab.ddhelpers.DDTree;
import thinclab.exceptions.ParserException;
import thinclab.legacy.Global;
import thinclab.legacy.ParseSPUDD;

/*
 * @author adityas
 *
 */
public class IPOMDPParser extends ParseSPUDD {
	/*
	 * Recursively parses nested IPOMDP domains
	 * 
	 * Attrib:
	 * 		level:			Strategy level for the current frame.
	 * 		frameID:		Frame ID for the current frame
	 * 		childFrames:	List of lower strategy level child frame parse objects
	 */
	public int level;
	public int frameID;
	public List<ParseSPUDD> childFrames = new ArrayList<ParseSPUDD>();
	
	/* Separate container for costs */
	public HashMap<String, DDTree> costMap;
	
	private static final Logger logger = Logger.getLogger(IPOMDPParser.class);
	
	// ------------------------------------------------------------------------------------------
	/*
	 * Constructors
	 */

	public IPOMDPParser(String fileName) {
		/*
		 * Call ParseSPUDD constructor to initialize stream tokenizer in super the
		 * class. Going to reuse super class methods as far as possible.
		 */
		this.initialize();
		try {
		    this.stream = new StreamTokenizer(new FileReader(fileName));
		} 
		
		catch (FileNotFoundException e) {             				
		    System.err.println("Error: file not found\n");
		    System.exit(-1);
		}
		
		this.stream.wordChars('\'', '\'');
		this.stream.wordChars('_', '_');
	}
	
	public IPOMDPParser(StreamTokenizer stream) {
		this.initialize();
	}
	
	// ------------------------------------------------------------------------------------------
	/*
	 * Parsing
	 */
	
	public void parseDomain() {
		/*
		 * Recursively extracts the problem definition from the stream.
		 * 
		 * The parsing loop is exactly same as Jesse Hoey's ParseSPUDD with some additions to parse
		 * frames and strategy levels.
		 */
		try {
		    boolean primeVarsCreated = false;
		    
		    while (true) {
		    	
		    	/*
		    	 * primeVars have to be created once the state and observation variables are parsed
		    	 * because transition DDs use primed vars
		    	 */
				if (!primeVarsCreated && nStateVars > 0 && (nObsVars > 0)) {
				    primeVarsCreated = true;
				    createPrimeVars();
				}
				
				stream.nextToken();
				
				switch(stream.ttype) {
				
					case '(':
					    stream.nextToken();
					    
					    if (stream.sval.compareTo("variables") == 0) {
					    	parseVariables();
					    }
					    
					    else if (stream.sval.compareTo("observations") == 0) {
					    	parseObservations();
					    }
					    
					    else if (stream.sval.compareTo("frames") == 0) {
					    	this.parseFrame();
					    }
					    
					    else throw new ParserException("Expected \"variables\" or \"observations\" got " 
					    		+ this.stream + " instead");
					    
					    break;
					    
					case StreamTokenizer.TT_WORD:
					    
						if (stream.sval.compareTo("unnormalized") == 0) {
							unnormalized = true;
							break;
					    }
						
					    else if (stream.sval.compareTo("unnormalised") == 0) {
					    	unnormalized = true;
					    	break;
					    }
						
					    else if (stream.sval.compareTo("dd") == 0) {
							parseDDdefinition();
							break;
					    }
						
					    else if (stream.sval.compareTo("action") == 0) {
							parseAction();
							break;
					    }
						
					    else if (stream.sval.compareTo("adjunct") == 0) {
							parseAdjunct();
							break;
					    }
						
					    else if (stream.sval.compareTo("reward") == 0) {
							parseReward();
							break;
					    }
						
					    else if (stream.sval.compareTo("discount") == 0) {
							parseDiscount();
							break;
					    }
						
					    else if (stream.sval.compareTo("horizon") == 0) {
							parseHorizon();
							break;
					    }
						
					    else if (stream.sval.compareTo("tolerance") == 0) {
							parseTolerance();
							break;
					    }
						
					    else if (stream.sval.compareTo("init") == 0) {
							parseInit();
							break;
					    }
						
					    error("Expected \"unnormalized\" or \"dd\" or \"action\" or \"reward\"");
					    
					case StreamTokenizer.TT_EOF:
						this.setGlobals();
						this.populateDDTreeObjects();
					    return;

					default:
						this.setGlobals();
						this.populateDDTreeObjects();
					    return;
				}
		    }
	
		} 
		
		catch (Exception e) {
		    System.err.println(e.getMessage());
		    e.printStackTrace();
	        System.exit(-1);
	    }
	}
	
	void parseNActions() throws Exception {
		/*
		 * Parses action names.
		 * 
		 * The actual parsing of CPTs for each action is done in super.parseAction()
		 */
		
	}
	
	void parseFrame() throws Exception{
		/*
		 * Parses the frame header in the SPUDD file for lower level opponents' models
		 */
		while (true) {
			/*
			 * Loop the frame parser to parse possible multiple frames
			 */
			this.logger.info("Parsing new frame");
			this.stream.nextToken();
			
			/*
			 * If the token is ')', we've reached the end of frame definitions section.
			 * Break and return to the main parsing loop
			 */
			if (this.stream.ttype == ')') break;
			
			/*
			 * If we get '(', this is the start of a new frame. 
			 * Parse the frame using another parser object
			 */
			if (this.stream.ttype == '(') {
				
				this.stream.nextToken();
				
				if (this.stream.sval.compareTo("frame") == 0) {
					Global.clearHashtables();
					int frameID = this.parseFrameID();
					int stratLevel = this.parseStratLevel();
					
					/*
					 * If the strat level is 0, the opponent's model is a POMDP.
					 * So use Jesse Hoey's parser here as it is.
					 */
					if (stratLevel == 0) {
						ParseSPUDD l0frame = new ParseSPUDD(this.stream);
						l0frame.parsePOMDP(false);
						l0frame.level = stratLevel;
						l0frame.frameID = frameID;
						this.childFrames.add(l0frame);
					}
					
					/*
					 * For strat levels greater than 0, use an IPOMDP Parser.
					 */
					else {
						IPOMDPParser lframe = new IPOMDPParser(this.stream);
						lframe.parseDomain();
						lframe.level = stratLevel;
						lframe.frameID = frameID;
						this.childFrames.add(lframe);
					}
					
				}
				
				else throw new ParserException(
						"Expected frame definition got " + this.stream + " instead");
			}
			
			else throw new ParserException(
					"Expected frame definition got " + this.stream + " instead");
			
		}
	}
	
	public int parseFrameID() throws Exception {
		/*
		 * Extracts frame ID from the frame definition 
		 */
		this.stream.nextToken();
		
		if (this.stream.ttype == StreamTokenizer.TT_NUMBER) {
			return (int) stream.nval;
		}
		
		else {
			throw new ParserException("Expected Frame ID, got " + this.stream);
		}
	}
	
	public int parseStratLevel() throws Exception {
		/*
		 * Extracts strategy level mentioned in the frame definition 
		 */
		this.stream.nextToken();

		if (this.stream.ttype == StreamTokenizer.TT_WORD && this.stream.sval.compareTo("level") == 0) {
			
			this.stream.nextToken();
			
			if (this.stream.ttype == StreamTokenizer.TT_NUMBER) {
				return (int) stream.nval;
			}
			
			else {
				throw new ParserException("Expected Level no, got " + this.stream);
			}
		}
		
		else throw new ParserException("Expected level definition, got " + this.stream);
	}
	
	// ------------------------------------------------------------------------------------------	
	public void setGlobals() {
		/*
		 * Sets the global statics. Splitting this into a separate function 
		 * so that the solver can set globals manually before starting the 
		 * PBVI loop. Setting it only once after the parsing may not be ideal
		 * since the lower frames in the recursion tree may overwrite the 
		 * globals set by the upper frames.
		 */
		Global.clearHashtables();
		this.logger.info("Setting globals");
		this.createPrimeVars();
		
		String[] actNamesArray = new String[actNames.size()];
	    
		for (int actId=0; actId<actNames.size(); actId++) {
	    	actNamesArray[actId] = (String)actNames.get(actId);
	    }
	    Global.setValNames(Global.valNames.length+1, actNamesArray);
									
									
	    /*
	     * Set varDomSize with extra action variable
	     */
	    int[] varDomSizeArray = new int[Global.varDomSize.length+1];
	    
	    for (int varId=0; varId<Global.varDomSize.length; varId++) {
	    	varDomSizeArray[varId] = Global.varDomSize[varId];
	    }
	    
	    varDomSizeArray[varDomSizeArray.length-1] = actNamesArray.length;
	    Global.setVarDomSize(varDomSizeArray);
	}
	
	@Override
	public void populateDDTreeObjects() {
		/*
		 * Override to re arrange costs into a HashMap
		 */
		super.populateDDTreeObjects();
		HashMap<String, DDTree> costMap = new HashMap<String, DDTree>(); 
		
		for (int a = 0; a < this.actNames.size(); a++)
			costMap.put(this.actNames.get(a), this.costs.get(a));
		
		this.costMap = costMap;
	}
	
}

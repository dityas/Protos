/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.parsers;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thinclab.ddinterface.DDTree;
import thinclab.exceptions.ParserException;
import thinclab.legacy.Global;

/*
 * @author adityas
 *
 */
public class IPOMDPParser extends ParseSPUDD {

	/*
	 * Recursively parses nested IPOMDP domains
	 * 
	 * Attrib: level: Strategy level for the current frame. frameID: Frame ID for
	 * the current frame childFrames: List of lower strategy level child frame parse
	 * objects
	 */

	private static final long serialVersionUID = -5363714209771866270L;

	public int level;
	public int frameID;
	public String mostProbableAi = null;
	public double mpAiProb = 0.99;
	public HashMap<String, Float> aiDist = new HashMap<String, Float>();
	public List<ParseSPUDD> childFrames = new ArrayList<ParseSPUDD>();

	private String domainPath = "";

	/* Separate container for costs */
	public HashMap<String, DDTree> costMap;

	private static final Logger LOGGER = LogManager.getLogger(IPOMDPParser.class);

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

			String[] pathParts = fileName.split("/");

			this.domainPath = "/" + String.join("/", ArrayUtils.subarray(pathParts, 0, pathParts.length - 1));

			this.stream = new StreamTokenizer(new FileReader(fileName));
			stream.wordChars('\'', '\'');
			stream.wordChars('_', '_');
			stream.quoteChar('"');
		}

		catch (FileNotFoundException e) {
			System.err.println("Error: file not found\n");
			System.exit(-1);
		}

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
		 * The parsing loop is exactly same as Jesse Hoey's ParseSPUDD with some
		 * additions to parse frames and strategy levels.
		 */
		try {
			boolean primeVarsCreated = false;

			while (true) {

				/*
				 * primeVars have to be created once the state and observation variables are
				 * parsed because transition DDs use primed vars
				 */
				if (!primeVarsCreated && nStateVars > 0 && (nObsVars > 0)) {
					primeVarsCreated = true;
					createPrimeVars();
				}

				stream.nextToken();

				switch (stream.ttype) {

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

					else
						throw new ParserException(
								"Expected \"variables\" or \"observations\" got " + this.stream + " instead");

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

					else if (stream.sval.compareTo("most_probable_ai") == 0) {
						this.aiDist.put(this.parseMostProbableAi(), (float) this.parseMostProbableAiVal());
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

	void parseFrame() throws Exception {
		/*
		 * Parses the frame header in the SPUDD file for lower level opponents' models
		 */
		while (true) {
			/*
			 * Loop the frame parser to parse possible multiple frames
			 */
			LOGGER.info("Parsing new frame");
			this.stream.nextToken();

			/*
			 * If the token is ')', we've reached the end of frame definitions section.
			 * Break and return to the main parsing loop
			 */
			if (this.stream.ttype == ')')
				break;

			/*
			 * If we get '(', this is the start of a new frame. Parse the frame using
			 * another parser object
			 */
			if (this.stream.ttype == '(') {

				this.stream.nextToken();

				if (this.stream.sval.compareTo("frame") == 0) {
					Global.clearHashtables();
					int frameID = this.parseFrameID();
					int stratLevel = this.parseStratLevel();
					boolean isExternal = this.isExternalFile();

					if (!isExternal) {

						/*
						 * If the strat level is 0, the opponent's model is a POMDP. So use Jesse Hoey's
						 * parser here as it is.
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

					else {

						this.stream.nextToken();

						String fileName = this.domainPath + "/" + stream.sval;

						LOGGER.debug("Importing from SPUDD file " + fileName);

						StreamTokenizer new_stream = new StreamTokenizer(new FileReader(fileName));
						new_stream.wordChars('\'', '\'');
						new_stream.wordChars('_', '_');
						new_stream.quoteChar('"');

						ParseSPUDD l0frame = new ParseSPUDD(new_stream);
						l0frame.parsePOMDP(false);
						l0frame.level = stratLevel;
						l0frame.frameID = frameID;

						this.childFrames.add(l0frame);

						this.stream.nextToken();
						if (this.stream.ttype != ')')
							throw new ParserException("frame def missing ')'");
					}

				}

				else
					throw new ParserException("Expected frame definition got " + this.stream + " instead");
			}

			else
				throw new ParserException("Expected frame definition got " + this.stream + " instead");

		}
	}

	public String parseMostProbableAi() throws Exception {
		/*
		 * For L0, joint action Ti needs a distribution of possible Ai probabilities. In
		 * case a particular action is given more weight, that action is parsed here.
		 */
		this.stream.nextToken();

		if (this.stream.ttype == StreamTokenizer.TT_WORD)
			return this.stream.sval;

		else {
			LOGGER.error("While parsing most_probable_ai");
			throw new ParserException("Action not specified");
		}
	}

	public double parseMostProbableAiVal() throws Exception {
		/*
		 * For L0, joint action Ti needs a distribution of possible Ai probabilities. In
		 * case a particular action is given more weight, that action is parsed here.
		 */
		this.stream.nextToken();

		if (this.stream.ttype == StreamTokenizer.TT_NUMBER)
			return (double) this.stream.nval;

		else {
			LOGGER.error("While parsing most_probable_ai probability");
			throw new ParserException("Action probability not specified");
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

		else
			throw new ParserException("Expected level definition, got " + this.stream);
	}

	private boolean isExternalFile() throws Exception {
		/*
		 * Check if frame is defined in the file or externally
		 */
		this.stream.nextToken();

		if (this.stream.ttype == StreamTokenizer.TT_WORD && this.stream.sval.compareTo("def") == 0)
			return false;

		else if (this.stream.ttype == StreamTokenizer.TT_WORD && this.stream.sval.compareTo("import") == 0)
			return true;

		else
			throw new ParserException("Frame header is of format (frame <id> level <L> def/import)");
	}

	// ------------------------------------------------------------------------------------------
	public void setGlobals() {
		/*
		 * Sets the global statics. Splitting this into a separate function so that the
		 * solver can set globals manually before starting the PBVI loop. Setting it
		 * only once after the parsing may not be ideal since the lower frames in the
		 * recursion tree may overwrite the globals set by the upper frames.
		 */
		Global.clearHashtables();
		LOGGER.info("Setting globals");
		this.createPrimeVars();

		String[] actNamesArray = new String[actNames.size()];

		for (int actId = 0; actId < actNames.size(); actId++) {
			actNamesArray[actId] = (String) actNames.get(actId);
		}
		Global.setValNames(Global.valNames.length + 1, actNamesArray);

		/*
		 * Set varDomSize with extra action variable
		 */
		int[] varDomSizeArray = new int[Global.varDomSize.length + 1];

		for (int varId = 0; varId < Global.varDomSize.length; varId++) {
			varDomSizeArray[varId] = Global.varDomSize[varId];
		}

		varDomSizeArray[varDomSizeArray.length - 1] = actNamesArray.length;
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

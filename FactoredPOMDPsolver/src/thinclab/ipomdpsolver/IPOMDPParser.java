/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.ipomdpsolver;

import java.io.IOException;
import java.io.StreamTokenizer;

import thinclab.symbolicperseus.Global;
import thinclab.symbolicperseus.ParseSPUDD;

/*
 * @author adityas
 *
 */
public class IPOMDPParser extends ParseSPUDD {

	public IPOMDPParser(String fileName) {
		/*
		 * Call ParseSPUDD constructor to initialize stream tokenizer in super the
		 * class. Going to reuse super class methods as far as possible.
		 */
		super(fileName);
	}

	public void parseFrame() {
		/*
		 * Same as super's parsePOMDP method. Made a few additions to parse levels,
		 * frames and other actions.
		 * 
		 */
		StreamTokenizer stream = getTokenizer();

		try {

			boolean primeVarsCreated = false;
			while (true) {

				if (!primeVarsCreated && nStateVars > 0) {
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

					else
						error("Expected \"variables\" or \"observations\"");

					break;

				// case '('

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

					// case StreamTokenizer.TT_WORD

				case StreamTokenizer.TT_EOF:

					// set valNames for actions
					String[] actNamesArray = new String[actNames.size()];
					for (int actId = 0; actId < actNames.size(); actId++) {
						actNamesArray[actId] = (String) actNames.get(actId);
					}

					Global.setValNames(Global.valNames.length + 1, actNamesArray);

					// set varDomSize with extra action variable
					int[] varDomSizeArray = new int[Global.varDomSize.length + 1];
					for (int varId = 0; varId < Global.varDomSize.length; varId++) {
						varDomSizeArray[varId] = Global.varDomSize[varId];
					}

					varDomSizeArray[varDomSizeArray.length - 1] = actNamesArray.length;
					Global.setVarDomSize(varDomSizeArray);
					return;
				
				// case StreamTokenizer.TT_EOF

				default:
					error(3);
				}
			}

		} 
		
		catch (IOException e) {
			System.out.println("Error: IOException\n");
			// System.exit(1);
		}
	}
}

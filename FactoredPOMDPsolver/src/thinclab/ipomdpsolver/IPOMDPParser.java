/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.ipomdpsolver;

import thinclab.symbolicperseus.ParseSPUDD;

/*
 * @author adityas
 *
 */
public class IPOMDPParser extends ParseSPUDD {

	public IPOMDPParser(String fileName) {
		/*
		 * Call ParseSPUDD constructor to initialize stream tokenizer in super the class.
		 * Going to reuse super class methods as far as possible.
		 */
		super(fileName);
	}

}

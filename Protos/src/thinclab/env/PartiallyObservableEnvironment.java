/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.env;

import thinclab.legacy.DD;

/*
 * @author adityas
 *
 */
public abstract class PartiallyObservableEnvironment implements Environment {

	
	
	public abstract DD getObservationStateless(DD previousState);
	public abstract DD getObservation();
	
	@Override
	public abstract DD stepStateless(DD previousState);

	@Override
	public abstract DD step();

}

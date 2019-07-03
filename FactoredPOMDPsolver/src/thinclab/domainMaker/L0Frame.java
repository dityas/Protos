/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.domainMaker;

/*
 * @author adityas
 *
 */
public abstract class L0Frame extends Domain {
	/*
	 * Defines the level 0 IPOMDP frame. This can be considered as a POMDP and will be solved by the
	 * default POMDP solver.
	 */
	public int level = 0;
	
	public int getLevel() {
		/*
		 * Getter for frame level
		 */
		return this.level;
	}
	
	@Override
	public void makeAll() {
		/*
		 * Populates the domainString attribute using super's implementation but surrounds it in
		 * the frame definition. 
		 */
		
		this.domainString = "(frame" + this.newLine;
		
		/*
		 * Call super here
		 */
		super.makeAll();
		
		this.domainString += this.newLine + ")" + this.newLine;
	}
}

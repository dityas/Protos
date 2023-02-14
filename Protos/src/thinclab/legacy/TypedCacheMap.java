/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.legacy;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * @author adityas
 *
 */
public class TypedCacheMap<K, V> extends ConcurrentHashMap<K, V> {

	/*
	 * Define typed version of the cache map using generics
	 * 
	 */

	private static final long serialVersionUID = 6641216381317045223L;
    private static final Logger LOGGER = 
        LogManager.getFormatterLogger(TypedCacheMap.class);

	public int maxCapacity;

	public TypedCacheMap() {

		super();
		maxCapacity = 100000;
	}

	public TypedCacheMap(int maxCapacity) {

		super();
		this.maxCapacity = maxCapacity;
	}
	
	public boolean clearIfFull() {
		
		if (size() > maxCapacity) {
			clear();
			return true;
		}
		
		return false;
	}
}

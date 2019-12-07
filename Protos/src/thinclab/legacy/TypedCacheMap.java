/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.legacy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * @author adityas
 *
 */
public class TypedCacheMap<K, V>  extends ConcurrentHashMap<K, V> {
	
	/*
	 * Define typed version of the cache map using generics
	 * 
	 */
	
	private static final long serialVersionUID = 6641216381317045223L;
	
	public int maxCapacity;

    public TypedCacheMap() {
    	super();
    	maxCapacity = 100000;
    }

    public TypedCacheMap(int maxCapacity) {
    	super();
    	this.maxCapacity = maxCapacity;
    }

	protected boolean removeEldestEntry(Map.Entry<Pair, DD> eldest) {
        return size() > maxCapacity;
    }
}

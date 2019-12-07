package thinclab.legacy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("rawtypes")
class CacheMap extends ConcurrentHashMap {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4124949012174782729L;
	
	public int maxCapacity;

    public CacheMap() {
	super();
	maxCapacity = 100000;
    }

    public CacheMap(int maxCapacity) {
	super();
	this.maxCapacity = maxCapacity;
    }

//    @SuppressWarnings("rawtypes")
	protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > maxCapacity;
    }
}
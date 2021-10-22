/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.utils;

import java.util.HashMap;

/*
 * @author adityas
 *
 */
public class TwoWayMap<K, V> {

	public HashMap<K, V> k2v = new HashMap<>(10);
	public HashMap<V, K> v2k = new HashMap<>(10);

	public void put(K key, V value) {

		k2v.put(key, value);
		v2k.put(value, key);
	}

}

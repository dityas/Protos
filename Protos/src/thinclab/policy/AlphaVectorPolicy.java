/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.policy;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import thinclab.legacy.DD;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public class AlphaVectorPolicy implements Policy {

	public List<Tuple<Integer, DD>> aVecs;

	public AlphaVectorPolicy(List<Tuple<Integer, DD>> alphaVectors) {

		this.aVecs = alphaVectors;
	}

	@Override
	public int getBestActionIndex(DD belief) {

		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getBestAction(DD belief) {

		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String toString() {
		return aVecs.toString();
	}

	public static AlphaVectorPolicy fromR(DD[] R) {

		return new AlphaVectorPolicy(
				IntStream.range(0, R.length).mapToObj(i -> new Tuple<>(i, R[i])).collect(Collectors.toList()));
	}

}

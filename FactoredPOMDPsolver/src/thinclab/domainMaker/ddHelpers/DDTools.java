package thinclab.domainMaker.ddHelpers;

public class DDTools {
	
	public static String defineDDInSPUDD(String ddName, DDTree dd) {
		String defn = "";
		defn += "dd" + " " + ddName + "\r\n";
		defn += dd.toSPUDD();
		defn += "\r\n";
		defn += "enddd" + "\r\n";
		return defn;
	}
}

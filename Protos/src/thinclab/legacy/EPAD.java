package thinclab.legacy;

import java.io.*;
import java.lang.*;

import thinclab.decisionprocesses.POMDP;


public class EPAD {

	
    /**
     * @param args
     */
	private static boolean POMDP_is_paused = false;
	private static NetworkManager netManager;

    public static void main(String[] args) {
	// TODO Auto-generated method stub
	// this should just run the process	

		
	 long time_interval = 5000; // 5 seconds
	 long t_start;
	
	long t_elapsed;

	int stepId = 0;
	netManager = new NetworkManager();

	// parse the input arguments
	String serveraddy = "127.0.0.1";
	String spuddfile = args[0];
	String flag, argval;
	boolean useHeuristic=false;

	if (args.length > 1) {
	    int arg=1;
	    while (arg < args.length) {
		flag = args[arg];
		if (flag.equalsIgnoreCase("-h")) {
		    useHeuristic=true;
		    arg++;
		} else if (flag.equalsIgnoreCase("-s")) {
		    arg++;
		    serveraddy = args[arg++];
		}
	    }
	}
	if (useHeuristic)
	    System.out.println("************************* using heuristic policy ***************");
	System.out.println("server address in use: "+serveraddy);
	try {
	    netManager.connect_to_server(serveraddy,2000); 
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
		
	t_start = System.currentTimeMillis();
	    

	String basename = spuddfile.substring(0,spuddfile.lastIndexOf("."));
	// the policy file
	String iofile = basename+".pomdp";

	// Read from disk using FileInputStream.
	FileInputStream f_in;
	try {
	    f_in = new FileInputStream (iofile);
	} catch (FileNotFoundException err) {
	    System.out.println("file not found error "+err);
	    return;
	}
	Object obj;
	ObjectInputStream obj_in;
	try {
	    
	    // Read object using ObjectInputStream.
	    obj_in = new ObjectInputStream (f_in);
	} catch (IOException err) {
	    System.out.println("file read error"+err);
	    return;
	} 
	try {
	    // Read an object.
	    obj = obj_in.readObject ();
	} catch (IOException err) {
	    System.out.println("file read error"+err);
	    return;
	} catch (ClassNotFoundException err) {
	    System.out.println("class error"+err);
	    return;
	} 
	// Is the object that you read in, say, an instance
	// of the POMDP class?
	POMDP pomdp;
	if (obj instanceof POMDP) {
	    // Cast object to a POMDP
	    pomdp = (POMDP) obj;
	} else {
	    System.out.println("that file does not contain a pomdp");
	    return;
	}
	pomdp.readFromFile(spuddfile);

	// start main loop
	DD belState = pomdp.initialBelState;
	double engagedp = pomdp.getSingleValue(belState,2,2);
	int actId;
	String [] obsnames = new String[pomdp.nObsVars];
	String actName;

	// temporarily always make this 0 to start
	actId = 0;

	while(1==1){
		try
		{
			Thread.sleep(100);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//pause for 1/10 sec to give time for prog to update

		String rec_str = null;
		rec_str = netManager.read_data();

		if (rec_str != null && rec_str.contains("applypause"))
		{
			managePause();
		}
		else
		{
			//System.out.println("rec_str is "+rec_str);
			//parse_and_accumulate
			netManager.parse_and_accumulate(rec_str);

			t_elapsed = System.currentTimeMillis() - t_start;
			//System.out.println("t_elapsed is "+t_elapsed+" but time_interval is "+time_interval);
			if (t_elapsed > time_interval)
			{
				// get observations 
				obsnames = netManager.getObsValFromRecieved(t_elapsed);
				System.out.println("observation " + obsnames[0] + "  " + obsnames[1]);//+" "+obsnames[2]
				// update belief state
				belState = pomdp.beliefUpdate(belState, actId, obsnames);
				pomdp.printBeliefState(belState);
				// get the policy action
				actId = pomdp.policyQuery(belState, useHeuristic);
				actName = pomdp.actions[actId].name;

				System.out.println("action suggested by policy: " + actId + " which is " + actName);

				// get engagement value
				engagedp = pomdp.getSingleValue(belState, 2, 2);

				// send action and engagement out
				netManager.write_data(actName, engagedp);


				// update time interval
				if (actName.equalsIgnoreCase("resetchange"))
				{
					time_interval = 5000; // needs time to reset the image
				}
				else if (actName.equalsIgnoreCase("give_motiv_prompt"))
				{
					time_interval = 5000; // needs time to say the prompt
				}
				else if (actName.equalsIgnoreCase("add_color"))
				{
					time_interval = 5000; // needs time to react to the color
				}
				else if (actName.equalsIgnoreCase("nothing"))
				{
					time_interval = 5000;
				}

				t_start = System.currentTimeMillis();
				Global.newHashtables();
			}
		}
	}

    }


	private Boolean currentlyPaused = false;
	private static void managePause()
	{
		System.out.println("Paused");
		String incomingMessage = "";
		while (incomingMessage.contains("removepause") == false)
		{
			incomingMessage = waitForMessage();
		}
		System.out.println("Resumed");

	}


	private static String waitForMessage()
	{


		String message = null;
		while (message == null)
		{
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//pause for 1/10 sec to give time for prog to update

			message = netManager.read_data();
		}

		return message;

	}

}

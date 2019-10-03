package thinclab.legacy;

import java.util.StringTokenizer;


public class NetMessage{
	public String m_type;
	public double[] m_data;
	public String m_action;
	public String m_pressedactive;
	public String m_time;
	
	public NetMessage()
	{
		m_type          = null;
		m_action        = null;
		m_pressedactive = null;
		m_time          = null;
		m_data			= new double[4];
	} 
	
	public void parse(String input)
	{
		// fill in the values for the string
		//int idx, cnt, num_pairs;
		//for (i=0; i<input.length();i++)
		//idx = input.indexOf("<");
		//if (idx != -1){
		//	while (	idx != -1 )
		//	{
		//		idx = input.indexOf("<", idx);
		//	}
		//}
		
		// go through and try and complete each type;
		int idx_st, idx_en;
		boolean done=false;
		while (!done){
			idx_st = input.indexOf("<");
			idx_en = input.indexOf(">");
			// if there is a 
			if ((idx_st!=-1) && (idx_en!=-1))
			{
				// get the data between them
				// now get the data 
				
				String ind_entry = input.substring(idx_st+1, idx_en-1);
				
				if (ind_entry.length()>1)
				{
					// get the info in the string					
				}
				
				
			}
		}
		
		// check there is an opening and a closing bracket <>
		
		// find out how many pairs there are
		
		// 
	}
	
	// parse the line
	public void parseLine(String line)
	{
		int messageType = -1;
	    if (line.indexOf("FACE") != -1)
	    	messageType = 1;
	    else if(line.indexOf("PAINT") != -1)
	    	messageType = 2;
	    else if(line.indexOf("POMDP") != -1)
	    	messageType = 3;
	    
	    if ((messageType != -1) && (messageType != 3))
	    {
	    	StringTokenizer st = new StringTokenizer(line," ,\n\t\r\f");
	    	int i=1;
	    	String curStr;
	    	// extract the information from the recieved string
		//System.out.println("line is "+line);
	        while (st.hasMoreTokens()) {
		    curStr = st.nextToken();
		    //System.out.println(" i "+i+" currStr "+curStr);
	            if (messageType == 1){//FACE
			// its a face message
	            	if (i==1)
			    m_type = curStr;
	            	else if (i == 2 || i == 3) 
			    m_data[i-1] = Double.parseDouble(curStr);
			else if (i==4)
			    m_time = curStr;
			
		    }else if (messageType == 2){//PAINT
			//its a paint message
			if (i==1)
			    m_type = curStr;
	                else if (i==2)
	                    m_action = curStr;   
	                else if (i==3)
	                    m_pressedactive = curStr; 
	                else if (i>3 && i<7)
	                    m_data[i-4] = Double.parseDouble(curStr);
			else 
			    m_time = curStr;
	                
		    }
	            i = i+1;
	        }
	        
	    	
	    	
	    }
	    
	    
	    
	}

}
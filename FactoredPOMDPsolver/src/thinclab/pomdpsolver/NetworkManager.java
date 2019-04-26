package thinclab.pomdpsolver;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Vector;


public class NetworkManager {
	
    int m_retry = 0;
    int m_max_retries = 10;
    Socket m_socket=null; 
    boolean m_connected=false;
    InputStream m_input_stream;
    DataInputStream m_data_input_stream;
    OutputStream m_output_stream;
    PrintWriter m_data_output_stream;
    Vector<NetMessage> m_vec=null;// to keep all the network messages
    String last_acton = "nothing";
    double time_interval = 10;
	
    public void connect_to_server(String host, int port) throws IOException, InterruptedException{
	if (m_connected==false){

	    m_retry = 0;
	    while ((m_retry < m_max_retries)&&(!m_connected))
		// connect to the server
		// may have to re-try several times.

		try{
		    //fprintf(1, "Retry %d connecting to %s:%d\n", ...
		    //    retry, host, port);

		    // throws if unable to connect
		    m_socket = new Socket(host, port);       
		    m_connected = true;
		}catch (IOException e) {
		    if (m_socket != null)
			m_socket.close();

		    Thread.currentThread();
		    // pause before retrying
		    Thread.sleep(1000);
		}



	    m_input_stream   = m_socket.getInputStream();

	    m_data_input_stream = new DataInputStream(m_input_stream);
	    //d_input_stream =  BufferedReader(InputStreamReader(input_stream));

	    m_output_stream   = m_socket.getOutputStream();
	    m_data_output_stream = new PrintWriter(m_output_stream, true);

	    m_vec = new Vector<NetMessage>();
	    //increment the number of retries
	    m_retry = m_retry + 1;

	}
    }
    public void write_data(String actName, double engaged) {
	m_data_output_stream.println("<POMDP,"+actName+","+engaged+">");  
    }

    public void  parse_and_accumulate( String input )
    {
	// go through and try and complete each type;
	int idx_st, idx_en;
	boolean done=false;
	if (input == null) {
	    return;
	}
	while (!done){

		try
		{
	    idx_st = input.indexOf("<");
	    idx_en = input.indexOf(">");
	    // if there is an entry 
	    //System.out.println("st: "+idx_st+" en "+idx_en);
	    if ((idx_st!=-1) && (idx_en!=-1)) {
		// get the data between them
		// now get the data 
		
		String ind_entry = input.substring(idx_st+1, idx_en);
		//System.out.println("ind_entry "+ind_entry+" len "+ind_entry.length());
		if (ind_entry.length()>0)
		    {
			// add each entry to the vector
			NetMessage mes = new NetMessage();
			mes.parseLine(ind_entry);
			m_vec.add(mes);	
			input = input.substring(idx_en+1);
		    }
		
	    } else {
		done = true;
	    }

	}
	catch (Exception e) { }
	}

		
    }
	
    public String read_data( ){
	int bytes_available=0;
	try {
	    bytes_available = m_input_stream.available();
	    //System.out.println("bytes "+bytes_available);
	    if (bytes_available > 0){
		int i;
		String rec_str="";
		char tmp;
		//rec_message = zeros(1, bytes_available, 'uint8');
		for (i=0; i<bytes_available; i++){
		    tmp = (char) m_data_input_stream.readByte();
		    //System.out.println("tmp is "+tmp);
		    rec_str = rec_str+tmp;
		}

		//System.out.println("ZUTIS: "+rec_str);
		return rec_str;
		//rec_str = char(rec_message);

		//if display==1
		//    fprintf(1, 'Reading %d bytes\n', bytes_available);
		//    disp(' ');
		//    fprintf(1, [char(rec_message), ' \n']);
		//    disp(' ');
		//end
	    }else{
		return null;
	    }
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return null;
    }
	
    // get the observations from the received parts
    public String [] getObsValFromRecieved(long time_int){
	//				{DRAW, FACE, COMPLETE}
	//DRAW     = {nothing, play, spot, draw}
	//FACE     = {yes,no}
	//COMPLETE = {yes,no}
	//obsValName = {'nothing','no', 'no'};
		
	String drawObs, faceObs, completeObs;
	drawObs 	= "nothing";
	faceObs 	= "no"; 
	completeObs = "no";
	int paint_count = 0;
	int sz = 0;
	double amt_complete;
	boolean pressed_act=false;
		
	// go through all the received entries and sort them out 
	Enumeration<NetMessage> e = m_vec.elements();
	while (e.hasMoreElements()) {
	    //System.out.println(e.nextElement());
	    NetMessage curMessage = e.nextElement();
	    //System.out.println(" type of message "+curMessage.m_type);
	    if (curMessage.m_type != null) {
		if (curMessage.m_type.equalsIgnoreCase("FACE")){
		    // there has been a face message
		    faceObs = "yes";
		}else if (curMessage.m_type.equalsIgnoreCase("PAINT")){
		    paint_count = paint_count + 1;
	            
		    // get the amount of the image which is filled with 
		    //sz = size(rec_data(n).data,2);
		    amt_complete = curMessage.m_data[2];// rec_data(n).data(sz);
		    //System.out.println("amt_complete: "+amt_complete);
		    // decide if screenfilled = yes or no
		    // make sure the lat action wasnt to reset the screen as there is a 
		    // sync problem in that old actions can be 
		    if (amt_complete > 0.5  && !last_acton.equalsIgnoreCase("resetchange")){
			completeObs = "yes";        
		    }
	            //System.out.println("pressed act "+curMessage.m_pressedactive);
		    if (curMessage.m_pressedactive.equalsIgnoreCase("true")){
			pressed_act = true;
		    }
		}
	    		
	    }
	    		    	
	}
	m_vec.clear();    
	// play draw spot nothing
	if (paint_count==0){
	    drawObs = "nothing";
	}else if (pressed_act == true){
	    drawObs = "play";
	}else{
	    System.out.println("paint count is "+paint_count);
	    double activity = paint_count*100/time_int;
	    if (activity < 0.5){
		drawObs = "spot";
	    }else{
		drawObs = "draw";    
	    }
	}
	    
	// return the string
	String [] outobs = new String[2];
	outobs[0] = drawObs;
	outobs[1] = faceObs;
	//outobs[1]="yes";
	//outobs[2] = completeObs;
	return outobs;
    }

}



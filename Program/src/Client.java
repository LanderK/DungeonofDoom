import java.io.*;
import java.net.*;

public class Client{
	
	private Socket socket;
	private PrintWriter out;

	public Client(String Ip,String Port,playerGUI gui){
		
		//first argument is the hostname of the server
		String hostname = Ip;
		//second argument is the port of the server
		int portNumer = Integer.parseInt(Port);
		
		try{
			//Connect to the server
			socket = new Socket(hostname,portNumer);
			//inform the user that they have connected
			System.out.println("Connected");
			//New Client_Server_Listener thread to listen to he server output
			Client_Server_Listener listener = new Client_Server_Listener(socket,gui);
			(new Thread(listener)).start();
			
			//PrintWriter to send data to the Server
			out = new PrintWriter(socket.getOutputStream(), true);
			
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	public void clientClose(){
		try{
			socket.close();
		}
		catch(IOException e){
			
		}
	}
	
	public void messageToServer(String input){
		if(input!=null){
			out.println(input);
		}
	}




}


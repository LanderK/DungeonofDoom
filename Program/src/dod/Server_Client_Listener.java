package dod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.PrintWriter;


public class Server_Client_Listener implements Runnable {

	private Socket socket;
	
	public Server_Client_Listener(Socket socket){
		this.socket=socket;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		BufferedReader in;
		try {
			PrintWriter out=new PrintWriter(socket.getOutputStream(), true);
			in =new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			while(true){
			    String input = in.readLine();
				out.println(input);
			}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
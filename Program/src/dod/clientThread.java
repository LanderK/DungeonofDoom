package dod;

import java.net.*;
import java.io.*;

import dod.game.GameLogic;

public class clientThread extends CommandLineUser implements Runnable{
	
	private Socket clientSocket = null;
	private PrintWriter out = null;
	private int playerID;
		
	//Client Thread Returns the Expected protocol to the User 
	public clientThread(Socket clientSocket,GameLogic game,int playerID){
			super(game,playerID);//Create a new CommandLineUser
			this.playerID=playerID; //Set ID play
			this.clientSocket = clientSocket; //sets the socket from the socket given from the server
			
			
			
		
	}
	@Override
	public void run(){
		
			try{
				//new BufferedReader and PrintWriter to send and recieve data to and from the user
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				out = new PrintWriter(clientSocket.getOutputStream(),true);
				addPlayer();//adds the Player
				while(true){
					String userIn;//String to store user Input
					//System.out.println("waiting for input");
				
					if(!clientSocket.isClosed()){
						userIn = in.readLine();//Takes User Input
						//if user In is Null the Client Socket has been close since the first if statement, meaning the User has disconnected
						if(userIn==null || clientSocket.isClosed()){
							doOutputMessage("Player '"+playerID+"' disconnected");
							removePlayer();
							break;
						}
						else{
							//prints the user Input to the server
							//System.out.println(userIn);
							//processes the input
							processCommand(userIn);
						}
					}
					else{
						doOutputMessage("Player '"+playerID+"' disconnected");
						removePlayer();
						break;
					}
				}
				
			}
			catch(IOException e1){
			}
	}
	@Override
	/**
	 * Simply prints the message to the terminal unless the output is a LOSE then the Server will close the clientsocket
	 */
	protected void doOutputMessage(String message) {
		try{	
			if(message.equals("LOSE")){
				System.out.println(message);
				out.println(message);
				out.flush();
				//removePlayer();
				//clientSocket.close();
			}
			else{
				out.println(message);
				System.out.println(message);
				out.flush();
			}
		}
		/*catch(IOException e1){
			System.out.println(e1.getMessage());
		}*/
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
		
}
	
	
	

package dod;

import java.io.*;
import java.net.*;
import java.text.ParseException;

import dod.game.GameLogic;

public class Server{
	
	public Server(String Map)throws IOException, ParseException{
		
	GameLogic game = null;
	int playerID = 0;
	
		
		try{
			ServerSocket serverSocket = new ServerSocket(55679);//Creates new Server Socket on the port 55679
			game = new GameLogic(Map);//Creates a new Game 
			
			//while the number of total clients connected is less than 5 Accecpt them and make a new ClientThread Thread.
			while(playerID<2 ){
				Socket clientSocket = serverSocket.accept();
				new Thread(new clientThread(clientSocket,game,playerID)).start();
				playerID++;
			}
			serverSocket.close();
		}
		catch(IOException e1){
		}
	}
}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


//Listens to all the replies of the Server
public class Client_Server_Listener implements Runnable {
	
	private Socket socket;
	private playerGUI gui;
	
	public Client_Server_Listener(Socket socket,playerGUI gui){
		this.socket=socket;
		this.gui=gui;
	}

	@Override
	public void run() {
		
		try {
			//Buffered Reader to reader the Servers output
			BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			boolean notDisconnected=true;
			while(notDisconnected){
				//string to store the servers output
				String serverOut;
					//Buffered Reader will send null lines when the Server has been close or the server disconected the user 
					if((serverOut = in.readLine())!=null){
						gui.ServerList.add(serverOut);
						//gui.MessagetextArea.setText(gui.MessagetextArea.getText()+serverOut+"\n");//prints the output for the user

					}
					else{
						//gui.MessagetextArea.setText(gui.MessagetextArea.getText()+"Disconnected"+"\n");
						notDisconnected=false;
						//socket.close();
						//System.exit(0);
					}
	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}

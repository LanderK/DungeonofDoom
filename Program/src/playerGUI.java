import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.JSeparator;

import java.awt.Font;


public class playerGUI extends JFrame implements ActionListener {
	//TextArea for Infomation and the Chat to be Displayed
	private JTextArea MessagetextArea;
	//Queue to store the output of the Server
	public Queue<String> ServerList = new LinkedList<String>();
	//TextFeild that takes the user Input and sends it to all other clients
	private JTextField ShouttextField;
	//Arrays 
	private char[][] lookArray = new char[7][7];
	private JLabel[][] tileGridArray = new JLabel[7][7];
	
	//JButtons needed to add ActionListener's too
	private JButton btnConnect;
	private JButton btnSend;
	private JButton btnN;
	private JButton btnS;
	private JButton btnE;
	private JButton btnW;
	private JButton btnPickup;
	private JButton btnEndTurn;
	
	//Info to Connect to Server
	private String name;
	private String IP;
	private String Port;
	//Logic Booleans
	private Boolean winner=null;
	private boolean firstpass=true;
	//Player information
	private int AP=6;
	private int goldC=0;
	private int goldN;
	private int hasLantern =0;
	private int hasSword = 0;
	private int hasArmour = 0;
	
	private Client Client;

	private Container graphicsPanel;
	
	public playerGUI(){
		//Generating the Jframe
		
		setResizable(false);
		setTitle("Dungeon of Doom");
		setSize(800 ,650);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		Container canvas = getContentPane();
		canvas.setLayout(null);
		//Adding Elements to the JFrame
		btnN = new JButton("N");
		btnN.setBounds(70, 500, 50, 50);
		canvas.add(btnN);
		
		btnE = new JButton("E");
		btnE.setBounds(120, 550, 50, 50);
		canvas.add(btnE);
		
		btnS = new JButton("S");
		btnS.setBounds(70, 550, 50, 50);
		canvas.add(btnS);
		
		btnW = new JButton("W");
		btnW.setBounds(20, 550, 50, 50);
		canvas.add(btnW);
		
		btnPickup = new JButton("PICKUP");
		btnPickup.setBounds(180, 540, 90, 60);
		canvas.add(btnPickup);
	
		btnConnect = new JButton("Connect");
		btnConnect.setBounds(380, 482, 190, 118);
		btnConnect.addActionListener(this);
		canvas.add(btnConnect);
		
		btnEndTurn = new JButton("END TURN");
		btnEndTurn.setBounds(280, 540, 90, 60);
		canvas.add(btnEndTurn);
		
		ShouttextField = new JTextField();
		ShouttextField.setBounds(596, 580, 114, 20);
		getContentPane().add(ShouttextField);
		ShouttextField.setColumns(10);
		
		btnSend = new JButton("Send");
		btnSend.setBounds(715, 577, 69, 23);
		canvas.add(btnSend);
		
		JLabel lblMovement = new JLabel("Movement");
		lblMovement.setHorizontalAlignment(SwingConstants.CENTER);
		lblMovement.setBounds(45, 460, 100, 50);
		canvas.add(lblMovement);
		setVisible(true);
		
		JLabel lblActions = new JLabel("Actions");
		lblActions.setBounds(251, 460, 50, 50);
		getContentPane().add(lblActions);
		
		JScrollPane messageScrollPane = new JScrollPane();
		messageScrollPane.setBounds(584, 11, 200, 558);
		canvas.add(messageScrollPane);
		
		MessagetextArea = new JTextArea();
		messageScrollPane.setViewportView(MessagetextArea);
		MessagetextArea.setEditable(false);
		MessagetextArea.setBorder(new LineBorder(new Color(0, 0, 0), 1));
		DefaultCaret caret = (DefaultCaret)MessagetextArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		// Makes the Graphic Pane to be added to the Jframe
		makeGraphicPane();
		//Start Listening to the Server
		listentoServer();

	}

	public static void main(String[] args) {
		new playerGUI();
		
	}
	//Method used to Generate a popup that will be used to get the Ip/port of the Server from the user of the GUI 
	private void connectToServer(){
		JPanel serverLoginPanel = new JPanel(new BorderLayout(5,5));
		
		JPanel labels = new JPanel(new GridLayout(0,1,2,2));
		labels.add(new JLabel("IP",SwingConstants.RIGHT));
		labels.add(new JLabel("PORT",SwingConstants.RIGHT));
		serverLoginPanel.add(labels,BorderLayout.WEST);
		
		JPanel input = new JPanel(new GridLayout(0,1,2,2));
		JTextField ipTextField = new JTextField("eg:127.0.0.1");
		JTextField portTextField = new JTextField();
		input.add(ipTextField);
		input.add(portTextField);
		serverLoginPanel.add(input, BorderLayout.CENTER);
		int option = JOptionPane.showConfirmDialog(this, serverLoginPanel, "Server Login",JOptionPane.OK_CANCEL_OPTION);
		
		if(option ==JOptionPane.OK_OPTION){
			IP= ipTextField.getText();
			Port= portTextField.getText();
		}
		
	}
	//ActionListener Method that activates when one of the buttons is pressed
	public void actionPerformed(ActionEvent e){
		//Checks which Button was Pressed
		if(e.getSource()==btnConnect){
			if(btnConnect.getText().equals("Connect")){
				//Enables the Buttons
				addActionListeners();
				connectToServer();
				//Makes sure the IP/PORT is valid and gets the name of the user
				if(IP!=null && Port!=null && IP!="" && Port.length()==5){
					btnConnect.setText("Disconnect");
					name = JOptionPane.showInputDialog("Enter Name:"); 
					//makes new Client Object
					Client = new Client(IP,Port,this);
					//Send HELLO Cmd to the Server
					messageToServer("HELLO "+name);
					//redraws the Grpahic Pane
					redrawGhapicsPane();
				}
				else{
					//IP/Port was invalid or not entered
					JOptionPane.showMessageDialog(this, "IP/Port was not Entered");
				}
			}
			else{
				reset();
			}
		}
		//Sends a Shout the server with named attached witht he text from the ShouttextField
		else if(e.getSource()==btnSend){
			messageToServer("SHOUT "+name+": "+ShouttextField.getText());
			ShouttextField.setText("");
			//System.out.println(ServerList);
		}
		/*Movement Buttons
		*Send ths Move comment to the Server, changed the AP of the user then Redraws the graphicPanel 
		*/
		else if(e.getSource()==btnN){
			messageToServer("MOVE N");
			AP=AP-1;
			//System.out.println(ServerList);
			redrawGhapicsPane();
			
		}
		else if(e.getSource()==btnE){
			messageToServer("MOVE E");
			AP=AP-1;
			//System.out.println(ServerList);
			redrawGhapicsPane();
		}
		else if(e.getSource()==btnS){
			messageToServer("MOVE S");
			AP=AP-1;
			//System.out.println(ServerList);
			redrawGhapicsPane();
		}
		else if(e.getSource()==btnW){
			messageToServer("MOVE W");
			AP=AP-1;
			//System.out.println(ServerList);
			redrawGhapicsPane();
		}
		//Pickup Button, Sends PICKUP cmd to the Server then redraws the graphicPanel
		else if(e.getSource()==btnPickup){
			messageToServer("PICKUP");
			//System.out.println(ServerList);
			redrawGhapicsPane();
		}
		//End Trun Button, Sends the ENDTURN cmd to the server to edn your go before AP=0
		else if(e.getSource()==btnEndTurn){
			messageToServer("ENDTURN");
			//System.out.println(ServerList);
		}
	}
	
	public void messageToServer(String message){
		Client.messageToServer(message);
	}
	//Adds the ActionListeners to the Button i didnt want active before the user has connect to the server
	public void addActionListeners(){
		btnN.addActionListener(this);
		btnE.addActionListener(this);
		btnS.addActionListener(this);
		btnW.addActionListener(this);
		btnPickup.addActionListener(this);
		btnSend.addActionListener(this);
		btnEndTurn.addActionListener(this);
		
	}
	//removes the AcctionsListners from the button all but the Connect Button
	public void removeActionListeners(){
		btnN.removeActionListener(this);
		btnE.removeActionListener(this);
		btnS.removeActionListener(this);
		btnW.removeActionListener(this);
		btnPickup.removeActionListener(this);
		btnEndTurn.removeActionListener(this);
	}
	//Makes the Graphics Pane used to display Graphical information to the user 
	public void makeGraphicPane(){
		//Creates new Jpanel
		graphicsPanel = new JPanel();
		graphicsPanel.setBackground(Color.DARK_GRAY);
		graphicsPanel.setBounds(20, 11, 550, 448);
		getContentPane().add(graphicsPanel);
		graphicsPanel.setLayout(null);
		//Add a Welcoming Message to the user
		JLabel lblWelcome = new JLabel("Welcome");
		lblWelcome.setForeground(new Color(0, 0, 0));
		lblWelcome.setFont(new Font("Swiss921 BT", Font.PLAIN, 20));
		lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
		lblWelcome.setBounds(219, 160, 99, 39);
		graphicsPanel.add(lblWelcome);
		
		JLabel lblTo = new JLabel("to\r\n\r\n");
		lblTo.setForeground(new Color(0, 0, 0));
		lblTo.setFont(new Font("Swiss921 BT", Font.PLAIN, 20));
		lblTo.setHorizontalAlignment(SwingConstants.CENTER);
		lblTo.setBounds(241, 199, 46, 14);
		graphicsPanel.add(lblTo);
		
		JLabel lblTheDungeon = new JLabel("The Dungeon of Doom\r\n");
		lblTheDungeon.setForeground(new Color(0, 0, 0));
		lblTheDungeon.setFont(new Font("Swiss921 BT", Font.PLAIN, 20));
		lblTheDungeon.setHorizontalAlignment(SwingConstants.CENTER);
		lblTheDungeon.setBounds(161, 210, 212, 47);
		graphicsPanel.add(lblTheDungeon);
		
		JLabel lblPressconnect = new JLabel("Press \"Connect\" to Continue");
		lblPressconnect.setForeground(new Color(0, 0, 0));
		lblPressconnect.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblPressconnect.setHorizontalAlignment(SwingConstants.CENTER);
		lblPressconnect.setBounds(171, 252, 185, 20);
		graphicsPanel.add(lblPressconnect);
		

	}
	//Makes the TileGrid for the Graphics Pane, it displays the Map and the Usefull infomation to the User
	public void makeTileGrid(){
		
		//removes all the previous elements of the JPanel
		graphicsPanel.removeAll();
		// Check if you are a winner or Loser to Display Congratulation/LOSER message
		checkWinner();
		//Adds Seperators to he JPanel the split the data the into nice logical buits
		addSeperators();
		//add the Infomational Data Labels
		addInfo();
		//Add the Item Labels which show when you pick up an item
		addItems();
		
		//Adds the Player Icon to the Center of the Grid
		JLabel lblPlayer = new JLabel("");
		lblPlayer.setIcon(new ImageIcon("Player.png"));
		lblPlayer.setBounds(292, 192, 64, 64);
		graphicsPanel.add(lblPlayer);
		
		//Itterates The 2d tileGridArray of JLabels, Seting the Images depending on the text of the JLabel
		//Places them Every 64 pixels from X=100 and Y=0 of the graphicsPanel
		for(int i=0;i<7;i++){
			int Ycord=64*i;
			for(int j=0;j<7;j++){
				int Xcord=100+j*64;
				if(tileGridArray[i][j].getText().equals(".")){
				
					tileGridArray[i][j].setIcon(new ImageIcon("Tile.png"));
				}
				else if(tileGridArray[i][j].getText().equals("#")){
					//Image is from http://opengameart.org/forumtopic/looking-for-2d-artist-for-a-one-week-project Floor.png 
					tileGridArray[i][j].setIcon(new ImageIcon("wall2.png"));
				}
				else if(tileGridArray[i][j].getText().equals("G")){
					tileGridArray[i][j].setIcon(new ImageIcon("Tile_G.png"));
				}
				else if(tileGridArray[i][j].getText().equals("L")){
					tileGridArray[i][j].setIcon(new ImageIcon("Tile_L.png"));
				}
				else if(tileGridArray[i][j].getText().equals("X")){
					//FOG images is an Edited Image of the Night image in the from http://opengameart.org/content/tiling-background-pack-ground
					tileGridArray[i][j].setIcon(new ImageIcon("TILE_X.png"));
				}
				else if(tileGridArray[i][j].getText().equals("A")){
					tileGridArray[i][j].setIcon(new ImageIcon("Tile_A.png"));
				}
				else if(tileGridArray[i][j].getText().equals("H")){
					tileGridArray[i][j].setIcon(new ImageIcon("Tile_H.png"));
				}
				else if(tileGridArray[i][j].getText().equals("S")){
					tileGridArray[i][j].setIcon(new ImageIcon("Tile_S.png"));
				}
				else if(tileGridArray[i][j].getText().equals("P")){
					tileGridArray[i][j].setIcon(new ImageIcon("Tile_P.png"));
					tileGridArray[i][j].setOpaque(true);
				}
				else if(tileGridArray[i][j].getText().equals("E")){
					tileGridArray[i][j].setIcon(new ImageIcon("Tile_E.png"));
				}
				//Tile & Exit images i created myslef but All other images come from http://opengameart.org/users/ravenmore Fantasy Packs
				
				tileGridArray[i][j].setBounds(Xcord, Ycord, 64, 64);
				graphicsPanel.add(tileGridArray[i][j]);
				
			}
		}
		
		
	}
	//Cheks if a Winner has been Declared
	public void checkWinner() {
		if(winner!=null){
			if(winner){
				
				JLabel lblWinner = new JLabel("WINNER");
				lblWinner.setForeground(new Color(0, 250, 154));
				lblWinner.setFont(new Font("Swiss921 BT", Font.BOLD, 99));
				lblWinner.setHorizontalAlignment(SwingConstants.CENTER);
				lblWinner.setBorder(new LineBorder(new Color(0, 0, 0), 1));
				lblWinner.setBounds(0, 136, 550, 148);
				lblWinner.setOpaque(true);
				graphicsPanel.add(lblWinner);
			}
			else{
				JLabel lblLoser = new JLabel("LOSER");
				lblLoser.setForeground(new Color(210, 90,90));
				lblLoser.setFont(new Font("Swiss921 BT", Font.BOLD, 99));
				lblLoser.setHorizontalAlignment(SwingConstants.CENTER);
				lblLoser.setBorder(new LineBorder(new Color(0, 0,0), 1));
				lblLoser.setBounds(0, 136, 550, 148);
				lblLoser.setOpaque(true);
				graphicsPanel.add(lblLoser);
			
			}
		}
		
	}
	//Redraw the Graphics Pane by Going through the  ServerList Queue
	public void redrawGhapicsPane(){
		try{
			//Sleeps so the Server can have time to fill the Queue
			Thread.sleep(150);
			clearList();

		}
		catch(InterruptedException e){
			
		}
		//repaint and validate the Jframe
		repaint();
		validate();
	}
	//Empties the the ServerList Queue
	public void clearList(){
		
		//Compares the First Entry of the Queue with the Protocol of the 
		while(ServerList.size()>0){
			String serverOut= ServerList.poll();
			String[] serverSplit = serverOut.split(" ");
			if(serverSplit[0].equals("GOLD")){
				//Sets the Needed Gold, so the User knows how much to get
				//System.out.println("Set Gold");
				goldN = Integer.parseInt(serverSplit[1]);
			}
			else if(serverSplit[0].equals("STARTTURN")){
				//Informs the User that its is there Turn
				MessagetextArea.setText(MessagetextArea.getText()+"Start Turn"+"\n");
				//Reset the AP to the maximmum possible
				AP = 6-hasLantern-hasSword-hasArmour;
				if(!firstpass){
					makeTileGrid();
				}
			}
			else if(serverSplit[0].equals("ENDTURN")){
				//Informs the user that it is the end of there turn
				MessagetextArea.setText(MessagetextArea.getText()+"End Turn"+"\n");
			}
			else if(serverSplit[0].equals("LOOKREPLY")){
				//fills THe LookArray
				fillLookArray();
			}
			else if(serverSplit[0].equals("CHANGE")){
				try{
					//Send LOOK cmd to the Server 
					messageToServer("LOOK");
					//sleeps so the Server can catch up
					Thread.sleep(150);
				}
				catch(InterruptedException e){
				
				}
			}
			else if(serverSplit[0].equals("SUCCESS")){
				//do nothing
			}
			else if(serverSplit[0].equals("FAIL")){
				//chech if the user tried to move into a wall and refund them a AP point
				if(serverSplit[1].equals("can't")){
					AP=AP+1;
				}
			}
			else if(serverSplit[0].equals("MESSAGE")){
				//Gets the Message from the Server, to display in in the ChatBox
				String message="";
				for(int i=1;i<serverSplit.length;i++){
					message = message+" " +serverSplit[i];
				}
				MessagetextArea.setText(MessagetextArea.getText()+message+"\n");
			}
			else if(serverSplit[0].equals("WIN")){
				//Set Winner
				winner=true;
				//disable buttons
				removeActionListeners();
			
			}
			else if(serverSplit[0].equals("LOSE")){
				
				//Set Loser
				winner=false;
				//disable buttons
				removeActionListeners();
				
			}
			else if(serverSplit[0].equals("TREASUREMOD")){
				//Increment the current Gold of the User
				goldC=goldC+Integer.parseInt(serverSplit[1]);
			}
			else if(serverSplit[0].equals("ATTMOD")){
				//Set the hasSword true 
				hasSword=1;
			}
			else if(serverSplit[0].equals("DEFMOD")){
				//Set the hasArmour true 
				hasArmour=1;
			}
			else if(serverSplit[0].equals("HITMOD")){
				AP=0;
			}
			//System.out.println("cleared at least one element");
		}
		//reset the firstpass boolean
		if(firstpass){
			firstpass=false;
		}
	}
	//Fills the LOOK array from the LOOKReply
	public void fillLookArray(){
		
		//System.out.println("Scanning look");
		//Sets the current LookDistance
		int lookdistance = Integer.parseInt(ServerList.poll());
		char[][] lookArrayTemp = new char[7][7];
		if(lookdistance==3){
			//set hasLantern to true
			hasLantern=1;
			//fill the entire TempLookarray with elements of the lookreply
			for(int i=0;i<7;i++){
				String lookLine= ServerList.poll();
				for(int j=0;j<7;j++){
					lookArrayTemp[i][j]=lookLine.charAt(j);
				}
			}
		}
		else{
			//fills the LOOK array with "FOG" elements
			for(int k=0;k<7;k++){
				Arrays.fill(lookArrayTemp[k],'X');
			}
			//Set the rest of the Array to depending lookreply lines
			for(int i=1;i<6;i++){
				String lookLine= ServerList.poll();
				for(int j=1;j<6;j++){
					lookArrayTemp[i][j]=lookLine.charAt(j-1);
				}
			}
		}
		//checks if the new Lookreply is different from the current one, if it isn't then set LookArray=TempLookarray and remake the TileGrid
		if(!Arrays.equals(lookArray,lookArrayTemp)){
			lookArray=lookArrayTemp;
			fillTileGridArray();
			makeTileGrid();
		}
	}
	//fills the TileGridArray
	public void fillTileGridArray(){
		
		for(int i=0;i<7;i++){
			for(int j=0;j<7;j++){
				tileGridArray[i][j]=new JLabel(Character.toString(lookArray[i][j]));
			}
		}
	}
	//Adds seperators to the graphics Panel
	public void addSeperators(){
		
		JSeparator separator = new JSeparator();
		separator.setBackground(Color.BLACK);
		separator.setForeground(Color.BLACK);
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setBounds(100, 0, 6, 466);
		graphicsPanel.add(separator);

		JSeparator separator_1 = new JSeparator();
		separator_1.setForeground(Color.BLACK);
		separator_1.setBackground(Color.BLACK);
		separator_1.setBounds(0, 43, 100, 3);
		graphicsPanel.add(separator_1);
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setBackground(Color.BLACK);
		separator_2.setForeground(Color.BLACK);
		separator_2.setBounds(0, 93, 100, 3);
		graphicsPanel.add(separator_2);
		
		JSeparator separator_3 = new JSeparator();
		separator_3.setForeground(Color.BLACK);
		separator_3.setBackground(Color.BLACK);
		separator_3.setBounds(0, 464, 550, 3);
		graphicsPanel.add(separator_3);
		JSeparator separator_4 = new JSeparator();
		separator_4.setOrientation(SwingConstants.VERTICAL);
		separator_4.setForeground(Color.BLACK);
		separator_4.setBackground(Color.BLACK);
		separator_4.setBounds(547, 0, 3, 466);
		graphicsPanel.add(separator_4);
		
		JSeparator separator_5 = new JSeparator();
		separator_5.setForeground(Color.BLACK);
		separator_5.setBackground(Color.BLACK);
		separator_5.setBounds(0, 1, 550, 3);
		graphicsPanel.add(separator_5);
		
		JSeparator separator_6 = new JSeparator();
		separator_6.setBounds(1, 0, 15, 466);
		graphicsPanel.add(separator_6);
		separator_6.setOrientation(SwingConstants.VERTICAL);
		separator_6.setForeground(Color.BLACK);
		separator_6.setBackground(Color.BLACK);
	}
	//add theItems to the Graphics Panel
	public void addItems(){
		
		JLabel lblSword = new JLabel("Sword");
		lblSword.setIcon(new ImageIcon("upg_sword.png"));
		lblSword.setBounds(25, 150, 50, 50);
		graphicsPanel.add(lblSword);
		lblSword.setVisible(false);
		
		JLabel lblLantern = new JLabel("Lantern");
		lblLantern.setIcon(new ImageIcon("scroll.png"));
		lblLantern.setBounds(25, 200, 50, 50);
		graphicsPanel.add(lblLantern);
		lblLantern.setVisible(false);
		
		JLabel lblArmour = new JLabel("Armour");
		lblArmour.setIcon(new ImageIcon("shield.png"));
		lblArmour.setBounds(25, 250, 50, 50);
		graphicsPanel.add(lblArmour);
		lblArmour.setVisible(false);
		
		if(hasLantern==1){
			lblLantern.setVisible(true);
		}
		if(hasSword==1){
			lblSword.setVisible(true);
		}
		if(hasArmour==1){
			lblArmour.setVisible(true);
		}
		
	}
	//Adds the info to the graphics Panel
	public void addInfo(){
		
		JLabel lblGold = new JLabel("GOLD:");
		lblGold.setForeground(Color.WHITE);
		lblGold.setBounds(20, 0, 50, 50);
		graphicsPanel.add(lblGold);
		
		JLabel lblAp = new JLabel("AP:");
		lblAp.setForeground(Color.WHITE);
		lblAp.setBounds(20, 43, 33, 50);
		graphicsPanel.add(lblAp);
		
		JLabel lblItems = new JLabel("\tITEMS\r\n");
		lblItems.setHorizontalAlignment(SwingConstants.CENTER);
		lblItems.setForeground(Color.WHITE);
		lblItems.setBounds(25, 80, 50, 50);
		graphicsPanel.add(lblItems);
		
		JLabel lblCurrentGold = new JLabel(Integer.toString(goldC)+"/"+Integer.toString(goldN));
		lblCurrentGold.setForeground(Color.WHITE);
		lblCurrentGold.setBounds(60, 18, 46, 14);
		graphicsPanel.add(lblCurrentGold);
		
		JLabel lblCurrentap = new JLabel(Integer.toString(AP));
		lblCurrentap.setForeground(Color.WHITE);
		lblCurrentap.setBounds(41, 61, 46, 14);
		graphicsPanel.add(lblCurrentap);
	}
	//Listen to  Server
	public void listentoServer(){
		
		//Check if the ServerList is not empty
		while(true){
			try{
				if(ServerList.size()>0){
					redrawGhapicsPane();
				}
				//sleeps Then Check again
				Thread.sleep(100);
			}
			catch(InterruptedException e){
				e.printStackTrace();
			}
		}
	}
	//Resets the Data of the GUI
	public void reset(){
		graphicsPanel.removeAll();
		
		AP=6;
		goldC=0;
		hasLantern =0;
		hasSword = 0;
		hasArmour = 0;
		
		name=null;
		IP=null;
		Port=null;
		Client.clientClose();
		btnConnect.setText("Connect");
	}
}
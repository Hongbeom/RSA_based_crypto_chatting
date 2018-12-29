package Chatting;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Frame;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import java.awt.Color;
import javax.swing.JList;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JButton;
import javax.swing.JProgressBar;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class ChatUi extends JFrame {

	private JPanel contentPane;
	private JTextField Chattextfield;
	private JTextField Idtextfield;
	private JTextField IPtextfield;
	private JTextField Porttextfield;
	private String Id;
	private String IPAddress;
	private int PortNumber;
	private Sender sender;
	private StyledDocument Chattingdoc;
	private StyledDocument keyInfodoc;
	private StyledDocument Filetransferdoc;
	private Receiver receiver;
	private String mes;
	private byte[] inputobj = null;
	private byte[] message = null;
	private byte[] receivedFile = null;
	private RSAEncryption RSA;
	private RSASignature RSASig;
	private PublicKey mypublicKey;
	private PublicKey clientpublicKey;
    private PrivateKey privateKey;
    private KeyPair keyPair;
    private Key symKey;
    private Object receiveobj= null;
    private File file = null;
    private FileOutputStream stream = null;
    private String fileName = null;
    private String filePath = null;
	ServerSocket serverSocket = null;
	private EncryptedSymKey ensobj = null;
	private SymEncryption sym = null;
	private SecretKey sck = null;
	Socket socket = null;
	private EncryptedSymKey keyobj;
	private JTextField filepathtextfield = null;
	private JTextField filenametextfield = null;
	private JButton btnSetFileOption = null;
	
	/**
	 * 
	 * Launch the application.
	 */
	public static void main(String[] args) {		// main method
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatUi frame = new ChatUi();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	// Sender class - The role of sending objects to receiver class such as byte[], Public Key, EncrpytedSymKey, EncryptedSymFile
	public class Sender{			
		ObjectOutputStream obs = null;		// I use the ObjectOutputStream object in sending objects to ObjectInputStream.
		
		public Sender(Socket socket){		// When you create an object, you need a socket object that initializes the obs(ObjectOutputStream) in the sender class member variable.
			try {
				obs = new ObjectOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		

		public void sendmessage(byte[] messa) {	// method of sending byte[] of message. messa is that byte[] comes from message.getEncoded()
			try{
					byte[] encryptedmessa = RSA.RSAencryption(messa, clientpublicKey);	// Encrypt messa using clientpublicKey. encryptedemessa is encrypted byte[] of message.
					obs.writeObject(encryptedmessa);									// write encryptedmessa to obs
					obs.flush();														// send encryptedmessa to Opponent ObjectInputStream.
						
			}catch(IOException e){
				System.out.println("ServerSender Error");
				e.printStackTrace();
			}
			
		}
		
		public void sendPublicKey(PublicKey key) { // method of seneding my public key to opponent. key comes from method of RSAKeygeneration.
			try {
				obs.writeObject(key);				// Write my public key(key object) to obs
				obs.flush();						// send key to opponent
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		public void sendFile(byte[] file) {		// method of sending encrypted file using symmetric Key to opponent
			try {
				EncryptedSymFile ensFile = new EncryptedSymFile(sym.AESkeyEncrpytion(file, symKey));	//create a object of EncryptedSymFile with initializing byte[] variable of the object.
				//sym.AESkeyEncryption method carrying out encryption file using symKey(Symmetric Key). it returns the encrypted byte[] of file.
				obs.writeObject(ensFile);		// write ensFile object to obs
				obs.flush();					// send ensFile to opponent
			} catch (IOException e) {
				e.printStackTrace();
			}
	
			
		}
		
		public void sendFileSignature(byte[] signaturebyte) {	// method of sending signature of my file to opponent. signaturebyte is that signature of transferred file.
			try {
				obs.writeObject(signaturebyte);					// write signature of transferred file to obs
				obs.flush();									// send signautre to opponent
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		public void sendSymKey(Key key) {					// method of sending Symmetric key(key) to opponent. key is the symmetric key using at encrypt file to transfer.
			ensobj = new EncryptedSymKey(RSA.RSAencryption(key.getEncoded(), clientpublicKey));		// create a object of EncryptedSymKey with initializing byte[] variable of the object. 
			// RSA.RSAencryption(key.getEncoded(), clientpublicKey) means that encrypt symmetric key using opponent public key. it returns encrypted byte[] of symmetric key.
			try {
				obs.writeObject(ensobj);					// write ensobj to obs
				obs.flush();								// send ensobj to opponent's ObjectInputStream
			} catch (IOException e) {
				e.printStackTrace();
			}
			
					
		}
			
		
	}
	
	// Receiver class - The role of receiving objects from sender class such as byte[], Public Key, EncrpytedSymKey, EncryptedSymFile
	public class Receiver extends Thread{
		private Socket socket;
		ObjectInputStream ois = null;		// I use the ObjectInputStream object in receiving objects from ObjectOutputStream
		public Receiver(Socket socket){     // When you create an object, you need a socket object that initializes the ois(ObjectOutputStream) in the receiver class member variable.
			this.socket=socket;
		}
		
		public Receiver(){}
		String abc=null;
		String clientpbk = null;
		String def=null;
		@Override
		public void run() {		// method of receiving. receiver class extends Thread class because receiver should always read obejects in ObjectInputstream from opponent's objectOutputStream.

			try{	
			ois = new ObjectInputStream(socket.getInputStream());
				
			while(true){
				try {	// 1st case that object from opponent sender is object of PublicKey
					receiveobj = ois.readObject();						// read object in ois. it returns object of type "Object"
					clientpublicKey = (PublicKey)receiveobj;			// casting receiveobj to PublicKey. if error occurs(receiveobj is not the opponet's public key from sender), go to catch clause.	
					
					byte[] clientpubk = clientpublicKey.getEncoded();																								//These codes carry out
					clientpbk = "";																																	//appearing opponent's public
			        for(byte b: clientpubk) clientpbk = clientpbk+Integer.toString((b & 0xff)+0x100, 16).substring(1)+" ";											//key information in chatting UI
			        keyInfodoc.insertString(keyInfodoc.getLength(), "\nOpponent Public Key: \n"+ clientpbk, keyInfodoc.getStyle("black"));							//
			        keyInfodoc.insertString(keyInfodoc.getLength(), "\nOpponent Public Key Length : "+clientpubk.length+ " byte" , keyInfodoc.getStyle("black"));	//
			        
				}catch(Exception e) { 
					e.printStackTrace();
					System.out.println(e);
					try {	// 2st case that object from opponent sender is object of EncrpytedSymKey
						keyobj = (EncryptedSymKey)receiveobj;			// casting receiveobj to EncryptedSymkey. if error occurs(receiveobj is not the symmetric key from sender), go to catch clause.
						
						byte[] receivedKeyobj = keyobj.getEncrpytedSymKey();										//Theses codes carry out saving symmetric key from sender into class variable sck.
						byte[] decryptedSymKeyByte = RSA.RSAdecryption(receivedKeyobj, privateKey);					//
						sck = new SecretKeySpec(decryptedSymKeyByte, 0, decryptedSymKeyByte.length, "AES");			//

						
						
						
					} catch (Exception e1) {
						System.out.println(e1);
						e1.printStackTrace();
						try {	//3rd case that object from opponent sender is byte[] of encrypted message 
							inputobj = (byte[]) receiveobj;				// casting receiveobj to byte[]. if error occurs(receiveobj is not byte[] of encrypted message from sender), go to catch clause.
							byte[] decryptedByte = RSA.RSAdecryption(inputobj, privateKey);	// decrypt inputobj(byte[] of encrypted message) using private key.
							def = new String(inputobj, 0, inputobj.length, "utf-8");											//These codes carry out appeariing encrypted message and decrpyted message
							abc = new String(decryptedByte, 0, RSA.RSAdecryption(inputobj, privateKey).length, "utf-8");		//in chatting UI.
							Chattingdoc.insertString(Chattingdoc.getLength(), "\n dec: "+def, Chattingdoc.getStyle("black")); 	//
							Chattingdoc.insertString(Chattingdoc.getLength(), "\n enc:"+abc, Chattingdoc.getStyle("black"));	//
							inputobj = null;
							
						}catch(Exception e2) {
							System.out.println(e2);
							e2.printStackTrace();
							
							try { // 4th case that object from opponent sender is file signature.
									RSASignature verify = new RSASignature();
									if(verify.verifyRSASignature(receivedFile, (byte[]) receiveobj, clientpublicKey)) {		// This case is that the file has not been corrupted of changed,
										Filetransferdoc.insertString(Filetransferdoc.getLength(), 							// because verify.verifyRSASignature(receivedFile, (byte[]) receiveobj, clientpublicKey)
												"\nFile Verification: True \nFile has not been corrupted or changed!!"  , 	// return true. And appears true message in chatting UI
												Filetransferdoc.getStyle("black"));	
										
									}else if(verify.verifyRSASignature(receivedFile, (byte[]) receiveobj, clientpublicKey)){// This case is that the file has been corrupted of changed,
										Filetransferdoc.insertString(Filetransferdoc.getLength(), 							// because verify.verifyRSASignature(receivedFile, (byte[]) receiveobj, clientpublicKey)
												"\nFile Verification: False \nFile has been corrpted or changed."  ,		// return false. And appears false message in chatting UI
												Filetransferdoc.getStyle("black"));
									}
									
									receivedFile=null;		// It is important to set null in receivedFile because receivedFile is the class member variable.
								
							    
							    
							} catch(Exception e3){
								System.out.println(e3);
								e3.printStackTrace();
								try {	// 5th case that object from opponent sender is object of EncryptedSymFile.
									SymEncryption dec = new SymEncryption();	
									EncryptedSymFile recensFileobj = (EncryptedSymFile) receiveobj; // casting receiveobj to EncryptedSymFile. 
																									// if error occurs(receiveobj is not file encrypted by symmetric key from sender), go to catch clause.		
									byte[] encFile = recensFileobj.getEncryptedFile();				// byte[] of encrypted file
									byte[] decFile = dec.AESdecryption(encFile, sck);				// decrypt byte[] of encrypted file using AESdecrpytion method. sck is symmetric key received from opponent.
									
									if(filePath==null) {		// case that user does not set the file path to receive
										stream = new FileOutputStream(new File("C:\\Users\\idd74\\Desktop\\Encrypted_Recevied_File"));
										// Initialize member variable 'stream' with default path(C:\\Users\\idd74\\Desktop\\Encrypted_Recevied_File)
										stream.write(encFile);																								// create encrypted file to default path
										Filetransferdoc.insertString(Filetransferdoc.getLength(), 															
									    		"\n****** File received !!******\nEncrypted File Name: Encrypted_Recevied_File\nEncrypted_File Path:"		// These codes carry out appearing encrypted file
									    		+ " C:\\Users\\idd74\\Desktop\nEncrypted File Size: "+encFile.length +"bytes", 								// information in chatting UI
									    		Filetransferdoc.getStyle("black"));																			//
										
										byte[] secretKeybb = sck.getEncoded();																				// These codes carry out appearing symmetric key
										String secretKeystring = "";																						// information in chatting UI
										for(byte b: secretKeybb) secretKeystring = secretKeystring+Integer.toString((b & 0xff)+0x100, 16).substring(1)+" ";	//
								        Filetransferdoc.insertString(Filetransferdoc.getLength(), 															//
									    		"\nSymmetric Key: "+secretKeystring +"Symmetric Key Size: " + secretKeybb.length +"byte", 					//
									    		Filetransferdoc.getStyle("black"));
									}
									else if(filePath != null) {	// case that user set the file path to receive before file transfer
										stream = new FileOutputStream(new File(filePath+"\\"+"Encrypted_"+fileName));
										// Initialize member variable 'stream' with specified path(filePath)
										stream.write(encFile);																								// create encrypted file to specified path
										
									    Filetransferdoc.insertString(Filetransferdoc.getLength(), 															// These codes carry out appearing encrypted file
									    		"\n****** File received!! ******\nEncrypted File Name: " +"Encrypted_"+fileName+"\nEncrypted File Path: "	// information in chatting UI
											    		+ filePath+"\nEncrypted File Size: "+encFile.length +"bytes", 										//
									    		Filetransferdoc.getStyle("black"));																			//
									    
									    byte[] secretKeybb = sck.getEncoded();																				// These codes carry out appearing symmetric key
										String secretKeystring = "";																						// information in chatting UI
								        for(byte b: secretKeybb) secretKeystring = secretKeystring+Integer.toString((b & 0xff)+0x100, 16).substring(1)+" ";	//
								        Filetransferdoc.insertString(Filetransferdoc.getLength(), 															//
									    		"\nSymmetric Key: "+secretKeystring +"\nSymmetric Key Size: " + secretKeybb.length +"byte", 				//
									    		Filetransferdoc.getStyle("black"));																			//
									}
									
									
								if(filePath==null) {	// case that user does not set the file path to receive
									receivedFile = decFile;																									// save byte[] of decrpyted file to receivedFile			
									stream = new FileOutputStream(new File("C:\\Users\\idd74\\Desktop\\Recevied_File"));
									// Initialize member variable 'stream' with default path(C:\\Users\\idd74\\Desktop\\Recevied_File)
									stream.write(decFile);																									// create decrypted file to default path
								    filepathtextfield.setText(null);			// These codes carry out controlling UI's contents
								    filenametextfield.setName(null);			//
								    filepathtextfield.setEnabled(true);			//
								    filenametextfield.setEnabled(true);			//
								    btnSetFileOption.setEnabled(true);			//
								    Filetransferdoc.insertString(Filetransferdoc.getLength(), 																// These codes carry out appearing decrypted file
								    		"\nDecrypted_File Name: Recevied_File\nDecrypted File Path:"													// information in chatting UI
								    		+ " C:\\Users\\idd74\\Desktop" + "\nDecrypted File Size: "+decFile.length +"bytes" + "\n******************", 	//
								    		Filetransferdoc.getStyle("black"));																				//
								}
								else if(filePath != null) {	// case that user set the file path to receive before file transfer
									receivedFile = decFile;																									// save byte[] of decrpyted file to receivedFile
									stream = new FileOutputStream(new File(filePath+"\\"+fileName));		
									// Initialize member variable 'stream' with user specified path(filePath)
									stream.write(decFile);																									// create decrypted file to specified path(filePath)
								    filepathtextfield.setText(null);			// These codes carry out controlling UI's contents
								    filenametextfield.setText(null);			//																	
								    filepathtextfield.setEnabled(true);			//
								    filenametextfield.setEnabled(true);			//
								    btnSetFileOption.setEnabled(true);			//
								    Filetransferdoc.insertString(Filetransferdoc.getLength(), 																// These codes carry out appearing decrypted file
								    		"\nDecrypted_File Name: " +fileName+"\nDecrypted_File Path: "													// information in chatting UI
										    		+ filePath+"\nFile Size: "+decFile.length +"bytes" + "\n******************", 							//
								    		Filetransferdoc.getStyle("black"));
								    filePath = null;							// set filePath and fileName null.
								    fileName = null;							// for next file.
								}
								
								
							}catch(Exception e4) {
								System.out.println(e4);
								e4.printStackTrace();
							}finally {
							    stream.close();		// close filestream
							}
								}
							
							
							
						}
					}
					
				}
				
				
				
				
				
				}
			}catch(NullPointerException | SocketException e){
				System.out.println("Chatting ended");
			}catch(IOException e){
				System.out.println("ServerReader Error");
				e.printStackTrace();
			}
		}

	}
	
	

	/**
	 * Create the frame.
	 */
	public ChatUi() {			
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 597, 1017);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JCheckBox chckbxClient = new JCheckBox("Client");			// client check box
		chckbxClient.setBounds(12, 47, 71, 23);					
		contentPane.add(chckbxClient);
		
		JCheckBox chckbxServer = new JCheckBox("Server");			// server check box
		chckbxServer.setBounds(87, 47, 71, 23);
		contentPane.add(chckbxServer);
		
		
		JLabel lblMode = new JLabel("Mode");
		lblMode.setBounds(12, 10, 126, 20);
		contentPane.add(lblMode);
		
		JLabel lblNewLabel = new JLabel("Communication Info");
		lblNewLabel.setBounds(10, 141, 126, 15);
		contentPane.add(lblNewLabel);
		
		JLabel lblKeyInfo = new JLabel("Key Info");					// Key info area
		lblKeyInfo.setBounds(12, 239, 57, 15);
		contentPane.add(lblKeyInfo);
		
		JButton btnkeygeneration = new JButton("Key generation");	// Key generation button
		btnkeygeneration.setBounds(103, 414, 172, 23);
		contentPane.add(btnkeygeneration);
		
		JButton sendPublicKey = new JButton("Send public key");		// Send Public Key button
		sendPublicKey.setBounds(326, 414, 172, 23);
		contentPane.add(sendPublicKey);
		
		JLabel lblChatting = new JLabel("Chatting");
		lblChatting.setBounds(12, 447, 57, 15);
		contentPane.add(lblChatting);
		
		JButton btnSend = new JButton("Send");						// Send button
		btnSend.setBounds(475, 680, 97, 23);
		contentPane.add(btnSend);
		
		Chattextfield = new JTextField();							// Chatting text field
		Chattextfield.setBounds(12, 680, 451, 23);
		contentPane.add(Chattextfield);
		Chattextfield.setColumns(10);
		
		JLabel lblFileTransfer = new JLabel("File transfer");
		lblFileTransfer.setBounds(12, 738, 71, 15);
		contentPane.add(lblFileTransfer);
		
		JButton sendFile = new JButton("Send file");				// Send File button
		sendFile.setBounds(475, 915, 97, 23);
		contentPane.add(sendFile);
		
		JButton btnFindfile = new JButton("Find file");				// Find File button
		btnFindfile.setBounds(475, 870, 97, 23);
		contentPane.add(btnFindfile);
		
		Idtextfield = new JTextField();								// ID text field
		Idtextfield.setBounds(266, 34, 166, 21);
		contentPane.add(Idtextfield);
		Idtextfield.setColumns(10);
		
		JButton btnConnect = new JButton("Connect");				// Connect button
		btnConnect.setBounds(444, 34, 97, 114);
		contentPane.add(btnConnect);
		
		JLabel lblId = new JLabel("ID");
		lblId.setBounds(266, 10, 97, 15);
		contentPane.add(lblId);
		
		IPtextfield = new JTextField();								// IP Address text field
		IPtextfield.setBounds(266, 80, 166, 21);
		contentPane.add(IPtextfield);
		IPtextfield.setColumns(10);
		
		JLabel lblIpAddress = new JLabel("IP Address");
		lblIpAddress.setBounds(266, 58, 97, 15);
		contentPane.add(lblIpAddress);
		
		JLabel lblPortNumber = new JLabel("Port Number");
		lblPortNumber.setBounds(266, 106, 97, 15);
		contentPane.add(lblPortNumber);
		
		Porttextfield = new JTextField();							// Port Number text field
		Porttextfield.setColumns(10);
		Porttextfield.setBounds(266, 131, 166, 21);
		contentPane.add(Porttextfield);
		
		JScrollPane commInfoScroll = new JScrollPane();
		commInfoScroll.setBounds(12, 166, 545, 63);
		contentPane.add(commInfoScroll);
		
		JTextPane CommInfo = new JTextPane();						// Communication Info area
		commInfoScroll.setViewportView(CommInfo);
		StyledDocument CommInfodoc = CommInfo.getStyledDocument();
		Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		Style s = CommInfodoc.addStyle("black", def);
		StyleConstants.setForeground(s,Color.black);
		
		JScrollPane FiletransferScroll = new JScrollPane();
		FiletransferScroll.setBounds(12, 848, 451, 120);
		contentPane.add(FiletransferScroll);
		
		JTextPane Filetransfer = new JTextPane();					// File transfer area
		FiletransferScroll.setViewportView(Filetransfer);
		Filetransferdoc = Filetransfer.getStyledDocument();
		Style fsd = Filetransferdoc.addStyle("black", def);
		StyleConstants.setForeground(fsd, Color.black);
		
		JScrollPane ChattiingScroll = new JScrollPane();
		ChattiingScroll.setBounds(6, 470, 558, 200);
		contentPane.add(ChattiingScroll);
		
		JTextPane Chatting = new JTextPane();						// Chatting area
		ChattiingScroll.setViewportView(Chatting);
		Chattingdoc = Chatting.getStyledDocument();
		Style cs = Chattingdoc.addStyle("black", def);
		StyleConstants.setForeground(cs,Color.black);
		
		JScrollPane keyinfoscrollPane = new JScrollPane();
		keyinfoscrollPane.setBounds(12, 264, 556, 144);
		contentPane.add(keyinfoscrollPane);
		
		JTextPane keyInfo = new JTextPane();						// Key Info area
		keyinfoscrollPane.setViewportView(keyInfo);
		
		JLabel lblFilePathTo = new JLabel("File Path to Receive");
		lblFilePathTo.setBounds(12, 763, 136, 15);
		contentPane.add(lblFilePathTo);
		
		JLabel lblFileNameTo = new JLabel("File Name to Receive");
		lblFileNameTo.setBounds(12, 801, 132, 15);
		contentPane.add(lblFileNameTo);
		
		filepathtextfield = new JTextField();						// File Path text field
		filepathtextfield.setBounds(151, 760, 254, 21);
		contentPane.add(filepathtextfield);
		filepathtextfield.setColumns(10);
		
		filenametextfield = new JTextField();						// File Name text field
		filenametextfield.setBounds(151, 798, 254, 21);
		contentPane.add(filenametextfield);
		filenametextfield.setColumns(10);
		
		btnSetFileOption = new JButton("Set File Option");			// Set File Option button
		btnSetFileOption.setBounds(427, 759, 137, 63);
		contentPane.add(btnSetFileOption);
		keyInfodoc = keyInfo.getStyledDocument();
		Style ks = keyInfodoc.addStyle("black", def);
		StyleConstants.setForeground(ks, Color.black);
		
		
		
		//action listener
		chckbxClient.addActionListener(new ActionListener() {		// Client check box action listener
			public void actionPerformed(ActionEvent e) {
				if(chckbxClient.isSelected()) {
					chckbxServer.setEnabled(false);
					
					
				}
				else {
					chckbxServer.setEnabled(true);
					
				}
					
			}
		});
		
		
		chckbxServer.addActionListener(new ActionListener() {		// Server check box action listener
			public void actionPerformed(ActionEvent e) {
				if(chckbxServer.isSelected()) {
					chckbxClient.setEnabled(false);
					lblIpAddress.setEnabled(false);
					IPtextfield.setEnabled(false);
				}
				else {
					chckbxClient.setEnabled(true);
					lblIpAddress.setEnabled(true);
					IPtextfield.setEnabled(true);
					}
			}
		});
		
		
		btnConnect.addActionListener(new ActionListener() {			// Connect button action listener
			public void actionPerformed(ActionEvent e) { 
				IPAddress = IPtextfield.getText();
				PortNumber = Integer.parseInt(Porttextfield.getText());
				Id=Idtextfield.getText();
				if(chckbxServer.isSelected()){						// Connection in Server
				try{
					serverSocket = new ServerSocket();																		// Initialize serverSocket
					serverSocket.bind(new InetSocketAddress(PortNumber));													// Binding port number (comes from user) to serverSocket
					socket = serverSocket.accept();																			// connect with client
					
					CommInfodoc.insertString(CommInfodoc.getLength(), "Connected!", CommInfodoc.getStyle("black"));			// These codes carry out 
					CommInfodoc.insertString(CommInfodoc.getLength(), "\n"+"Client IP Address: "							// appearing connection information
					+socket.getInetAddress().toString(), CommInfodoc.getStyle("black"));									// in chatting UI
				
					Receiver receiver = new Receiver(socket);																// create object of Receiver class
					receiver.start();																						// running thread method of Receiver class
					
					
					sender = new Sender(socket);																			// create object of Sender class

					
				}catch(IOException e1){
					System.out.println("Server Error occured");
					e1.printStackTrace();
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}finally{
					try{
						if(serverSocket!=null) serverSocket.close();
					}catch(IOException e1){
						System.out.println("");
						e1.printStackTrace();
					}
				}
				}
				else if(chckbxClient.isSelected()){		// Connection in Client
					
					try{
						socket =new Socket(); 																				// initialize socket.
						socket.connect(new InetSocketAddress(IPAddress, PortNumber));										// connect with server using IP address and port number.
						CommInfodoc.insertString(CommInfodoc.getLength(), "Connected!", CommInfodoc.getStyle("black"));		// appearing connection information in chatting UI
				
						
					
						sender = new Sender(socket);																		// create object of Sender class
						
						
					
						receiver = new Receiver(socket);																	// create object of Receiver class
						receiver.start();																					// running thread method of Receiver class
						
					}catch(IOException e1){
						try {
							CommInfodoc.insertString(CommInfodoc.getLength(), "Client Error!", CommInfodoc.getStyle("black"));
						} catch (BadLocationException e2) {
							e2.printStackTrace();
						}
						e1.printStackTrace();
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		
		btnSend.addActionListener(new ActionListener() {	// Send button actionListener
			public void actionPerformed(ActionEvent e) {
				mes = "["+Id+"]: " +Chattextfield.getText();															// message is that [ID]:~~~
				Chattextfield.setText(null);					
				try {
					message=mes.getBytes("utf-8");																		// convert message to byte[]		
					Chattingdoc.insertString(Chattingdoc.getLength(), "\n"+mes, Chattingdoc.getStyle("black"));			// show my message in chatting UI
					sender.sendmessage(message);																		// send message using sender.sendmessage() method
					message = null;												
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				
			}
		});
		
		btnFindfile.addActionListener(new ActionListener() {	// Find File button actionListener
			public void actionPerformed(ActionEvent arg0) {
				
				Frame f = new Frame();
				FileDialog fileOpen = new FileDialog(f , "Open File", FileDialog.LOAD);		// I use filedialog to select file

		        f.setVisible(false);

		        fileOpen.setDirectory("c:\\jdk1.5");

		        fileOpen.setVisible(true);													
		        if(fileOpen.getFile() == null) return;
		        
		        file = new File(fileOpen.getDirectory() +"/" +fileOpen.getFile());			// Initialize file using selected file in filedialog
		        

		        

		        
		        try {
		        	Filetransferdoc.insertString(Filetransferdoc.getLength(), "\n***File to transfer Information***", Filetransferdoc.getStyle("black"));		// These codes carrying out
					Filetransferdoc.insertString(Filetransferdoc.getLength(), "\nFile Path: "+file.getAbsolutePath(), Filetransferdoc.getStyle("black"));		// showing information of
					Filetransferdoc.insertString(Filetransferdoc.getLength(), "\nFile Name: "+file.getName(), Filetransferdoc.getStyle("black"));				// file to transfer
					Filetransferdoc.insertString(Filetransferdoc.getLength(), "\n**********************************", Filetransferdoc.getStyle("black"));		//
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
		        
		        
		        
		        

		    
				
			}
		});
		
		
		btnkeygeneration.addActionListener(new ActionListener() {		// Key generation button actionListener
			public void actionPerformed(ActionEvent arg0) {
				
				RSA = new RSAEncryption();								// Initialize RSA
				keyPair = RSA.RSAkeygenerate();							// Initialize keyPair using RSAkeygenerate() method. this method returns keyPair.
				mypublicKey = keyPair.getPublic();						// Initialize mypublicKey using keyPair
				privateKey = keyPair.getPrivate();						// Initialize privateKey using keyPair
				byte[] mypubk = mypublicKey.getEncoded();																									// These codes carry out showing information of
		        byte[] prik = privateKey.getEncoded();																										// my public key(RSA) and private key(RSA) in
		        String pbk = "";																															// chatting UI
		        for(byte b: mypubk) pbk = pbk+Integer.toString((b & 0xff)+0x100, 16).substring(1)+" ";														//
		        String pik = "";																															//
		        for(byte b: prik) pik = pik+Integer.toString((b & 0xff)+0x100, 16).substring(1)+" ";														//
		        																																			//
		        																																			//
		        try {																																		//
					keyInfodoc.insertString(keyInfodoc.getLength(), "Publick Key: \n"+pbk, keyInfodoc.getStyle("black"));									//
					keyInfodoc.insertString(keyInfodoc.getLength(), "\nPublic Key Length : "+mypubk.length+ " byte" , keyInfodoc.getStyle("black"));		//
					keyInfodoc.insertString(keyInfodoc.getLength(), "\n"+ "Private Key: \n"+pik, keyInfodoc.getStyle("black"));								//
					keyInfodoc.insertString(keyInfodoc.getLength(), "\nPrivate Key Length : "+prik.length+ " byte", keyInfodoc.getStyle("black"));			//
																																							
				} catch (BadLocationException e) {																											
					e.printStackTrace();																													
				}
		 
		        
				
			}
		});
		
		sendFile.addActionListener(new ActionListener() {		// Send File button actionListner
			public void actionPerformed(ActionEvent arg0) {
				sym = new SymEncryption();						// initialize sym
				symKey = sym.AESkeygeneration();				// initialize symKey using sym. AESkeygeneration() method generate symmetric key and returns it
				sender.sendSymKey(symKey);						// send symmetric key first
				RSASig = new RSASignature();					// initialize RSASig
				Path path = Paths.get(file.getAbsolutePath());	// create path using file path comes from file.getAbsolutePath()
				byte[] data = null;								
				try {
					data = Files.readAllBytes(path);			// initialize data with byte[] of file.
				} catch (IOException e) {
					e.printStackTrace();
				}
				byte[] signature = RSASig.generationRSASignature(data, privateKey);		// initialize signature using RSASig.generationRSASignature method. 
																						// this method create AES signature of file using private key and returns the signature
				
				sender.sendFile(data);							// send byte[] of file secondly
				
				
				
				
				
				
				try {
					Filetransferdoc.insertString(Filetransferdoc.getLength(), "\n****** Transfer completed!! ******", Filetransferdoc.getStyle("black"));				// These codes carry out showing
					byte[] symKeybb = symKey.getEncoded();																												// information of symmetric key and
					String symKeystring = "";																															// "Transfer completed" in chatting UI
			        for(byte b: symKeybb) symKeystring = symKeystring+Integer.toString((b & 0xff)+0x100, 16).substring(1)+" ";											//
					Filetransferdoc.insertString(Filetransferdoc.getLength(), "\nSymmetric Key: " + symKeystring, Filetransferdoc.getStyle("black"));					//
					Filetransferdoc.insertString(Filetransferdoc.getLength(), "\nSymmetric Key Size: " + symKeybb.length + "byte", Filetransferdoc.getStyle("black"));	//
					Filetransferdoc.insertString(Filetransferdoc.getLength(), "\n**********************************", Filetransferdoc.getStyle("black"));				//
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally {
					sender.sendFileSignature(signature);			// finally, send signature of file that transferred.
				}

				
			}
		});
		
		btnSetFileOption.addActionListener(new ActionListener() {	// Set File Option button actionListener
			public void actionPerformed(ActionEvent arg0) {
				filePath = filepathtextfield.getText();				// initialize filePath
				fileName = filenametextfield.getText();				// initialize fileName
				
				filepathtextfield.setEnabled(false);
				filenametextfield.setEnabled(false);
				btnSetFileOption.setEnabled(false);
			}
		});
		
		
		sendPublicKey.addActionListener(new ActionListener() {		// Send Public Key button actionListener
			public void actionPerformed(ActionEvent e) {
				sender.sendPublicKey(mypublicKey);					// send my public key to opponent
				
			}
		});
	}
}

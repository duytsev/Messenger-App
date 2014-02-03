package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JTextArea;
import javax.swing.ListModel;

import Msg.Message;
import Msg.MessageUtils;

import date_helpers.dateHelper;


public class MClient {
	
	private final int PORT = 7777;
	private String adress = "127.0.0.1";
	private String name = null;
	private ArrayList<String> users = new ArrayList<String>();
	
	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	private Message msg = null;
	private String inpStr;
	
	private Thread ml;
	
	public MClient(String name, String adress) {
		this.name = name;
		this.adress = adress;
	}
	
	public void Start() throws ConnectException {
		try {
			InetAddress ipAdress = InetAddress.getByName(adress);
			
		    socket = new Socket(ipAdress, PORT);
			
		    oos = new ObjectOutputStream(socket.getOutputStream());
		    ois = new ObjectInputStream(socket.getInputStream());
			
		    msg = new Message(MessageUtils.CONNECTED, this.name);
		    msg.setText(name + " connected, ip: " + 
							socket.getInetAddress().toString().replaceAll("\\/", "")  
							+ "(" + dateHelper.getDate() + ")" );
		    oos.writeObject(msg);
			
		    

			
		} catch (Exception e) {
			System.out.println("Cannot connect to the server");
			e.printStackTrace();
			throw new ConnectException();
		}
	}
	
	
	public void listenMsg(JTextArea ta) throws IOException {
		ml = new Thread(new MsgListener(socket, ois, oos, ta));
		ml.start();
	}
	
	
	public void sendMsg(String inpStr) {
		if(socket.isConnected()) {
			Message m = new Message(MessageUtils.MSG, name);
			m.setText(m.getName() + "(" + dateHelper.getCurrentTime() +"): " + inpStr);
			try {
				oos.writeObject(m);
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
	}
	public void disconnect(){
		try {
			msg = new Message(MessageUtils.DISCONNECTED, name);
			msg.setText(name + " disconnected" + "(" + dateHelper.getCurrentTime() + ")" );
			oos.writeObject(msg);
			oos.writeObject(null);
			//ois.close();
			//oos.close();
			//socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getName() {
		return this.name;
	}
}

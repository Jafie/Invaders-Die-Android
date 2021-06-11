package org.jafie.Invaders_Die_Android;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pInfo;
import android.util.Log;

/**
 * Manage socket connection, streams creation and data sent and received.
 * Multiple thread class.
 * @author Silver
 *
 */
public class SocketManager extends Invader_Die_Main_Activity {

	private static final int SOCKET_TIMEOUT = 5000;

	private static ServerSocket serverSocket8988 = null;
	private static ServerSocket serverSocket8956 = null;
	private static Socket clientSocket8988 = null;
	private static Socket clientSocket8956 = null;
	private static Socket socket8988 = null;
	private static Socket socket8956 = null;

	private static OutputStream out8988 = null;
	private static OutputStream out8956 = null;
	private static InputStream in8988 = null;
	private static InputStream in8956 = null;
	
	private static ArrayList<TowerWait> towerWaits = null;
	
	private boolean connection8988 = false;
	private boolean connection8956 = false;
	
	
	WifiP2pInfo info;
	private static Context context;
	//private static ProgressDialog progressDialog;
	
	/**
	 * Default Constructor
	 */
	public SocketManager() {
		this.info = null;
		this.context = null;
		setTowerWaits(new ArrayList<TowerWait>());
	}
	
	public SocketManager(Context context, WifiP2pInfo info) {
		this.info = info;
		this.context = context;
		//this.progressDialog = progressDialog;
		
		setTowerWaits(new ArrayList<TowerWait>());
	}

	/**
	 * Creation of a thread to send data.
	 * @param data
	 */
	public void sendData(String data) {
		new Thread(new OutputThread(data)).start();
	}
	
	/**
	 * Create threads for socket connection of the network group owner.
	 */
	public void connectOwner() {
		new Thread(new ConnectServerSocket8988()).start();
		new Thread(new ConnectServerSocket8956()).start();
	}

	/**
	 * Create threads for socket connection of the network client.
	 */
	public void connectNoOwner() {
		new Thread(new ConnectClientSocket8988()).start();
		new Thread(new ConnectClientSocket8956()).start();
	}

	/**
	 * Safely disconnect all sockets.
	 */
	public void disconnectSockets() {

		try {
			if (out8988 != null)
				out8988.close();
			if (out8956 != null)
				out8956.close();
			if (in8988 != null)
				in8988.close();
			if (in8956 != null)
				in8956.close();

			if (clientSocket8988 != null)
				clientSocket8988.close();
			if (clientSocket8956 != null)
				clientSocket8956.close();
			if (socket8988 != null)
				socket8988.close();
			if (socket8956 != null)
				socket8956.close();

			if (serverSocket8988 != null)
				serverSocket8988.close();
			if (serverSocket8956 != null)
				serverSocket8956.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get list of towers waiting to be displayed.
	 * @return ArrayList<TowerWait>
	 */
	public ArrayList<TowerWait> getTowerWaits() {
		return towerWaits;
	}

	/**
	 * Set list of towers waiting to be displayed.
	 * @param towerWaits
	 */
	public void setTowerWaits(ArrayList<TowerWait> towerWaits) {
		SocketManager.towerWaits = towerWaits;
	}

	/**
	 * Thread used to send data through the network
	 * @author Silver
	 *
	 */
	public class OutputThread implements Runnable {
		
		String data = "";
		
		public OutputThread(String data) {
			this.data = data;
		}

		@Override
		public void run() {
			try {
				//Different output stream for server and client
				if (info.isGroupOwner) {
					Log.i("Data Sent", data);
					out8956.write(data.getBytes());
				} else {
					Log.i("Data Sent", data);
					out8988.write(data.getBytes());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	
	/**
	 * Group owner socket 8988 creation
	 * @author Silver
	 */
	public class ConnectServerSocket8988 implements Runnable {

		@Override
		public void run() {

			try {
				serverSocket8988 = new ServerSocket(8988);
				Log.i("Network - Sockets", "Socket 1 Waiting Connection");
				clientSocket8988 = serverSocket8988.accept(); //Wait for connection
				Log.i("Network - Sockets", "Socket 1 Connected");

				in8988 = clientSocket8988.getInputStream();
				//Start the input thread for this socket.
				new Thread(new InputThread8988()).start();  
				connection8988 = true;

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				//endConnect();
			}
		}
	}
	
	/**
	 * Group owner socket 8956 creation
	 * @author Silver
	 */
	public class ConnectServerSocket8956 implements Runnable {

		@Override
		public void run() {

			try {
				serverSocket8956 = new ServerSocket(8956);
				Log.i("Network - Sockets", "Socket 2 Waiting Connection");
				clientSocket8956 = serverSocket8956.accept(); //Wait for connection
				Log.i("Network - Sockets", "Socket 2 Connected");

				out8956 = clientSocket8956.getOutputStream();
				connection8956 = true;
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				//endConnect();
			}
		}
	}

	/**
	 * Socket 8988 connection from the client side
	 * @author Silver
	 */
	public class ConnectClientSocket8988 implements Runnable {

		@Override
		public void run() {
			String host = info.groupOwnerAddress.getHostAddress();
			int port8988 = 8988;
			
			socket8988 = new Socket();			
			Log.i("Network - Sockets", "Socket 1 Attempt Connection");
			//Try to connect socket while it is not connected.
			while (!socket8988.isConnected()) {
				try {
					socket8988 = null;
					socket8988 = new Socket();
					socket8988.bind(null);
					socket8988.connect((new InetSocketAddress(host, port8988)),
							SOCKET_TIMEOUT);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Log.i("Network - Sockets", "Socket 1 Connected");

			try {
				out8988 = socket8988.getOutputStream();
				connection8988 = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Socket 8988 connection from the client side
	 * @author Silver
	 */
	public class ConnectClientSocket8956 implements Runnable {

		@Override
		public void run() {
			String host = info.groupOwnerAddress.getHostAddress();
			int port8956 = 8956;

			socket8956 = new Socket();
			Log.i("Network - Sockets", "Socket 2 Attempt Connection");
			//Try to connect socket while it is not connected.
			while (!socket8956.isConnected()) {
				try {
					socket8956 = null;
					socket8956 = new Socket();
					socket8956.bind(null);
					socket8956.connect((new InetSocketAddress(host, port8956)),
							SOCKET_TIMEOUT);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}			
			Log.i("Network - Sockets", "Socket 2 Connected");

			try {
				in8956 = socket8956.getInputStream();
				connection8956 = true;
				new Thread(new InputThread8956()).start(); //Start the input thread for this socket.
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Input stream reading for socket 8988.
	 * @author Silver
	 */
	public class InputThread8988 implements Runnable {

		@Override
		public void run() {

			while (clientSocket8988.isConnected()) {
				byte buffer[] = new byte[1024];
				try {
					//While there is data to be read.
					while (in8988.read(buffer) != -1) {
						String text = new String(buffer); //Change bytes into string						
						String[] splittext = text.split(";"); //"Parsing"
						Log.i("Data Received", text);
						//Add a new tower to be created in the waiting list.
						getTowerWaits().add(new TowerWait(Float.parseFloat(splittext[1]),
								Float.parseFloat(splittext[2]), Integer.parseInt(splittext[3])));							
					}
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
		}
	}
	
	/**
	 * Input stream reading for socket 8956.
	 * @author Silver
	 */
	public class InputThread8956 implements Runnable {

		@Override
		public void run() {

			while (socket8956.isConnected()) {
				byte buffer[] = new byte[1024];
				try {
					//While there is data to be read.
					while (in8956.read(buffer) != -1) {
						final String text = new String(buffer); //Change bytes into string
						final String[] splittext = text.split(";"); //"Parsing"
						Log.i("Data Received", text);
						//Add a new tower to be created in the waiting list.
						getTowerWaits().add(new TowerWait(Float.parseFloat(splittext[1]),
								Float.parseFloat(splittext[2]), Integer.parseInt(splittext[3])));					
					}
				} catch (IOException e) {
						e.printStackTrace();
				}
			}			
		}
	}

	/**
	 * Return a boolean to check if both sockets are connected.
	 * @return boolean
	 */
	public boolean isConnected() {
		if(connection8988 && connection8956) return true;
		else return false;
	}
}

package de.kp.gate;

import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;

import java.io.*;
import java.net.*;
import java.util.Vector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class TextAServer {

	private ServerSocket serverSocket;
	
	private boolean stopped = false;
	private Vector<Thread> serverThreads;

	private static GATEWrapper gate;
	
	public TextAServer(String home, int port) {

		try {
			/*
			 * Initialize GATE
			 */
			gate = new GATEWrapper(home);

			/*
			 * Creating a server socket
			 */
			serverSocket = new ServerSocket(port);			
			System.out.println("Socket Server at: " + serverSocket);
			
			this.serverThreads = new Vector<Thread>();

		} catch (GateException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		}

	}
	
	public void run() {
		
		if (serverSocket == null) return;
		System.out.println("Server started");
		
		while (this.stopped == false) {
			try {
				
				Socket clientSocket = this.serverSocket.accept();
				serverThreads.add(new ServerThread(clientSocket));
				
			} catch (Exception e) {
				e.printStackTrace();
				
			}
		}
	}
	
	public void stop() {
		
		this.stopped = true;
		terminate();
		
		try {
			this.serverSocket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void terminate() {
		
		for (Thread serverThread:serverThreads) {
			if (serverThread.isAlive()) serverThread.interrupt();
		}
		
	}
	
	private class ServerThread extends Thread {
		
		private Socket clientSocket;
		
		public ServerThread(Socket socket) {
			
			this.clientSocket = socket;
			start();
			
		}
		
		public void run() {
			
			try {
			
				/*
				 * Receive
				 */
				 ObjectInputStream is = new ObjectInputStream(this.clientSocket.getInputStream());
				 String message = (String) is.readObject();

				 //System.out.println("Incoming: " + message);
				 
				 /*
				  * Annotate
				  */
				 String response = gate.getAnnotation(message);
				 //System.out.println("RESPONSE: " + response);
				 
				 /*
				  * Send
				  */
				 ObjectOutputStream os = new ObjectOutputStream(this.clientSocket.getOutputStream());
				 os.writeObject(response);

				 is.close();
				 os.close();

				 this.clientSocket.close();

			} catch (IOException e) {
				e.printStackTrace();

			} catch (ResourceInstantiationException e) {
				e.printStackTrace();
			
			} catch (ExecutionException e) {
				e.printStackTrace();
			
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			
		}
		
	}
	
	public static void main(String[] args) {

		/*
		 * Welcome message
		 */
		System.out.println("");
		System.out.println("The GATE Socket Server by (c) Dr. Krusche & Partner PartG");
		
		System.out.println("");
		System.out.println("");

		/*
		 * This annotator accepts a file name as a single input
		 */
		if ((args == null) || (args.length == 0)) {
			
			System.out.println("No parameters provided.");
			System.exit(0);
			
			return;
		
		}

		String port = null;
		String home = null;
		
		/* 
		 * Create options
		 */
		Options options = new Options();
		
		options.addOption("p", true, "Server Port");	
		options.addOption("g", true, "GATE Home");
		
		try {
			/*
			 * Create parser and parse
			 */
			CommandLineParser parser = new PosixParser();
			CommandLine cli = parser.parse(options, args);
			
			/*
			 * Configuration file
			 */
			if (cli.hasOption("p")) {
				port = cli.getOptionValue("p");
			}

			if (cli.hasOption("g")) {
				home = cli.getOptionValue("g");
			}
			
			if ((home == null) || (port == null)) {
				
				System.out.println("No parameters provided");
				System.exit(0);
				
				return;				
			
			}
			
			TextAServer server = new TextAServer(home, Integer.valueOf(port));
			server.run();
			
		} catch(ParseException e) {
			System.out.println(e.getMessage());

		} finally {
			System.exit(0);
		}

	}

}
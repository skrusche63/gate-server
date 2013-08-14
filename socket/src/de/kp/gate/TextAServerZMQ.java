package de.kp.gate;

import gate.util.GateException;

import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.jeromq.ZMQ;

public class TextAServerZMQ {

	private ZMQ.Context zeroMqContext;
	private ZMQ.Socket socket;

	private static GATEWrapper gate;
	private String url;

	public TextAServerZMQ(String home, String port) {

		try {
			/*
			 * Initialize GATE
			 */
			gate = new GATEWrapper(home);
	        url = "tcp://localhost:" + port;


		} catch (GateException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		}
		
	}

	public void run() {

		/*
		 * Initialize ZMQ server
		 */
		zeroMqContext = ZMQ.context(1);
        socket = zeroMqContext.socket(ZMQ.REP);
 
        socket.bind (url);

        while(!Thread.currentThread().isInterrupted()) {
        	
			try {

				byte[] request = socket.recv(0);
				if (request != null) {
					
					String message = new String(request);
					
					String response = gate.getAnnotation(message);
					socket.send(response.getBytes(), 0);

				} else {

					Thread.sleep(1);

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

        System.out.println("Server stopped");
        stop();
        
	}
	
	public void stop() {

		socket.close();
		zeroMqContext.term();
		
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
			
			TextAServerZMQ server = new TextAServerZMQ(home, port);
			server.run();
			
		} catch(ParseException e) {
			System.out.println(e.getMessage());

		} finally {
			System.exit(0);
		}

	}

}

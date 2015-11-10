package com.mygdx.game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * A Simple Socket client that connects to our socket server
 *
 */
public class ServerData {

    private String hostname;
    private int port;
    Socket socketClient;

    public ServerData(String hostname, int port){
        this.hostname = hostname;
        this.port = port;
    }

    public void connect() throws UnknownHostException, IOException{
        System.out.println("Attempting to connect to "+hostname+":"+port);
        socketClient = new Socket(hostname,port);
        System.out.println("Connection Established");
    }

    public String readResponse() throws IOException{
    	String rtn = "";
       String userInput;
       BufferedReader stdIn = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));

       System.out.print("RESPONSE FROM SERVER:");
       while ((userInput = stdIn.readLine()) != null) {
           System.out.println(userInput);
           rtn += userInput;
       }
       
       return rtn;
    }
    
    public void askForTime() throws IOException{
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
       writer.write("TIME?");
       writer.newLine();
       writer.flush();
    }
}
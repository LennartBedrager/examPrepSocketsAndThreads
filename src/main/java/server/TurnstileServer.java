/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TurnstileServer {

    private ServerSocket serverSocket;
    private static volatile int spectator = 0;
    

    public static void main(String[] args) throws IOException {
        TurnstileServer server = new TurnstileServer();
        server.start(4444);
    }

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        while (true) {
            new EchoClientHandler(serverSocket.accept()).start();
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    private static class EchoClientHandler extends Thread {

        private Socket clientSocket;
        private PrintWriter out;
        private Scanner in;

        private EchoClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new Scanner(clientSocket.getInputStream());

              
                //DO STUFF HERE
                handleClient(clientSocket);

                in.close();
                out.close();
                clientSocket.close();
                
            } catch (IOException ex) {
                Logger.getLogger(TurnstileServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        private static void handleClient(Socket socket) throws IOException {
            Scanner scan = new Scanner(socket.getInputStream());
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true); //DONT FORGET AUTOFLUSH
            //IMPORTANT: BLOCKING
            Boolean isTurnstile = false;
            
            while(true){
            pw.println("Are you a turnstile? (y/n)");
            String input = scan.nextLine();
            
            if(input.equalsIgnoreCase("n")){
                
                pw.println("Hello monitor!");
                pw.println("Use show to see numbers of specatators.");
                
                while (true) {
                
                input = scan.nextLine();
                if (input.toLowerCase().equals("exit") || input.toLowerCase().equals("quit")) {
                    pw.println("Disconnected!");
                    break;
                }
                
                pw.println(parseCommandMonitor(input));
            }
                
            }
            
            
            
            if(input.equalsIgnoreCase("y")){
            Turnstile turnstile = new Turnstile();

                pw.println("Hello turnstile" + turnstile.getTurnstileID()+"!");
                pw.println("Use add/show to increase or show specatators.");
                pw.println("Use addnumber to show specatators you have added.");
            
            while (true) {
                
                input = scan.nextLine();
                if (input.toLowerCase().equals("exit") || input.toLowerCase().equals("quit")) {
                    pw.println("Disconnected!");
                    break;
                }
                
                pw.println(parseCommandTurnstile(input, turnstile));
            }
            }

        }}

        private static int parseCommandTurnstile(String input, Turnstile turnstile) {
           
            
            switch (input.toUpperCase()) {
                case "ADD":
                    turnstile.addSpecatator();
                    return turnstile.getSpectators();
                case "SHOW":
                    return turnstile.getSpectators();
                case "ADDNUMBER":
                    return turnstile.getMadeSpectators();
                
                default:
                    break;
            }
            return 0;
        }
        private static int parseCommandMonitor(String input) {
           
            
            switch (input.toUpperCase()) {
               
                case "SHOW":
                    return Turnstile.getSpectators();
                
                default:
                    break;
            }
            return 0;
        }

    }

}
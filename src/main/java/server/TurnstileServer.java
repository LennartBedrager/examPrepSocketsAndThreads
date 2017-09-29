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
            Turnstile turnstile = new Turnstile();

            String input;
            
            while (true) {
                pw.println("Hello turnstile" + turnstile.getTurnstileID()+"! Use add/show to increase or show specatators");
                input = scan.nextLine();
                if (input.toLowerCase().equals("exit") || input.toLowerCase().equals("quit")) {
                    pw.println("Disconnected!");
                    break;
                }
                
                pw.println(parseCommand(input, turnstile));
            }

        }

        private static int parseCommand(String input, Turnstile turnstile) {
           
            
            switch (input.toUpperCase()) {
                case "ADD":
                    turnstile.addSpecatator();
                    return turnstile.getSpectators();
                case "SHOW":
                    return turnstile.getSpectators();
                
                default:
                    break;
            }
            return 0;
        }

    }

}
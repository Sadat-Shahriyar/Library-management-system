/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package libraryserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Taufiqun Nur farid
 */
public class LibraryServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        ServerSocket ss =new ServerSocket(4557);
        while(true){
            Socket s= null;
            try {
                s= ss.accept();
                System.out.println("Server is running...");
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(s.getInputStream()));
                PrintStream outToClient = new PrintStream(s.getOutputStream());
                System.out.println("Server is running...");
                Thread t= new ClientHandler(inFromClient, outToClient , s);
                t.start();
        
            } catch (Exception e) {
                s.close();
                e.printStackTrace();          
            }
        }

    }

}

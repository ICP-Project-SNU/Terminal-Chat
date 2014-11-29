/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package terminal.chat;

/**
 *
 * @author Saketh
 */
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
public class TerminalChat {

    /**
     * @param args the command line arguments
     */
    static final String DATABASE_URL = "jdbc:mysql://sql4.freemysqlhosting.net/sql459803";
    static final String DUserName = "sql459803";
    static final String DPassword = "aL8!rK4!";
    static final String Table = "users";
    static Connection connection = null;
    static Statement statement = null;
    
    public static void main(String[] args) {
        ResultSet result = null;
        try {
            connection = DriverManager.getConnection(DATABASE_URL, DUserName, DPassword);
            statement = connection.createStatement();
            Socket sock = null;
            String ans;
            String friend;
           // ResultSet ip;
            Scanner inp = new Scanner(System.in);
            
            createUser();
            do{
                System.out.println("1.Make Available");
                System.out.println("2.Chat with a friend\nans: ");
                ans = inp.nextLine();
                if( null != ans)
                    switch (ans) {
                        case "1":
                            sock = server();
                            break;
                        case "2":
                            //Display name and availability of all records from the table
                            System.out.print("Please enter your friend's username : ");
                            friend = inp.nextLine();
                            ResultSet ip=statement.executeQuery("SELECT IP FROM "+Table+" WHERE Name='"+friend+"'");
                            sock = connect(ip.toString());
                            break;
                    }
            }while(!("1".equals(ans) || "2".equals(ans)));
            msgSend msgU = new msgSend(sock);
            msgRecv msgR = new msgRecv(sock);
            
            msgU.start();
            msgR.start();
        } catch (SQLException ex) {
            Logger.getLogger(TerminalChat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static Socket connect(String ip){
        try{
            Socket sock = new Socket(ip, 6789);
            return sock;
        }
        catch(IOException e){
            System.out.println(e);
            return null;
        }
    }
    
    public static Socket server(){
        try{
            ServerSocket server = new ServerSocket(6789);
            Socket sock = server.accept();
            return sock;
        }catch(IOException e){
            System.out.println(e);
            return null;
        }
    }
    
    public static void createUser(){
        while(true){
            Scanner input = new Scanner(System.in);
            System.out.print("Enter the UserName : ");
            String user = input.nextLine();
            try {
                ResultSet result = statement.executeQuery("SELECT PASSWORD FROM "+Table+" WHERE Name='"+user+"'");    
                if(!result.first()){
                    System.out.println("New here? Sign up!");
                    System.out.print("Enter your password : ");
                    String Password = input.nextLine();
                    String IP = Inet4Address.getLocalHost().getHostAddress();
                    statement.executeUpdate("INSERT INTO "+Table+" VALUES('"+user+"','"+Password+"','"+IP+"','alive')");
                    System.out.println("User Created. Happy chatting");
                    break;
                }
                else{
                    System.out.println("Welcome back!");
                   // System.out.println(result.getObject("PASSWORD"));
                    System.out.println("Enter the password");
                    String Password = input.nextLine();
                    if(!result.getObject("PASSWORD").toString().equals(Password)){
                        System.out.println("Password is Wrong");
                    }
                    else{
                         String IP = Inet4Address.getLocalHost().getHostAddress();
                         statement.executeUpdate("Update "+Table+" Set IP = "+IP+" where Name ='"+user+"'");
                        System.out.println("Logged in! Happy Chatting!");
                        break;
                    }
                }
            } catch (SQLException ex) {
                //System.out.println("User doesn't exist");
            } catch (UnknownHostException ex) {
                Logger.getLogger(TerminalChat.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
}

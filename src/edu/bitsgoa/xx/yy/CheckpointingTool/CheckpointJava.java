/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.bitsgoa.xx.yy.CheckpointingTool;

/**
 *
 * @author chintan
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CheckpointJava {

    private static BufferedReader runProcess(String command) 
	{
		try{
			Process process;
			process = Runtime.getRuntime().exec(command);
			//process.waitFor();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(process.getInputStream()));
			return buffer;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;	
		}
		finally
		{

		}
	}
	
	private static void printProcessOutput(BufferedReader buffer) throws IOException
	{
            String line="";
        try {
            while((line=buffer.readLine())!=null)
            {
                System.out.println(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(CheckpointJava.class.getName()).log(Level.SEVERE, null, ex);
        }
            buffer.close();
	}
 
 public static void checkpoint()
{

System.out.println("I want a CHKPT after ");
String command="/home/chintan/Downloads/SOP/FinalProjectStructure/JavaCheckpointingTool/libs/dmtcp/test/plugin/chkptplugin";

        try {
            printProcessOutput(runProcess(command));
        } catch (IOException ex) {
            Logger.getLogger(CheckpointJava.class.getName()).log(Level.SEVERE, null, ex);
        }

			
}
/*    public static void checkpoint() {
 System.out.println("CHECKPOINT CALLED ");
 String command="pwd";
//printProcessOutput(runProcess(command));
Process process;
     try {
         process = Runtime.getRuntime().exec(command);
//runProcess(command);
     } catch (IOException ex) {
         Logger.getLogger(CheckpointJava.class.getName()).log(Level.SEVERE, null, ex);
     }
    }*/

    public static void main(String args[]) {
        CheckpointJava.checkpoint();

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CheckpointJava.class.getName()).log(Level.SEVERE, null, ex);
        }

        Connection con = null;
        String URL = "jdbc:mysql://localhost:3306/LARGE?autoReconnect=true&useSSL=false", username = "root", password = "J!4192chb";
        try {
            con = DriverManager.getConnection(URL, username, password);

        } catch (SQLException ex) {
            Logger.getLogger(CheckpointJava.class.getName()).log(Level.SEVERE, null, ex);
        }

        Scanner sc = null;
        try {
            sc=new Scanner(new File("/home/chintan/Downloads/SOP/FinalProjectStructure/JavaCheckpointingTool/input/query_file"));
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CheckpointJava.class.getName()).log(Level.SEVERE, null, ex);
            sc = new Scanner(System.in);
        }
       // 
        int i = 1;
        String query;
        query = sc.nextLine();
        while (sc.hasNext()) {
            try {

                String command_called = query.split(" ")[0];
                //for(int randele=0;randele<150000;randele++)System.out.println(randele);
                System.out.println("Your query was : " + query);
                System.out.println("Your command was :" + command_called.toLowerCase());
                if (command_called.toLowerCase().equals("update")) {
                    PreparedStatement preparedStmt = con.prepareStatement(query);
                    preparedStmt.executeUpdate();
                    CheckpointJava.checkpoint();
                } else {
                    // 	System.out.println("Command was **"+command_called.toLowerCase()+"**  instead of update");
                    Statement stmt = con.createStatement();
                    stmt.executeQuery(query);
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  " + rs.getString(7));
                    }
                    //for(int randele=0;randele<150000;randele++)System.out.println(randele);

                }
                System.out.println("Executing i=" + i + "");
                i++;
                query = sc.nextLine();
            } catch (Exception e) {
                System.out.println("ERROR");
            }
        }
        sc.close();
       // new Exception().printStackTrace();
    }
}

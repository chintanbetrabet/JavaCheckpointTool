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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package checkpointjava;

/**
 *
 * @author chintan
 */
import java.sql.*;  
import java.io.*;
import java.util.*;

public class NoCheckpointJava{  

public void move(String s,String s1)
{
    //System.out.println("OLD");
}
@SuppressWarnings("empty-statement")
public static void main(String args[]){
    System.out.println("MID");
try{  
Class.forName("com.mysql.jdbc.Driver");  
}
catch(Exception e)
{
    
}
Connection con = null;
 try { con=DriverManager.getConnection( "jdbc:mysql://localhost:3306/LARGE?autoReconnect=true&&"+"useSSL=false","root","J!4192chb");}
  catch(Exception e)
    {
        
    }
Scanner sc=null;
try{sc=new Scanner(new File("query_file"));}
catch (IOException e){}
int i=1;
while(sc.hasNext())
{
    try {
       String query ;
       query=sc.nextLine();
       String command_called=query.split(" ")[0];
       System.out.println("Your query was : "+query+"\nYour command was :"+command_called.toLowerCase());
       if(command_called.toLowerCase().equals("update"))
       {
       PreparedStatement preparedStmt = con.prepareStatement(query);
       preparedStmt.executeUpdate();
       }
       else
       {
       	System.out.println("Command was **"+command_called.toLowerCase()+"**  instead of update");
       	Statement stmt=con.createStatement();
        ResultSet rs=stmt.executeQuery("select * from user_details where user_id="+i);
        while(rs.next())
        System.out.println(rs.getInt(1)+"  "+rs.getString(2)+"  "+rs.getString(7));
        
        }
       System.out.println("Executing i="+i+"");
       i++;
      
    }

    catch(Exception e)
    {
        
    }
}
sc.close();
}
}  
 

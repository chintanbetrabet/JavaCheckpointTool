/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.bitsgoa.xx.yy.CheckpointingTool;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author chintan
 */
public class Parasite {
    public void move()
    {
        
    }
    public static void checkpoint(String methodname) {
        
        try
        {System.out.println("I want a CHKPT after ");
        String command = "./chkptplugin";
        Process process = Runtime.getRuntime().exec(command);
        } catch (java.io.IOException ex) {
            java.util.logging.Logger.getLogger(Parasite.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }
}

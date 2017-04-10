/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.bitsgoa.xx.yy.CheckpointingTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;

/**
 *
 * @author chintan
 *
 *
 */
public class BinaryInstrumenterCheckpoint {

    public static void writeSystemCommandToFile(BufferedReader buffer, String outString) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(outString);
            String line;
            while ((line = buffer.readLine()) != null) {
                fileWriter.write(line);
                fileWriter.write("\n");
            }
            buffer.close();
        } catch (IOException ex) {
            Logger.getLogger(BinaryInstrumenterCheckpoint.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fileWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(BinaryInstrumenterCheckpoint.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static TreeMap<String, HashSet<String>> callgraphClassesAndTheirMethods(String filename) throws IOException {
        BufferedReader read;
        TreeMap<String, HashSet<String>> tm = new TreeMap<>();
        try {

            read = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = read.readLine()) != null) {
                if (line.startsWith("M:") && line.contains("(I)")) {
                    String temp = line.split(":")[1];
                    String classname = temp;

                    //temp = line.split("\\)")[1];
                    String methodname = line.split("[)]")[1];
                    //System.out.println(classname+" "+methodname);
                    if (!tm.containsKey(classname)) {
                        HashSet<String> methodset = new HashSet<>();
                        methodset.add(methodname);
                        tm.put(classname, methodset);
                    } else {
                        HashSet<String> methodset = tm.get(classname);
                        methodset.add(methodname);
                        tm.remove(classname);
                        tm.put(classname, methodset);
                    }

                }
            }
            read.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BinaryInstrumenterCheckpoint.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BinaryInstrumenterCheckpoint.class.getName()).log(Level.SEVERE, null, ex);
        }

        return tm;
    }

    public static BufferedReader runProcess(String command) {
        try {
            Process process;
            process = Runtime.getRuntime().exec(command);
            //process.waitFor();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return buffer;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {

        }
    }

    public static boolean isNative(CtMethod method) {
        return Modifier.isNative(method.getModifiers());
    }

    public static void modifyJarUsingCallGraph(ClassPool pool, String callgraphFilename, String destination, String checkpointingCode, String jarName) {
        try {
            TreeMap<String, HashSet<String>> classWithMethods;
            classWithMethods = BinaryInstrumenterCheckpoint.callgraphClassesAndTheirMethods(callgraphFilename);

            Set set = classWithMethods.entrySet();
            Iterator i = set.iterator();
            CtClass ctClass = null;
            while (i.hasNext()) {
                Map.Entry me = (Map.Entry) i.next();
                String classname = (String) me.getKey();
                ctClass = pool.get(classname);
                for (CtMethod method : ctClass.getDeclaredMethods()) {
                    method = ctClass.getDeclaredMethod(method.getName());
                    ctClass.defrost();
                    MethodFinder.modifyCalledMethodsFromMethod(ctClass, pool, method, destination, checkpointingCode, jarName);
                    ctClass.writeFile(destination);

                }
            }
            if (ctClass != null) {
                ctClass.writeFile();
                JarHandler.writeJar(ctClass, pool, jarName);
            }
        } catch (NotFoundException | IOException | CannotCompileException ex) {
            Logger.getLogger(BinaryInstrumenterCheckpoint.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static String readFileToString(String filename) {
        String output = "";
        BufferedReader br = null;
        FileReader fr = null;

        try {

            fr = new FileReader(filename);
            br = new BufferedReader(fr);

            String sCurrentLine;

            br = new BufferedReader(new FileReader(filename));

            while ((sCurrentLine = br.readLine()) != null) {
                output = output.concat(sCurrentLine);
            }

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (br != null) {
                    br.close();
                }

                if (fr != null) {
                    fr.close();
                }

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }
        return output;
    }

    public static void main(String args[]) {

        Scanner configScanner = null;
        try {
            configScanner = new Scanner(new File("../config/docs.config"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BinaryInstrumenterCheckpoint.class.getName()).log(Level.SEVERE, null, ex);
        }
        HashMap<String,String> configMap=new HashMap<>();
        while(configScanner.hasNext())
        {
            String configData=configScanner.nextLine();
            String key,value;
            key=configData.split(":")[0];
            value=configData.split(":")[1];
            configMap.put(key, value);
        }
        System.out.println(configMap);
        String jarName = configMap.get("JAR_PATH");
        String callgraphFile = "callgraph_output";
        String callgraphCreateCommand = configMap.get("CALLGRAPH_LOCATION");
        String destination = configMap.get("OUTPUT_LOCATION");
        //String checkpointCode = readFileToString("../config/checkpointCodeJava");
        String checkpointCommand = configMap.get("CHECKPOINT_COMMAND");
        
        
        String checkpointCode = "try"
                + "        {"
                + "         System.out.println(\"I want a CHKPT after \");"
                + "        String command = \"%s\";"
                + "        Process process = Runtime.getRuntime().exec(command);process.waitFor();"
                + "        } catch (java.io.IOException ex) {"
                + "            System.out.println(\"Glitch\");"
                + "        }"
                + " ";

        checkpointCode = String.format(checkpointCode, checkpointCommand);
        // checkpointCode=String.format(checkpointCode, methodName);

        //System.out.println(checkpointCode);
        ClassPool pool = ClassPool.getDefault();
        try {
            pool.appendClassPath(jarName);
        } catch (NotFoundException ex) {
            Logger.getLogger(BinaryInstrumenterCheckpoint.class.getName()).log(Level.SEVERE, null, ex);
        }
        writeSystemCommandToFile(runProcess(callgraphCreateCommand + " "+jarName), callgraphFile);
        modifyJarUsingCallGraph(pool, callgraphFile, destination, checkpointCode, jarName);
        System.out.println("Finished");
    }
}

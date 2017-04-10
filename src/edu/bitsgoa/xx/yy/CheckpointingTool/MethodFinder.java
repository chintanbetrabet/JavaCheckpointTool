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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class MethodFinder {
    public static void main(String a[])
    {
        BinaryInstrumenterCheckpoint.main(null);
    }
    public static void modifyCalledMethodsFromMethod(CtClass ctClass, ClassPool cp, CtMethod method, String destination, String checkpointingCode, String jarName) {
        try {
            ctClass.defrost();
            //ADD checkpoint method here
            HashMap<Integer, String> lines;
            lines = new HashMap<>();
            ArrayList<CtMethod> getConnection = new ArrayList<>();

            method.instrument(
                    new ExprEditor() {
                @Override
                public void edit(final MethodCall m) throws CannotCompileException {

                    //        System.out.println("methodnames="+m.getMethodName());
                    lines.put(m.getLineNumber(), m.getMethodName());

                    // else
                    //{
                    //System.out.println("\n\n\n\nBOGEY:::\n\n\n\n="+m.getMethodName());
                    //}
                    //m.getMethod().get
                    //ctClass.defrost();
                    // System.out.println(m.getMethodName()+" "+m.getLineNumber());
                    
                    if(m.getMethodName().equals("createStatement"))
                    {
                        m.replace("java.sql.Connection con1 = null;"
                                + "con1=java.sql.DriverManager.getConnection(  \"jdbc:mysql://localhost:3306/LARGE?autoReconnect=true\",\"root\",\"J!4192chb\");"
                                + "$0=con1;"
                                
                                
                                + "$_ = $proceed($$);");
                    }
                   else if(m.getMethodName().equals("prepareStatement"))
                    {
                        m.replace("java.sql.Connection con1 = null;"
                               + "con1=java.sql.DriverManager.getConnection( \"jdbc:mysql://localhost:3306/LARGE?autoReconnect=true\",\"root\",\"J!4192chb\");"
                                + "$0=con1;"
                                
                                
                                + "$_ = $proceed($$);");
                    }
                    
                    else if (m.getMethodName().equals("executeUpdate"))
                    {
                        //System.out.println("GOT update command ");
                        m.replace("$_ = $proceed($$);");
                    }
                    else if (m.getClassName().equals("java.sql.DriverManager")
                            && m.getMethodName().equals("getConnection")) {
                        //System.out.println("Found method");

                        /*m.replace("{long startMs = System.currentTimeMillis(); " +
            "$_ = $proceed($$); " +
            "long endMs = System.currentTimeMillis();" +
            "System.out.println(\"Executed in ms: \" + (endMs-startMs));}");*/
                       m.replace("String s=$1;System.out.println(\"Connection called called by\"+\" \" +$1);"
                                + "if (!s.contains(\"autoReconnect\"))\n"
                                + "{\n"
                                + "    if(s.contains(\"?\"))\n"
                                + "    {\n"
                                + "        s=s+\"&&autoReconnect=true&&maxReconnects=3\";\n"
                                + "    }\n"
                                + "    else\n"
                                + "    {\n"
                                + "        s=s+\"?autoReconnect=true&&maxReconnects=3\";\n"
                                + "    }\n"
                                + "}\n"
                                + "$1=s;"
                                + "System.out.println(\"URL=\"+s)\n;"
                                + "$_ = $proceed($$);"
                        );
                    }

                    //System.out.println(m.getClassName() + "." + m.getMethodName());
                }
            });
            for (Integer ln : lines.keySet()) {
                //  method.insertAt(ln, "System.out.println(\"Will Call Checkpoint before " + lines.get(ln) + "\");");
                if (lines.get(ln).equals("executeUpdate")) {
                    method.insertAt(ln + 1, String.format(checkpointingCode, lines.get(ln)));
                }
                //method.insertAt(ln, "Parasite.checkpoint(" + lines.get(ln) + ");");
            }

            ctClass.writeFile(destination);
            
            JarHandler.writeJar(ctClass, cp, jarName);
        } catch (CannotCompileException | IOException ex) {
            Logger.getLogger(MethodFinder.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

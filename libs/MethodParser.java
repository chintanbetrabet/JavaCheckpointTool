import java.util.*;
import java.io.*;

public class MethodParser {

   public static void main(String args[])  throws Exception
   {
      TreeMap tm = new TreeMap();
	  BufferedReader read = new BufferedReader(new FileReader("callgraph"));
	  String line = null;
	  while ((line = read.readLine()) != null)
	  {
		if (line.startsWith("M:") && !line.contains("<init>"))
		{
			String temp = line.split(" ")[1];
			temp = temp.split("\\)")[1];
			String temp2 = temp.split(":")[1];
			tm.put(temp2, temp);

		}
	  }
      Set set = tm.entrySet();
      Iterator i = set.iterator();
      while(i.hasNext()) 
	  {
         Map.Entry me = (Map.Entry)i.next();
         System.out.println(me.getValue());
      }
   }
}

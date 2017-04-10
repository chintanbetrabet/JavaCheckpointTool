/*
 * Change to PARASIte and SendSignal
 */
import java.lang.Thread;
import java.io.*;
import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;


class Generator
{
	public static void main(String[] args) throws Exception
	{
		DomParserDemo.parseDemo();
	}
}

class DomParserDemo {
	public static void parseDemo() throws Exception
	{

		BufferedWriter wr = new BufferedWriter(new FileWriter("app2.btm"));
		try
		{
			File inputFile = new File("input.xml");
			DocumentBuilderFactory dbFactory
				= DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("class");
			for (int temp = 0; temp < nList.getLength(); temp++)
			{
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE)
				{
					Element eElement = (Element) nNode;
					String str2 = "RULE trace " +  eElement.getAttribute("method") +  " entry\nCLASS " + eElement.getAttribute("name")  +  "\nMETHOD " + eElement.getAttribute("method") + "\nHELPER Parasite\nAT ENTRY\nIF true\nDO signal(" + eElement.getAttribute("prob") + ")" + "\nENDRULE";
					wr.write(str2);
					wr.newLine();
				}
			}
			wr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

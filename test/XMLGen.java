import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

class XMLGen
{

	public static void main(String args[])
	{
		try
		{
			String line;
			BufferedReader br = new BufferedReader(new FileReader("methods"));
			
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();
			Element root = document.createElement("begin");
			document.appendChild(root);

			while ((line = br.readLine()) != null)
			{
				int i = line.lastIndexOf(":");
				String[] splitLine = {line.substring(0,i), line.substring(i+1)};
				Element elem = document.createElement("class");
				root.appendChild(elem);
				Attr method = document.createAttribute("method");
				method.setValue(splitLine[1]);
				Attr name = document.createAttribute("name");
				name.setValue(splitLine[0]);
				Attr prob = document.createAttribute("prob");
				prob.setValue("0.7");
				elem.setAttributeNode(method);
				elem.setAttributeNode(name);
				elem.setAttributeNode(prob);
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = new StreamResult(new File("input.xml"));
			transformer.transform(domSource, streamResult);
			System.out.println("Created XML file successfully");
		}
		catch (ParserConfigurationException pce)
		{
			pce.printStackTrace();
		}
		catch (TransformerException tfe)
		{
			tfe.printStackTrace();
		}
		catch (FileNotFoundException fne)
		{
			fne.printStackTrace();
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
}

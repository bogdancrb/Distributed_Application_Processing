import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import controller.ClientController;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.w3c.dom.Document;

public class app2 
{
	public static void main(String args[]) throws SocketException, UnknownHostException
	{
		Integer random = (int) (Math.random() * 1000 + 1);
		
		for (int i = 0; i <= random; i++)
		{
			;
		}
		
		String resultString = String.valueOf(random);
		
		NetworkInterface network = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
        byte[] mac = network.getHardwareAddress();
        
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < mac.length; i++) 
        {
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));        
        }
        
		
		try 
		{
	         DocumentBuilderFactory dbFactory =
	         DocumentBuilderFactory.newInstance();
	         DocumentBuilder dBuilder = 
	            dbFactory.newDocumentBuilder();
	         Document doc = dBuilder.newDocument();
	         // root element
	         Element rootElement = doc.createElement("resultinfo");
	         doc.appendChild(rootElement);

	         //  supercars element
	         Element supercar = doc.createElement("clientinfo");
	         rootElement.appendChild(supercar);

	         // setting attribute to element
	         Attr attr = doc.createAttribute("macaddress");
	         attr.setValue(sb.toString());
	         supercar.setAttributeNode(attr);

	         // carname element
	         Element carname = doc.createElement("result");
	         carname.appendChild(
	         doc.createTextNode(resultString));

	         // write the content into xml file
	         TransformerFactory transformerFactory =
	         TransformerFactory.newInstance();
	         Transformer transformer =
	         transformerFactory.newTransformer();
	         DOMSource source = new DOMSource(doc);
	         StreamResult result =
	         new StreamResult(new File(app1.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()));
	         transformer.transform(source, result);
	         // Output to console for testing
	         StreamResult consoleResult =
	         new StreamResult(System.out);
	         transformer.transform(source, consoleResult);
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	}
}

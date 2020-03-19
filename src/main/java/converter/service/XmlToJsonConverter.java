package converter.service;

import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class XmlToJsonConverter {
    private Document document;

    public JSONObject convert(byte[] bytes) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream stream = new ByteArrayInputStream(bytes);
        document = builder.parse(stream);

        Element root = document.getDocumentElement();
        NodeList list = root.getChildNodes();

        int sum = getChildSum(list);

        Element value = document.createElement("value");
        Text text = document.createTextNode(String.valueOf(sum));
        root.appendChild(value);
        value.appendChild(text);

        DOMImplementation impl = document.getImplementation();
        DOMImplementationLS implLS = (DOMImplementationLS) impl.getFeature("LS", "3.0");
        LSSerializer serializer = implLS.createLSSerializer();
        serializer.getDomConfig().setParameter("format-pretty-print", true);
        serializer.getDomConfig().setParameter("xml-declaration", false);
        String xmlString = serializer.writeToString(document);

        return XML.toJSONObject(xmlString);
    }

    private int getChildSum(NodeList nodeList) {
        int sum = 0;
        for (int i = 0; i < nodeList.getLength(); i++) {
            int childSum = 0;
            Node node = nodeList.item(i);

            if (node instanceof Element) {
                if (!node.getTextContent().trim().isEmpty() && !((Text) node.getFirstChild()).getData().trim().isEmpty() && !((Text) node.getFirstChild()).getData().trim().equals("\n")) {
                    Text text = (Text) node.getFirstChild();
                    String data = text.getData().trim();
                    try {
                        childSum += Integer.parseInt(data.replaceAll("[*a-zA-Z]", "").trim());
                    } catch (NumberFormatException ignored) {

                    }
                    node.removeChild(node.getFirstChild());
                }

                if (node.hasChildNodes()) {
                    childSum += getChildSum(node.getChildNodes());
                }

                Element value = document.createElement("value");
                Text text = document.createTextNode(String.valueOf(childSum));
                node.appendChild(value);
                value.appendChild(text);

                sum += childSum;
            }
        }
        return sum;
    }
}

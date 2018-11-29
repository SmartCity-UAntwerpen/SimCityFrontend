package be.uantwerpen.sc.services.sim;

import be.uantwerpen.sc.models.sim.SimWorker;
import be.uantwerpen.sc.models.sim.SimWorkerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads a file with some workers defined. For easier deployment and development.
 */
@Service
@Profile("dev")
public class SimWorkerFileService {

    private static final Logger logger = LoggerFactory.getLogger(SimWorkerFileService.class);

    @Value("${worker.preloadFile:#{null}}")
    private String workersFile;

    public List<SimWorker> getWorkers() {
        List<SimWorker> workersList = new ArrayList<>();
        if(workersFile == null) return workersList;

        ClassLoader classLoader = getClass().getClassLoader();

        try {
            File typesFile = new File(classLoader.getResource(workersFile).toURI());

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(typesFile);

            document.getDocumentElement().normalize();

            NodeList nodeList = document.getElementsByTagName("worker");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                SimWorker worker = processNode(node);
                if(worker != null) workersList.add(worker);

            }
        } catch (IOException e) {
            logger.warn("Could not load workers list! " + e.getMessage());
        }
        catch (SAXException e) {
            logger.error("Syntax error parsing workers preload file. Starting without workers.");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return workersList;
    }

    private SimWorker processNode(Node node) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            String hostname = ((Element) node).getElementsByTagName("host").item(0).getTextContent();
            String portString = ((Element) node).getElementsByTagName("port").item(0).getTextContent();
            String name = ((Element) node).getElementsByTagName("name").item(0).getTextContent();

            String typeString = ((Element) node).getElementsByTagName("type").item(0).getTextContent();
            SimWorkerType type = StringToType(typeString);

            SimWorker currentWorker = new SimWorker(name, hostname+":"+portString, type);
            return currentWorker;
        }
        else return null;
    }

    private SimWorkerType StringToType(String type) {
        return SimWorkerType.StringToType(type);
    }
}


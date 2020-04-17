package ru.stepanov.uocns.network.common.generator.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.stepanov.uocns.common.exceptions.InternalErrorException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;


@Component
public class XmlWriter {

    @Value("${generator.results.path}")
    String RESULTS_PATH;

    public boolean write(String netlistData, String routingData, String descr) throws InternalErrorException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        try {
            builder = factory.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();

            Document doc = impl.createDocument(null, "TaskOCNS", null);
            Element rootElement = doc.getDocumentElement();
            rootElement.setAttributeNS(null, "Description", descr);
            rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema");

            // добавляем первый дочерний элемент к корневому
            Element network = doc.createElement("Network");
            rootElement.appendChild(network);

            //добавляем детей для узла
            Element netlist = doc.createElement("Netlist");
            netlist.appendChild(doc.createTextNode(netlistData));
            network.appendChild(netlist);

            Element routing = doc.createElement("Routing");
            routing.appendChild(doc.createTextNode(routingData));
            network.appendChild(routing);

            Element link = doc.createElement("Link");
            network.appendChild(link);

            Element fifosize = doc.createElement("Parameter");
            fifosize.setAttribute("FifoSize", "4");
            link.appendChild(fifosize);

            Element fifocount = doc.createElement("Parameter");
            fifocount.setAttribute("FifoCount", "4");
            link.appendChild(fifocount);

            Element traffic = doc.createElement("Traffic");
            rootElement.appendChild(traffic);

            Element flitSize = doc.createElement("Parameter");
            flitSize.setAttribute("FlitSize", "32");
            traffic.appendChild(flitSize);

            Element packetSizeAvg = doc.createElement("Parameter");
            packetSizeAvg.setAttribute("PacketSizeAvg", "10");
            traffic.appendChild(packetSizeAvg);

            Element packetSizeIsFixed = doc.createElement("Parameter");
            packetSizeIsFixed.setAttribute("PacketSizeIsFixed", "true");
            traffic.appendChild(packetSizeIsFixed);

            Element packetPeriodAvg = doc.createElement("Parameter");
            packetPeriodAvg.setAttribute("PacketPeriodAvg", "5");
            traffic.appendChild(packetPeriodAvg);

            Element simulation = doc.createElement("Simulation");
            rootElement.appendChild(simulation);

            Element countRun = doc.createElement("Parameter");
            countRun.setAttribute("CountRun", "1");
            simulation.appendChild(countRun);

            Element countPacketRx = doc.createElement("Parameter");
            countPacketRx.setAttribute("CountPacketRx", "1100");
            simulation.appendChild(countPacketRx);

            Element countPacketRxWarmUp = doc.createElement("Parameter");
            countPacketRxWarmUp.setAttribute("CountPacketRxWarmUp", "100");
            simulation.appendChild(countPacketRxWarmUp);

            Element isModeGALS = doc.createElement("Parameter");
            isModeGALS.setAttribute("IsModeGALS", "false");
            simulation.appendChild(isModeGALS);

            //создаем объект TransformerFactory для печати в консоль
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            // для красивого вывода в консоль
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(doc);

            //печатаем в консоль или файл
            StreamResult console = new StreamResult(System.out);
            File folder = new File(RESULTS_PATH + descr);
            if (!folder.exists()) {
                folder.mkdir();
            }
            StreamResult file = new StreamResult(new File(RESULTS_PATH + descr + "/config-" + descr + "__2____" + ".xml"));

            //записываем данные
            transformer.transform(source, console);
            transformer.transform(source, file);
            System.out.println("Создание XML файла закончено");

            return true;
        } catch (Exception e) {
            throw new InternalErrorException(e.getMessage());
        }
    }

}

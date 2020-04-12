package ru.stepanov.uocns.network.common;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Random;
import java.util.Vector;

public class TUtilities {
    private Random fRandGenerator = new Random(12345);
    private final Vector<Vector<TNocParameter>> fVtrNetworkParameters = new Vector();

    public static int getMinBitsCount(int aValue) {
        return (int) Math.ceil(Math.log(aValue) / Math.log(2.0));
    }

    public void setRandSeedRandom() {
        this.fRandGenerator = new Random();
    }

    public double getRandNextDouble() {
        return this.fRandGenerator.nextDouble();
    }

    public Vector<TNocParameter> getConfig(int index) {
        return index < this.fVtrNetworkParameters.size() ? this.fVtrNetworkParameters.get(index) : null;
    }

    public void doReadConfigFile(String aFileName) {
        Document aDocument;
        Node iNode;
        DocumentBuilderFactory aDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder aDocumentBuilder = aDocumentBuilderFactory.newDocumentBuilder();
            aDocument = aDocumentBuilder.parse(new File(aFileName));
        } catch (Exception e) {
            return;
        }
        Vector<TNocParameter> aVtrNetworkConfig = new Vector<TNocParameter>();
        Node iNodeChild = aDocument.getFirstChild().getAttributes().item(0);
        aVtrNetworkConfig.add(new TNocParameter(iNodeChild.getNodeName(), iNodeChild.getNodeValue()));
        String[] nXmlNodesA = new String[]{"Netlist", "Routing"};
        int iXmlNodeId = 0;
        while (iXmlNodeId < nXmlNodesA.length) {
            iNode = aDocument.getElementsByTagName(nXmlNodesA[iXmlNodeId]).item(0);
            if (iNode == null) {
                return;
            }
            aVtrNetworkConfig.add(new TNocParameter(iNode.getNodeName(), iNode.getTextContent()));
            ++iXmlNodeId;
        }
        String[] nXmlNodesB = new String[]{"Link", "Traffic", "Simulation"};
        int iXmlNodeId2 = 0;
        while (iXmlNodeId2 < nXmlNodesB.length) {
            iNode = aDocument.getElementsByTagName(nXmlNodesB[iXmlNodeId2]).item(0);
            if (iNode == null) {
                return;
            }
            int iNodeId = 0;
            while (iNodeId < iNode.getChildNodes().getLength()) {
                if ("Parameter".equals(iNode.getChildNodes().item(iNodeId).getNodeName())) {
                    iNodeChild = iNode.getChildNodes().item(iNodeId).getAttributes().item(0);
                    aVtrNetworkConfig.add(new TNocParameter(iNodeChild.getNodeName(), iNodeChild.getNodeValue()));
                }
                ++iNodeId;
            }
            ++iXmlNodeId2;
        }
        this.fVtrNetworkParameters.add(aVtrNetworkConfig);
    }
}


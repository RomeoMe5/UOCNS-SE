package ru.stepanov.uocns.web.services.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.stepanov.uocns.common.exceptions.InternalErrorException;
import ru.stepanov.uocns.network.common.generator.utils.XmlWriter;

@Component
public class XmlHelper {
    @Autowired
    XmlWriter xmlWriter;

    public void createXml(int[][] netlist, int[][] routing, String descr) throws InternalErrorException {
        StringBuilder netlistData = new StringBuilder("\n");
        for (int i = 0; i < routing.length; i++) {
            for (int j = 0; j < 4; j++) {
                netlistData.append(netlist[i][j]).append(" ");
            }
            netlistData.append("\n");
        }

        StringBuilder routingData = new StringBuilder("\n");
        for (int[] ints : routing) {
            for (int j = 0; j < routing.length; j++) {
                routingData.append(ints[j]).append(" ");
            }
            routingData.append("\n");
        }

        xmlWriter.write(netlistData.toString(), routingData.toString(), descr);
    }

}

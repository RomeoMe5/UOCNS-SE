package ru.stepanov.uocns.database.entities.auto;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import ru.stepanov.uocns.database.entities.Topology;

/**
 * Class _TopologyXml was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _TopologyXml extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ID_PK_COLUMN = "id";

    public static final Property<String> CONTENT = Property.create("content", String.class);
    public static final Property<String> NAME = Property.create("name", String.class);
    public static final Property<Topology> XML_TO_TOPOLOGY = Property.create("xmlToTopology", Topology.class);

    public void setContent(String content) {
        writeProperty("content", content);
    }
    public String getContent() {
        return (String)readProperty("content");
    }

    public void setName(String name) {
        writeProperty("name", name);
    }
    public String getName() {
        return (String)readProperty("name");
    }

    public void setXmlToTopology(Topology xmlToTopology) {
        setToOneTarget("xmlToTopology", xmlToTopology, true);
    }

    public Topology getXmlToTopology() {
        return (Topology)readProperty("xmlToTopology");
    }


}

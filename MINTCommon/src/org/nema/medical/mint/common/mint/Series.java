
package org.nema.medical.mint.common.mint;

import java.util.*;

import com.vitalimages.contentserver.mint.Mint2Gpb.AttributeData;
import com.vitalimages.contentserver.mint.Mint2Gpb.InstanceData;
import com.vitalimages.contentserver.mint.Mint2Gpb.SeriesData;

/**
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://vitalimages.com/contentserver/mint" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="SeriesType">
 *   &lt;xs:sequence>
 *     &lt;xs:element type="ns:AttributesType" name="Attributes" minOccurs="1" maxOccurs="1"/>
 *     &lt;xs:element type="ns:NormalizedInstanceAttributesType" name="NormalizedInstanceAttributes" minOccurs="1" maxOccurs="1"/>
 *     &lt;xs:element type="ns:InstancesType" name="Instances" minOccurs="1" maxOccurs="1"/>
 *   &lt;/xs:sequence>
 *   &lt;xs:attribute type="xs:string" use="required" name="seriesInstanceUID"/>
 * &lt;/xs:complexType>
 * </pre>
 */
public class Series implements AttributeStore
{
    private final Map<Integer,Attribute> attributeMap = new HashMap<Integer,Attribute>();
    private final Map<Integer,Attribute> normalizedInstanceAttributeMap = new HashMap<Integer,Attribute>();
    private final List<Instance> instanceList = new ArrayList<Instance>();
    private String seriesInstanceUID;

    /**
     * @param tag
     * @return the attribute for the given tag
     */
    public Attribute getAttribute(final int tag) {
        return attributeMap.get(tag);
    }

    /**
     * puts an Attribute into the Series - attributes are unique per tag
     * @param attr
     */
    public void putAttribute(final Attribute attr) {
        attributeMap.put(attr.getTag(), attr);
    }

    /**
     * removes the Attribute with the given tag from the Series
     * @param tag
     */
    public void removeAttribute(final int tag) {
        attributeMap.remove(tag);
    }

    /**
     * @return an iterator of all Attributes in the Series
     */
    public Iterator<Attribute> attributeIterator() {
        return attributeMap.values().iterator();
    }

    /**
     * @param tag
     * @return the normalized instance attribute for the given tag
     */
    public Attribute getNormalizedInstanceAttribute(final String tag) {
        return normalizedInstanceAttributeMap.get(tag);
    }

    /**
     * puts a NormalizedInstanceAttribute into the Series - attributes are unique per tag
     * @param attr
     */
    public void putNormalizedInstanceAttribute(final Attribute attr) {
        normalizedInstanceAttributeMap.put(attr.getTag(), attr);
    }

    /**
     * removes the NormalizedInstanceAttribute with the given tag from the Series
     * @param tag
     */
    public void removeNormalizedInstanceAttribute(final String tag) {
        normalizedInstanceAttributeMap.remove(tag);
    }

    /**
     * @return an iterator of all NormalizedInstanceAttributes in the Series
     */
    public Iterator<Attribute> normalizedInstanceAttributeIterator() {
        return normalizedInstanceAttributeMap.values().iterator();
    }

    /**
     * puts an Instance into the Series - instances are unique per xfer
     * @param inst
     */
    public void putInstance(final Instance inst) {
        instanceList.add(inst);
    }

    /**
     * @return an iterator of all Instances in the Series
     */
    public Iterator<Instance> instanceIterator() {
        return instanceList.iterator();
    }

    /**
     * @return the number of Instances in the Series
     */
    public int instanceCount() {
        return instanceList.size();
    }

    /**
     * Get the 'seriesInstanceUID' attribute value.
     *
     * @return value
     */
    public String getSeriesInstanceUID() {
        return seriesInstanceUID;
    }

    /**
     * Set the 'seriesInstanceUID' attribute value.
     *
     * @param seriesInstanceUID
     */
    public void setSeriesInstanceUID(String seriesInstanceUID) {
        this.seriesInstanceUID = seriesInstanceUID;
    }

    //
    // Google Protocol Buffer support - package protection intentional
    //
    static Series fromGPB(SeriesData seriesData) {
        Series series = new Series();
        series.setSeriesInstanceUID(seriesData.getSeriesInstanceUid());
        for (AttributeData attrData : seriesData.getAttributesList()) {
            series.putAttribute(Attribute.fromGPB(attrData));
        }
        for (AttributeData attrData : seriesData.getNormalizedInstanceAttributesList()) {
            series.putNormalizedInstanceAttribute(Attribute.fromGPB(attrData));
        }
        for (InstanceData instData : seriesData.getInstancesList()) {
            series.putInstance(Instance.fromGPB(instData));
        }
        return series;
    }

    SeriesData toGPB() {
        SeriesData.Builder builder = SeriesData.newBuilder();
        if (this.seriesInstanceUID != null ) {
        	builder.setSeriesInstanceUid(this.seriesInstanceUID);
        }
        for (Attribute attr : this.attributeMap.values()) {
            builder.addAttributes(attr.toGPB());
        }
        for (Attribute attr : this.normalizedInstanceAttributeMap.values()) {
            builder.addNormalizedInstanceAttributes(attr.toGPB());
        }
        for (Instance inst : this.instanceList) {
            builder.addInstances(inst.toGPB());
        }
        SeriesData data = builder.build();
        return data;
    }
}

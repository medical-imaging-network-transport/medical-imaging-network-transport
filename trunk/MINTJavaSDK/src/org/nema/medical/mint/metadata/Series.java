/*
 *   Copyright 2010 MINT Working Group
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.nema.medical.mint.metadata;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.nema.medical.mint.metadata.GPB.AttributeData;
import org.nema.medical.mint.metadata.GPB.InstanceData;
import org.nema.medical.mint.metadata.GPB.SeriesData;


/**
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://medical.nema.org/mint" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="SeriesType">
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
    private final Map<Integer,Attribute> attributeMap = new TreeMap<Integer,Attribute>();
    private final Map<Integer,Attribute> normalizedInstanceAttributeMap = new TreeMap<Integer,Attribute>();
    private final Map<String, Instance> instances = new TreeMap<String, Instance>();
    private String seriesInstanceUID;
    private String exclude;

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
    public Attribute getNormalizedInstanceAttribute(final int tag) {
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
    public void removeNormalizedInstanceAttribute(final int tag) {
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
        instances.put(inst.getSOPInstanceUID(), inst);
    }

    /**
     * removes an Instance from the Series based on its SOP Instance UID
     * @param sopInstanceUID
     */
    public Instance removeInstance(final String sopInstanceUID) {
        return instances.remove(sopInstanceUID);
    }

    /**
     * gets an Instance from the Series based on its SOP Instance UID
     * @param sopInstanceUID
     */
    public Instance getInstance(final String sopInstanceUID) {
        return instances.get(sopInstanceUID);
    }

    /**
     * @return an iterator of all Instances in the Series
     */
    public Iterator<Instance> instanceIterator() {
        return instances.values().iterator();
    }

    /**
     * @return the number of Instances in the Series
     */
    public int instanceCount() {
        return instances.size();
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

    /**
     * Get the 'exclude' attribute value.
     *
     * @return value
     */
    public String getExclude() {
        return exclude;
    }

    /**
     * Set the 'exclude' attribute value.
     *
     * @param exclude
     */
    public void setExclude(String exclude) {
        this.exclude = exclude;
    }

	/**
	 * @return the number of instances in this series
	 */
	public int getInstanceCount() {
		return instances.size();
	}

    /**
     * This is here only to satisfy JiBX. Does nothing, as the value is computed.
	 * @param instanceCount the number of instances to set
	 */
	private void setInstanceCount(final int instanceCount) {
        //Do nothing
	}

	//
    // Google Protocol Buffer support - package protection intentional
    //
    static Series fromGPB(SeriesData data) {
        Series series = new Series();
        if (data.hasSeriesInstanceUid()) {
            series.setSeriesInstanceUID(data.getSeriesInstanceUid());
        }
        if (data.hasExclude()) {
        	series.setExclude(data.getExclude());
        }
        for (AttributeData attrData : data.getAttributesList()) {
            series.putAttribute(Attribute.fromGPB(attrData));
        }
        for (AttributeData attrData : data.getNormalizedInstanceAttributesList()) {
            series.putNormalizedInstanceAttribute(Attribute.fromGPB(attrData));
        }
        for (InstanceData instData : data.getInstancesList()) {
            series.putInstance(Instance.fromGPB(instData));
        }
        return series;
    }

    SeriesData toGPB() {
        SeriesData.Builder builder = SeriesData.newBuilder();
        if (this.seriesInstanceUID != null ) {
            builder.setSeriesInstanceUid(this.seriesInstanceUID);
        }
        if (this.exclude != null) {
            builder.setExclude(this.exclude);
        }
        builder.setInstanceCount(this.getInstanceCount());
        for (Attribute attr : this.attributeMap.values()) {
            builder.addAttributes(attr.toGPB());
        }
        for (Attribute attr : this.normalizedInstanceAttributeMap.values()) {
            builder.addNormalizedInstanceAttributes(attr.toGPB());
        }
        for (Instance inst : this.instances.values()) {
            builder.addInstances(inst.toGPB());
        }
        SeriesData data = builder.build();
        return data;
    }
}

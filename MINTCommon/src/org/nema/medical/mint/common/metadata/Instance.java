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

package org.nema.medical.mint.common.metadata;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.nema.medical.mint.common.metadata.gpb.MINT2GPB.AttributeData;
import org.nema.medical.mint.common.metadata.gpb.MINT2GPB.InstanceData;


/**
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://medical.nema.org/mint" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="InstanceType">
 *   &lt;xs:sequence>
 *     &lt;xs:element type="ns:AttributesType" name="Attributes" minOccurs="0" maxOccurs="unbounded"/>
 *   &lt;/xs:sequence>
 *   &lt;xs:attribute type="xs:string" use="required" name="xfer"/>
 * &lt;/xs:complexType>
 * </pre>
 */
public class Instance implements AttributeStore
{
    private final Map<Integer,Attribute> attributeMap = new HashMap<Integer,Attribute>();
    private String xfer;

    /**
     * @param tag
     * @return the attribute for the given tag (in hex)
     */
    public Attribute getAttribute(final int tag) {
        return attributeMap.get(tag);
    }

    // todo pull into superclass
    public String getValueForAttribute(final int tag) {
        Attribute attr = getAttribute(tag);
        return attr != null ? attr.getVal() : null;
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
     * Get the 'xfer' attribute value.
     *
     * @return value
     */
    public String getXfer() {
        return xfer;
    }

    /**
     * Set the 'xfer' attribute value.
     *
     * @param xfer
     */
    public void setXfer(String xfer) {
        this.xfer = xfer;
    }

    //
    // Google Protocol Buffer support - package protection intentional
    //
    static Instance fromGPB(InstanceData instanceData) {
        Instance instance = new Instance();
        instance.setXfer(instanceData.getTransferSyntaxUid());
        for (AttributeData attrData : instanceData.getAttributesList()) {
            instance.putAttribute(Attribute.fromGPB(attrData));
        }
        return instance;
    }

    InstanceData toGPB() {
        InstanceData.Builder builder = InstanceData.newBuilder();
        if (this.xfer != null) {
        	builder.setTransferSyntaxUid(this.xfer);
        }
        for (Attribute attr : this.attributeMap.values()) {
            builder.addAttributes(attr.toGPB());
        }
        InstanceData data = builder.build();
        return data;
    }
}

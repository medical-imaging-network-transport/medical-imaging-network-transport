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
import org.nema.medical.mint.metadata.GPB.ItemData;


/**
 * Schema fragment(s) for this class:
 *
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://medical.nema.org/mint" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="ItemType">
 *   &lt;xs:complexContent>
 *     &lt;xs:extension base="ns:AttributesType"/>
 *   &lt;/xs:complexContent>
 * &lt;/xs:complexType>
 * </pre>
 */
public class Item implements AttributeContainer, Cloneable {
    private Map<Integer, Attribute> attributeMap = new TreeMap<Integer, Attribute>();

    @Override
    public Object clone() throws CloneNotSupportedException {
        final Item clone = (Item) super.clone();
        clone.attributeMap = new TreeMap<Integer, Attribute>(attributeMap);
        for (final Map.Entry<Integer, Attribute> entry: attributeMap.entrySet()) {
            clone.attributeMap.put(entry.getKey(), (Attribute) entry.getValue().clone());
        }
        return clone;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return attributeMap.equals(((Item)o).attributeMap);
    }

    @Override
    public int hashCode() {
        return attributeMap.hashCode();
    }

    /**
     * @param tag
     * @return the attribute for the given tag
     */
    public Attribute getAttribute(final int tag) {
        return attributeMap.get(tag);
    }

    /**
     * puts an Attribute into the Series - attributes are unique per tag
     *
     * @param attr
     */
    public void putAttribute(final Attribute attr) {
        attributeMap.put(attr.getTag(), attr);
    }

    /**
     * removes the Attribute with the given tag from the Series
     *
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

    //
    // Google Protocol Buffer support - package protection intentional
    //
    static Item fromGPB(ItemData itemData) {
        Item item = new Item();
        for (AttributeData attrData : itemData.getAttributesList()) {
            item.putAttribute(Attribute.fromGPB(attrData));
        }
        return item;
    }

    ItemData toGPB() {
        ItemData.Builder builder = ItemData.newBuilder();
        for (Attribute attr : this.attributeMap.values()) {
            builder.addAttributes(attr.toGPB());
        }
        ItemData data = builder.build();
        return data;
    }
}

package org.nema.medical.mint.common.metadata;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.nema.medical.mint.common.metadata.Mint2Gpb.AttributeData;
import org.nema.medical.mint.common.metadata.Mint2Gpb.ItemData;


/**
 * Schema fragment(s) for this class:
 *
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://vitalimages.com/contentserver/mint" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="ItemType">
 *   &lt;xs:complexContent>
 *     &lt;xs:extension base="ns:AttributesType"/>
 *   &lt;/xs:complexContent>
 * &lt;/xs:complexType>
 * </pre>
 */
public class Item implements AttributeStore {
    private final Map<Integer, Attribute> attributeMap = new HashMap<Integer, Attribute>();

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

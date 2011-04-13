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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.nema.medical.mint.metadata.GPB.AttributeData;
import org.nema.medical.mint.metadata.GPB.ItemData;

import com.google.protobuf.ByteString;


/**
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://medical.nema.org/mint" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="AttrType">
 *   &lt;xs:sequence>
 *     &lt;xs:element type="ns:ItemType" name="Item" minOccurs="0" maxOccurs="1"/>
 *   &lt;/xs:sequence>
 *   &lt;xs:attribute type="xs:string" use="required" name="tag"/>
 *   &lt;xs:attribute type="xs:string" use="required" name="vr"/>
 *   &lt;xs:attribute type="xs:string" use="optional" name="val"/>
 *   &lt;xs:attribute type="xs:string" use="optional" name="bid"/>
 *   &lt;xs:attribute type="xs:string" use="optional" name="bsize"/>
 *   &lt;xs:attribute type="xs:string" use="optional" name="bytes"/>
 * &lt;/xs:complexType>
 * </pre>
 */
public class Attribute implements Cloneable, Excludable {
    private List<Item> items = new ArrayList<Item>();
    private int tag;
    private String vr;
    private String val;
    private int bid = -1; // index must be a positive integer
    private int bsize = -1;
    private int frameCount = 1; // index must be a positive integer
    private byte[] bytes;
    //TODO unclear what the contents of the exclude String are supposed to mean; currently used in just a boolean fashion
    private String exclude;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Attribute attribute = (Attribute) o;

        if (tag != attribute.tag) {
            return false;
        }
        if (vr != attribute.vr && (vr == null || !vr.equals(attribute.vr))) {
            return false;
        }
        if (!Arrays.equals(bytes, attribute.bytes)) {
            return false;
        }
        if (val != attribute.val && (val == null || !val.equals(attribute.val))) {
            return false;
        }
        if (bid != attribute.bid) {
            return false;
        }
        if (bsize != attribute.bsize) {
            return false;
        }
        if (frameCount != attribute.frameCount) {
            return false;
        }
        if (exclude != attribute.exclude && (exclude == null || !exclude.equals(attribute.exclude))) {
            return false;
        }
        if (!items.equals(attribute.items)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = items.hashCode();
        result = 31 * result + tag;
        result = 31 * result + (vr != null ? vr.hashCode() : 0);
        result = 31 * result + (val != null ? val.hashCode() : 0);
        result = 31 * result + bid;
        result = 31 * result + bsize;
        result = 31 * result + frameCount;
        result = 31 * result + (bytes != null ? Arrays.hashCode(bytes) : 0);
        result = 31 * result + (exclude != null ? exclude.hashCode() : 0);
        return result;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        final Attribute clone = (Attribute) super.clone();
        clone.items = new ArrayList<Item>(items.size());
        for (final Item item: items) {
            clone.items.add((Item)item.clone());
        }
        if (bytes != null) {
            clone.bytes = new byte[bytes.length];
            System.arraycopy(bytes, 0, clone.bytes, 0, bytes.length);
        }
        return clone;
    }

    /**
     * puts an Item into the Attribute
     * @param item
     */
    public void addItem(final Item item) {
        items.add(item);
    }

    /**
     * removes the Item at the given index in the list
     * @param index
     */
    public void removeItem(final int index) {
        items.remove(index);
    }

    /**
     * @return an iterator of all Items in the Attribute
     */
    public Iterator<Item> itemIterator() {
        return items.iterator();
    }

    /**
     * @return true if the Attribute has sequence items
     */
    public boolean hasSequenceItems() {
        return !items.isEmpty();
    }

    /**
     * Get the 'tag' attribute value.
     *
     * @return tag
     */
    public int getTag() {
        return tag;
    }

    /**
     * Set the 'tag' attribute value.
     *
     * @param tag
     */
    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getBinarySize() {
    	return bsize;
    }

    public void setBinarySize(int size) {
    	if (size < -1) size = -1;
    	this.bsize = size;
    }

    /**
     * Set the 'tag' attribute value ... as a hex string
     *
     * @param hex
     */
    public void setTag(String hex) {
        this.tag = StudyIO.hex2int(hex);
    }

    /**
     * Get the 'vr' attribute value.
     *
     * @return vr
     */
    public String getVr() {
        return vr;
    }

    /**
     * Set the 'vr' attribute value.
     *
     * @param vr
     */
    public void setVr(String vr) {
        this.vr = vr;
    }

    /**
     * Get the 'val' attribute value.
     *
     * @return val
     */
    public String getVal() {
        return val;
    }

    /**
     * Set the 'val' attribute value.
     *
     * @param val
     */
    public void setVal(String val) {
        this.val = val;
    }

    /**
     * Get the 'bid' attribute value.
     *
     * @return bid
     */
    public int getBid() {
        return bid;
    }

    /**
     * Set the 'bid' attribute value.
     * Negative values are not valid, except -1 which signifies no value,
     * since zero is valid and null is not allowed for a primitive.
     * @param bid
     */
    public void setBid(int bid) {
        if (bid < -1) bid = -1;
        this.bid = bid;
    }

    /**
     * Get the 'frameCount' attribute value.
     *
     * @return frameCount
     */
    public int getFrameCount() {
        return frameCount;
    }

    /**
     * Set the 'frameCount' attribute value.
     * The minimum (and default) value is 1.
     * @param frameCount
     */
    //TODO It's odd to have a default of 1 when we don't care about frameCount as in the case of non-binary attributes
    //TODO It's odd as well to automatically clamp the value without the caller knowing, instead of throwing an error
    public void setFrameCount(int frameCount) {
        if (frameCount < 1) frameCount = 1;
        this.frameCount = frameCount;
    }

    /**
     * Get the actual bytes stored in the base64 encoded attribute value 'bytes'.
     *
     * @return bytes
     */
    public byte[] getBytes() {
        return bytes;
    }

    /**
     * Set the 'bytes' attribute value using the exact bytes (this will be encoded as base64 in xml and json)
     * @param bytes
     */
    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * Get the 'exclude' attribute value.
     *
     * @return exclude
     */
    @Override
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

    //
    //  Google Protocol Buffer support - package protection intentional
    //
    static Attribute fromGPB(AttributeData attrData) {
        Attribute attr = new Attribute();
        attr.setTag(attrData.getTag());
        attr.setVr(attrData.getVr());
        if (attrData.hasStringValue()) attr.setVal(attrData.getStringValue());
        if (attrData.hasExclude()) attr.setExclude(attrData.getStringValue());
        if (attrData.hasBinaryItemId()) { attr.setBid(attrData.getBinaryItemId()); }
        if (attrData.hasBinaryItemSize()) { attr.setBinarySize(attrData.getBinaryItemSize()); }
        if (attrData.hasFrameCount()) { attr.setFrameCount(attrData.getFrameCount()); }
        if (attrData.hasBytes()) { attr.setBytes(attrData.getBytes().toByteArray()); }
        for (ItemData itemData : attrData.getItemsList()) {
            attr.addItem(Item.fromGPB(itemData));
        }
        return attr;
    }

    AttributeData toGPB() {
        AttributeData.Builder builder = AttributeData.newBuilder();
        builder.setTag(this.tag);
        if (this.bid >=0 ) builder.setBinaryItemId(this.bid);
        if (this.bsize >= 0) builder.setBinaryItemSize(this.bsize);
        if (this.frameCount > 1) builder.setFrameCount(this.frameCount);
        if (this.vr != null) builder.setVr(this.vr);
        if (this.val != null) builder.setStringValue(this.val);
        if (this.exclude != null) builder.setExclude(this.val);
        if (this.bytes != null) builder.setBytes(ByteString.copyFrom(this.bytes));
        for (Item item : this.items) {
            builder.addItems(item.toGPB());
        }
        AttributeData data = builder.build();
        return data;
    }

}

package org.nema.medical.mint.datadictionary;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://medical.nema.org/mint" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="attributeType">
 *   &lt;xs:simpleContent>
 *     &lt;xs:extension base="xs:string">
 *       &lt;xs:attribute type="xs:hexBinary" use="optional" name="tag"/>
 *       &lt;xs:attribute type="xs:string" use="optional" name="desc"/>
 *     &lt;/xs:extension>
 *   &lt;/xs:simpleContent>
 * &lt;/xs:complexType>
 * </pre>
 */
public class LevelAttribute
{
    private int tag;
    private String desc;

    /**
     * Get the 'tag' attribute value.
     * 
     * @return value
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

    /** 
     * Get the 'desc' attribute value.
     * 
     * @return value
     */
    public String getDesc() {
        return desc;
    }

    /** 
     * Set the 'desc' attribute value.
     * 
     * @param desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }
}

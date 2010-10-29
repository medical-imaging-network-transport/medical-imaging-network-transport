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

package org.nema.medical.mint.datadictionary;

import java.util.ArrayList;
import java.util.List;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://medical.nema.org/mint" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="attributesType">
 *   &lt;xs:sequence>
 *     &lt;xs:element type="ns:elementType" name="element" minOccurs="0" maxOccurs="unbounded"/>
 *   &lt;/xs:sequence>
 *   &lt;xs:attribute type="xs:string" name="unknown-attributes"/>
 * &lt;/xs:complexType>
 * </pre>
 */
public class AttributesType
{
    private List<ElementType> elementList = new ArrayList<ElementType>();
    private String unknownAttributes;

    /** 
     * Get the list of 'element' element items.
     * 
     * @return list
     */
    public List<ElementType> getElements() {
        return elementList;
    }

    /** 
     * Set the list of 'element' element items.
     * 
     * @param list
     */
    public void setElements(List<ElementType> list) {
        elementList = list;
    }

    /** 
     * Get the 'unknown-attributes' attribute value.
     * 
     * @return value
     */
    public String getUnknownAttributes() {
        return unknownAttributes;
    }

    /** 
     * Set the 'unknown-attributes' attribute value.
     * 
     * @param unknownAttributes
     */
    public void setUnknownAttributes(String unknownAttributes) {
        this.unknownAttributes = unknownAttributes;
    }
}

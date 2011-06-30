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

import org.nema.medical.mint.metadata.StudyIO;

import java.util.*;

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
public class AttributesType implements Iterable<ElementType>
{
    private Map<Integer, ElementType> elementMap = new HashMap<Integer, ElementType>();
    private UnknownAttribute unknownAttributes;

    public enum UnknownAttribute {
        REJECT, ACCEPT
    }

    /** 
     * Get the {@link ElementType} elements in this type.
     * 
     * @return list
     */
    @Override
    public Iterator<ElementType> iterator() {
        return elementMap.values().iterator();
    }

    public boolean containsElement(final int tag) {
        return elementMap.containsKey(tag);
    }

    public ElementType getElement(final int tag) {
        return elementMap.get(tag);
    }

    /**
     * Add an element item. The element tag may be a regular tag or a template such as "002031xx".
     * 
     * @param element
     */
    public void addElement(final ElementType element) {
        addElements(element.getStringTag().toCharArray(), 0, element);
    }

    private void addElements(final char[] chars, final int curPos, final ElementType element) {
        if (curPos == chars.length) {
            //Parse as a long, then chop off the top 32 bits to arrive at the right (possibly negative) int value
            elementMap.put(StudyIO.hex2int(String.valueOf(chars)), element);
        } else if (chars[curPos] == 'x') {
            for (char curChar = '0'; curChar <= '9'; ++curChar) {
                chars[curPos] = curChar;
                addElements(chars, curPos + 1, element);
            }
            for (char curChar = 'A'; curChar <= 'F'; ++curChar) {
                chars[curPos] = curChar;
                addElements(chars, curPos + 1, element);
            }
            chars[curPos] = 'x';
        } else {
            addElements(chars, curPos + 1, element);
        }
    }

    /** 
     * Get the 'unknown-attributes' attribute value.
     * 
     * @return value
     */
    public UnknownAttribute getUnknownAttributes() {
        return unknownAttributes;
    }

    /** 
     * Set the 'unknown-attributes' attribute value.
     * 
     * @param unknownAttributes
     */
    public void setUnknownAttributes(final UnknownAttribute unknownAttributes) {
        this.unknownAttributes = unknownAttributes;
    }
}

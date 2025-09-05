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
 * &lt;xs:complexType xmlns:ns="http://medical.nema.org/mint" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="series-attributesType"&gt;
 *   &lt;xs:sequence&gt;
 *     &lt;xs:element type="ns:attributeType" name="attribute" minOccurs="0" maxOccurs="unbounded"/&gt;
 *   &lt;/xs:sequence&gt;
 * &lt;/xs:complexType&gt;
 * </pre>
 */
public class SeriesAttributesType
{
    private List<AttributeType> attributeList = new ArrayList<AttributeType>();

    /** 
     * Get the list of 'attribute' element items.
     * 
     * @return list
     */
    public List<AttributeType> getAttributes() {
        return attributeList;
    }

    /** 
     * Set the list of 'attribute' element items.
     * 
     * @param list
     */
    public void setAttributes(List<AttributeType> list) {
        attributeList = list;
    }
}

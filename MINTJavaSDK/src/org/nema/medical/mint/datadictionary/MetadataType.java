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

import java.util.Collection;
import java.util.HashSet;

/**
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://medical.nema.org/mint" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="metadataType">
 *   &lt;xs:sequence>
 *     &lt;xs:element type="ns:attributesType" name="attributes" minOccurs="1"/>
 *     &lt;xs:element type="ns:study-attributesType" name="study-attributes" minOccurs="1"/>
 *     &lt;xs:element type="ns:series-attributesType" name="series-attributes" minOccurs="1"/>
 *   &lt;/xs:sequence>
 *   &lt;xs:attribute type="xs:string" name="type"/>
 *   &lt;xs:attribute type="xs:float" name="version"/>
 * &lt;/xs:complexType>
 * </pre>
 */
public class MetadataType
{
    private AttributesType attributes;
    private StudyAttributesType studyAttributes;
    private SeriesAttributesType seriesAttributes;
    private String type;
    private Float version;

    /**
     * Convenience method
     */
    //TODO Should really refactor StudyAttributesType & SeriesAttributesType so that we don't need this method
    public static final Collection<Integer> extractTagSet(final Collection<AttributeType> attributes) {
        final Collection<Integer> attributeTags = new HashSet<Integer>(attributes.size());
        for (final AttributeType attributeType: attributes) {
            attributeTags.add(Integer.valueOf(attributeType.getTag(), 16));
        }
        return attributeTags;
    }

    /**
     * Get the 'attributes' element value.
     * 
     * @return value
     */
    public AttributesType getAttributes() {
        return attributes;
    }

    /** 
     * Set the 'attributes' element value.
     * 
     * @param attributes
     */
    public void setAttributes(AttributesType attributes) {
        this.attributes = attributes;
    }

    /** 
     * Get the 'study-attributes' element value.
     * 
     * @return value
     */
    public StudyAttributesType getStudyAttributes() {
        return studyAttributes;
    }

    /** 
     * Set the 'study-attributes' element value.
     * 
     * @param studyAttributes
     */
    public void setStudyAttributes(StudyAttributesType studyAttributes) {
        this.studyAttributes = studyAttributes;
    }

    /** 
     * Get the 'series-attributes' element value.
     * 
     * @return value
     */
    public SeriesAttributesType getSeriesAttributes() {
        return seriesAttributes;
    }

    /** 
     * Set the 'series-attributes' element value.
     * 
     * @param seriesAttributes
     */
    public void setSeriesAttributes(SeriesAttributesType seriesAttributes) {
        this.seriesAttributes = seriesAttributes;
    }

    /** 
     * Get the 'type' attribute value.
     * 
     * @return value
     */
    public String getType() {
        return type;
    }

    /** 
     * Set the 'type' attribute value.
     * 
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /** 
     * Get the 'version' attribute value.
     * 
     * @return value
     */
    public float getVersion() {
        return version;
    }

    /** 
     * Set the 'version' attribute value.
     * 
     * @param version
     */
    public void setVersion(float version) {
        this.version = version;
    }
}

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

import java.math.BigDecimal;

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
 *   &lt;xs:attribute type="xs:decimal" name="version"/>
 * &lt;/xs:complexType>
 * </pre>
 */
public class MetadataType
{
    private AttributesType attributes;
    private LevelAttributes studyAttributes;
    private LevelAttributes seriesAttributes;
    private BigDecimal version;

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
    public LevelAttributes getStudyAttributes() {
        return studyAttributes;
    }

    /** 
     * List the 'study-attributes' element value.
     * 
     * @param studyAttributes
     */
    public void setStudyAttributes(LevelAttributes studyAttributes) {
        this.studyAttributes = studyAttributes;
    }

    /** 
     * Get the 'series-attributes' element value.
     * 
     * @return value
     */
    public LevelAttributes getSeriesAttributes() {
        return seriesAttributes;
    }

    /** 
     * Set the 'series-attributes' element value.
     * 
     * @param seriesAttributes
     */
    public void setSeriesAttributes(LevelAttributes seriesAttributes) {
        this.seriesAttributes = seriesAttributes;
    }

    /**
     * Get the 'version' attribute value.
     * 
     * @return value
     */
    public BigDecimal getVersion() {
        return version;
    }

    /** 
     * Set the 'version' attribute value.
     * 
     * @param version
     */
    public void setVersion(BigDecimal version) {
        this.version = version;
    }
}

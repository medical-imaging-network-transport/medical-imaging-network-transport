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

/**
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://medical.nema.org/mint" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="elementType"&gt;
 *   &lt;xs:simpleContent&gt;
 *     &lt;xs:extension base="xs:string"&gt;
 *       &lt;xs:attribute type="xs:string" use="required" name="tag"/&gt;
 *       &lt;xs:attribute type="xs:string" use="required" name="keyword"/&gt;
 *       &lt;xs:attribute type="xs:string" use="required" name="vr"/&gt;
 *       &lt;xs:attribute type="xs:string" use="optional" name="vm"/&gt;
 *       &lt;xs:attribute type="xs:string" use="optional" name="ret"/&gt;
 *     &lt;/xs:extension&gt;
 *   &lt;/xs:simpleContent&gt;
 * &lt;/xs:complexType&gt;
 * </pre>
 */
public class ElementType
{
    private String string;
    private String tag;
    private String keyword;
    private String vr;
    private String vm;
    private boolean retired;

    /** 
     * Get the extension value.
     * 
     * @return value
     */
    public String getString() {
        return string;
    }

    /** 
     * Set the extension value.
     * 
     * @param string
     */
    public void setString(String string) {
        this.string = string;
    }

    /**
     * Get the 'tag' attribute value.
     *
     * @return value
     * @exception  NumberFormatException if the tag is a template, i.e. it does not have an integer representation.
     */
    public int getTag() throws NumberFormatException {
        return StudyIO.hex2int(tag);
    }

    /**
     * Set the 'tag' attribute value.
     *
     * @param tag
     */
    public void setTag(int tag) {
        this.tag = StudyIO.int2hex(tag);
    }


    /** 
     * Get the 'tag' attribute value. Besides returning an actual tag, this may return a tag template, e.g. "002031xx".
     * 
     * @return value
     */
    public String getStringTag() {
        return tag;
    }

    /** 
     * Set the 'tag' attribute value. This takes a string, as tag templates may be passed, e.g. "002031xx", besides
     * regular tags.
     * 
     * @param tag
     */
    public void setStringTag(String tag) {
        this.tag = tag;
    }

    /** 
     * Get the 'keyword' attribute value.
     * 
     * @return value
     */
    public String getKeyword() {
        return keyword;
    }

    /** 
     * Set the 'keyword' attribute value.
     * 
     * @param keyword
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    /** 
     * Get the 'vr' attribute value.
     * 
     * @return value
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
     * Get the 'vm' attribute value.
     * 
     * @return value
     */
    public String getVm() {
        return vm;
    }

    /** 
     * Set the 'vm' attribute value.
     * 
     * @param vm
     */
    public void setVm(String vm) {
        this.vm = vm;
    }

    /** 
     * Get the 'ret' attribute value.
     * 
     * @return value
     */
    public boolean getRetired() {
        return retired;
    }

    /** 
     * Set the 'ret' attribute value.
     * 
     * @param retired
     */
    public void setRetired(boolean retired) {
        this.retired = retired;
    }
}

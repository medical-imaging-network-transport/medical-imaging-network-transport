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

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://medical.nema.org/mint" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="elementType">
 *   &lt;xs:simpleContent>
 *     &lt;xs:extension base="xs:string">
 *       &lt;xs:attribute type="xs:string" use="required" name="tag"/>
 *       &lt;xs:attribute type="xs:string" use="required" name="keyword"/>
 *       &lt;xs:attribute type="xs:string" use="required" name="vr"/>
 *       &lt;xs:attribute type="xs:string" use="optional" name="vm"/>
 *       &lt;xs:attribute type="xs:string" use="optional" name="ret"/>
 *     &lt;/xs:extension>
 *   &lt;/xs:simpleContent>
 * &lt;/xs:complexType>
 * </pre>
 */
public class ElementType
{
    private String string;
    private String tag;
    private String keyword;
    private String vr;
    private String vm;
    private String ret;

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
     */
    public String getTag() {
        return tag;
    }

    /** 
     * Set the 'tag' attribute value.
     * 
     * @param tag
     */
    public void setTag(String tag) {
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
    public String getRet() {
        return ret;
    }

    /** 
     * Set the 'ret' attribute value.
     * 
     * @param ret
     */
    public void setRet(String ret) {
        this.ret = ret;
    }
}

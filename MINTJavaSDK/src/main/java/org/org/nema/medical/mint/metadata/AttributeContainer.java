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

import java.util.Iterator;

/**
 * @author Uli Bubenheimer
 *
 */
public interface AttributeContainer {
    /**
     * @param tag
     * @return the attribute for the given tag
     */
    Attribute getAttribute(final int tag);

    /**
     * puts an Attribute into the Series - attributes are unique per tag
     * @param attr
     */
    void putAttribute(final Attribute attr);

    /**
     * removes the Attribute with the given tag from the Series
     * @param tag
     */
    void removeAttribute(final int tag);

    /**
     * @return an iterator of all Attributes in the Series
     */
    Iterator<Attribute> attributeIterator();
}

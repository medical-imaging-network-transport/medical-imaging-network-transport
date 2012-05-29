/*
 *   Copyright 2011 MINT Working Group
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

import java.util.*;

public class LevelAttributes
{
    private Map<Integer, LevelAttribute> attributeTypes = new HashMap<Integer, LevelAttribute>();

    public boolean containsTag(int tag) {
        return attributeTypes.containsKey(tag);
    }

    public String getDescription(int tag) {
        return attributeTypes.get(tag).getDesc();
    }

    /**
     * For JiBX.
     */
    public Iterator<LevelAttribute> attributeIterator() {
        return attributeTypes.values().iterator();
    }

    /**
     * For JiBX.
     */
    public void addAttributeType(LevelAttribute levelAttribute) {
        addAttributeType(levelAttribute.getTag(), levelAttribute.getDesc());
    }

    public void addAttributeType(int tag, String description) {
        final LevelAttribute levelAttribute = new LevelAttribute();
        levelAttribute.setTag(tag);
        levelAttribute.setDesc(description);
        attributeTypes.put(tag, levelAttribute);
    }
}

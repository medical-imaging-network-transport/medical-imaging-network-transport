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
package org.nema.medical.mint.utils;

import org.nema.medical.mint.metadata.*;

/**
* @author Uli Bubenheimer
*/
public class StudyTraversals {

    StudyTraversals() {
        throw new AssertionError("Class not to be instantiated");
    }

    public static class TraversalException extends Exception {
        public TraversalException() {
        }

        public TraversalException(Throwable cause) {
            super(cause);
        }

        public TraversalException(String message) {
            super(message);
        }

        public TraversalException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static interface AttributeAction {
        void doAction(Attribute attribute) throws TraversalException;
    }

    public static void allAttributeTraverser(final StudyMetadata study, final AttributeAction action)
            throws TraversalException {
        hierarchicalAttributeStoreTraverser(study, action);

        for (final Series series: Iter.iter(study.seriesIterator())) {
            hierarchicalAttributeStoreTraverser(series, action);

            for (final Attribute attr: Iter.iter(series.normalizedInstanceAttributeIterator())) {
                hierarchicalAttributeTraverser(attr, action);
            }

            for (final Instance instance: Iter.iter(series.instanceIterator())) {
                hierarchicalAttributeStoreTraverser(instance, action);
            }
        }
    }

    public static void flatStudyAttributeTraverser(final StudyMetadata study, final AttributeAction action)
            throws TraversalException {
        flatAttributeStoreTraverser(study, action);
    }

    public static void flatSeriesAttributeTraverser(final StudyMetadata study, final AttributeAction action)
            throws TraversalException {
        for (final Series series: Iter.iter(study.seriesIterator())) {
            flatAttributeStoreTraverser(series, action);
        }
    }

    public static void flatAttributeStoreTraverser(final AttributeStore attributes, final AttributeAction action)
            throws TraversalException {
        for (final Attribute attr: Iter.iter(attributes.attributeIterator())) {
            action.doAction(attr);
        }
    }

    public static void hierarchicalAttributeStoreTraverser(final AttributeStore attributes, final AttributeAction action)
            throws TraversalException {
        for (final Attribute attr: Iter.iter(attributes.attributeIterator())) {
            hierarchicalAttributeTraverser(attr, action);
        }
    }

    public static void hierarchicalAttributeTraverser(final Attribute attribute, final AttributeAction action)
            throws TraversalException {
        action.doAction(attribute);

        for (final Item item: Iter.iter(attribute.itemIterator())) {
            for (final Attribute attr: Iter.iter(item.attributeIterator())) {
                hierarchicalAttributeTraverser(attr, action);
            }
        }
    }
}

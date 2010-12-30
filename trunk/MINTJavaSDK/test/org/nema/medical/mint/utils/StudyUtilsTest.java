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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nema.medical.mint.metadata.*;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * @author Uli Bubenheimer
 */
public class StudyUtilsTest {
    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testMergeStudy() {
        //TODO
    }

    @Test
    public void testApplyExcludes() {
        //TODO
    }

    @Test
    public void testShiftStudyBids() {
        //TODO
    }

    @Test
    public void testRemoveExcludes() {
        //TODO
    }

    @Test
    public void testIsExclude() {
        //TODO
    }

    @Test
    public void testDenormalizeStudy() {
        //TODO
    }

    @Test
    public void testNormalizeStudy() {
        //TODO
    }

    @Test
    public void testEqualAttributes() {
        //TODO
    }

    @Test
    public void testWriteStudy() {
        //TODO
    }

    @Test
    public void testMoveBinaryItems() {
        //TODO
    }

    @Test
    public void testDeleteFolder() {
        //TODO
    }

    @Test
    public void testGetBaseVersion() {
        //We probably shouldn't just change what version a study starts with, so having a test for this makes sense
        assertEquals("0", StudyUtils.getBaseVersion());
    }

    @Test
    public void testGetNextVersion() {
        assertEquals("1", StudyUtils.getNextVersion("0"));
        assertEquals("10", StudyUtils.getNextVersion("9"));
    }

    @Test
    public void testTagString() {
        assertEquals("0A001000", StudyUtils.tagString(167776256));
    }
}

//------------------------------------------------------------------------------
// Copyright (c) 2011 Vital Images, Inc. All Rights Reserved.
//
// This is UNPUBLISHED PROPRIETARY SOURCE CODE of Vital Images, Inc.;
// the contents of this file may not be disclosed to third parties,
// copied or duplicated in any form, in whole or in part, without the
// prior written permission of Vital Images, Inc.
//
// RESTRICTED RIGHTS LEGEND:
// Use, duplication or disclosure by the Government is subject to
// restrictions as set forth in subdivision (c)(1)(ii) of the Rights
// in Technical Data and Computer Software clause at DFARS 252.227-7013,
// and/or in similar or successor clauses in the FAR, DOD or NASA FAR
// Supplement. Unpublished rights reserved under the Copyright Laws of
// the United States.
//------------------------------------------------------------------------------

package org.nema.medical.mint.metadata;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author Uli Bubenheimer
 */
public class ItemTest {
    @Test
    public void testEquals() throws Exception {
        final Item item1 = new Item();
        assertFalse(item1.equals(null));
        final Item item2 = new Item();
        assertEquals(item1, item2);
        final Attribute attr1 = new Attribute();
        attr1.setTag(123);
        attr1.setVal("456");
        item1.putAttribute(attr1);
        assertFalse(item1.equals(item2));
        final Attribute attr2 = new Attribute();
        attr2.setTag(123);
        attr2.setVal("456");
        item2.putAttribute(attr2);
        assertEquals(item1, item2);
    }

    @Test
    public void testHashcode() throws CloneNotSupportedException {
        final Item item1 = new Item();
        final Item item2 = new Item();
        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());

        final Attribute attr1 = new Attribute();
        attr1.setTag(123);
        attr1.setVal("456");
        item1.putAttribute(attr1);

        final Attribute attr2 = new Attribute();
        attr2.setTag(123);
        attr2.setVal("456");
        item2.putAttribute(attr2);

        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    public void testClone() throws CloneNotSupportedException {
        final Item item = new Item();
        assertEquals(item, item.clone());

        final Attribute attr1 = new Attribute();
        attr1.setTag(123);
        attr1.setVr("456");
        item.putAttribute(attr1);
        assertEquals(item, item.clone());
        final Attribute attr2 = new Attribute();
        attr2.setTag(789);
        attr2.setVal("012");
        item.putAttribute(attr2);
        assertEquals(item, item.clone());
    }

}

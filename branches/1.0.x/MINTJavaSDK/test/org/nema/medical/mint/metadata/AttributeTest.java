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
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author Uli Bubenheimer
 */
public class AttributeTest {
    @Test
    public void testEquals() {
        final Attribute attribute1 = new Attribute();
        assertThat(attribute1, is(notNullValue(null)));
        final Attribute attribute2 = new Attribute();
        assertThat(attribute1, is(attribute2));

        attribute1.setTag(0x00020010);
        assertThat(attribute1, is(not(attribute2)));
        assertThat(attribute2, is(not(attribute1)));
        attribute2.setTag(0x00030010);
        assertThat(attribute1, is(not(attribute2)));
        attribute2.setTag(0x00020010);
        assertThat(attribute1, is(attribute2));

        attribute1.setVr("");
        assertThat(attribute1, is(not(attribute2)));
        assertThat(attribute2, is(not(attribute1)));
        attribute2.setVr("");
        assertThat(attribute1, is(attribute2));
        attribute2.setVr(null);
        attribute1.setVr("SQ");
        assertThat(attribute1, is(not(attribute2)));
        assertThat(attribute2, is(not(attribute1)));
        attribute2.setVr("");
        assertThat(attribute1, is(not(attribute2)));
        attribute2.setVr("OW");
        assertThat(attribute1, is(not(attribute2)));
        attribute2.setVr("SQ");
        assertThat(attribute1, is(attribute2));

        attribute1.setBytes(new byte[0]);
        assertThat(attribute1, is(not(attribute2)));
        assertThat(attribute2, is(not(attribute1)));
        attribute2.setBytes(new byte[0]);
        assertThat(attribute1, is(attribute2));
        attribute2.setBytes(null);
        attribute1.setBytes(new byte[]{1, 2, 3});
        assertThat(attribute1, is(not(attribute2)));
        assertThat(attribute2, is(not(attribute1)));
        attribute2.setBytes(new byte[]{});
        assertThat(attribute1, is(not(attribute2)));
        attribute2.setBytes(new byte[]{1});
        assertThat(attribute1, is(not(attribute2)));
        attribute2.setBytes(new byte[]{1, 2, 3});
        assertThat(attribute1, is(attribute2));

        attribute1.setVal("");
        assertThat(attribute1, is(not(attribute2)));
        assertThat(attribute2, is(not(attribute1)));
        attribute2.setVal("");
        assertThat(attribute1, is(attribute2));
        attribute2.setVal(null);
        attribute1.setVal("1");
        assertThat(attribute1, is(not(attribute2)));
        assertThat(attribute2, is(not(attribute1)));
        attribute2.setVal("");
        assertThat(attribute1, is(not(attribute2)));
        attribute2.setVal("2");
        assertThat(attribute1, is(not(attribute2)));
        attribute2.setVal("1");
        assertThat(attribute1, is(attribute2));

        attribute1.setBid(0);
        assertThat(attribute1, is(not(attribute2)));
        assertThat(attribute2, is(not(attribute1)));
        attribute1.setBid(1);
        assertThat(attribute1, is(not(attribute2)));
        assertThat(attribute2, is(not(attribute1)));
        attribute2.setBid(0);
        assertThat(attribute1, is(not(attribute2)));
        attribute2.setBid(2);
        assertThat(attribute1, is(not(attribute2)));
        attribute2.setBid(1);
        assertThat(attribute1, is(attribute2));

        attribute1.setBinarySize(0);
        assertThat(attribute1, is(not(attribute2)));
        assertThat(attribute2, is(not(attribute1)));
        attribute1.setBinarySize(1);
        assertThat(attribute1, is(not(attribute2)));
        assertThat(attribute2, is(not(attribute1)));
        attribute2.setBinarySize(0);
        assertThat(attribute1, is(not(attribute2)));
        attribute2.setBinarySize(2);
        assertThat(attribute1, is(not(attribute2)));
        attribute2.setBinarySize(1);
        assertThat(attribute1, is(attribute2));

        attribute1.setFrameCount(1);
        assertThat(attribute1, is(attribute2));
        attribute1.setFrameCount(2);
        assertThat(attribute1, is(not(attribute2)));
        assertThat(attribute2, is(not(attribute1)));
        attribute2.setFrameCount(2);
        assertThat(attribute1, is(attribute2));

        attribute1.setExclude("");
        assertThat(attribute1, is(not(attribute2)));
        assertThat(attribute2, is(not(attribute1)));
        attribute2.setExclude("");
        assertThat(attribute1, is(attribute2));
        attribute2.setExclude(null);
        attribute1.setExclude("1");
        assertThat(attribute1, is(not(attribute2)));
        assertThat(attribute2, is(not(attribute1)));
        attribute2.setExclude("");
        assertThat(attribute1, is(not(attribute2)));
        attribute2.setExclude("2");
        assertThat(attribute1, is(not(attribute2)));
        attribute2.setExclude("1");
        assertThat(attribute1, is(attribute2));

        final Item item1 = new Item();
        final Item item2 = new Item();
        item2.putAttribute(new Attribute());
        attribute1.addItem(item1);
        assertThat(attribute1, is(not(attribute2)));
        assertThat(attribute2, is(not(attribute1)));
        attribute2.addItem(item2);
        assertThat(attribute1, is(not(attribute2)));
        attribute2.removeItem(0);
        attribute2.addItem(item1);
        assertThat(attribute1, is(attribute2));
        attribute2.addItem(item2);
        assertThat(attribute1, is(not(attribute2)));
    }

    @Test
    public void testHashcode() {
        final Attribute attr1 = new Attribute();
        final Attribute attr2 = new Attribute();
        assertThat(attr1, is(attr2));
        assertThat(attr1.hashCode(), is(attr2.hashCode()));

        attr1.setBid(1);
        attr1.setBinarySize(2);
        attr1.setBytes(new byte[]{3, 4, 5});
        attr1.setExclude("6");
        attr1.setFrameCount(7);
        attr1.setTag(0x00100002);
        attr1.setVal("8");
        attr1.setVr("ST");
        final Item item1 = new Item();
        final Attribute subAttr1 = new Attribute();
        subAttr1.setTag(0x00080001);
        item1.putAttribute(subAttr1);
        attr1.addItem(item1);

        attr2.setBid(1);
        attr2.setBinarySize(2);
        attr2.setBytes(new byte[]{3, 4, 5});
        attr2.setExclude("6");
        attr2.setFrameCount(7);
        attr2.setTag(0x00100002);
        attr2.setVal("8");
        attr2.setVr("ST");
        final Item item2 = new Item();
        final Attribute subAttr2 = new Attribute();
        subAttr2.setTag(0x00080001);
        item2.putAttribute(subAttr2);
        attr2.addItem(item2);

        assertThat(attr1, is(attr2));
        assertThat(attr1.hashCode(), is(attr2.hashCode()));
    }

    @Test
    public void testClone() throws CloneNotSupportedException {
        final Attribute attr1 = new Attribute();
        assertThat(attr1, is(attr1.clone()));

        attr1.setBid(1);
        attr1.setBinarySize(2);
        attr1.setBytes(new byte[]{3, 4, 5});
        attr1.setExclude("6");
        attr1.setFrameCount(7);
        attr1.setTag(0x00100002);
        attr1.setVal("8");
        attr1.setVr("ST");
        final Item item = new Item();
        final Attribute attr2 = new Attribute();
        attr2.setTag(0x00080001);
        item.putAttribute(attr2);
        attr1.addItem(item);

        assertThat(attr1, is(attr1.clone()));
    }

}

package org.nema.medical.mint.util;

import java.util.Iterator;

/* Copyright (c) Vital Images, Inc. 2010. All Rights Reserved.
 *
 *    This is UNPUBLISHED PROPRIETARY SOURCE CODE of Vital Images, Inc.;
 *    the contents of this file may not be disclosed to third parties,
 *    copied or duplicated in any form, in whole or in part, without the
 *    prior written permission of Vital Images, Inc.
 *
 *    RESTRICTED RIGHTS LEGEND:
 *    Use, duplication or disclosure by the Government is subject to
 *    restrictions as set forth in subdivision (c)(1)(ii) of the Rights
 *    in Technical Data and Computer Software clause at DFARS 252.227-7013,
 *    and/or in similar or successor clauses in the FAR, DOD or NASA FAR
 *    Supplement. Unpublished rights reserved under the Copyright Laws of
 *    the United States.
 */

public final class Iter {

    public static <Element> Iterable<Element> iter(final Iterator<Element> baseIter) {
        return new Iterable<Element>() {
            @Override
            public Iterator<Element> iterator() {
                return baseIter;
            }
        };
    }

    private Iter() {
        throw new Error("Not to be instantiated");
    }
}

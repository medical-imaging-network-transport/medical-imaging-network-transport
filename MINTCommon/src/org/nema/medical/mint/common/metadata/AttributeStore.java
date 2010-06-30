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

package org.nema.medical.mint.common.metadata;

import java.util.Iterator;

/**
 * @author Uli Bubenheimer
 *
 */
public interface AttributeStore {
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

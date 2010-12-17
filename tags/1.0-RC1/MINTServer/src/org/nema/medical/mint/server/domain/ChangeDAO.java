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
package org.nema.medical.mint.server.domain;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * @author Rex
 *
 */
public class ChangeDAO extends HibernateDaoSupport {
	
	@SuppressWarnings("unchecked")
	public List<Change> findChanges(final Date since, int first, int max) {
		if (since != null) {
            final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Change.class);                    
            detachedCriteria.add(Restrictions.gt("dateTime", since));
            detachedCriteria.addOrder(Order.desc("dateTime"));
            final List<Change> list = getHibernateTemplate().findByCriteria(detachedCriteria, first, max);
			return list;
		}
		return null;
	}
	
    @SuppressWarnings("unchecked")
    public List<Change> findChanges(final int first, final int max) {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Change.class);
        detachedCriteria.addOrder(Order.desc("dateTime"));

        final List<Change> list = getHibernateTemplate().findByCriteria(detachedCriteria, first, max);
        return list;
    }

	@SuppressWarnings("unchecked")
	public List<Change> findChanges(final String studyID) {
		if (StringUtils.isNotBlank(studyID)) {
			final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Change.class).add(
					Restrictions.eq("studyID", studyID));
			final List<Change> list = getHibernateTemplate().findByCriteria(detachedCriteria);

			return list;
		}
		return null;
	}
	
	public Change saveChange(final Change change) {
		if (change != null) {
			getHibernateTemplate().save(change);
			getHibernateTemplate().flush();
			getHibernateTemplate().refresh(change);
		}
		return change;
	}
}

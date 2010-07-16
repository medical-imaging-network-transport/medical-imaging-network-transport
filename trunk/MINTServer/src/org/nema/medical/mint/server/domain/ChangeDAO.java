package org.nema.medical.mint.server.domain;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * @author Rex
 *
 */
public class ChangeDAO extends HibernateDaoSupport {
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
	
	@SuppressWarnings("unchecked")
	public List<Change> getMostRecentChanges(final int max) {
		final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Change.class);
		
		final List<Change> list = getHibernateTemplate().findByCriteria(detachedCriteria, 0, max);
		return list;
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

package org.nema.medical.mint.common.domain;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class StudyDAO extends HibernateDaoSupport {

	public static final TimeZone GMT = TimeZone.getTimeZone("GMT");

	private static int MAXSECONDS = 60 * 60 * 24;

	private int between(final int first, final int second, final int value) {
		final int min = Math.min(first, second);
		final int max = Math.max(first, second);
		return Math.min(Math.max(value, min), max);
	}

	@SuppressWarnings("unchecked")
	public Study findStudy(final String uid) {
		if (StringUtils.isNotBlank(uid)) {
			final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Study.class).add(
					Restrictions.eq("id", uid));
			final List<Study> list = getHibernateTemplate().findByCriteria(detachedCriteria);
			if (!list.isEmpty()) {
				return list.get(0);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Study> getMostRecentStudies(final int max, final int seconds) {
		final Calendar calendar = Calendar.getInstance(GMT);
		calendar.add(Calendar.SECOND, -between(1, MAXSECONDS, seconds));

		final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Study.class).add(
				Restrictions.ge("updateTime", calendar.getTime())).addOrder(Order.desc("updateTime"));
		final List<Study> list = getHibernateTemplate().findByCriteria(detachedCriteria, 0, between(1, 50, max));
		return list;
	}

	public Study saveOrUpdateStudy(final Study study) {
		if (study != null) {
			study.setUpdateTime(Study.now());
			getHibernateTemplate().saveOrUpdate(study);
			getHibernateTemplate().flush();
			getHibernateTemplate().refresh(study);
		}
		return study;
	}
}

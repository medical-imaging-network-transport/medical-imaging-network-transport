package org.nema.medical.mint.common.domain;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class JobInfoDAO extends HibernateDaoSupport {
	@SuppressWarnings("unchecked")
	public JobInfo findJobInfo(final String uid) {
		if (StringUtils.isNotBlank(uid)) {
			final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Study.class).add(
					Restrictions.eq("id", uid));
			final List<JobInfo> list = getHibernateTemplate().findByCriteria(detachedCriteria);
			if (!list.isEmpty()) {
				return list.get(0);
			}
		}
		return null;
	}
	
	public JobInfo saveOrUpdateJobInfo(final JobInfo jobInfo) {
		if (jobInfo != null) {
			jobInfo.setUpdateTime(JobInfo.now());
			getHibernateTemplate().saveOrUpdate(jobInfo);
			getHibernateTemplate().flush();
			getHibernateTemplate().refresh(jobInfo);
		}
		return jobInfo;
	}
}

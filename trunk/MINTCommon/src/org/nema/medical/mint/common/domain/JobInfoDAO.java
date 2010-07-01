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
package org.nema.medical.mint.common.domain;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * @author Rex
 *
 */
public class JobInfoDAO extends HibernateDaoSupport {
	@SuppressWarnings("unchecked")
	public JobInfo findJobInfo(final String uid) {
		if (StringUtils.isNotBlank(uid)) {
			final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(JobInfo.class).add(
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

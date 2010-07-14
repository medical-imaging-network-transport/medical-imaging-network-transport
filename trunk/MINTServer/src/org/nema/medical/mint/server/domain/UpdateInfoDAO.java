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
public class UpdateInfoDAO extends HibernateDaoSupport {
	@SuppressWarnings("unchecked")
	public List<UpdateInfo> findUpdateInfo(final String studyID) {
		if (StringUtils.isNotBlank(studyID)) {
			final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(UpdateInfo.class).add(
					Restrictions.eq("studyID", studyID));
			final List<UpdateInfo> list = getHibernateTemplate().findByCriteria(detachedCriteria);

			return list;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<UpdateInfo> getMostRecentUpdates(final int max) {
		final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(UpdateInfo.class);
		
		final List<UpdateInfo> list = getHibernateTemplate().findByCriteria(detachedCriteria, 0, max);
		return list;
	}
	
	public UpdateInfo saveOrUpdateUpdateInfo(final UpdateInfo updateInfo) {
		if (updateInfo != null) {
			updateInfo.setUpdateTime(JobInfo.now());
			getHibernateTemplate().saveOrUpdate(updateInfo);
			getHibernateTemplate().flush();
			getHibernateTemplate().refresh(updateInfo);
		}
		return updateInfo;
	}
}

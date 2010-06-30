package org.nema.medical.mint.common.domain;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ConfigurationDAO extends HibernateDaoSupport {

	public void deleteValue(final String key) {
		final Configuration configuration = getConfiguration(key);
		if (StringUtils.isNotBlank(configuration.getId())) {
			getHibernateTemplate().delete(configuration);
		}
	}

	@SuppressWarnings("unchecked")
	protected Configuration getConfiguration(final String param) {
		if (StringUtils.isNotBlank(param)) {
			final DetachedCriteria criteria = DetachedCriteria.forClass(Configuration.class).add(
					Restrictions.eq("param", param));
			final List<Configuration> list = getHibernateTemplate().findByCriteria(criteria);
			if (!list.isEmpty()) {
				return list.get(0);
			}
		}
		return new Configuration();
	}

	public String getValue(final String key) {
		final Configuration configuration = getConfiguration(key);
		return configuration.getValue();
	}

	@Override
	protected void initDao() throws Exception {
		super.initDao();

		// Shouldn't be many...just cache 'em all!
		getHibernateTemplate().loadAll(Configuration.class);
	}

	public void saveValue(final String key, final String value) {
		if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
			final Configuration configuration = getConfiguration(key);
			if (StringUtils.isEmpty(configuration.getParam())) {
				configuration.setParam(key);
			}
			configuration.setValue(value);
			getHibernateTemplate().saveOrUpdate(configuration);
		}
	}
}

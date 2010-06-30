package org.nema.medical.mint.server;

import java.util.Properties;

import org.nema.medical.mint.common.MINTCommonConfiguration;
import org.nema.medical.mint.common.domain.ConfigurationDAO;
import org.nema.medical.mint.common.domain.StudyDAO;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
public class MintServerConfiguration extends MINTCommonConfiguration {

	protected ConfigurationDAO configurationDao = null;

	protected StudyDAO studyDao = null;

	@Bean(name = "configurationDao", autowire = Autowire.BY_NAME)
	public ConfigurationDAO configurationDao() throws Exception {
		if (configurationDao == null) {
			configurationDao = new ConfigurationDAO();
			configurationDao.setSessionFactory(sessionFactory());
			configurationDao.afterPropertiesSet();
		}
		return configurationDao;
	}

	@Bean(name = "studyDao", autowire = Autowire.BY_NAME)
	public StudyDAO studyDao() throws Exception {
		if (studyDao == null) {
			studyDao = new StudyDAO();
			studyDao.setSessionFactory(sessionFactory());
			studyDao.afterPropertiesSet();
		}
		return studyDao;
	}

	@Bean
	public ViewResolver viewResolver() {
		final InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
		internalResourceViewResolver.setAttributes(new Properties());
		internalResourceViewResolver.setOrder(Ordered.HIGHEST_PRECEDENCE);
		internalResourceViewResolver.setPrefix("/WEB-INF/jsp/");
		internalResourceViewResolver.setSuffix(".jsp");
		internalResourceViewResolver.setViewClass(JstlView.class);
		return internalResourceViewResolver;
	}
}

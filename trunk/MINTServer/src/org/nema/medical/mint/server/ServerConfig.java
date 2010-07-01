package org.nema.medical.mint.server;

import java.io.File;
import java.util.Properties;

import org.nema.medical.mint.common.MINTCommonConfiguration;
import org.nema.medical.mint.common.domain.StudyDAO;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
public class ServerConfig extends MINTCommonConfiguration {

	protected StudyDAO studyDao = null;
	protected File jobTemp = null;
	protected File studyRoot = null;

	@Bean(name = "jobTemp", autowire = Autowire.BY_NAME)
	public File jobTemp() throws Exception {
		jobTemp = new File(mintHome(), "jobs");
		return jobTemp;
	}

	@Bean(name = "studyRoot", autowire = Autowire.BY_NAME)
	public File studyRoot() throws Exception {
		studyRoot = new File(mintHome(), "studies");
		return studyRoot;
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

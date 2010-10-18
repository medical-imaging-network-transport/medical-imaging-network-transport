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
package org.nema.medical.mint.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.naming.Context;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.nema.medical.mint.dcm2mint.ProcessImportDir;
import org.nema.medical.mint.server.domain.ChangeDAO;
import org.nema.medical.mint.server.domain.JobInfoDAO;
import org.nema.medical.mint.server.domain.StudyDAO;
import org.nema.medical.mint.server.receiver.DICOMReceive;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
public class ServerConfig {
	/*
	 * Common stuff
	 */
	private static final Logger LOG = Logger.getLogger(ServerConfig.class);

	protected static HibernateTransactionManager hibernateTransactionManager = null;
	protected static SessionFactory sessionFactory = null;
	protected Context envContext = null;
	protected Properties properties = null;

	/*
	 * MINTServer specific
	 */
	protected String xmlStylesheet = null;
	protected StudyDAO studyDAO = null;
	protected ChangeDAO updateDAO = null;
	protected JobInfoDAO jobInfoDAO = null;
	protected File jobTemp = null;
	protected File studiesRoot = null;
	protected File mintHome = null;
	protected File typesRoot = null;
	protected Boolean enableSCP = null;
	protected Boolean enableProcessor = null;
	protected File storageRootDir = null;
	protected String aeTitle = null;
	protected Integer port = null;
	protected Integer reaperTimeoutMS = null;
	protected URI serverURI = null;
	protected Boolean useXMLNotGPB = null;
	protected Boolean deletePhysicalFiles = null;
	protected Boolean forceCreate = null;
	protected Integer binaryInlineThreshold = null;
    protected DICOMReceive dcmRcv = null;
    protected ScheduledExecutorService dcm2MintExecutor = null;
    protected Integer binaryItemStreamBufferSize = null;
    protected Integer binaryItemResponseBufferSize = null;
    protected Integer fileResponseBufferSize = null;
    protected Integer fileStreamBufferSize = null;

    @PostConstruct
    public void postConstruct() {
        //@PostConstruct method cannot throw checked exception
        try {
            setUpCStoreSCP();
            setUpDICOM2MINT();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void setUpCStoreSCP() throws IOException {
        if (!enableSCP()) {
            return;
        }
        dcmRcv = new DICOMReceive();
        dcmRcv.setStorageRootDir(storageRootDir());
        dcmRcv.setAETitle(aeTitle());
        dcmRcv.setPort(port());
        dcmRcv.setReaperTimeout(reaperTimeoutMS());

        try {
            dcmRcv.start();
        } catch (final IOException e) {
            //Need to catch and re-throw checked exceptions for @PostConstruct
            throw new RuntimeException(e);
        }
    }

    private void setUpDICOM2MINT() throws IOException, URISyntaxException {
        if (!enableProcessor()) {
            return;
        }
        dcm2MintExecutor = Executors.newScheduledThreadPool(2);
        //Create an instance of the Directory Processing Class
        final ProcessImportDir importProcessor = new ProcessImportDir(
                storageRootDir(), serverURI(), useXMLNotGPB(), deletePhysicalFiles(),
                forceCreate(), binaryInlineThreshold());
        final Runnable checkResponsesTask = new Runnable() {
            public void run() {
                try {
                    importProcessor.handleResponses();
                    importProcessor.handleSends();
                } catch(final Throwable e) {
                    System.err.println("An exception occurred while uploading to the server:");
                    e.printStackTrace();
                }
            }
        };
        dcm2MintExecutor.scheduleWithFixedDelay(checkResponsesTask, 1, 1, TimeUnit.SECONDS);

        final Runnable dirTraverseTask = new Runnable() {
            public void run() {
                try {
                    importProcessor.processDir();
                } catch(final Throwable e) {
                    System.err.println("An exception occurred while processing files:");
                    e.printStackTrace();
                }
            }
        };
        dcm2MintExecutor.scheduleWithFixedDelay(dirTraverseTask, 0, 3, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void preDestroy() {
        //TODO The teardowns below could be done in parallel

        //Tear down CStore SCP
        if (dcmRcv != null) {
            dcmRcv.stop();
            dcmRcv = null;
        }

        //Tear down DICOM-to-MINT
        if (dcm2MintExecutor != null) {
            dcm2MintExecutor.shutdown();
            try {
                if (!dcm2MintExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
                    dcm2MintExecutor.shutdownNow();
                    if (!dcm2MintExecutor.awaitTermination(1, TimeUnit.SECONDS)) {
                        LOG.warn("DICOM-to-MINT Executor service did not terminate.");
                    }
                }
            } catch (final InterruptedException e) {
                dcm2MintExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
            dcm2MintExecutor = null;
        }
    }

    @Bean(name = "mintHome", autowire = Autowire.BY_NAME)
	public File mintHome() {
    	if (mintHome == null) {
			String path = System.getenv("MINT_HOME");
			if (path == null) {
				path = System.getProperty("user.home") + "/MINT_HOME";
				LOG.warn("MINT_HOME enviornment variable not found, using " + path);
			}
			mintHome = new File(path);
			if (!mintHome.exists()) {
				LOG.warn("MINT_HOME does not exist, creating");
				mintHome.mkdirs();
			}
    	}
		return mintHome;
	}

	private Properties envSpecificProperties() throws IOException {
		if (properties == null) {
			final ClassPathResource classPathResource = new ClassPathResource("default.config");
			final Properties defaultProperties = new Properties();
			defaultProperties.load(classPathResource.getInputStream());

			final File mintHome = mintHome();
			final File customConfigFile = new File(mintHome, "mint-server.config");
			if (!customConfigFile.exists()) {
				final Writer customConfigWriter = new BufferedWriter(new FileWriter(customConfigFile));
				try {
					defaultProperties.store(customConfigWriter, null);
				} finally {
					customConfigWriter.close();
				}
			}
			final Properties customProperties = new Properties();
			final Reader customConfigReader = new BufferedReader(new FileReader(customConfigFile));
			try {
				customProperties.load(customConfigReader);
			} finally {
				customConfigReader.close();
			}
			final Set<Object> defaultPropKeys = defaultProperties.keySet();
			final Set<Object> customPropKeys = customProperties.keySet();
			final Set<Object> defaultNotCustomKeys = new HashSet<Object>(defaultPropKeys);
			defaultNotCustomKeys.removeAll(customPropKeys);
			final Set<Object> customNotDefaultKeys = new HashSet<Object>(customPropKeys);
			customNotDefaultKeys.removeAll(defaultPropKeys);
			if (!customNotDefaultKeys.isEmpty()) {
				final StringBuilder diffKeysText = new StringBuilder(
						"The following properties exist in mint-server.config," +
						" but not in default.config:");
				for (final Object key: customNotDefaultKeys) {
					diffKeysText.append(' ');
					diffKeysText.append(key);
				}
				LOG.warn(diffKeysText);
			}
			if (!defaultNotCustomKeys.isEmpty()) {
				final StringBuilder diffKeysText = new StringBuilder(
						"The following properties exist in default.config," +
						" but not in mint-server.config;" +
						" default values (in parentheses) will be used:");
				for (final Object key: defaultNotCustomKeys) {
					final Object defaultPropValue = defaultProperties.get(key);

					diffKeysText.append(' ');
					diffKeysText.append(key);
					diffKeysText.append(" (");
					diffKeysText.append(defaultPropValue);
					diffKeysText.append(')');

					//Also add default property to custom properties
					customProperties.put(key, defaultPropValue);
				}
				LOG.warn(diffKeysText);
			}

			properties = customProperties;
		}
		return properties;
	}

	protected String getConfigString(final String key) throws IOException {
		return envSpecificProperties().getProperty(key);
	}

	protected Integer getConfigInt(final String key) throws IOException {
		final String stringVal = getConfigString(key);
		return StringUtils.isBlank(stringVal) ? null : Integer.valueOf(stringVal);
	}

	protected Boolean getConfigBool(final String key) throws IOException {
		final String stringVal = getConfigString(key);
		return StringUtils.isBlank(stringVal) ? null : Boolean.valueOf(stringVal);
	}

	protected String[] getPackagesToScan() {
		return new String[] { "org.nema.medical.mint.server" };
	}

	@Bean(destroyMethod = "close")
	public SessionFactory sessionFactory() throws Exception {
		if (sessionFactory == null) {
			final AnnotationSessionFactoryBean annotationSessionFactoryBean = new AnnotationSessionFactoryBean();

			if (StringUtils.isBlank(getConfigString("hibernate.connection.datasource"))) {
				// Not using JNDI data source
				final BasicDataSource dataSource = new BasicDataSource();
				dataSource.setDriverClassName(getConfigString("hibernate.connection.driver_class"));
				String url = getConfigString("hibernate.connection.url");
				url = url.replace("$MINT_HOME", mintHome().getPath());
				dataSource.setUrl(url);

				dataSource.setUsername(getConfigString("hibernate.connection.username"));
				dataSource.setPassword(getConfigString("hibernate.connection.password"));
				annotationSessionFactoryBean.setDataSource(dataSource);
			} else {
				// Using a JNDI dataSource
				final JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
				jndiObjectFactoryBean.setExpectedType(DataSource.class);
				jndiObjectFactoryBean.setJndiName(getConfigString("hibernate.connection.datasource"));
				jndiObjectFactoryBean.afterPropertiesSet();
				annotationSessionFactoryBean.setDataSource((DataSource) jndiObjectFactoryBean.getObject());
			}

			final Properties hibernateProperties = new Properties();
			hibernateProperties.put("hibernate.connection.autocommit", Boolean.TRUE);

			final String dialect = getConfigString("hibernate.dialect");
			if (StringUtils.isNotBlank(dialect)) {
				hibernateProperties.put("hibernate.dialect", dialect);
			}

			final String hbm2dll = getConfigString("hibernate.hbm2ddl.auto");
			hibernateProperties.put("hibernate.hbm2ddl.auto", hbm2dll == null ? "verify" : hbm2dll);

			hibernateProperties.put("hibernate.show_sql", "true"
					.equalsIgnoreCase(getConfigString("hibernate.show_sql")));

			hibernateProperties.put("hibernate.c3p0.max_statement", 50);
			hibernateProperties.put("hibernate.c3p0.maxPoolSize", 20);
			hibernateProperties.put("hibernate.c3p0.minPoolSize", 5);
			hibernateProperties.put("hibernate.c3p0.testConnectionOnCheckout", Boolean.FALSE);
			hibernateProperties.put("hibernate.c3p0.timeout", 600);
			annotationSessionFactoryBean.setHibernateProperties(hibernateProperties);

			annotationSessionFactoryBean.setPackagesToScan(getPackagesToScan());
			annotationSessionFactoryBean.afterPropertiesSet();

			sessionFactory = annotationSessionFactoryBean.getObject();
		}
		return sessionFactory;
	}

	@Bean
	public HibernateTransactionManager transactionManager() throws Exception {
		if (hibernateTransactionManager == null) {
			hibernateTransactionManager = new HibernateTransactionManager();
			hibernateTransactionManager.setSessionFactory(sessionFactory());
			hibernateTransactionManager
					.setTransactionSynchronization(AbstractPlatformTransactionManager.SYNCHRONIZATION_ON_ACTUAL_TRANSACTION);
			hibernateTransactionManager.afterPropertiesSet();
		}
		return hibernateTransactionManager;
	}

	@Bean(name = "jobTemp", autowire = Autowire.BY_NAME)
	public File jobTemp() throws Exception {
		jobTemp = new File(mintHome(), "jobtemp");
		return jobTemp;
	}

	@Bean(name = "studiesRoot", autowire = Autowire.BY_NAME)
	public File studiesRoot() throws Exception {
		studiesRoot = new File(mintHome(), "studies");
		return studiesRoot;
	}

	@Bean(name = "typesRoot", autowire = Autowire.BY_NAME)
	public File typesRoot() throws Exception {
		typesRoot = new File(mintHome(), "types");

		if(!typesRoot.exists())
		{
			typesRoot.mkdirs();
		}

		File dicomFile = new File(typesRoot, "DICOM.xml");

		if(!dicomFile.exists())
		{
			InputStream internalDicomFile = getDicomTypeDefStream();

			FileCopyUtils.copy(internalDicomFile, new FileOutputStream(dicomFile));
		}

		return typesRoot;
	}

	private InputStream getDicomTypeDefStream()
	{
		return ServerConfig.class.getClassLoader().getResourceAsStream("DICOM.xml");
	}

	@Bean(name = "studyDAO", autowire = Autowire.BY_NAME)
	public StudyDAO studyDAO() throws Exception {
		if (studyDAO == null) {
			studyDAO = new StudyDAO();
			studyDAO.setSessionFactory(sessionFactory());
			studyDAO.afterPropertiesSet();
		}
		return studyDAO;
	}

	@Bean(name = "updateDAO", autowire = Autowire.BY_NAME)
	public ChangeDAO updateDAO() throws Exception {
		if (updateDAO == null) {
			updateDAO = new ChangeDAO();
			updateDAO.setSessionFactory(sessionFactory());
			updateDAO.afterPropertiesSet();
		}
		return updateDAO;
	}

	@Bean(name = "jobInfoDAO", autowire = Autowire.BY_NAME)
	public JobInfoDAO jobInfoDAO() throws Exception {
		if (jobInfoDAO == null) {
			jobInfoDAO = new JobInfoDAO();
			jobInfoDAO.setSessionFactory(sessionFactory());
			jobInfoDAO.afterPropertiesSet();
		}
		return jobInfoDAO;
	}

	@Bean
	public ViewResolver viewResolver() {
		final InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
		internalResourceViewResolver.setOrder(Ordered.HIGHEST_PRECEDENCE);
		internalResourceViewResolver.setPrefix("/WEB-INF/jsp/");
		internalResourceViewResolver.setSuffix(".jsp");
		internalResourceViewResolver.setViewClass(JstlView.class);
		return internalResourceViewResolver;
	}

    @Bean
    public Boolean enableSCP() throws IOException {
        if (enableSCP == null) {
        	enableSCP = getConfigBool("scp.enable");
        }
        return enableSCP;
    }

	@Bean
	public File storageRootDir() throws IOException {
	    if (storageRootDir == null) {
	        final String prop = getConfigString("scp.storage.path");
	        if (StringUtils.isNotBlank(prop)) {
	            storageRootDir = new File(prop);
	            storageRootDir.mkdirs();
	        }
	    }
	    return storageRootDir;
	}

    @Bean
    public String aeTitle() throws IOException {
        if (aeTitle == null) {
            final String prop = getConfigString("scp.aetitle");
            if (StringUtils.isBlank(prop)) {
                aeTitle = "";
            } else {
                aeTitle = prop;
            }
        }
        return aeTitle;
    }

    @Bean
    public Integer port() throws IOException {
        if (port == null) {
            port = getConfigInt("scp.port");
        }
        return port;
    }

    @Bean
    public Integer reaperTimeoutMS() throws IOException {
        if (reaperTimeoutMS == null) {
        	reaperTimeoutMS = getConfigInt("scp.reaper_timeout_ms");
        }
        return reaperTimeoutMS;
    }

    @Bean
    public Boolean enableProcessor() throws IOException {
        if (enableProcessor == null) {
        	enableProcessor = getConfigBool("processor.enable");
        }
        return enableProcessor;
    }

    @Bean
    public URI serverURI() throws IOException, URISyntaxException {
        //TODO set this automatically from local information
        if (serverURI == null) {
            final String prop = getConfigString("processor.server_uri");
            if (StringUtils.isNotBlank(prop)) {
                serverURI = new URI(prop);
            }
        }
        return serverURI;
    }

    @Bean
    public Boolean useXMLNotGPB() throws IOException {
        if (useXMLNotGPB == null) {
        	useXMLNotGPB = getConfigBool("processor.use_xml_not_gpb");
        }
        return useXMLNotGPB;
    }

    @Bean
    public Boolean deletePhysicalFiles() throws IOException {
        if (deletePhysicalFiles == null) {
        	deletePhysicalFiles = getConfigBool("processor.delete_files");
        }
        return deletePhysicalFiles;
    }

    @Bean
    public Boolean forceCreate() throws IOException {
        if (forceCreate == null) {
        	forceCreate = getConfigBool("processor.force_create");
        }
        return forceCreate;
    }

    @Bean
    public Integer binaryInlineThreshold() throws IOException {
        if (binaryInlineThreshold == null) {
        	binaryInlineThreshold = getConfigInt("processor.binary_inline_threshold");
        }
        return binaryInlineThreshold;
    }

    @Bean
    public Integer binaryItemResponseBufferSize() throws IOException {
        if (binaryItemResponseBufferSize == null) {
        	binaryItemResponseBufferSize = getConfigInt("binaryitem.response.bufsize");
        }
        return binaryItemResponseBufferSize;
    }

    @Bean
    public Integer binaryItemStreamBufferSize() throws IOException {
        if (binaryItemStreamBufferSize == null) {
        	binaryItemStreamBufferSize = getConfigInt("binaryitem.stream.bufsize");
        }
        return binaryItemStreamBufferSize;
    }

    @Bean
    public Integer fileResponseBufferSize() throws IOException {
        if (fileResponseBufferSize == null) {
        	fileResponseBufferSize = getConfigInt("file.response.bufsize");
        }
        return fileResponseBufferSize;
    }

    @Bean
    public Integer fileStreamBufferSize() throws IOException {
        if (fileStreamBufferSize == null) {
        	fileStreamBufferSize = getConfigInt("file.stream.bufsize");
        }
        return fileStreamBufferSize;
    }
    
    @Bean
    public String xmlStylesheet() {
    	return "type=\"text/xsl\" href=\"style.xsl\"";
    }
	

}

package com.example;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class DatasourceConfig {

	/*
	 * set all the parameter into some variable to further use in this class
	 * only
	 * 
	 * the data came from application.properties file using @Value annotation we
	 * just set the value which is already defined in the properties file
	 */

	@Value("${spring.datasource.driver-class-name}")
	private String DATABASE_DRIVER_CLASS_NAME;

	@Value("${spring.datasource.url}")
	private String DATABASE_URL;

	@Value("${spring.datasource.username}")
	private String DATABASE_USER_NAME;

	@Value("${spring.datasource.password}")
	private String DATABASE_PASSWORD;

	@Value("${spring.jpa.properties.hibernate.dialect}")
	private String HIBERNATE_DIALECT;

	@Value("${spring.jpa.show-sql}")
	private String HIBRENATE_SHOW_SQL;

	@Value("${hibernate.hbm2ddl.auto}")
	private String HIBERNATE_HBM2DDL_AUTO;

	@Value("${entitymanager.packagesToScan}")
	private String ENTITY_MANAGER_PACKEGES_TO_SCAN;

	@Bean
	public DataSource dataSource() {

		// configure datasource for accessing database through hibernate
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(DATABASE_DRIVER_CLASS_NAME);
		dataSource.setUrl(DATABASE_URL);
		dataSource.setUsername(DATABASE_USER_NAME);
		dataSource.setPassword(DATABASE_PASSWORD);

		return dataSource;
	}

	@Bean
	public LocalSessionFactoryBean sessionFactoryBean() {

		/**
		 * configure hibernate5 session to use all over the project
		 */
		LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
		sessionFactoryBean.setDataSource(dataSource());
		sessionFactoryBean.setPackagesToScan(ENTITY_MANAGER_PACKEGES_TO_SCAN);

		// setting Util.Properties to add extra property to hibernate5
		Properties hibernateProperties = new Properties();
		hibernateProperties.put("spring.jpa.properties.hibernate.dialect", HIBERNATE_DIALECT);
		hibernateProperties.put("spring.jpa.show-sql", HIBRENATE_SHOW_SQL);
		hibernateProperties.put("hibernate.hbm2ddl.auto", HIBERNATE_HBM2DDL_AUTO);

		sessionFactoryBean.setHibernateProperties(hibernateProperties);

		return sessionFactoryBean;
	}

	@Bean
	public HibernateTransactionManager transactionManager() {

		// set Hibernate Transaction manager
		HibernateTransactionManager transactionManager = new HibernateTransactionManager();
		transactionManager.setSessionFactory(sessionFactoryBean().getObject());

		return transactionManager;
	}

}

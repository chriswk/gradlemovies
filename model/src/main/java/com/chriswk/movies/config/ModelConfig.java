package com.chriswk.movies.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan({"com.chriswk.movies.domain"})
public class ModelConfig {

	@Value("${jdbc.driverClassName}")
	private String driverClassName;

	@Value("${jdbc.url}")
	private String url;

	@Value("${jdbc.username}")
	private String userName;

	@Value("${jdbc.password}")
	private String passWord;

	@Value("${jpa.generateDdl}")
	boolean jpaGenerateDdl;

	@Value("${hibernate.dialect}")
	String hibernateDialect;

	@Value("${hibernate.show_sql}")
	boolean hibernateShowSql;

	@Value("${hibernate.hbm2ddl.auto}")
    String hibernateHbm2ddlAuto;


	
	public ModelConfig() {
		super();
	}

	// beans
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
		final LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
		factoryBean.setDataSource(dataSource())
		factoryBean.setPackagesToScan(new String[]{"com.chriswk.movies.domain"});

		final JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter() {
			{
				setDataBase(Database.H2);
				setDatabasePlatform(hibernateDialect);
				setShowSql(hibernateShowSql);
				setGenerateDdl(jpaGenerateDdl);
			}
		};
		factoryBean.setJpaVendorAdapter(vendorAdapter);
		factoryBean.setJpaProperties(addtionalProperties());

		return factoryBean;
	}

	@Bean
	public DataSource dataSource() {
		final DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setUrl(url);
		dataSource.setUsername(userName);
		dataSource.setPassword(passWord);
		return dataSource;
	}

	@Bean
	public JpaTransactionManager transactionManager() {
		final JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactoryBean().getObject());
		return transactionManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	final Properties addtionalProperties() {
		return new Properties() {
			{
				setProperty("hibernate.hbm2ddl.auto", hibernateHbm2ddlAuto);
			}
		}
	}
}
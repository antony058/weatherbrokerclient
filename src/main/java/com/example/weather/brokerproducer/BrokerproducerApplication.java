package com.example.weather.brokerproducer;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jms.AtomikosConnectionFactoryBean;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.XAConnectionFactory;
import javax.transaction.SystemException;

@SpringBootApplication
public class BrokerproducerApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(BrokerproducerApplication.class, args);
	}
	@Bean
	public JmsTemplate jmsTemplate() {
		JmsTemplate jmsTemplate = new JmsTemplate();
		jmsTemplate.setConnectionFactory(connectionFactoryBean());
		jmsTemplate.setPubSubDomain(true);
		jmsTemplate.setDefaultDestination((Destination) destinationJndi().getObject());
		jmsTemplate.setSessionTransacted(true);

		return jmsTemplate;
	}

	@Bean
	public JndiObjectFactoryBean destinationJndi() {
		JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
		jndiObjectFactoryBean.setJndiName("java:jboss/exported/jms/topic/testTopic");
		jndiObjectFactoryBean.setResourceRef(true);

		return jndiObjectFactoryBean;
	}

	@Bean
	@Primary
	public JndiObjectFactoryBean connectionFactoryJndi() {
		JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
		jndiObjectFactoryBean.setJndiName("java:/JmsXA");
		jndiObjectFactoryBean.setResourceRef(true);

		return jndiObjectFactoryBean;
	}

	@Bean
	public DefaultJmsListenerContainerFactory containerFactory() throws SystemException {
		DefaultJmsListenerContainerFactory containerFactory = new DefaultJmsListenerContainerFactory();
		containerFactory.setConnectionFactory(connectionFactoryBean());
		containerFactory.setConcurrency("1-1");
		containerFactory.setSessionTransacted(true);
		containerFactory.setPubSubDomain(true);
		containerFactory.setTransactionManager(transactionManager());

		return containerFactory;
	}

	@Bean
	public AtomikosConnectionFactoryBean connectionFactoryBean() {
		AtomikosConnectionFactoryBean connectionFactoryBean = new AtomikosConnectionFactoryBean();
		connectionFactoryBean.setUniqueResourceName("atomikosConnectionFactoryCustomBean");
		connectionFactoryBean.setXaConnectionFactory((XAConnectionFactory) connectionFactoryJndi().getObject());
		connectionFactoryBean.setMaxPoolSize(10);

		return connectionFactoryBean;
	}

	@Bean(initMethod = "init", destroyMethod = "close")
	public UserTransactionManager jtaTransactionManager() {
		UserTransactionManager userTransactionManager = new UserTransactionManager();
		userTransactionManager.setForceShutdown(false);

		return userTransactionManager;
	}

	@Bean
	public UserTransactionImp jtaUserTransaction() throws SystemException {
		UserTransactionImp userTransactionImp = new UserTransactionImp();
		userTransactionImp.setTransactionTimeout(300);

		return userTransactionImp;
	}

	@Bean
	public JtaTransactionManager transactionManager() throws SystemException {
		JtaTransactionManager transactionManager = new JtaTransactionManager();
		transactionManager.setTransactionManager(jtaTransactionManager());
		transactionManager.setUserTransaction(jtaUserTransaction());

		return transactionManager;
	}
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package configuration_1;



import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.dao.annotation.PersistenceExceptionTranslationAdvisor;
import org.springframework.stereotype.Repository;
/**
 *
 * @author dinah
 */
@Configuration
@EnableTransactionManagement
public class TransactionConfig {
    
    @Bean
    public DataSource dataSource2() {
        
        BasicDataSource source = new BasicDataSource();
        source.setDriverClassName("com.mysql.jdbc.Driver");
        source.setUrl("jdbc:mysql://localhost:3306/sakila_2?useSSL=false");
        source.setUsername("root");
        source.setPassword("gw7749");
   
        return source;
    }
    
    @Bean
    public DataSource dataSource() throws ClassNotFoundException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
        ConnectionFactory connectFact = new DriverManagerConnectionFactory(
                "jdbc:mysql://localhost:3306/sakila_2?useSSL=false",
                "root", "gw7749" );
        
        PoolableConnectionFactory poolableConnectFact = new PoolableConnectionFactory
           (connectFact, null);
        
        GenericObjectPool pool = new GenericObjectPool(poolableConnectFact);
        
        poolableConnectFact.setPool(pool);
        
        PoolingDataSource dataSource = new PoolingDataSource(pool);
        
        return dataSource;
    }
    
    @Bean
    public LocalSessionFactoryBean sessionFactory() throws ClassNotFoundException {
        
        String [] packagesToScan = {"model", "model.customer"};
        
        LocalSessionFactoryBean factory = new LocalSessionFactoryBean();
        
        factory.setDataSource(dataSource());
        
        Properties prop = new Properties();
        
        prop.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        prop.setProperty("hibernate.show_sql", "false");
        prop.setProperty("javax.persistence.validation.mode", "NONE");
                
        factory.setHibernateProperties(prop);               
        
        factory.setPackagesToScan(packagesToScan);
        
        return factory;
        
    }
    @Autowired
    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        
        HibernateTransactionManager manager = new HibernateTransactionManager();
        
        manager.setSessionFactory(sessionFactory);
        
        manager.setNestedTransactionAllowed(true);
        
        return manager;
        
    }
    /* Adds an Advisor to the proxied @Transactional component if annotated  @Repository */
    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionPostProcessor () {
        
        return new PersistenceExceptionTranslationPostProcessor();
    }
    
    @Bean
    public HibernateExceptionTranslator exceptionTranslator () {
        
        return new HibernateExceptionTranslator();
    }
    
    @Bean
    public PersistenceExceptionTranslationAdvisor translationAdvisor() {
        
        PersistenceExceptionTranslationAdvisor advisor = 
                new PersistenceExceptionTranslationAdvisor(exceptionTranslator(), Repository.class);
        
        return advisor;
    }
    
}

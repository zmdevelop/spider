package net.kernal.spiderman.kit;

import java.util.ResourceBundle;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

public class TomcatDataSource {

	public static DataSource datasource = new DataSource();
	static{
		ResourceBundle bundle = ResourceBundle.getBundle("jdbc"); 
    	PoolProperties p = new PoolProperties();
    	p.setUrl(bundle.getString("jdbc.url"));
    	p.setDriverClassName(bundle.getString("jdbc.driverClassName"));
    	p.setUsername(bundle.getString("jdbc.username"));
    	p.setPassword(bundle.getString("jdbc.password"));
    	p.setJmxEnabled(true);
    	p.setTestWhileIdle(false);
    	p.setTestOnBorrow(true);
    	p.setValidationQuery("SELECT 1");
    	p.setTestOnReturn(false);
    	p.setValidationInterval(30000);
    	p.setTimeBetweenEvictionRunsMillis(30000);
    	p.setMaxActive(100);
    	p.setInitialSize(10);
    	p.setMaxWait(10000);
    	p.setRemoveAbandonedTimeout(60);
    	p.setMinEvictableIdleTimeMillis(30000);
    	p.setMinIdle(10);
    	p.setLogAbandoned(true);
    	p.setRemoveAbandoned(false);
    	p.setJdbcInterceptors( "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
    	+ "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
    	datasource.setPoolProperties(p);    		
	}
	public TomcatDataSource() {

	}
	public static  javax.sql.DataSource getDataSource()
	{
		return datasource;
	}

}

package org.selknam.textualservice.arq.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.selknam.textualservice.arq.exception.ConnectionException;
import org.selknam.textualservice.utils.Constants;
import org.selknam.textualservice.utils.PropLoader;

public class BaseDAO {
	private static Logger logger = Logger.getLogger(BaseDAO.class);
	
	private Connection connection;
	
	public BaseDAO() throws ConnectionException {
		createConnection();
	}
	
    protected void createConnection() throws ConnectionException {
        try {
        	String host = PropLoader.get(Constants.DB_HOST);
        	String port = PropLoader.get(Constants.DB_PORT);
        	String name = PropLoader.get(Constants.DB_NAME);
        	String user = PropLoader.get(Constants.DB_USER);
        	String pass = PropLoader.get(Constants.DB_PASS);
        	String url = "jdbc:mariadb://"+host+":"+port+"/"+name+"?user="+user+"&password="+pass;
			Class.forName("org.mariadb.jdbc.Driver");
			connection = DriverManager.getConnection(url);
		} catch (SQLException e) {
			logger.error("Database error ", e);
			throw new ConnectionException(e.getMessage());
		} catch (ClassNotFoundException e) {
			logger.error("Database error ", e);
			throw new ConnectionException(e.getMessage());
		}
    }
    
    public void close() throws ConnectionException {
    	try {
    		if (connection!=null) {
    			connection.close();
    		}
		} catch (SQLException e) {
			logger.error("Database error ", e);
			throw new ConnectionException(e.getMessage());
		}
    }
    
    protected Connection getConnection() {
    	return this.connection;
    }
    
    protected PreparedStatement createQuery(String sqlQuery) throws ConnectionException {
    	try {
			return this.connection.prepareStatement(sqlQuery);
		} catch (SQLException e) {
			logger.error("Database error ", e);
			throw new ConnectionException(e.getMessage());
		}
    }
    
    protected PreparedStatement createQuery(String sqlQuery, int generatedKeys) throws ConnectionException {
    	try {
			return this.connection.prepareStatement(sqlQuery, generatedKeys);
		} catch (SQLException e) {
			logger.error("Database error ", e);
			throw new ConnectionException(e.getMessage());
		}
    }
    
}

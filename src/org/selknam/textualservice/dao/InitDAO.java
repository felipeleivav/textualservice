package org.selknam.textualservice.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.selknam.textualservice.arq.dao.BaseDAO;
import org.selknam.textualservice.arq.exception.ConnectionException;

public class InitDAO extends BaseDAO {
	private static Logger logger = Logger.getLogger(InitDAO.class);
	
	public InitDAO () throws ConnectionException {
		super();
	}
	
	public boolean isDbInstalled() throws ConnectionException {
		int rightTables = 0;
		try {
			PreparedStatement stmt = createQuery("SHOW TABLES");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String table = rs.getString(1);
				if (table.equalsIgnoreCase("note") ||
					table.equalsIgnoreCase("user")) {
					rightTables++;
				}
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			logger.error("Database error ", e);
			throw new ConnectionException(e.getMessage());
		}
		return rightTables==2;
	}
	
	public void installDb() throws ConnectionException {
		try {
			PreparedStatement stmt = createQuery("CREATE TABLE IF NOT EXISTS note (id int(11) NOT NULL AUTO_INCREMENT,user_id int(11) NOT NULL,title varchar(255) NOT NULL,content text,last_update datetime NOT NULL,PRIMARY KEY (id))");
			stmt.executeUpdate();
			stmt.close();
			PreparedStatement stmt2 = createQuery("CREATE TABLE IF NOT EXISTS user (id int(11) NOT NULL AUTO_INCREMENT,username varchar(50) NOT NULL,password varchar(50) NOT NULL,PRIMARY KEY (id))");
			stmt2.executeUpdate();
			stmt2.close();
		} catch (SQLException e) {
			logger.error("Database error ", e);
			throw new ConnectionException(e.getMessage());
		}
	}
	
}

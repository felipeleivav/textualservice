package org.selknam.textualservice.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.selknam.textualservice.arq.dao.BaseDAO;
import org.selknam.textualservice.arq.exception.ConnectionException;

public class TestDAO extends BaseDAO {
	private static Logger logger = Logger.getLogger(TestDAO.class);

	public TestDAO() throws ConnectionException {
		super();
	}
	
	public boolean testConnection() throws ConnectionException {
		boolean ok = false;
		try {
			PreparedStatement stmt = createQuery("SELECT 1");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				rs.getInt(1);
			}
			rs.close();
			stmt.close();
			ok = true;
		} catch (SQLException e) {
			logger.error("Database error ", e);
			throw new ConnectionException(e.getMessage());
		}
		return ok;
	}
	
}

package org.selknam.textualservice.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.selknam.textualservice.arq.dao.BaseDAO;
import org.selknam.textualservice.arq.exception.ConnectionException;
import org.selknam.textualservice.dbo.UserDBO;

public class UserDAO extends BaseDAO {
	private static Logger logger = Logger.getLogger(UserDAO.class);
	
	public UserDAO() throws ConnectionException {
		super();
	}
	
	public boolean validateUser(String username, String password) throws ConnectionException {
		boolean isValid = false;
		try {
			PreparedStatement stmt = createQuery("SELECT id FROM user WHERE username=? AND password=?");
			stmt.setString(1, username);
			stmt.setString(2, password);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				isValid = true;
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			logger.error("Database error ", e);
			throw new ConnectionException(e.getMessage());
		}
		return isValid;
	}
	
	public int getUserId(String username) throws ConnectionException {
		int userId = 0;
		try {
			PreparedStatement stmt = createQuery("SELECT id FROM user WHERE username=?");
			stmt.setString(1,  username);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				userId = rs.getInt(1);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			logger.error("Database error ", e);
			throw new ConnectionException(e.getMessage());
		}
		return userId;
	}
	
	public String getUsername(int userId) throws ConnectionException {
		String username = null;
		try {
			PreparedStatement stmt = createQuery("SELECT username FROM user WHERE id=?");
			stmt.setInt(1, userId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				username = rs.getString(1);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			logger.error("Database error ", e);
			throw new ConnectionException(e.getMessage());
		}
		return username;
	}
	
	public int userExists(String username) throws ConnectionException {
		int returnedUserId = 0;
		try {
			PreparedStatement stmt = createQuery("SELECT id FROM user WHERE username=?");
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				returnedUserId = rs.getInt(1);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			logger.error("Database error ", e);
			throw new ConnectionException(e.getMessage());
		}
		return returnedUserId;
	}
	
	public boolean createUser(UserDBO user) throws ConnectionException {
		boolean res = false;
		try {
			PreparedStatement stmt = createQuery("INSERT INTO user (username,password) VALUES(?,?)");
			stmt.setString(1, user.getUsername());
			stmt.setString(2, user.getPassword());
			res = stmt.executeUpdate()>0;
			stmt.close();
		} catch (SQLException e) {
			logger.error("Database error ", e);
			throw new ConnectionException(e.getMessage());
		}
		return res;
	}
	
	public boolean updatePassword(UserDBO user) throws ConnectionException {
		boolean res = false;
		try {
			PreparedStatement stmt = createQuery("UPDATE user SET password=? WHERE id=?");
			stmt.setString(1, user.getPassword());
			stmt.setInt(2, user.getId());
			res = stmt.executeUpdate()>0;
			stmt.close();
		} catch (SQLException e) {
			logger.error("Database error ", e);
			throw new ConnectionException(e.getMessage());
		}
		return res;
	}
	
	public boolean deleteUser(int userId) throws ConnectionException {
		boolean res = false;
		try {
			NoteDAO noteDao = new NoteDAO();
			if (noteDao.deleteAllUserNotes(userId)) {
				PreparedStatement stmt = createQuery("DELETE FROM user WHERE id=?");
				stmt.setInt(1, userId);
				res = stmt.executeUpdate()>0;
				stmt.close();
			}
			noteDao.close();
		} catch (SQLException e) {
			logger.error("Database error ", e);
			throw new ConnectionException(e.getMessage());
		}
		return res;
	}
	
}

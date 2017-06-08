package org.selknam.textualservice.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.selknam.textualservice.arq.dao.BaseDAO;
import org.selknam.textualservice.arq.exception.ConnectionException;
import org.selknam.textualservice.dbo.NoteDBO;

public class NoteDAO extends BaseDAO {
	private static Logger logger = Logger.getLogger(NoteDAO.class);
	
	public NoteDAO() throws ConnectionException {
		super();
	}
	
	public List<NoteDBO> getAllNotes(int userId) throws ConnectionException {
		List<NoteDBO> notes = new ArrayList<NoteDBO>();
		try {
			PreparedStatement stmt = createQuery("SELECT id,user_id,title,content,last_update FROM note WHERE user_id=?");
			stmt.setInt(1, userId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				NoteDBO note = new NoteDBO();
				note.setId(rs.getInt(1));
				note.setUserId(rs.getInt(2));
				note.setTitle(rs.getString(3));
				note.setContent(rs.getString(4));
				note.setLastUpdate(rs.getTimestamp(5));
				notes.add(note);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			logger.error("Database error ", e);
			throw new ConnectionException(e.getMessage());
		}
		return notes;
	}
	
	public List<NoteDBO> getUpdateDates(int userId) throws ConnectionException {
		List<NoteDBO> notes = new ArrayList<NoteDBO>();
		try {
			PreparedStatement stmt = createQuery("SELECT id,last_update FROM note WHERE user_id=?");
			stmt.setInt(1, userId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				NoteDBO note = new NoteDBO();
				note.setId(rs.getInt(1));
				note.setLastUpdate(rs.getTimestamp(2));
				notes.add(note);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			logger.error("Database error ", e);
			throw new ConnectionException(e.getMessage());
		}
		return notes;
	}
	
	public NoteDBO getNote(int userId, int noteId) throws ConnectionException {
		NoteDBO note = null;
		try {
			PreparedStatement stmt = createQuery("SELECT id,user_id,title,content,last_update FROM note WHERE user_id=? AND id=?");
			stmt.setInt(1, userId);
			stmt.setInt(2, noteId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				note = new NoteDBO();
				note.setId(rs.getInt(1));
				note.setUserId(rs.getInt(2));
				note.setTitle(rs.getString(3));
				note.setContent(rs.getString(4));
				note.setLastUpdate(rs.getTimestamp(5));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			logger.error("Database error ", e);
			throw new ConnectionException(e.getMessage());
		}
		return note;
	}
	
	public NoteDBO createNote(NoteDBO note) throws ConnectionException {
		NoteDBO createdNote = new NoteDBO();
		Date actualDate = new Date();
		try {
			PreparedStatement stmt = createQuery("INSERT INTO note (user_id,title,content,last_update) VALUES(?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, note.getUserId());
			stmt.setString(2, note.getTitle());
			stmt.setString(3, note.getContent());
			stmt.setTimestamp(4, new Timestamp(actualDate.getTime()));
			if (stmt.executeUpdate()>0) {
				int id = 0;
				ResultSet rs = stmt.getGeneratedKeys();
				while (rs.next()) {
					id = rs.getInt(1);
				}
				rs.close();
				createdNote.setId(id);
				createdNote.setLastUpdate(actualDate);
			}
			stmt.close();
		} catch (SQLException e) {
			logger.error("Database error ", e);
			throw new ConnectionException(e.getMessage());
		}
		return createdNote;
	}
	
	public NoteDBO updateNote(NoteDBO note) throws ConnectionException {
		NoteDBO updatedNote = new NoteDBO();
		Date actualDate = new Date();
		try {
			PreparedStatement stmt = createQuery("UPDATE note SET title=?,content=?,last_update=? WHERE user_id=? AND id=?");
			stmt.setString(1, note.getTitle());
			stmt.setString(2, note.getContent());
			stmt.setTimestamp(3, new Timestamp(actualDate.getTime()));
			stmt.setInt(4, note.getUserId());
			stmt.setInt(5, note.getId());
			if (stmt.executeUpdate()>0) {
				updatedNote.setId(note.getId());
				updatedNote.setLastUpdate(actualDate);
			}
			stmt.close();
		} catch (SQLException e) {
			logger.error("Database error ", e);
			throw new ConnectionException(e.getMessage());
		}
		return updatedNote;
	}
	
	public boolean deleteNote(int userId, int noteId) throws ConnectionException {
		boolean res = false;
		try {
			PreparedStatement stmt = createQuery("DELETE FROM note WHERE user_id=? AND id=?");
			stmt.setInt(1, userId);
			stmt.setInt(2, noteId);
			res = stmt.executeUpdate()>0;
			stmt.close();
		} catch (SQLException e) {
			logger.error("Database error ", e);
			throw new ConnectionException(e.getMessage());
		}
		return res;
	}
	
	public boolean deleteAllUserNotes(int userId) throws ConnectionException {
		boolean res = false;
		try {
			PreparedStatement stmt = createQuery("DELETE FROM note WHERE user_id=?");
			stmt.setInt(1, userId);
			res = stmt.executeUpdate()>0;
			stmt.close();
		} catch (SQLException e) {
			logger.error("Database error ", e);
			throw new ConnectionException(e.getMessage());
		}
		return res;
	}
	
}

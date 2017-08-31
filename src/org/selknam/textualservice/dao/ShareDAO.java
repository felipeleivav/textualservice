package org.selknam.textualservice.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.selknam.textualservice.arq.dao.BaseDAO;
import org.selknam.textualservice.arq.exception.ConnectionException;
import org.selknam.textualservice.dbo.ShareDBO;
import org.selknam.textualservice.utils.Constants;

public class ShareDAO extends BaseDAO {
	private static Logger logger = Logger.getLogger(BaseDAO.class);
	
	public ShareDAO() throws ConnectionException {
		super();
	}
	
	public List<ShareDBO> getValidRequests(int userId) throws ConnectionException {
		List<ShareDBO> shares = new ArrayList<ShareDBO>();
		try {
			PreparedStatement stmt = createQuery("SELECT id,user_requester,user_invited,note_id,request_status FROM share_request WHERE (user_requester=? OR user_invited=?) AND request_status IN (?,?)");
			stmt.setInt(1, userId);
			stmt.setInt(2, userId);
			stmt.setInt(3, Constants.ACCEPTED);
			stmt.setInt(4, Constants.PENDING);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ShareDBO share = new ShareDBO();
				share.setId(rs.getInt(1));
				share.setUserRequester(rs.getInt(2));
				share.setUserInvited(rs.getInt(3));
				share.setNoteId(rs.getInt(4));
				share.setRequestStatus(rs.getInt(5));
				shares.add(share);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			logger.error("Database error ", e);
			throw new ConnectionException(e.getMessage());
		}
		return shares;
	}
	
	public ShareDBO createRequest(ShareDBO share) throws ConnectionException {
		ShareDBO newShare = new ShareDBO();
		try {
			PreparedStatement stmt = createQuery("INSERT INTO share_request (user_requester,user_invited,note_id,request_status) VALUES(?,?,?,?)");
			stmt.setInt(1, share.getUserRequester());
			stmt.setInt(2, share.getUserInvited());
			stmt.setInt(3, share.getNoteId());
			stmt.setInt(4, Constants.PENDING);
			if (stmt.executeUpdate()>0) {
				int id = 0;
				ResultSet rs = stmt.getGeneratedKeys();
				while (rs.next()) {
					id = rs.getInt(1);
				}
				rs.close();
				newShare.setId(id);
			}
			stmt.close();
		} catch (SQLException e) {
			logger.error("Database error ", e);
			throw new ConnectionException(e.getMessage());
		}
		return newShare;
	}
	
	public ShareDBO updateRequest(ShareDBO share) throws ConnectionException {
		try {
			PreparedStatement stmt = createQuery("UPDATE share_request SET request_status=? WHERE id=?");
			stmt.setInt(1, share.getRequestStatus());
			stmt.setInt(2, share.getId());
			if (stmt.executeUpdate()<=0) {
				return null;
			}
			stmt.close();
		} catch (SQLException e) {
			logger.error("Database error ", e);
			throw new ConnectionException(e.getMessage());
		}
		return share;
	}
	
	public boolean userIsRequester(int userId, int requestId) throws ConnectionException {
		boolean ret = false;
		try {
			PreparedStatement stmt = createQuery("SELECT id FROM share_request WHERE id=? AND user_requester=?");
			stmt.setInt(1, requestId);
			stmt.setInt(2, userId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ret = true;
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			logger.error("Database error ", e);
			throw new ConnectionException(e.getMessage());
		}
		return ret;
	}
	
	public boolean userIsInvited(int userId, int requestId) throws ConnectionException {
		boolean ret = false;
		try {
			PreparedStatement stmt = createQuery("SELECT id FROM share_request WHERE id=? AND user_invited=?");
			stmt.setInt(1, requestId);
			stmt.setInt(2, userId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ret = true;
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			logger.error("Database error ", e);
			throw new ConnectionException(e.getMessage());
		}
		return ret;
	}
	
	public boolean userHasInvitation(int userId, int noteId) throws ConnectionException {
		boolean ret = false;
		try {
			PreparedStatement stmt = createQuery("SELECT note.id FROM note,share_request WHERE note.id=share_request.note_id AND note.id=? AND share_request.user_invited=? AND share_request.request_status=?");
			stmt.setInt(1, noteId);
			stmt.setInt(2, userId);
			stmt.setInt(3, Constants.ACCEPTED);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ret = true;
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			logger.error("Database error ", e);
			throw new ConnectionException(e.getMessage());
		}
		return ret;
	}
	
	public boolean isRequestPendingOrAccepted(int requestId) throws ConnectionException {
		boolean ret = false;
		try {
			PreparedStatement stmt = createQuery("SELECT id FROM share_request WHERE id=? AND (request_status=? OR request_status=?)");
			stmt.setInt(1, requestId);
			stmt.setInt(2, Constants.ACCEPTED);
			stmt.setInt(3, Constants.PENDING);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ret = true;
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			logger.error("Database error ", e);
			throw new ConnectionException(e.getMessage());
		}
		return ret;
	}
	
	public boolean noDuplicates(int noteId, int userInvited) throws ConnectionException {
		boolean ret = true;
		try {
			PreparedStatement stmt = createQuery("SELECT id FROM share_request WHERE note_id=? AND user_invited=? AND (request_status=0 OR request_status=1)");
			stmt.setInt(1, noteId);
			stmt.setInt(2, userInvited);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ret = false;
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			logger.error("Database error ", e);
			throw new ConnectionException(e.getMessage());
		}
		return ret;
	}
	
}

package org.selknam.textualservice.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.selknam.textualservice.arq.exception.ConnectionException;
import org.selknam.textualservice.dao.NoteDAO;
import org.selknam.textualservice.dao.ShareDAO;
import org.selknam.textualservice.dao.UserDAO;
import org.selknam.textualservice.dbo.ShareDBO;
import org.selknam.textualservice.utils.Constants;

@Path("/share")
public class ShareRest {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ShareDBO> getRequests(@Context HttpServletRequest request) throws ConnectionException {
    	int userId = Integer.parseInt(request.getAttribute("userid").toString());
    	ShareDAO shareDao = new ShareDAO();
    	List<ShareDBO> shareRequests = shareDao.getValidRequests(userId);
    	shareDao.close();
		UserDAO userDao = new UserDAO();
    	for (ShareDBO share : shareRequests) {
    		if (userId==share.getUserInvited()) {
    			share.setUsernameInvited(userDao.getUsername(share.getUserRequester()));
    		} else if (userId==share.getUserRequester()) {
    			share.setUsernameInvited(userDao.getUsername(share.getUserInvited()));
    		}
    	}
		userDao.close();
    	return shareRequests;
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ShareDBO createRequest(@Context HttpServletRequest request, ShareDBO share) throws ConnectionException {
    	int userId = Integer.parseInt(request.getAttribute("userid").toString());
    	ShareDAO shareDao = new ShareDAO();
    	UserDAO userDao = new UserDAO();
    	NoteDAO noteDao = new NoteDAO();
    	share.setUserRequester(userId);
    	ShareDBO newShare = new ShareDBO();
    	int invitedUserId = userDao.userExists(share.getUsernameInvited());
    	if (invitedUserId>0 &&
    		shareDao.noDuplicates(share.getNoteId(), invitedUserId) &&
    		noteDao.userOwnsNote(userId, share.getNoteId()) &&
    		userId!=invitedUserId) {
    		share.setUserInvited(invitedUserId);
    		newShare = shareDao.createRequest(share);
    		newShare.setUserInvited(invitedUserId);
    	}
    	userDao.close();
    	shareDao.close();
    	return newShare;
	}
	
	@POST
	@Path("/{requestId}")
	@Produces(MediaType.APPLICATION_JSON)
	public ShareDBO acceptRequest(@Context HttpServletRequest request, @PathParam("requestId") int requestId) throws ConnectionException {
    	int userId = Integer.parseInt(request.getAttribute("userid").toString());
    	ShareDAO shareDao = new ShareDAO();
    	ShareDBO acceptedShare = new ShareDBO();
    	if (shareDao.userIsInvited(userId, requestId) && shareDao.isRequestPendingOrAccepted(requestId)) {
    		acceptedShare.setId(requestId);
    		acceptedShare.setRequestStatus(Constants.ACCEPTED);
    		acceptedShare = shareDao.updateRequest(acceptedShare);
    		if (acceptedShare==null) {
    			acceptedShare = new ShareDBO();
    		}
    	}
    	shareDao.close();
    	return acceptedShare;
	}
	
	@DELETE
	@Path("/{requestId}")
	@Produces(MediaType.APPLICATION_JSON)
	public ShareDBO cancelRequest(@Context HttpServletRequest request, @PathParam("requestId") int requestId) throws ConnectionException {
    	int userId = Integer.parseInt(request.getAttribute("userid").toString());
    	ShareDAO shareDao = new ShareDAO();
    	ShareDBO cancelledShare = new ShareDBO();
    	int status = 0;
    	if (shareDao.userIsInvited(userId, requestId)) {
    		status = Constants.DECLINED;
    	} else if (shareDao.userIsRequester(userId, requestId)) {
    		status = Constants.CANCELLED;
    	}
    	if ((status==Constants.DECLINED || status==Constants.CANCELLED) && shareDao.isRequestPendingOrAccepted(requestId)) {
    		cancelledShare.setId(requestId);
    		cancelledShare.setRequestStatus(status);
    		cancelledShare = shareDao.updateRequest(cancelledShare);
    		if (cancelledShare==null) {
    			cancelledShare = new ShareDBO();
    		}
    	}
    	return cancelledShare;
	}
	
}

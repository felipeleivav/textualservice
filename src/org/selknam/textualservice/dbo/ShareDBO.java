package org.selknam.textualservice.dbo;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ShareDBO {
	
	private int id;
	private int userRequester;
	private int userInvited;
	private String usernameInvited; //TODO: remove this attr (used for creating requests using username and not id, for preventing user enumeration)
	private int noteId;
	private int requestStatus;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getUserRequester() {
		return userRequester;
	}
	
	public void setUserRequester(int userRequester) {
		this.userRequester = userRequester;
	}
	
	public int getUserInvited() {
		return userInvited;
	}
	
	public void setUserInvited(int userInvited) {
		this.userInvited = userInvited;
	}
	
	public String getUsernameInvited() {
		return usernameInvited;
	}
	
	public void setUsernameInvited(String usernameInvited) {
		this.usernameInvited = usernameInvited;
	}
	
	public int getNoteId() {
		return noteId;
	}
	
	public void setNoteId(int noteId) {
		this.noteId = noteId;
	}
	
	public int getRequestStatus() {
		return requestStatus;
	}
	
	public void setRequestStatus(int requestStatus) {
		this.requestStatus = requestStatus;
	}
	
}

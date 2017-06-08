package org.selknam.textualservice.dbo;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserDBO {
	
	private Integer id;
	private String username;
	private String password;
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
}

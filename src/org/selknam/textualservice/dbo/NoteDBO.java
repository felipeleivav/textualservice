package org.selknam.textualservice.dbo;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.selknam.textualservice.utils.DateFormatterAdapter;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class NoteDBO {

	private Integer id;
	private Integer userId;
	private String title;
	private String content;
	private Date lastUpdate;
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getUserId() {
		return userId;
	}
	
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	public Date getLastUpdate() {
		return lastUpdate;
	}
	
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
}

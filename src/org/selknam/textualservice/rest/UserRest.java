package org.selknam.textualservice.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.selknam.textualservice.arq.exception.ConnectionException;
import org.selknam.textualservice.dao.UserDAO;
import org.selknam.textualservice.dbo.UserDBO;

@Path("/user")
public class UserRest {
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public boolean registerUser(UserDBO user) {
		boolean created = false;
		if (user!=null) {
			if (user.getUsername()!=null && !user.getUsername().isEmpty()) {
				if (user.getPassword()!=null && !user.getPassword().isEmpty()) {
					try {
						UserDAO userDao = new UserDAO();
						created = userDao.createUser(user);
					} catch (ConnectionException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return created;
	}
	
	@GET
	public int getUserId(@Context HttpServletRequest request) {
		int userId = Integer.parseInt(request.getAttribute("userid").toString());
		return userId;
	}
	
}

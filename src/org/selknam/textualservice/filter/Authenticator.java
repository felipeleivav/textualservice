package org.selknam.textualservice.filter;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;
import org.glassfish.jersey.internal.util.Base64;
import org.selknam.textualservice.arq.exception.ConnectionException;
import org.selknam.textualservice.dao.UserDAO;

@Provider
@PreMatching
public class Authenticator implements ContainerRequestFilter {
	private static Logger logger = Logger.getLogger(Authenticator.class);

	public static final String AUTHENTICATION_HEADER = "Authorization";
	
	public void filter(ContainerRequestContext containerRequest) throws WebApplicationException {
		if (containerRequest.getMethod().equals("OPTIONS") ||
			containerRequest.getUriInfo().getPath().equalsIgnoreCase("user") ||
			containerRequest.getUriInfo().getPath().equalsIgnoreCase("meta")) {
			return;
		}
		
		String authCredentials = containerRequest.getHeaderString(AUTHENTICATION_HEADER);
		
		if (null==authCredentials) {
			throw new WebApplicationException(Status.UNAUTHORIZED);
		}
		
		authCredentials = Base64.decodeAsString(authCredentials.substring(6));
		String[] user = authCredentials.split(":");
		
		try {
			UserDAO userDao = new UserDAO();
			boolean authStatus = userDao.validateUser(user[0], user[1]);
			
			if (authStatus) {
				int userId = userDao.getUserId(user[0]);
				containerRequest.setProperty("userid", userId);
				userDao.close();
				logger.debug("User success: "+user[0]);
			} else {
				userDao.close();
				logger.debug("User failed: "+user[0]);
				throw new WebApplicationException(Status.UNAUTHORIZED);
			}
		} catch (ConnectionException e) {
			logger.error("Authentication error ", e);
			throw new WebApplicationException(Status.SERVICE_UNAVAILABLE);
		}
	}
	
}

package org.selknam.textualservice.rest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/meta")
public class MetaRest {
	
	@POST
	public String validateServer(String question) {
		if (question.equalsIgnoreCase("are you the note server?")) {
			return "yes i am!";
		} else {
			return "wot u want??";
		}
	}
	
}

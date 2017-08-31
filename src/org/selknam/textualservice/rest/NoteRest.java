package org.selknam.textualservice.rest;

import java.util.ArrayList;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.selknam.textualservice.arq.exception.ConnectionException;
import org.selknam.textualservice.dao.NoteDAO;
import org.selknam.textualservice.dao.ShareDAO;
import org.selknam.textualservice.dbo.NoteDBO;

@Path("/note")
public class NoteRest {
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<NoteDBO> getAllNotes(@Context HttpServletRequest request, @QueryParam("lastUpdate") String lastUpdate) throws ConnectionException {
    	int userId = Integer.parseInt(request.getAttribute("userid").toString());
    	NoteDAO noteDao = new NoteDAO();
    	List<NoteDBO> notes = new ArrayList<NoteDBO>();
    	if (null==lastUpdate) {
    		notes = noteDao.getAllNotes(userId);
    	} else {
        	notes = noteDao.getUpdateDates(userId);
    	}
    	noteDao.close();
        return notes;
    }
    
    @GET
    @Path("{noteId}")
    @Produces(MediaType.APPLICATION_JSON)
    public NoteDBO getSingleNote(@Context HttpServletRequest request, @PathParam("noteId") int noteId) throws ConnectionException {
    	int userId = Integer.parseInt(request.getAttribute("userid").toString());
    	NoteDAO noteDao = new NoteDAO();
    	NoteDBO note = noteDao.getNote(userId, noteId);
    	if (null==note) {
    		ShareDAO shareDao = new ShareDAO();
    		if (shareDao.userHasInvitation(userId, noteId)) {
    			note = noteDao.getNoteWithoutUserValidation(noteId);
    		}
    		shareDao.close();
    	}
    	noteDao.close();
    	return note;
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public NoteDBO saveNote(@Context HttpServletRequest request, NoteDBO note) throws ConnectionException {
    	int userId = Integer.parseInt(request.getAttribute("userid").toString());
    	NoteDAO noteDao = new NoteDAO();
		note.setUserId(userId);
		NoteDBO createdNote = noteDao.createNote(note);
    	noteDao.close();
    	return createdNote;
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public NoteDBO updateNotes(@Context HttpServletRequest request, NoteDBO note) throws ConnectionException {
    	int userId = Integer.parseInt(request.getAttribute("userid").toString());
    	NoteDAO noteDao = new NoteDAO();
		note.setUserId(userId);
		NoteDBO updatedNote = noteDao.updateNote(note);
    	if (null==updatedNote.getId()) {
    		ShareDAO shareDao = new ShareDAO();
    		if (shareDao.userHasInvitation(userId, note.getId())) {
    			updatedNote = noteDao.updateNoteWithoutUserValidation(note);
    		}
    		shareDao.close();
    	}
    	noteDao.close();
    	return updatedNote;
    }
    
    @DELETE
    @Path("/{noteId}")
    public int deleteNote(@Context HttpServletRequest request, @PathParam("noteId") int noteId) throws ConnectionException {
    	int userId = Integer.parseInt(request.getAttribute("userid").toString());
    	NoteDAO noteDao = new NoteDAO();
    	boolean res = noteDao.deleteNote(userId, noteId);
    	noteDao.close();
    	return res?noteId:0;
    }
    
    @GET
    @Path("test")
    public boolean test(@Context HttpServletRequest request) {
    	return true;
    }
    
}

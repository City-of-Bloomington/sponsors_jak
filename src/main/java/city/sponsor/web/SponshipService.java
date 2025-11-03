
package city.sponsor.web;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.util.ArrayList;
import java.util.List;
import city.sponsor.model.*;
import city.sponsor.list.*;
import city.sponsor.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger; 

/**
 *
 *
 *
 */
@WebServlet(urlPatterns = {"/SponshipService"})
public class SponshipService extends TopServlet{

    static final long serialVersionUID = 78L;	
    static Logger logger = LogManager.getLogger(SponshipService.class);
    /**
     * Generates the Group form and processes view, add, update and delete
     * operations.
     * @param req
     * @param res
     */
    
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	doPost(req,res);
    }
    /**
     * @link #doGetost
     */

    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
    
	String id = "";

	//
	String message="", action="";
	res.setContentType("application/json");
	PrintWriter out = res.getWriter();
	String name, value;
	String term ="", type="";
	boolean success = true;
	HttpSession session = null;
	Enumeration<String> values = req.getParameterNames();
	String [] vals = null;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("term")) { // this is what jquery sends
		term = value;
	    }
	    else if (name.equals("type")) {
		type = value;
	    }
	    else if (name.equals("action")){ 
		action = value;  
	    }
	    else{
		// logger.debug(name+" "+value);
	    }
	}
	SponshipAbbrList sponships = null;
	if(term.length() > 1){
	    //
	    sponships = new SponshipAbbrList(debug, term);
	    sponships.find();
	}
	if(sponships != null && sponships.size() > 0){
	    String json = writeJson(sponships, type);
	    out.println(json);
	}
	out.flush();
	out.close();
    }
    String writeJson(SponshipAbbrList sponships, String type){
	String json="";
	for(Sponsorship spon:sponships){
	    if(!json.equals("")) json += ",";
	    json += "{\"id\":\""+spon.getId()+"\",\"value\":\""+spon.getDetails()+"\"}";
	}
	json = "["+json+"]";
	if(debug)
	    logger.debug("json "+json);
	return json;
    }

}























































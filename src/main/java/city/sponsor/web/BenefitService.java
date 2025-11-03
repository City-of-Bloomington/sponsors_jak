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
@WebServlet(urlPatterns = {"/BenefitService"})
public class BenefitService extends TopServlet{

    static final long serialVersionUID = 21L;	
    static Logger logger = LogManager.getLogger(BenefitService.class);
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
		// System.err.println(name+" "+value);
	    }
	}
	BenTypeList types = null;
	if(term.length() > 1){
	    //
	    types = new BenTypeList(debug, term);
	    types.find();
	}
	if(types != null && types.size() > 0){
	    String json = writeJson(types, type);
	    out.println(json);
	}
	out.flush();
	out.close();
    }
    /**
     * *************************
     *
     * json format as an array
     [
     {"value":"W Sibo",
     "id":"sibow",
     "dept":"ITS"
     },
     {"value":"schertza",
     "id":"A Schertz",
     "dept":"ITS"
     }
     ]
     ***************************
     */
    String writeJson(BenTypeList types, String type){
	String json="";
	for(Type tp:types){
	    if(!json.equals("")) json += ",";
	    json += "{\"id\":\""+tp.getId()+"\",\"value\":\""+tp.getName()+"\"}";
	}
	json = "["+json+"]";
	return json;
    }

}























































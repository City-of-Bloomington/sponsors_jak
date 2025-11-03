package city.sponsor.web;
import java.util.*;
import java.sql.*;
import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import city.sponsor.model.*;
import city.sponsor.list.*;
import city.sponsor.util.*;
/**
 *
 *
 *
 */
@WebServlet(urlPatterns = {"/Logout"})
public class Logout extends TopServlet{

    static final long serialVersionUID = 48L;	
    /**
     * Deletes the sesion info.
     * @param req
     * @param res
     */
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException{

	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	Enumeration<String> values = req.getParameterNames();
	String name= "";
	String value = "";
	String switchTo = "";
	String id = "";
	while (values.hasMoreElements()) {
	    name = values.nextElement().trim();
	    value = req.getParameter(name).trim();
	    if (name.equals("id"))
		id = value;	
	}	
	HttpSession session = null;
	session = req.getSession(false);
	if(session != null){
    req.logout();
	}
	res.sendRedirect(endpoint_logout_uri);	 			
    }
}























































package city.sponsor.web;

import java.util.*;
import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import city.sponsor.model.*;
import city.sponsor.list.*;
import city.sponsor.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 * used in testing only not in production
 *
 */
@WebServlet(urlPatterns = {"/AdLogin"})
public class AdLogin extends TopServlet{

    //
    static final long serialVersionUID = 14L;	
    static Logger logger = LogManager.getLogger(AdLogin.class);
    /**
     * Generates the login form.
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	String name, value, id="", username="";
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	Enumeration<String> values = req.getParameterNames();
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    value = (req.getParameter(name)).trim();
	    if (name.equals("id")) {
		id = value;
	    }
	}

	out.println("<HTML><HEAD><TITLE>User Login</TITLE>");
	//
	out.println("</HEAD><body>");
	out.println("<br /><br />");
	out.println("<center><h2>Welcome to Sponsors </h2>");
	out.println("<form method=\"post\">");
	out.println("<table><tr><td>Username</td><td><input name=\"username\" type=\"text\" size=\"10\" /></td><tr>");
	out.println("<tr><td>&nbsp;</td><td><input type=\"submit\" /></td></tr>");
	out.println("</table> ");
	out.println("</form> ");	

	out.println("</body></html>");
	out.close();
    }									
    /**
     * Processes the login operation and validate user's authonticity.
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */			
    public void doPost(HttpServletRequest req, HttpServletResponse res) 
	throws ServletException, IOException{

	String message = "", username="";
	PrintWriter os = null;
	boolean connectDbOk = false, success=true;
	res.setStatus(HttpServletResponse.SC_OK);
	res.setContentType("text/html");
	os = res.getWriter();
	// 
	Enumeration<String> values = req.getParameterNames();
	String name, value, id="";
	os.println("<html>");

	while (values.hasMoreElements()) {
	    name = values.nextElement().trim();
	    value = (req.getParameter(name)).trim();
	    if (name.equals("username")) {
		username = value.toLowerCase();
	    }
	}

	try {
	    //
	    User user = getUser(username);
	    //
	    // add the user to the session
	    //
	    if(user != null){
		HttpSession session = req.getSession(true);
		if(session != null){
		    session.setAttribute("user",user);
		}
		os.println("<head><title></title><META HTTP-EQUIV=\""+
			   "refresh\" CONTENT=\"0; URL=" + url +
			   "Starter?\"></head>");
		os.println("<body>");
	    }
	    else{
		message = "Unauthorized to access this system";
		os.println("<head><title></title><body><h2>Unauthorized access </h2>");
		os.println("<h3>Please try again or check with ITS</h3>");
	    }
	    os.println("</body>");
	    os.println("</html>");
	}
	catch (Exception ex) {
	    System.err.println(""+ex);
	    os.println(ex);
	}
	os.flush();
	//
    }
    //
    User getUser(String username){

	boolean success = true;
	String message = "";
		
	User user = null;
	String fullName="",role="",dept="";
	user = new User(debug, username);
	String back = user.doSelect();
	if(!back.equals("")){
	    message += back;
	    logger.error(back);
	    return null;
	}		
	return user;
    }

}























































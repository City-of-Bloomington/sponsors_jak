package city.sponsor.web;
import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import org.jasig.cas.client.authentication.AttributePrincipal;
import java.net.URL;
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
// change the following to /Login to use with CAS
@WebServlet(urlPatterns = {"/CasLogin"})
public class Login extends TopServlet{

    //
    static final long serialVersionUID = 47L;
    static Logger logger = LogManager.getLogger(Login.class);		
    String cookieName = ""; 
    String cookieValue = "";
    /**
     * Generates the login form for all users.
     *
     * @param req the request 
     * @param res the response
     */
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	String userid="", message="", id="";
	boolean found = false;
	
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	HttpSession session = null;
	AttributePrincipal principal = null;				
	if (req.getUserPrincipal() != null) {
	    principal = (AttributePrincipal) req.getUserPrincipal();
	    userid = principal.getName();
	}
	if(userid == null || userid.isEmpty()){
	    userid = req.getRemoteUser();
	}
	System.err.println(" userid "+userid);
	if(userid != null){
	    session = req.getSession(false);			
	    // setCookie(req, res);
	    User user = getUser(userid);
	    if(user != null && user.userExists() && session != null){
		session.setAttribute("user",user);
		out.println("<head><title></title><META HTTP-EQUIV=\""+
			    "refresh\" CONTENT=\"0; URL=" + url +
			    "Starter?" + 
			    "\"></head>");
		out.println("<body>");
		out.println("</body>");
		out.println("</html>");
		out.flush();
		return;
	    }
	    else{
		message = " Unauthorized access";
	    }
	}
	else{
	    message += " You can not access this system, check with IT or try again later";
	}
	out.println("<head><title></title><body>");
	out.println("<p><font color=red>");
	out.println(message);
	out.println("</p>");
	out.println("</body>");
	out.println("</html>");
	out.flush();	
    }
	
    void setCookie(HttpServletRequest req, 
		   HttpServletResponse res){ 
	Cookie cookie = null;
	boolean found = false;
	Cookie[] cookies = req.getCookies();
	if(cookies != null){
	    for(int i=0;i<cookies.length;i++){
		String name = cookies[i].getName();
		if(name.equals(cookieName)){
		    found = true;
		}
	    }
	}
	//
	// if not found create one with 0 time to live;
	//
	// System.err.println(" cookie found ? "+found);
	if(!found){
	    cookie = new Cookie(cookieName,cookieValue);
	    res.addCookie(cookie);
	}
    }
    /**
     * Procesesses the login and check for authontication.
     * @param req
     * @param res
     */		
    User getUser(String username){

	User user = null;
	user = new User(debug, username);
	String back = user.doSelect();
	if(!back.equals("")){
	    logger.error(back);
	    System.err.println(" user error "+back);
	    return null;
	}
	return user;
    }

}


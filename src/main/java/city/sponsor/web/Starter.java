package city.sponsor.web;
import java.io.*;
import java.util.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import city.sponsor.model.*;
import city.sponsor.util.*;
/**
 * The intro page
 *
 *
 */
@WebServlet(urlPatterns = {"/Starter"})
public class Starter extends TopServlet{

    static final long serialVersionUID = 83L;	
    /**
     * Generates the frame set.
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException{

	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	HttpSession session = null;
	Enumeration<String> values = req.getParameterNames();
	String name, value, id="";
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    value = (req.getParameter(name)).trim();
	    if(name.equals("id")){
		id = value;
	    }
	}
	User user = null;
	session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		String str = url+"/Login?";
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    String str = url+"/Login?";
	    res.sendRedirect(str);
	    return; 
	}		
	//
	// check for the user
	//
	// Inserts
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url,true));
	out.println(Inserts.sideBar(url, user));

	out.println("<div id=\"mainContent\">");
	out.println("<h2>Sponsors Program</h2>");
	out.println("<fieldset><legend>Introduction </legend>");
	out.println("Select one of the following options from the left side menu:<br />");
	out.println("<ul>");
	out.println("<li>To add a new Sponsor, select 'New Sponsor'</li>");
	out.println("<li>To add a new Sponsorship select 'New Sponsorship'</li>");
	out.println("<li>To search for sponsors, select 'Search Sponsors'</li>");
	out.println("<li>To search for sponsorship and donations, select 'Search Sponsorship'</li>");
	out.println("<li>To look for critical followups select 'Need Attenstion'</li>");
	out.println("<li>When done please click on 'Logout' from the menu bar</li>");
	out.println("</ul>");
	out.println("</fieldset>");
	out.println("</div>");
	out.println("</html>");
	out.close();
    }				   
    /**
     * @link #doGet
     */		  
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException{
	doGet(req, res);
    }

}


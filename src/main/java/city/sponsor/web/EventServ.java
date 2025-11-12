package city.sponsor.web;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.sql.*;
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
 *
 */
@WebServlet(urlPatterns = {"/EventServ"})
public class EventServ extends TopServlet{

    String url="",url2="";
    static final long serialVersionUID = 19L;	
    boolean debug = false;
    String emailStr="";
    static Logger logger = LogManager.getLogger(EventServ.class);
    /**
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
    
	String sponship_id="";
	boolean success=true;
	//
	String message="", action="";

	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value;
	User user = null;
		
	HttpSession session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		String str = url+"Login";
		res.sendRedirect(str);
		return; 
	    }
	}
	String id = "";
		
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	Event event = new Event(debug);
		
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("id")) {
		if(value != null){
		    id = value;
		    event.setId(value);
		}
	    }
	    else if (name.equals("name")) {
		if(value != null)
		    event.setName(value);
	    }
	    else if (name.equals("action")){
		if(value != null)
		    action = value;

	    }
	    else if (name.equals("action2")){
		if(!value.equals(""))
		    action = value;  
	    }	
	}
	// 
	if(action.equals("Save") && user.canEdit()){
	    //
	    String back = event.doSave();
	    if(!back.equals("")){
		message += back; 
	    }
	    else{
		id = event.getId();
		message += " Saved successfully";
	    }
	}
	if(action.equals("Update") && user.canEdit()){
	    //
	    String back = event.doUpdate();
	    if(!back.equals("")){
		message += back; 
	    }
	    else{
		message += " Updated successfully";
	    }
	}		
	else if(!id.equals("")){
	    String back = event.doSelect();
	    if(!back.equals("")){
		message += back; 
	    }
	}
	//
	out.println(Inserts.xhtmlHeaderInc);
	out.println(Inserts.banner(url));
	out.println(Inserts.menuBar(url, true));
	out.println(Inserts.sideBar(url, user));
	out.println("<div id=\"mainContent\">");
	out.println("<script type=\"text/javascript\">");
	out.println("//<![CDATA[  ");
	out.println("  function validateForm(){	                     ");
	out.println("  with(document.myForm){                        ");
	//
	// checking dates and numeric values
	// check the numbers
	//
	out.println("  return true;			        ");
	out.println(" }}	         		        ");
	out.println("  function validateDelete2(){	        ");
	out.println("   var x = confirm(\"Are you sure you want to delete this record\");");
	out.println("   if(x){                       ");
	out.println("    document.forms[0].action2.value=\"Delete\"; ");
	out.println("    document.forms[0].submit();   ");
	out.println("    return true;                    ");
	out.println("	}	             		     ");
	out.println("  }	             		     ");			
	out.println(" //]]>   ");
	out.println(" </script>				        ");
	//
    	// delete startNew
	//
	out.println("<div class=\"center\">");
	if(id.equals("")){
	    out.println("<h2> New Event </h2>");
	}
	else{
	    out.println("<h2> Edit Event </h2>");
	}
	out.println("</div>"); 		
	if(!message.equals("")){
	    if(success)
		out.println("<p class=\"center\">"+message+"</p>");
	    else
		out.println("<p class=\"warning center\">"+message+"</p>");
	}
	out.println("<form name=\"myForm\" method=\"post\" "+
		    " action=\""+url+"EventServ?\""+
		    " onsubmit=\"return validateForm()\">");

	if(!id.equals("")){
	    out.println("<input type=\"hidden\" name=\"id\" value=\""+id+"\" />");
	    out.println("<input type=\"hidden\" name=\"action2\" value=\"\" />");
	}
	out.println("<table width=\"60%\">");
	out.println("<caption>Event</caption>");	
	if(!id.equals("")){
	    out.println("<tr><th>ID:</th><td>"+id+"</td></tr>");
	}
	out.println("<tr><th><label for=\"name\">Name:</label></th>");
	out.println("<td><input name=\"name\" value=\""+event.getName()+"\" size=\"30\" maxlength=\"70\" id=\"name\"/></td>");
	out.println("</tr>");
	out.println("<tr><td>&nbsp;</td></tr>");
	if(id.equals("")){
	    out.println("<tr><td></td><td align=\"right\"><input type=\"submit\" "+
			" name=\"action\" value=\"Save\" /></td></tr>");
	}
	else {
	    out.println("<tr><td></td><td align=\"right\"><input type=\"submit\" "+
			" name=\"action\" value=\"Update\" /></td></tr>");
	}		
	out.println("</table>");  
	out.println("</form>");
	EventList events = new EventList(debug);
	String back = events.find();
	if(events.size() > 0){
	    out.println("<p class=\"center\">");
	    out.println("<table border=\"1\" width=\"70%\"><caption>Events</caption>");
	    out.println("<tr><td>ID</td><td>Name</td></tr>");				
	    for(Type one:events){
		out.println("<tr><td><a href=\""+url+"EventServ?id="+one.getId()+"\">"+one.getId()+"</a></td><td>"+one+"</td></tr>");
	    }
	    out.println("</table>");
	    out.println("</p>");
	}
	out.print("<br /><br />");

	out.print("</div>");
	out.print("</body></html>");
	out.flush();
	out.close();
    }

}























































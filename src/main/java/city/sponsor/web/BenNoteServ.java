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
 *
 */
@WebServlet(urlPatterns = {"/BenNoteServ"})
public class BenNoteServ extends TopServlet{

    static final long serialVersionUID = 18L;	
    static Logger logger = LogManager.getLogger(BenNoteServ.class);
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
	DonBenType dbt = new DonBenType(debug);
		
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("id")) {
		id = value;
		dbt.setId(value);
	    }
	    else if (name.equals("notes")) {
		// dbt.setNotes(value);				
	    }
	    else if (name.equals("sponship_id")) {
		sponship_id = value;
		dbt.setSponship_id(value);
	    }	
	    else if (name.equals("action")){ 
		action = value;

	    }
	    else if (name.equals("action2")){
		if(!value.equals(""))
		    action = value;  
	    }	
	}
	// 
	if(!action.equals("") && user.canEdit()){
	    //
	    String back = ""; // dbt.updateNotes();
	    if(!back.equals("")){
		message += back; // some of benefits may already been added
	    }
	    else{
		message += " Updated successfully";
	    }
	}
	else{
	    String back = dbt.doSelect();
	    if(!back.equals("")){
		message += back; // some of benefits may already been added
	    }
	}
	//
	out.println(Inserts.xhtmlHeaderInc);
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
	String title = dbt.hasNotes()?"Edit":"Add";
	out.println("<h2> "+title+" Benefit Notes</h2>");
	out.println("</div>"); 		
	if(!message.equals("")){
	    if(success)
		out.println("<p class=\"center\">"+message+"</p>");
	    else
		out.println("<p class=\"warning center\">"+message+"</p>");
	}
	out.println("<form name=\"myForm\" method=\"post\" "+
		    " action=\""+url+"BenNoteServ?\""+
		    " onsubmit=\"return validateForm()\">");
	out.println("<fieldset><legend>Benefit Note</legend>");
	if(!id.equals("")){
	    out.println("<input type=\"hidden\" name=\"id\" value=\""+id+"\" />");
	    out.println("<input type=\"hidden\" name=\"action2\" value=\"\" />");
	}
	if(!sponship_id.equals("")){
	    out.println("<input type=\"hidden\" name=\"sponship_id\" value=\""+sponship_id+"\" />");
	}	
	out.println("<table border=\"1\" width=\"90%\">"); 
	out.println("<tr><th>Notes</th></tr>");
	out.println("<tr><td><textarea name=\"notes\" rows=\"5\" cols=\"50\">");
	// out.println(dbt.getNotes());
	out.println("</textarea></td></tr>");
	if(user.canEdit()){
	    String str = dbt.hasNotes()?"Update":"Save";
	    out.println("<tr><td align=\"center\"><input type=\"submit\" "+
			" name=\"action\" value=\""+str+"\" />");
	    out.println("</td></tr>");
	}
	out.println("</table>");  
	out.println("</fieldset>");			
	out.println("</form>");
	out.println("<center>");
	out.println("<a href=\"#\" onclick=\"javascript:window.close();\">Close This Window</a>");
	out.println("</center>");
		
	out.print("<br /><br />");
		

	out.print("</div>");
	out.print("</body></html>");
	out.flush();
	out.close();
    }

}























































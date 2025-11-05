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
 */
@WebServlet(urlPatterns = {"/CategoryServ"})
public class CategoryServ extends TopServlet{

    static final long serialVersionUID = 23L;	
    static Logger logger = LogManager.getLogger(CategoryServ.class);
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
    
	String id="", spon_id="", cat_name="", new_name="", replace="";
	boolean success=true;
	//
	String message="", action="";

	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value;
	User user = null;
		
	if(url.equals("")){
	    url    = getServletContext().getInitParameter("url");
	    url2   = getServletContext().getInitParameter("url2");
	    //
	    String debug2 = getServletContext().getInitParameter("debug");
	    if(debug2.equals("true")) debug = true;
	}
	HttpSession session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		String str = url+"Login";
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    String str = url+"Login";
	    res.sendRedirect(str);
	    return; 
	}
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	GenTypeList types = null;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("cat_name")) {
		cat_name = value;
	    }
	    else if (name.equals("id")){ 
		id = value;   //for replacement
	    }
	    else if (name.equals("new_name")){ 
		new_name = value;  
	    }
	    else if (name.equals("replace")){ 
		replace = value;  
	    }
	    else if (name.equals("action")){
		if(!value.equals(""))
		    action = value;  
	    }	
	}
	// 
	if(action.equals("Submit") && user.canEdit()){
	    //
	    GenType gtype = new GenType(debug);
	    gtype.setTableName(cat_name);
	    if(!replace.equals("") && !id.equals("")){
		gtype.setId(id);
		gtype.setName(replace);
		String back = gtype.doReplace();
		if(!back.equals("")){
		    message += back;
		    success = false;
		}
		else{
		    message += "Replaced Successfully";
		}
	    }
	    else{
		message += " To replace an option you need to choose one and enter the replacement text";
		success = false;
	    }
	    if(!new_name.equals("")){
		gtype.setName(new_name);
		String back = gtype.addNew();
		if(!back.equals("")){
		    message += back;
		    success = false;
		}
		else{
		    message += "Added Successfully";
		}
	    }
	}
	if(!cat_name.equals("")){
	    types = new GenTypeList(debug, cat_name);
	    String back = types.find();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	}
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
	out.println(" //]]>   ");
	out.println(" </script>				        ");
	//
    	// delete startNew
	//
	out.println("<div class=\"center\">");
	out.println("<h2>Category Editor</h2>");
	out.println("</div>"); 		
	if(!message.equals("")){
	    if(success)
		out.println("<p class=\"center\">"+message+"</p>");
	    else
		out.println("<p class=\"warning center\">"+message+"</p>");
	}
	out.println("<form name=\"myForm\" method=\"post\" "+
		    " action=\""+url+"CategoryServ?\""+
		    " onsubmit=\"return validateForm()\">");
	out.println("<table width=\"100%\">");	
	out.println("<caption>Category Editor</caption>");

	out.println("<tr><td colspan=\"2\" class=\"center title\">");
	out.println("Pick A Category Group</td></tr>");
	out.println("<tr><th><label for=\'cat_name\'>Categories</label></th>");		
	out.println("<td class=\"left\">");
	out.println("<select name=\"cat_name\" id=\"cat_name\" onchange=\"forms[0].submit();\">");
	out.println("<option value=\"\">Pick One</option>");
	String selected="", disabled="";
	if(cat_name.equals("org_types")) selected="selected=\"selected\"";
	out.println("<option value=\"org_types\" "+selected+">Industry Category</option>");
	selected="";
	if(cat_name.equals("seasons")) selected="selected=\"selected\"";		
	out.println("<option value=\"seasons\" "+selected+">Seasons</option>");
	selected="";		
	if(cat_name.equals("budget_cycles")) selected="selected=\"selected\"";		
	out.println("<option value=\"budget_cycles\" "+selected+">Budget Cycles</option>");
	selected="";		
	if(cat_name.equals("cont_times")) selected="selected=\"selected\"";		
	out.println("<option value=\"cont_times\" "+selected+">Pref Contact Time</option>");
	selected="";
	if(cat_name.equals("benefit_types")) selected="selected=\"selected\"";		
	out.println("<option value=\"benefit_types\" "+selected+">Benefit Types</option>");
	selected="";
	if(cat_name.equals("events")) selected="selected=\"selected\"";		
	out.println("<option value=\"events\" "+selected+">Events</option>");	
	out.println("</select></td></tr>");
	if(types != null){
	    out.println("<tr><th><label for=\"id\">Available Options</label></th>");
	    out.println("<td class=\"left\">");			
	    out.println("<select name=\"id\" id=\"id\">");
	    out.println("<option value=\"\"></option>\n");			
	    for(Type type:types){
		out.println("<option value=\""+type.getId()+"\">"+type+"</option>\n");
	    }
	    out.println("</select></td></tr>");
	}
	if(cat_name.equals("")) disabled="disabled=\"disabled\"";
	out.println("<tr><th><label for\"new_name\">Add New Category Option</label></th>");		
	out.println("<td class=\"left\">");
	out.println("<input name=\"new_name\" size=\"70\" maxlength=\"150\" "+
		    " id=\"new_name\" value=\"\" "+disabled+"/>");		
	out.println("</td></tr>");
	out.println("<tr><th><label for=\"replace\">Replace Selected Option with</label></th>");		
	out.println("<td class=\"left\">");
	out.println("<input name=\"replace\" size=\"70\" maxlength=\"150\" "+
		    " id=\"replace\" value=\"\" "+disabled+"/>");		
	out.println("</td></tr>");
	if(user.canEdit()){
	    out.println("<tr><td class=\"center\" colspan=\"2\"><input type=\"submit\" "+
			" name=\"action\" value=\"Submit\" />");
	    out.println("</td></tr>");
	}
	out.println("</table>");
	out.println("</form>");
	out.print("<br /><br />");
	out.print("</div>");
	out.print("</body></html>");
	out.flush();
	out.close();
    }
}























































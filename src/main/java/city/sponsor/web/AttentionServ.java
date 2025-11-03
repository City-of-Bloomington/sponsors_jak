package city.sponsor.web;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import city.sponsor.model.*;
import city.sponsor.list.*;
import city.sponsor.util.*;
import jakarta.servlet.annotation.WebServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 */
@WebServlet(urlPatterns = {"/AttentionServ","/Attention"})
public class AttentionServ extends TopServlet{

    static final long serialVersionUID = 17L;	

    static Logger logger = LogManager.getLogger(AttentionServ.class);
    /**
     * @param req
     * @param res
     */
    
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
	doGet(req,res);
    }
    /**
     * @link #doGetost
     */

    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
    
	boolean success=true;
	//
	String message="", action="";
	String            
	    spon_id="", sponship_id="",       
	    oppt_id="", id="", cont_id="",   
	    date_from="", date_to="", dateType = "date",            
	    sortBy="date", status="";

	String pageSize = "50", pageNumber = "1";
		
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
	else{
	    String str = url+"Login";
	    res.sendRedirect(str);
	    return; 
	}
	ActionList actions = new ActionList(debug);
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("sponship_id")) {
		actions.setSponship_id(value);
		sponship_id = value;
	    }
	    else if (name.equals("spon_id")) {
		actions.setSpon_id(value);
		spon_id = value;
	    }
	    else if (name.equals("oppt_id")) {
		actions.setOppt_id(value);
		oppt_id = value;
	    }
	    else if (name.equals("date_from")) {
		actions.setDate_from(value);
		date_from = value;
	    }	
	    else if (name.equals("date_to")) {
		actions.setDate_to(value);
		date_to = value;
	    }
	    else if (name.equals("dateType")) {
		actions.setDateType(value);
		dateType = value;
	    }	
	    else if (name.equals("sortBy")) {
		actions.setSortBy(value);
		sortBy = value;
	    }
	    else if (name.equals("status")) {
		actions.setStatus(value);
		status = value;
	    }				
	    else if (name.equals("pageSize")) {
		pageSize = value;
	    }
	    else if (name.equals("pageNumber")) {
		pageNumber = value;
	    }			
	    else if (name.equals("action")){ 
		action = value;  
	    }
	}
	//
	String spon_name = "", opport_name="", details="", fullName="";
	if(!spon_id.equals("")){
	    Sponsor sp = new Sponsor(debug, spon_id);
	    String back = sp.doSelect();
	    if(back.equals(""))
		spon_name = sp.getOrgname();
	}
	if(!oppt_id.equals("")){
	    Opportunity op = new Opportunity(debug, oppt_id);
	    String back = op.doSelect();
	    if(back.equals(""))
		opport_name = op.getInfo();
	}
	if(!sponship_id.equals("")){
	    Sponsorship sp = new Sponsorship(debug, sponship_id);
	    String back = sp.doSelect();
	    if(back.equals(""))
		details = sp.getDetails();
	}
	if(!cont_id.equals("")){
	    Contact ct = new Contact(debug, cont_id);
	    String back = ct.doSelect();
	    if(back.equals(""))
		fullName = ct.getFullName();
	}
	actions.setStatus("Ongoing");
	String back = actions.find();
	if(!back.equals("")){
	    message += back;
	    logger.error(back);
	}
	else{
	    int cnt = actions.getCount();
	    if(actions.size() == 1){
		Action act = actions.get(0);
		String str = url+"ActionServ?id="+act.getId();
		res.sendRedirect(str);
		return; 
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
	out.println("  return true;			        ");
	out.println(" }}	         		        ");
	out.println(" //]]>   ");
	out.println(" </script>				        ");
	//
	out.println("<div class=\"center\">");
	out.println("<h2>Need Attention Notes</h2>");
	out.println("<fieldset>");
	if(!message.equals("")){
	    if(success)
		out.println("<p class=\"center\">"+message+"</p>");
	    else
		out.println("<p class=\"warning center\">"+message+"</p>");
	}
	Helper.writeActions(out, actions, url);
	out.println("</fieldset>");					
	out.print("</div>");
	out.print("</body></html>");
	out.flush();
	out.close();
    }

}























































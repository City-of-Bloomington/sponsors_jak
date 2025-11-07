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
@WebServlet(urlPatterns = {"/SendOut"})
public class SendOut extends TopServlet{

    static final long serialVersionUID = 66L;		
	
    static Logger logger = LogManager.getLogger(SendOut.class);
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
    
	String date_from="", date_to="", letter="", oppt_id="",
	    opptName="";
	boolean success = true;
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
	else{
	    String str = url+"Login";
	    res.sendRedirect(str);
	    return; 
	}
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	SponshipList splist = new SponshipList(debug);		
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("date_from")) {
		if(!value.equals("")){
		    splist.setDate_from(value);
		    date_from = value;
		}
	    }
	    else if (name.equals("date_to")){
		if(!value.equals("")){
		    splist.setDate_to(value);
		    date_to = value;
		}
	    }
	    else if (name.equals("sender")){ 
		sender = value;  
	    }
	    else if (name.equals("oppt_id")){
		if(!value.equals("")){
		    splist.setOppt_id(value);	
		    oppt_id = value;
		}
	    }
	    else if (name.equals("opptName")){ 
		opptName = value;  
	    }	
	    else if (name.equals("letter")){ 
		letter = value;  
	    }
	    else if (name.equals("action")){
		if(!value.equals(""))
		    action = value;  
	    }	
	}
	if(date_to.equals("")){
	    date_to = Helper.getToday();
	    splist.setDate_to(date_to);
	}
	// 
	if(!action.equals("") && user.canEdit()){
	    String back = splist.find();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	    else if(splist.size() == 0){
		message += " No sponsorships found ";
		success = false;
	    }
	    else{
		if(letter.equals("thank")){
		    //System.err.println("processing thank letters");
		    // System.err.println(" size "+splist.size());
		    message += doThankLetters(out, splist, sender);
		}
		else{
		    // System.err.println("processing confirm letters");
		    message += doConfirmLetters(out, splist, sender);	
		}
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
		
	out.println("  if(oppt_id.value == '' && date_from.value == ''){ ");
	out.println("    alert(' Start Date or opportunity is needed');    ");
	out.println("    oppName.focus();  ");
	out.println("    return false;     ");
	out.println("  }	         		        ");
	//
	// checking dates and numeric values
	// check the numbers
	//
	out.println("  return true;			        ");
	out.println(" }}         		        ");
	out.println(" //]]>   ");
	out.println(" </script>				        ");
	//
	out.println("<div class=\"center\">");
	out.println("<h2>Send Out</h2>");
	out.println("</div>"); 		
	if(!message.equals("")){
	    if(success)
		out.println("<p class=\"center\">"+message+"</p>");
	    else
		out.println("<p class=\"warning center\">"+message+"</p>");
	}
	out.println("<form name=\"myForm\" method=\"post\" "+
		    " action=\""+url+"SendOut?\""+
		    " onsubmit=\"return validateForm()\">");
	out.println("<input type=\"hidden\" id=\"oppt_id\" name=\"oppt_id\" value=\""+oppt_id+"\" />");
	out.println("<fieldset><legend>Send Out</legend>");

	out.println("<table width=\"90%\" border=\"1\">");
	out.println("<caption>Letter and Label Process</caption>");
	out.println("<tr><th width=\"15%\"><label for=\"opptName\">Opportunity</label></th>");		
	out.println("<td class=\"left\">");
	out.println("<input type=\"text\" name=\"opptName\" id=\"opptName\" value=\""+opptName+"\" size=\"70\" />");
	out.println("</td></tr>");
		
	String check="checked=\"checked\"";
	if(letter.equals("thank")) check="checked=\"checked\"";
	out.println("<tr><th colspan=\"2\">");		
	out.println("<input type=\"radio\" name=\"letter\" value=\"thank\" "+check+" id=\"thank\" />");
	out.println("<label for=\"thank\">Thank You letter</label></th></tr>");
	out.println("<tr><th colspan=\"2\">");
	check="";
	if(letter.equals("confirm")) check="checked=\"checked\"";
	out.println("<input type=\"radio\" name=\"letter\" value=\"confirm\" id=\"letter\" />");
	out.println("<label for=\"letter\">Confirmation letter</label></th></tr>");
	//
	out.println("<tr><th><label for=\"date_from\">Date from</label></th>");
	out.println("<td class=\"left\">");
	out.println("<input type=\"text\" name=\"date_from\" size=\"10\" id=\"date_from\" value=\""+date_from+"\" />");		
	out.println("</td></tr>");
	out.println("<tr><th><label for=\"date_to\">Date to</label></th>");	
	out.println("<td class=\"left\">");
	out.println("<input type=\"text\" name=\"date_to\" id=\"date_to\" size=\"10\" value=\""+date_to+"\" />");
	out.println("</td></tr>");
	out.println("<tr><th><label for=\"sender\">Sender</label></th>");		
	out.println("<td class=\"left\">");
	out.println("<input type=\"text\" name=\"sender\" id=\"sender\" value=\""+sender+"\" />");
	out.println("</td></tr>");	
	if(user.canEdit()){
	    out.println("<tr><td colspan=\"2\" class=\"center\"><input type=\"submit\" "+
			" name=\"action\" value=\"Process Letters and Labels\" />");
	    out.println("</td></tr>");
	}
	out.println("</table>");
	out.println("</fieldset>");			
	out.println("</form>");
	out.print("<br /><br />");
	out.print("</div>");
	out.println(Inserts.footer(url));	
	out.println("<script>");
	out.println("//<![CDATA[  ");		
	out.println("  $( \"#date_from\" ).datepicker("+Inserts.jqDateStr(url)+"); ");
	out.println("  $( \"#date_to\" ).datepicker("+Inserts.jqDateStr(url)+"); ");
	out.println(" $(\"#opptName\").autocomplete({ ");
	out.println("		source: '"+url+"OpptService?format=json&type=name', ");
	out.println("		minLength: 2, ");
	out.println("		select: function( event, ui ) { ");
	out.println("			if(ui.item){ ");
	out.println("				$(\"#oppt_id\").val(ui.item.id); ");
	out.println("			} ");
	out.println("		}  ");
	out.println("	}); ");
	out.println(" //]]>   ");
	out.println("</script>");
	out.print("</body></html>");
	out.flush();
	out.close();
    }
    String doThankLetters(PrintWriter out, SponshipList splist, String sender){
	BufferedWriter bw = null;
	BufferedWriter bw2 = null;
	String all = "", back="";
	String today = Helper.getToday2(); // Mon day, year
	try{
	    bw = new BufferedWriter(new 
				    FileWriter(server_path+"thankData.txt",false));

	    bw2 = new BufferedWriter(new 
				     FileWriter(server_path+"SponLabels.txt",false));
			
	    if(bw == null){
		all = "Could not open files "+server_path;
		return all;
	    }
	    if(bw2 == null){
		all = "Could not open files "+server_path;
		return all;
	    }
	    //
	    // The label table as Excel/html
	    //
	    bw.write("\"curDate\",\"Orgname\",\"ContactName\",\"Address\",\"CityStateZip\",\"FirstName\",\"SponLevel\",\"Event\",\"Donation\",\"TotalValue\",\"b1\",\"b2\",\"b3\",\"b4\",\"b5\",\"b6\",\"b7\",\"b8\",\"b9\",\"b10\",\"b11\",\"b12\",\"Sender\"");
	    bw.newLine();
	    bw2.write("\"BusinessOrganization\","+  
		      "\"Contact_Name\","+  
		      "\"Address\","+ 
		      "\"City\","+ 
		      "\"State\","+
		      "\"Zip\"" );
	    bw2.newLine();
	    for(Sponsorship spsh:splist){
		Opportunity oppt = spsh.getOpportunity();
		Sponsor spon = spsh.getSponsor();
		Contact cont = spon.getFirstContact(); // primary contact
		String orgname="", address="", city="",state="",zip="",
		    fullName="",cityStateZip="";
		orgname = spon.getOrgname();
		address = spon.getAddress();
		city = spon.getCity();
		state = spon.getState();
		zip = spon.getZip();
		DonBenTypeList benefits = spsh.getBenefits();
		if(cont != null){
		    fullName=cont.getFullName();
		    String str = cont.getAddress();
		    if(!str.equals("") && address.equals("")){
			address = str;
		    }
		    str = cont.getCity();
		    if(!str.equals("") && city.equals("")){
			city = str;
		    }
		    str = cont.getState();
		    if(!str.equals("") && state.equals("")){
			state = str;
		    }					
		    str = cont.getZip();
		    if(!str.equals("") && zip.equals("")){
			zip = str;
		    }					
		}
		cityStateZip = city+", "+state+" "+zip;
		bw2.write("\""+orgname+"\","+
			  "\""+fullName+"\","+ // contact not used
			  "\""+address+"\","+
			  "\""+city+"\","+
			  "\""+state+"\","+
			  "\""+zip+"\"");
		bw2.newLine();
		bw.write("\""+today+"\","+
			 "\""+spon.getOrgname()+"\","+
			 "\""+fullName+"\","+
			 "\""+address+"\","+
			 "\""+cityStateZip+"\","+
			 "\""+fullName+"\","+
			 "\""+spsh.getSpon_level()+"\","+
			 "\""+oppt.getName()+"\","+
			 "\""+spsh.getDetails()+"\","+
			 "\""+spsh.getTotalValue()+"\",");
		int size = 12;
		if(benefits != null){
		    for(Type ben:benefits){
			bw.write("\""+ben+"\",");
		    }
		    size = size - benefits.size();
		    if(size < 0) size = 0; // max 12
		}
		for(int i=0; i<size; i++){
		    bw.write("\"\",");
		}
		bw.write("\""+sender+"\"");
		bw.newLine();
	    }
	    bw.newLine();
	    bw.flush();
	    bw2.newLine();
	    bw2.flush();
	    all += "<center><p>";
	    all += "Found total "+splist.size()+" records. <br />";
	    all += "Data written to file successfully<br>";
	    all += "Now run the 'Mail Merger' from your desktop by double clicking on the related Icon</p>";			
	}
	catch(Exception ex){
	    logger.error(ex);
	    all += ex;
	}
	finally{
	    try{
		if(bw != null) bw.close();
		if(bw2 != null) bw2.close();
	    }catch(Exception ex){};
	}
	return all;

    }
    String doConfirmLetters(PrintWriter out, SponshipList splist, String sender){
	BufferedWriter bw = null;
	BufferedWriter bw2 = null;
	String all = "", back="";
	String today = Helper.getToday2(); // Mon day, year
	try{
	    bw = new BufferedWriter(new 
				    FileWriter(server_path+"confirmData.txt",false));

	    bw2 = new BufferedWriter(new 
				     FileWriter(server_path+"SponLabels.txt",false));
			
	    if(bw == null){
		all = "Could not open files "+server_path;
		return all;
	    }
	    if(bw2 == null){
		all = "Could not open files "+server_path;
		return all;
	    }
	    //
	    // The label table as Excel/html
	    //
	    bw.write("\"cdate\",\"business\",\"attn\",\"Address\",\"CityStateZip\",\"event\",\"event_date\",\"event_desc\",\"spon_level\",\"b1\",\"b2\",\"b3\",\"b4\",\"b5\",\"b6\",\"b7\",\"b8\",\"b9\",\"b10\",\"b11\",\"b12\",\"donation\",\"don2\",\"don3\",\"don4\",\"don5\",\"don6\",\"don7\",\"Sender\"");
	    bw.newLine();
	    bw2.write("\"BusinessOrganization\","+  
		      "\"Contact_Name\","+  
		      "\"Address\","+ 
		      "\"City\","+ 
		      "\"State\","+
		      "\"Zip\"" );
	    bw2.newLine();
	    for(Sponsorship spsh:splist){
		Opportunity oppt = spsh.getOpportunity();
		Sponsor spon = spsh.getSponsor();
		Contact cont = spon.getFirstContact(); // primary contact
		String orgname="", address="", city="",state="",zip="",
		    fullName="",cityStateZip="";
		orgname = spon.getOrgname();
		address = spon.getAddress();
		city = spon.getCity();
		state = spon.getState();
		zip = spon.getZip();
		DonBenTypeList benefits = spsh.getBenefits();
		if(cont != null){
		    fullName=cont.getFullName();
		    String str = cont.getAddress();
		    if(!str.equals("") && address.equals("")){
			address = str;
		    }
		    str = cont.getCity();
		    if(!str.equals("") && city.equals("")){
			city = str;
		    }
		    str = cont.getState();
		    if(!str.equals("") && state.equals("")){
			state = str;
		    }					
		    str = cont.getZip();
		    if(!str.equals("") && zip.equals("")){
			zip = str;
		    }					
		}
		cityStateZip = city+", "+state+" "+zip;
		bw2.write("\""+orgname+"\","+
			  "\""+fullName+"\","+ // contact not used
			  "\""+address+"\","+
			  "\""+city+"\","+
			  "\""+state+"\","+
			  "\""+zip+"\"");
		bw2.newLine();
		bw.write("\""+today+"\","+
			 "\""+spon.getOrgname()+"\","+
			 "\""+fullName+"\","+
			 "\""+address+"\","+
			 "\""+cityStateZip+"\","+
			 "\""+oppt.getName()+"\","+
			 "\""+oppt.getStart_date()+"\","+
			 "\""+oppt.getName()+"\","+
			 "\""+spsh.getSpon_level()+"\",");
		int size = 12;
		if(benefits != null){
		    for(Type ben:benefits){
			bw.write("\""+ben+"\",");
		    }
		    size = size - benefits.size();
		    if(size < 0) size = 0; // max 12
		}
		for(int i=0; i<size; i++){
		    bw.write("\"\",");
		}
		bw.write("\""+spsh.getDetails()+"\","+
			 "\""+spsh.getTotalValue()+"\",");
		for(int i=0;i<5;i++){
		    bw.write("\"\","); // empty 5 don3-don7
		}
		bw.write("\""+sender+"\"");
		bw.newLine();
	    }
	    bw.newLine();
	    bw.flush();
	    bw2.newLine();
	    bw2.flush();
	    all += "<center><p>";
	    all += "Found total "+splist.size()+" records. <br />";
	    all += "Data written to file successfully<br>";
	    all += "Now run the 'Mail Merger' from your desktop by double clicking on the related Icon</p>";			
	}
	catch(Exception ex){
	    logger.error(ex);
	    all += ex;
	}
	finally{
	    try{
		if(bw != null) bw.close();
		if(bw2 != null) bw2.close();
	    }catch(Exception ex){};
	}
	return all;
    }	
    /*
    //
    // thank you letter
    //
    "curDate","Orgname","ContactName","Address","CityStateZip","FirstName","SponLeve
    l","Event","Donation","TotalValue","b1","b2","b3","b4","b5","b6","b7","b8","b9",
    "b10","b11","b12","Sender"


    // confirm letter
    //
    "cdate","business","attn","address","cityStateZip","event","event_date","event_d
    esc","spon_level","b1","b2","b3","b4","b5","b6","b7","b8","b9","b10","b11","b12"
    ,"donation","don2","don3","don4","don5","don6","don7","Sender"

    //
    // labels
    //
    "BusinessOrganization","Contact_Name","Address","City","State","Zip"


    */
}























































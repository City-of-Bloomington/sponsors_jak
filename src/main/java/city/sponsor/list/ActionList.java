package city.sponsor.list;

import java.util.*;
import java.sql.*;
import javax.sql.*;
import java.io.*;
import city.sponsor.model.*;
import city.sponsor.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 *
 */

public class ActionList extends ArrayList<Action>{

    boolean debug;
    static final long serialVersionUID = 11L;	
    static Logger logger = LogManager.getLogger(ActionList.class);
    String spon_id="", oppt_id="", sponship_id="", notes="", cont_id="",
	actionBy="", type="", status="", ben_id="";
    String date_from="",date_to="", sortBy="";
    String dateType = ""; // date, followup
    int pageSize = 50, pageNumber = 1;

    PageList pages = null;
    int count = 0;
    //
    // basic constructor
    //
    public ActionList(boolean deb){

	debug = deb;
	//
	// initialize
	//
	pages = new PageList(deb);
	pages.setPageSize(pageSize);
    }
    public ActionList(boolean deb,
		      String spon_id,
		      String oppt_id,
		      String sponship_id){

	debug = deb;
	pages = new PageList(deb);
	pages.setPageSize(pageSize);
	setSpon_id(spon_id);
	setOppt_id(oppt_id);
	setSponship_id(sponship_id);
	//
	// initialize
	//
    }
    public ActionList(boolean deb,
		      String spon_id,
		      String oppt_id,
		      String sponship_id,
		      String cont_id){

	debug = deb;
	pages = new PageList(deb);
	pages.setPageSize(pageSize);		
	setSpon_id(spon_id);
	setOppt_id(oppt_id);
	setSponship_id(sponship_id);
	setCont_id(cont_id);
	//
	// initialize
	//
    }		
    //
    // setters
    //
    public void setSpon_id(String val){
	if(val != null){
	    spon_id = val;
	    pages.addPair("spon_id",val);
	}
    }
    public void setOppt_id(String val){
	if(val != null){
	    oppt_id = val;
	    pages.addPair("oppt_id",val);
	}
    }
    public void setSponship_id(String val){
	if(val != null){
	    sponship_id = val;
	    pages.addPair("sponship_id",val);
	}
    }
    public void setCont_id(String val){
	if(val != null){
	    cont_id = val;
	    pages.addPair("cont_id",val);
	}
    }
    public void setBen_id(String val){
	if(val != null){
	    ben_id = val;
	    pages.addPair("ben_id",val);
	}
    }	
    public void setBy(String val){
	if(val != null){
	    actionBy = val;
	    pages.addPair("by",val);
	}
    }
    public void setSortBy(String val){
	if(val != null){
	    sortBy = val;
	    pages.addPair("sortBy",val);
	}
    }	
    public void setType(String val){
	if(val != null){
	    type = val;
	    pages.addPair("type",val);
	}
    }
    public void setActionBy(String val){
	if(val != null){
	    actionBy = val;
	    pages.addPair("actionBy",val);
	}
    }	
    public void setStatus(String val){
	if(val != null){
	    status = val;
	    pages.addPair("status",val);
	}
    }	
    public void setDate_from(String val){
	if(val != null){
	    date_from = val;
	    pages.addPair("date_from",val);
	}
    }
    public void setDate_to(String val){
	if(val != null){
	    date_to = val;
	    pages.addPair("date_to",val);
	}
    }
    public void setDateType(String val){
	if(val != null){
	    dateType = val;
	    pages.addPair("dateType",val);
	}
    }
    public void setPageSize(String val) {
	if(val != null && !val.equals("")){
	    try{
		this.pageSize = Integer.parseInt(val);
	    }
	    catch(Exception ex){}
	    pages.setPageSize(val);
	}
    }
    public void setPageNumber(String val) {
	if(val != null && !val.equals("")){
	    try{
		this.pageNumber = Integer.parseInt(val);
	    }
	    catch(Exception ex){}		
	    pages.setPageNumber(val);
	}
    }	
    public int getCount(){
	return count;
    }
    //
    // save a new record in the database
    // return "" or any exception thrown by DB
    //
    public String find(){
	//
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String qq ="", qo = "";
	String qc = " select count(*) ";
	String qf =	"from spons_actions ";		
	String q = "select id,spon_id,oppt_id,sponship_id,actionBy,"+ // 5
	    "date_format(date,'%m/%d/%Y'), "+
	    "notes,date_format(followup,'%m/%d/%Y'),cont_id,status, "+ // 10
	    "alert,date_format(alert_date,'%m/%d/%Y'),"+ // 12
	    "IFNULL(datediff(followup,now()),null) as days,ben_id, "+
	    "another_userid ";

	String back="", qw = "";
		
	if(!spon_id.equals("")){
	    qw = " spon_id = ? ";
	}
	if(!oppt_id.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " oppt_id = ? ";
	}
	if(!sponship_id.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " sponship_id = ? ";
	}
	if(!cont_id.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " cont_id = ? ";
	}	
	if(!actionBy.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " actionBy = ? ";
	}
	if(!notes.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " notes like ? ";
	}
	if(!status.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " status = ? ";
	}
	if(!ben_id.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw = " ben_id = ? ";
	}
	if(dateType.equals("")) dateType="date";
	if(!date_from.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += dateType+" >=str_to_date('"+date_from+"','%m/%d/%Y')";
	}
	if(!date_to.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += dateType+" <=str_to_date('"+date_to+"','%m/%d/%Y')";
	}
	if(!qw.equals("")){
	    qw = " where "+qw;
	}
	if(sortBy.equals(""))
	    sortBy = "date";
	qo = " order by "+sortBy+" DESC ";
	qq = qc + qf + qw;			
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}		
	try{
			
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    back = setPStatement(pstmt);
	    if(back.equals("")){
		rs = pstmt.executeQuery();
		if(rs.next()){
		    count = rs.getInt(1);
		}
	    }
	    if(count > 0){
		if(pageSize > 1){
		    if(pageNumber > 1){
			int offset = pageSize*(pageNumber - 1);
			qo += " limit "+offset+","+pageSize;
		    }
		    else{
			qo += " limit "+pageSize;
		    }
		}
		qq = q+qf+qw+qo;
		if(debug){
		    logger.debug(qq);
		}
		pstmt = con.prepareStatement(qq);
		back = setPStatement(pstmt);
		if(back.equals("")){
		    rs = pstmt.executeQuery();
		    while(rs.next()){
			String str  = rs.getString(1);
			String str2 = rs.getString(2);
			String str3 = rs.getString(3);
			String str4 = rs.getString(4);
			String str5 = rs.getString(5);
			String str6 = rs.getString(6);
			String str7 = rs.getString(7);
			String str8 = rs.getString(8);
			String str9 = rs.getString(9);
			String str10 = rs.getString(10);
			String str11 = rs.getString(11);
			String str12 = rs.getString(12);
			String str13 = rs.getString(13);
			String str14 = rs.getString(14);
			String str15 = rs.getString(15);						
			int days = 10;
			if(str13 != null){
			    days = rs.getInt(13);
			}
			Action action = new Action(debug, str, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, str14, str15);
			if((days < Helper.criticalDays) &&
			   (str10 == null ||
			    str10.equals("Ongoing"))) // status
			    action.setCritical();
			this.add(action);
		    }
		}
	    }
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    return ex.toString();
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	rs = null;
	return "";
    }
    private String setPStatement(PreparedStatement pstmt){
	String back = "";
	try{
	    int jj = 1;
	    if(!spon_id.equals("")){
		pstmt.setString(jj++, spon_id);
	    }
	    if(!oppt_id.equals("")){
		pstmt.setString(jj++, oppt_id);
	    }
	    if(!sponship_id.equals("")){
		pstmt.setString(jj++, sponship_id);
	    }
	    if(!cont_id.equals("")){
		pstmt.setString(jj++, cont_id);
	    }
	    if(!actionBy.equals("")){
		pstmt.setString(jj++, actionBy);
	    }
	    if(!notes.equals("")){
		pstmt.setString(jj++, notes);
	    }
	    if(!status.equals("")){
		pstmt.setString(jj++, status);
	    }
	    if(!ben_id.equals("")){
		pstmt.setString(jj++, ben_id);
	    }
	}
	catch(Exception ex){
	    back = ""+ex;
	    logger.error(back);
	}
	return back;
    }
    public PageList buildPages(String url){
	if(pages != null && !url.equals("")){
	    pages.setCount(count);
	    pages.setUrl(url);
	    pages.build();
	}
	return pages;
    }
}























































package city.sponsor.list;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.sql.*;
import java.text.SimpleDateFormat;
import city.sponsor.model.*;
import city.sponsor.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 *
 */

public class SponsorshipList extends ArrayList<Sponsorship>{

    static final long serialVersionUID = 81L;
    static Logger logger = LogManager.getLogger(SponsorshipList.class);		
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    boolean debug = false;

    String oppt_id="", spon_id="", id="", date_from="", date_to="",
	event_id="", year="", season_id="", program_area="",
	which_date="cont_start_date";
    public SponsorshipList(boolean val){
	debug = val;
    }
	
    public SponsorshipList(boolean val, String val2, String val3){
	debug = val;
	setOppt_id(val2);
	setSpon_id(val3);
    }
    public void setOppt_id(String val){
	if(val != null)
	    oppt_id = val;
    }
    public void setSpon_id(String val){
	if(val != null)
	    spon_id = val;
    }
    public void setSeason_id(String val){
	if(val != null)
	    season_id = val;
    }
    public void setEvent_id(String val){
	if(val != null)
	    event_id = val;
    }		
    public void setId(String val){
	if(val != null)
	    id = val;
    }
    public void setDate_from(String val){
	if(val != null)
	    date_from = val;
    }
    public void setDate_to(String val){
	if(val != null)
	    date_to = val;
    }
    public void setWhich_date(String val){
	if(val != null)
	    which_date = val;
    }
    public void setYear(String val){
	if(val != null)
	    year = val;
    }
    public void setProgram_area(String val){
	if(val != null)
	    program_area = val;
    }		
    public String find(){
		
	String back = "", qw="";
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	Connection con = Helper.getConnection();
	String qq = "select s.id,s.oppt_id,s.spon_id,"+
	    "s.details, s.don_type,"+
	    "date_format(s.start_date,'%m/%d/%Y'), "+
	    "s.terms,s.value, s.spon_level,s.pay_type, "+
	    "date_format(s.cont_start_date,'%m/%d/%Y'), "+
	    "date_format(s.cont_end_date,'%m/%d/%Y'), "+
	    "s.notes "+
	    "from spons_sponsorships s ";
	boolean optTbl = false;
	if(!oppt_id.equals("")){
	    qw += " s.oppt_id=? "; 
	}
	else if(!spon_id.equals("")){
	    qw += " s.spon_id=? "; 
	}
	else if(!id.equals("")){
	    qw += " s.id=? "; 
	}
	else {				
	    if(!event_id.equals("")){
		optTbl = true;
		if(!qw.equals("")) qw += " and ";
		qw += " opt.event_id=? "; 						
	    }
						
	    if(!date_from.equals("")){
		qw += "s."+which_date +" >= ? ";
	    }
	    if(!date_to.equals("")){
		if(!qw.equals("")) qw += " and ";
		qw += "s."+which_date+" <= ? ";
	    }
	    if(!year.equals("")){
		optTbl = true;
		if(!qw.equals("")) qw += " and ";								
		qw += " opt.year = ? ";
	    }
	    if(!season_id.equals("")){
		optTbl = true;
		if(!qw.equals("")) qw += " and ";
		qw += " opt.season = ? ";								
	    }
	    if(!program_area.equals("")){
		optTbl = true;
		if(!qw.equals("")) qw += " and ";								
		qw += " opt.program_area like ? ";
	    }
	}
	if(optTbl){
	    qq += ", spons_opportunities opt ";		
	    if(!qw.equals("")) qw += " and ";
	    qw += " s.oppt_id=opt.id ";
	}
	if(!qw.equals("")){
	    qq += " where "+qw;
	}
	qq += " order by s.start_date DESC ";

	if(con == null){
	    back = "Could not connect to DB";
	    return back;
	}
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    int jj  = 1;
	    if(!oppt_id.equals("")){
		pstmt.setString(jj++, oppt_id);
	    }
	    else if(!spon_id.equals("")){
		pstmt.setString(jj++, spon_id);
	    }
	    else if(!id.equals("")){
		pstmt.setString(jj++, id);
	    }
	    else {
		if(!event_id.equals("")){
		    pstmt.setString(jj++, event_id);
		}
		if(!date_from.equals("")){
		    pstmt.setDate(jj++, new java.sql.Date(dateFormat.parse(date_from).getTime()));
		}
		if(!date_to.equals("")){
		    pstmt.setDate(jj++, new java.sql.Date(dateFormat.parse(date_to).getTime()));												
		}
		if(!year.equals("")){
		    pstmt.setString(jj++, year);										
		}
		if(!season_id.equals("")){
		    pstmt.setString(jj++, season_id);	
		}								
		if(!program_area.equals("")){
		    pstmt.setString(jj++, program_area);											
		}

	    }
	    rs = pstmt.executeQuery();
	    while(rs.next()){
		Sponsorship sponship =
		    new Sponsorship(debug,
				    rs.getString(1),
				    rs.getString(2),
				    rs.getString(3),
				    rs.getString(4),
				    rs.getString(5),
				    rs.getString(6),  
				    rs.getString(7),
				    rs.getString(8),
				    rs.getString(9),
				    rs.getString(10),
				    rs.getString(11),
				    rs.getString(12),
				    rs.getString(13)
				    );
		this.add(sponship);
	    }
	}
	catch(Exception ex){
	    back += ex+" : "+qq;
	    logger.error(back);
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	return back;
    }
}























































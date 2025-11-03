package city.sponsor.model;

import city.sponsor.util.*;
import java.text.SimpleDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Thank {
	
    boolean debug;
    static Logger logger = LogManager.getLogger(Thank.class);
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	
    String id="", don_id="", 
	sent="", // yes, or null
	date = "";
	
    public Thank(boolean deb) {
	debug = deb;
    }
    public Thank(boolean deb, String id) {
	debug = deb;
	setId(id);
    }	
    public String getId() {
	return id;
    }
    public void setId(String id) {
	if(id != null)
	    this.id = id;
    }
    public String getDon_id() {
	return don_id;
    }
    public void setDon_id(String don_id) {
	if(don_id != null)
	    this.don_id = don_id;
    }
    public String getSent() {
	return sent;
    }
    public void setSent(String sent) {
	if(sent != null)
	    this.sent = sent;
    }
    public String getDate() {
	return date;
    }
    public void setDate(String date) {
	if(date != null)
	    this.date = date;
    }

	
}

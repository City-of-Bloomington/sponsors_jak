package city.sponsor.model;

import java.sql.*;
import javax.sql.*;
import city.sponsor.util.*;
import java.text.SimpleDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Income {
    boolean debug;
    static Logger logger = LogManager.getLogger(Income.class);
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    String receipt_id="", inv_id="", value="", spon_id="", sponship_id="",
	oppt_id="", received="";
    //
    // one or more for multiple receipts per payment
    //	
    Invoice invoice = null;
    Receipt receipt = null;
    Opportunity oppt = null;
    Sponsorship sponship = null;
    Sponsor sponsor = null;
    public Income(boolean deb,
		  String receipt_id,
		  String inv_id,
		  String value,
		  String received,
		  String sponship_id,	                             
		  String oppt_id
		  ) {
	debug = deb;
	setReceipt_id(receipt_id);
	setInv_id(inv_id);		
	setValue(value);
	setReceived(received);
	setSponship_id(sponship_id);
	setOppt_id(oppt_id);
    }

    public void setReceipt_id(String val) {
	if(val != null)
	    receipt_id = val;
    }
	
    public String getInv_id() {
	return inv_id;
    }
    public void setSpon_id(String val) {
	if(val != null)
	    spon_id = val;
    }
    public void setSponship_id(String val) {
	if(val != null)
	    sponship_id = val;
    }	
    public void setInv_id(String inv_id) {
	if(inv_id != null)
	    this.inv_id = inv_id;
    }
    public void setOppt_id(String val) {
	if(val != null)
	    oppt_id = val;
    }
    public void setReceived(String val) {
	if(val != null)
	    received = val;
    }	
    public String getValue() {
	return value;
    }
    public String getSpon_id() {
	return spon_id;
    }
    public String getSponship_id() {
	return sponship_id;
    }
    public String getReceipt_id() {
	return receipt_id;
    }
    public String getOppt_id() {
	return oppt_id;
    }	
    public double getValueDbl() {
	double val = 0.;
	try{
	    val = Double.parseDouble(value);
	}catch(Exception ex){}
	return val;
    }
    public void setValue(String value) {
	if(value != null)
	    this.value = value;
    }

    public Invoice getInvoice(){
	if(invoice == null && !inv_id.equals("")){
	    Invoice inv =  new Invoice(debug, inv_id);
	    String back = inv.doSelect();
	    if(back.equals("")){
		invoice = inv;
	    }
	}
	return invoice;
    }
    public Opportunity getOppt(){
	if(oppt == null && !oppt_id.equals("")){
	    Opportunity one =  new Opportunity(debug, oppt_id);
	    String back = one.doSelect();
	    if(back.equals("")){
		oppt = one;
	    }
	}
	return oppt;
    }	
    public Sponsorship getSponship(){
	if(sponship == null && !sponship_id.equals("")){
	    Sponsorship one =  new Sponsorship(debug, sponship_id);
	    String back = one.doSelect();
	    if(back.equals("")){
		sponship = one;
	    }
	}
	return sponship;
    }	
    public Sponsor getSponsor(){
	if(sponsor == null){
	    if(sponship == null){
		getSponship();
	    }
	    if(sponship != null){
		sponsor = sponship.getSponsor();
	    }
	}
	return sponsor ;
    }
}

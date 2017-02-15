package dslam.info;

import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class hosts {
private String ip;
private Hashtable<String, OidTable> dslamOidTables;
private static final Logger logger = LoggerFactory.getLogger("connectProcess");
private int dslamIndex;
 public hosts(String ip,int dslamIndex) {
	this.ip=ip;
	this.dslamOidTables=new Hashtable<String, OidTable>();
	this.dslamIndex=dslamIndex;
	logger.trace("Created new Dslam with ip"+this.ip);

 }
 

/**
 * @return the dslamIndex
 */
public int getDslamIndex() {
	return dslamIndex;
}

/**
 * @return the ip
 */
public String getIp() {
	return ip;
}

/**
 * @param ip the ip to set
 */
public void setIp(String ip) {
	this.ip = ip;
}

public void addToDslamOidTables (String oid, OidTable oidtable) {
	if ( this.dslamOidTables.get(oid) == null ) {
		this.dslamOidTables.put(oid, oidtable);
		logger.trace("Added to OidTable:"+oid+" to dslam:"+this.ip);
	} else {
		logger.error("Already have OidTable:"+this.dslamOidTables.get(oid)+" for dslam:"+this.ip);
	}
}

public OidTable getToDslamOidTables (String oid) {
	if ( this.dslamOidTables.get(oid) == null ) {
		logger.error("Undefined OidTable:"+oid+" to dslam:"+this.ip);
		return null;
	} else {
		return this.dslamOidTables.get(oid);
	}
}

/**
 * @return the dslamOidTables
 */
public Hashtable<String, OidTable> getDslamOidTables() {
	return this.dslamOidTables;
}

}

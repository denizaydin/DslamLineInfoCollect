package dslam.info;

import org.snmp4j.smi.OID;

public class MyOid extends OID{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OID oid;
	private String name;
	private int GetBulkSize;
	private String oidstring;
	private String SNMPReqType;
	private OID LastOidsToMatch;
	private int avgresponsetime;
	public MyOid (String oid, String name) {
	this.name=name;
	this.oid=new OID(oid);
	this.SNMPReqType="BULK";
	this.GetBulkSize=20;
	this.LastOidsToMatch=new OID();
	this.avgresponsetime=3000;
	}
	/**
	 * @return the avgresponsetime
	 */
	public int getAvgresponsetime() {
		return avgresponsetime;
	}
	/**
	 * @param avgresponsetime the avgresponsetime to set
	 */
	public void setAvgresponsetime(int avgresponsetime) {
		this.avgresponsetime = avgresponsetime;
	}
	/**
	 * @return the getBulkSize
	 */
	public int getGetBulkSize() {
		return GetBulkSize;
	}
	/**
	 * @param getBulkSize the getBulkSize to set
	 */
	public void setGetBulkSize(int getBulkSize) {
		GetBulkSize = getBulkSize;
	}
	/**
	 * @return the oid
	 */
	public OID getOid() {
		return oid;
	}
	/**
	 * @return the sNMPReqType
	 */
	public String getSNMPReqType() {
		return this.SNMPReqType;
	}
	/**
	 * @param sNMPReqType the sNMPReqType to set
	 */
	public void setSNMPReqType(String sNMPReqType) {
		this.SNMPReqType = sNMPReqType;
	}
	/**
	 * @param oid the oid to set
	 */
	public void setOid(OID oid) {
		this.oid = oid;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the oidstring
	 */
	public String getOidstring() {
		return oidstring;
	}


	/**
	 * @return the lastOidsToMatch
	 */
	public OID getLastOidsToMatch() {
		return LastOidsToMatch;
	}
	/**
	 * @param lastOidsToMatch the lastOidsToMatch to set
	 */
	public void setLastOidsToMatch(OID lastOidsToMatch) {
		LastOidsToMatch = lastOidsToMatch;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MyOid [oid=" + oid + ", name=" + name + ", GetBulkSize=" + GetBulkSize + ", oidstring=" + oidstring
				+ ", SNMPReqType=" + SNMPReqType + ", LastOidsToMatch=" + LastOidsToMatch + ", avgresponsetime="
				+ avgresponsetime + "]";
	}
	
	
	

}

package dslam.info;

import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.smi.VariableBinding;

public class OidTable {
	private MyOid oid;
	private int dslampointer;
	private Hashtable<String, String> indexToValue;
	private int error;
	private int retrycount;
	private static final Logger logger = LoggerFactory.getLogger("connectProcess");

	public OidTable (MyOid oid,int dslampointer) {
		this.oid=oid;
		this.dslampointer=dslampointer;
		this.retrycount=0;
		this.error=0;
		this.indexToValue=new Hashtable<String, String>();
	}
	public void addToTable (VariableBinding varBinding) {
		if (this.oid.getLastOidsToMatch().size()== 0) {
			
			// last element int the oid is the ifindex, AND as a result returns one value for each ifindex
		this.indexToValue.put(varBinding.getOid().toString().substring(this.oid.getOid().toString().length()+1), varBinding.getVariable().toString());
		logger.trace("index:"+varBinding.getOid().toString().substring(this.oid.getOid().toString().length()+1)+
				" value:"+varBinding.getVariable().toString()+" added to the table"+this.oid.getName());
		} else {				
				if (varBinding.getOid().rightMostCompare(this.oid.getLastOidsToMatch().size(), this.oid.getLastOidsToMatch()) == 0) {
					String ifindex = Integer.toString(varBinding.getOid().get(varBinding.getOid().size()-this.oid.getLastOidsToMatch().size()-1));
					this.indexToValue.put(ifindex, varBinding.getVariable().toString());
					logger.trace("index:"+ifindex+" value:"+varBinding.getVariable().toString()+" added to the table"+this.oid.getName());
				}
			}
	
	}
	/**
	 * @return the oid
	 */
	public MyOid getOid() {
		return oid;
	}
	/**
	 * @return the indexToValue
	 */
	public Hashtable<String, String> getIndexToValue() {
		return indexToValue;
	}
	
	/**
	 * @return the error
	 */
	public int getError() {
		return error;
	}
	/**
	 * @param error the error to set
	 */
	public void setError(int error) {
		this.error = error;
	}
	/**
	 * @return the retrycount
	 */
	public int getRetrycount() {
		return retrycount;
	}
	/**
	 * @param retrycount the retrycount to set
	 */
	public void incrRetrycount() {
		this.retrycount++;
	}
	/**
	 * @return the dslampointer
	 */
	public int getDslampointer() {
		return dslampointer;
	}
}

package dslam.info;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class Oids {
	// SNMP OIDS to retrive for each line from target
	private List<MyOid> oids;

	private static final Logger logger = LoggerFactory.getLogger("main");

	public Oids () {
		this.oids=new LinkedList<MyOid>();
	}

	/**
	 * @return the oids
	 */
	public LinkedList<MyOid> getOids() {
		return (LinkedList<MyOid>) oids;
	}

	public void addOids (MyOid oid) {
		this.oids.add(oid);
		logger.info("Adding oid:"+oid.getName()+" value:"+oid);
	}
	public int getSize () {
		return this.oids.size();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Oids [oids=" + oids + "]";
	}
	
}

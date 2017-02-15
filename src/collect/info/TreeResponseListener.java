package collect.info;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeListener;

import dslam.info.hosts;
import dslam.info.OidTable;

public class TreeResponseListener implements TreeListener {
	private static final Logger logger = LoggerFactory.getLogger("connectProcess");
	private hosts dslam;
	private boolean finished;
    private long StartTime;
    private OidTable oidtable;

    
    /**
     * Instantiates a new Tree response listener.
     *
     * @param networkDevice the network device
     * @param oids          the oids
     * @param treeUtils     the tree utils
     */
    
    public TreeResponseListener(hosts dslam,OidTable oidtable) {
		this.StartTime=Instant.now().getEpochSecond();
		this.dslam=dslam;
		this.oidtable=oidtable;
		logger.trace("Created new listener" + dslam.getIp() + " oid:"+ oidtable.getOid().getName()+" StartTime:"+StartTime);

		
    }
    /**
     * Stop walk.
     */
    public void stopWalk() {
		this.oidtable.setError(1);
        finished = true;
		logger.debug("Stopted snmp listener for dslam:" + dslam.getIp() + " oid:"+ oidtable.getOid().getName()+" error:" + oidtable.getError()  + " finished:"+this.finished);
    	synchronized (this) {
			this.notify();
		}
    }
	public void finished(TreeEvent e) {
		if ((e.getVariableBindings() != null) && (e.getVariableBindings().length > 0)) {
			next(e);
			this.oidtable.setError(0);;
			long now=Instant.now().getEpochSecond();
			long totalRunTime=now-this.getStartTime();
			logger.debug("completed snmp for dslam:" + dslam.getIp() + " oid:"+ oidtable.getOid().getName()+ " total run time:"+totalRunTime);
		}
		if (e.isError()) {
			this.oidtable.setError(1);
			long now=Instant.now().getEpochSecond();
			long totalRunTime=now-this.getStartTime();
			logger.error("The following error occurred during for dslam:" + dslam.getIp() + " oid:"
					+ oidtable.getOid().getName() + " error:" + oidtable.getError() +" "
					+ e.getErrorMessage() + " total run time:"+totalRunTime);
		}
		finished = true;
		synchronized (this) {
			this.notify();
		}
	}

	public boolean isFinished() {
		return finished;
	}

	public boolean next(TreeEvent e) {
		if (e.getVariableBindings() != null) {
			VariableBinding[] varBindings = e.getVariableBindings();
			for (VariableBinding varBinding : varBindings) {
	            if (varBinding == null) {
	              continue;
	            }
	            logger.trace("Reply : "+ dslam.getIp()+ " for oidtable : "+oidtable.getOid().getName()+" : oid: "+ 
						  varBinding.getOid().toString()+ 
						    " : " + 
						  varBinding.getVariable().getSyntaxString() +
						    " : " +
						  varBinding.getOid().last() +
						    " : " +
						  varBinding.getVariable() +
						    " : Value as String " +
						  varBinding.getVariable().toString());
	            oidtable.addToTable(varBinding);

	          }
		}
		return true;
	}
	/**
	 * @return the dslam
	 */
	public hosts getDslam() {
		return dslam;
	}
	/**
	 * @param dslam the dslam to set
	 */
	public void setDslam(hosts dslam) {
		this.dslam = dslam;
	}
	/**
	 * @return the startTime
	 */
	public long getStartTime() {
		return StartTime;
	}
	/**
	 * @return the oidtable
	 */
	public OidTable getOidtable() {
		return oidtable;
	}
	

}

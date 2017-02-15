package collect.info;



import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.TreeUtils;

import dslam.info.Oids;
import dslam.info.MyOid;
import dslam.info.hosts;
import dslam.info.OidTable;





public class collectInfoFromDslams {
	private static final Logger logger = LoggerFactory.getLogger("connectProcess");
	public static void main(String[] args) throws IOException, InterruptedException, SQLException {
		Timestamp StarTime = new Timestamp(System.currentTimeMillis());
		logger.info("StartTime, timestamp:"+StarTime.toString());
		hostparams hostParam = new hostparams();
		try {
			hostParam.initialize();
		} catch (Exception e) {
			logger.error("Error while initializing hostparams:"+e.toString());
			System.exit(0);
		}
		LinkedList<String> peersToConnect=hostParam.getPeersToConnect();
		int num_hosts=peersToConnect.size();
		logger.info("Number of dslams:"+num_hosts);
		if (num_hosts == 0) {
			logger.warn("No host found, exiting");
			System.exit(0);
		}
		Oids oids = new Oids();
		MyOid ifIndex = new MyOid("1.3.6.1.2.1.31.1.1.1.1","ifIndex"); // fast
		ifIndex.setGetBulkSize(100);
		oids.addOids(ifIndex);

		MyOid VDSLDescription = new MyOid("1.3.6.1.2.1.2.2.1.2", "VDSLDescription"); // fast
		VDSLDescription.setGetBulkSize(100);
		VDSLDescription.setAvgresponsetime(15000);
		oids.addOids(VDSLDescription);

		MyOid VDSLLineProfile = new MyOid("1.3.6.1.4.1.3902.1082.130.2.2.1.2.1.9", "VDSLLineProfile");  // slow
		//VDSLLineProfile.setSNMPReqType("walk");
		VDSLLineProfile.setAvgresponsetime(15000);
		oids.addOids(VDSLLineProfile);

		MyOid VDSLLineStatus = new MyOid("1.3.6.1.2.1.10.251.1.1.1.1.14", "VDSLLineStatus"); // slow
		oids.addOids(VDSLLineStatus);

		MyOid VDSLMaxUp = new MyOid("1.3.6.1.2.1.10.251.1.1.1.1.21", "VDSLMaxUp"); // slow
		VDSLMaxUp.setAvgresponsetime(45000);
		oids.addOids(VDSLMaxUp);

		MyOid VDSLMaxDown = new MyOid("1.3.6.1.2.1.10.251.1.1.1.1.20", "VDSLMaxDown"); // slow
		VDSLMaxDown.setAvgresponsetime(45000);
		oids.addOids(VDSLMaxDown);

		MyOid VDSLATUX = new MyOid("1.3.6.1.2.1.10.251.1.2.2.1.2", "VDSLATUX"); // fast
		VDSLATUX.setGetBulkSize(100);
		oids.addOids(VDSLATUX);

		MyOid VDSLSNR = new MyOid("1.3.6.1.2.1.10.251.1.1.2.1.4", "VDSLSNR"); // fast
		VDSLSNR.setGetBulkSize(100);
		oids.addOids(VDSLSNR);

		MyOid VDSLAttenuation = new MyOid("1.3.6.1.2.1.10.251.1.1.2.1.2", "VDSLAttenuation"); // fast
		VDSLAttenuation.setGetBulkSize(100);
		oids.addOids(VDSLAttenuation);

		MyOid VDSLTransmissionMode = new MyOid("1.3.6.1.2.1.10.251.1.1.1.1.13", "VDSLTransmissionMode"); // slow
		oids.addOids(VDSLTransmissionMode);

		MyOid VDSLxdsl2PMLCurr1DayTimeElapsed_Loss = new MyOid("1.3.6.1.2.1.10.251.1.4.1.1.1.12", "VDSLxdsl2PMLCurr1DayTimeElapsed_Loss");  // slow
		VDSLxdsl2PMLCurr1DayTimeElapsed_Loss.setAvgresponsetime(45000);
		VDSLxdsl2PMLCurr1DayTimeElapsed_Loss.setLastOidsToMatch(new OID("1"));
		oids.addOids(VDSLxdsl2PMLCurr1DayTimeElapsed_Loss);


		MyOid VDSLxdsl2PMLCurr1DayLoss = new MyOid("1.3.6.1.2.1.10.251.1.4.1.1.1.16", "VDSLxdsl2PMLCurr1DayLoss");  // slow
		VDSLxdsl2PMLCurr1DayLoss.setAvgresponsetime(45000);
		VDSLxdsl2PMLCurr1DayLoss.setLastOidsToMatch(new OID("1"));
		oids.addOids(VDSLxdsl2PMLCurr1DayLoss);

		MyOid VDSLxdsl2PMLInitCurr1DayTimeElapsed_Inits = new MyOid("1.3.6.1.2.1.10.251.1.4.1.2.1.10", "VDSLxdsl2PMLInitCurr1DayTimeElapsed_Inits");  // slow
		VDSLxdsl2PMLInitCurr1DayTimeElapsed_Inits.setAvgresponsetime(45000);
		oids.addOids(VDSLxdsl2PMLInitCurr1DayTimeElapsed_Inits);

		MyOid VDSLxdsl2PMLInitCurr1DayFullInits = new MyOid("1.3.6.1.2.1.10.251.1.4.1.2.1.11", "VDSLxdsl2PMLInitCurr1DayFullInits");  // slow
		VDSLxdsl2PMLInitCurr1DayFullInits.setAvgresponsetime(45000);
		oids.addOids(VDSLxdsl2PMLInitCurr1DayFullInits);


		MyOid VDSLxdsl2PMLHist1DLoss = new MyOid("1.3.6.1.2.1.10.251.1.4.1.4.1.7", "VDSLxdsl2PMLHist1DLoss");  // slow
		VDSLxdsl2PMLHist1DLoss.setAvgresponsetime(45000);
		VDSLxdsl2PMLHist1DLoss.setLastOidsToMatch(new OID("1.1"));
		oids.addOids(VDSLxdsl2PMLHist1DLoss);

		MyOid VDSLxdsl2PMLInitHist1DFullInits = new MyOid("1.3.6.1.2.1.10.251.1.4.1.6.1.3", "VDSLxdsl2PMLInitHist1DFullInits");  // slow
		VDSLxdsl2PMLInitHist1DFullInits.setAvgresponsetime(45000);
		VDSLxdsl2PMLInitHist1DFullInits.setLastOidsToMatch(new OID("1"));
		oids.addOids(VDSLxdsl2PMLInitHist1DFullInits);
		if (oids.getSize() == 0) {
			logger.warn("No oid found, exiting");
			System.exit(0);
		}

		logger.debug(oids.toString());
		Snmp snmp = new Snmp(new DefaultUdpTransportMapping());  
		snmp.listen();
		//FIXME : contextEngineId NEVER USED. DONT KNOW MUCH ABOUT IT
		OctetString contextEngineId = new OctetString("00:00:00:09:02:00:00:02:4a:28:f4:00");  
		// CREATE ARRAY FOR DSLAM INFO
		hosts[] dslams = new hosts[num_hosts];
		// CREATE SNMP TARGET ARRAY. THIS MUST BE USER TARGET IN CASE OF SNMPV3
		CommunityTarget [] targets = new CommunityTarget[num_hosts];
		// CREATE COMMUNUTY TARGET FOR EACH HOST AND ADD TO SNMP TARGET ARRAY
		long TotalOidAvgResponseTime=0;
		for(int i=0;i<num_hosts;++i)
		{
			hosts dslam = new hosts(peersToConnect.get(i),i);
			int snmptimeout=10000;
			for (MyOid oid : oids.getOids()) {
				OidTable oidtable = new OidTable(oid,i);
				logger.info("Adding Oid Table:"+oid.getName()+" value:"+oid.getOid().toString()+" to Dslam:"+peersToConnect.get(i));
				dslam.addToDslamOidTables(oid.getOid().toString(), oidtable);
				if (snmptimeout < oid.getAvgresponsetime()) snmptimeout=oid.getAvgresponsetime();
			}
			dslams[i]=dslam;
			String ip = peersToConnect.get(i)+"/161";
			CommunityTarget target = new CommunityTarget();
			target.setCommunity(new OctetString("xxxx"));
			target.setAddress(new UdpAddress(ip));
			target.setRetries(3);
			target.setTimeout(snmptimeout/2);
			target.setVersion(SnmpConstants.version2c);
			targets[i]=target;
		}
		// Creating Treelistener array for each request
		Vector<TreeResponseListener> treelisteners = new Vector<TreeResponseListener>((targets.length*oids.getSize()), 10);
		logger.debug("Initial Treelisteners vector size:"+treelisteners.capacity());
		Timestamp InitialSNMPReqSendingStartTime = new Timestamp(System.currentTimeMillis());
		logger.info("Starting to get info via SNMP from DSLAMS, timestamp:"+InitialSNMPReqSendingStartTime.toString());
		if (targets.length > 0) {
			for (MyOid oid : oids.getOids()) {
				for (int i = 0; i < targets.length; i++) {
					if (oid.getSNMPReqType() == "walk") {
						logger.debug("Creating SNMP listener with number for dslam:"+dslams[i].getIp()+" oid:"+oid.getName());
						TreeResponseListener treelistener=new TreeResponseListener(dslams[i],dslams[i].getToDslamOidTables(oid.getOid().toString()));
						treelisteners.add(treelistener);
						logger.debug("Sending SNMP request to dslam:"+dslams[i].getIp()+" oid:"+oid.getName());
						TreeUtils treeUtils = new TreeUtils(snmp, new MyDefaultPDUFactory(PDU.GETNEXT, contextEngineId));
						treeUtils.getSubtree(targets[i], oid.getOid(), i, treelistener);
					} else {
						logger.debug("Creating SNMP listener with number for dslam:"+dslams[i].getIp()+" oid:"+oid.getName());
						TreeResponseListener treelistener=new TreeResponseListener(dslams[i],dslams[i].getToDslamOidTables(oid.getOid().toString()));
						treelisteners.add(treelistener);
						logger.debug("Sending SNMP request to dslam:"+dslams[i].getIp()+" oid:"+oid.getName());
						TreeUtils treeUtils = new TreeUtils(snmp, new MyDefaultPDUFactory(PDU.GETBULK, contextEngineId));
						treeUtils.setMaxRepetitions(oid.getGetBulkSize());
						treeUtils.getSubtree(targets[i], oid.getOid(), i, treelistener);
					}
				}
				logger.debug("Waiting at least "+oid.getAvgresponsetime()+" miliseconds for the requesst");
				TotalOidAvgResponseTime = TotalOidAvgResponseTime + oid.getAvgresponsetime();
				Thread.sleep(oid.getAvgresponsetime());
			}
			Timestamp SNMPReqSendingEndTime = new Timestamp(System.currentTimeMillis());
			logger.info("All Requests are sent, timestamp:"+SNMPReqSendingEndTime.toString()+ 
					" elapsed time:" + (SNMPReqSendingEndTime.getTime() - InitialSNMPReqSendingStartTime.getTime() ) + " TotalOidWaiTTime:"+TotalOidAvgResponseTime);

			boolean finished = false;
			while (!finished) {
				boolean treelistenersFinished = true;
				for (TreeResponseListener treelistener : treelisteners) {
					logger.debug("is treelistenersFinished?:"+treelistenersFinished);
					logger.debug("Checking snmp listener for dslam:"+treelistener.getDslam().getIp()+" oid:"+treelistener.getOidtable().getOid().getName()+
							" finished?:"+treelistener.isFinished() +
							" is treelistenersFinished?:"+treelistenersFinished);
					// checking runtime for the listener
					long now = Instant.now().getEpochSecond();
					long runtime = now-treelistener.getStartTime();
					if (!treelistener.isFinished()) {
						if (runtime > (treelistener.getOidtable().getOid().getAvgresponsetime()*2/1000)) {
							treelistener.stopWalk();
							logger.error("Long runtime:"+ runtime + " for treelistener of dslam:"+treelistener.getDslam().getIp()+" for oid:"+
									treelistener.getOidtable().getOid().getName()+
									" error:"+treelistener.getOidtable().getError() );
						}
					}
					if (treelistenersFinished == true && treelistener.isFinished()) {
						treelistenersFinished = true;
					} else {
						treelistenersFinished = treelistener.isFinished();
					}
				}
				if (treelistenersFinished == true) {
					logger.debug("All snmp listners are finished, ending check");
					finished=treelistenersFinished;
				} else {
					logger.debug("All snmp listners are not FINISHED, will retry check");
					Thread.sleep(10000);
				}
			}
		}

		Timestamp InitialCollectEndTime = new Timestamp(System.currentTimeMillis());
		logger.info("Initial information collection via snmp is FINISHED time:"+InitialCollectEndTime.toString()+" total wait time for oids:"+TotalOidAvgResponseTime);


		logger.info("Waiting for 1 minute to cooldown the dslams");
		Thread.sleep(61000);
		TotalOidAvgResponseTime = TotalOidAvgResponseTime + 61000;


		if (targets.length > 0) {
			boolean errorCheckFinished = false;
			while (!errorCheckFinished) {
				boolean haveError = false;

				treelisteners = new Vector<TreeResponseListener>((targets.length*oids.getSize()), 10);
				for (MyOid oid : oids.getOids()) {
					boolean oiderror = false;
					for (int i = 0; i < targets.length; i++) {
						if ( dslams[i].getToDslamOidTables(oid.getOid().toString()).getError() > 0 ) {
							logger.debug("Found error for dslam:"+dslams[i].getIp()+" oid:"+oid.getName()+
									" retrycount:"+dslams[i].getToDslamOidTables(oid.getOid().toString()).getRetrycount());
							if ( dslams[i].getToDslamOidTables(oid.getOid().toString()).getRetrycount() <=2 ) {
								dslams[i].getToDslamOidTables(oid.getOid().toString()).incrRetrycount();
								logger.debug("Creating SNMP listener with number for dslam:"+dslams[i].getIp()+" oid:"+oid.getName());
								TreeResponseListener treelistener=new TreeResponseListener(dslams[i],dslams[i].getToDslamOidTables(oid.getOid().toString()));
								treelisteners.add(treelistener);
								logger.debug("Sending SNMP request to dslam:"+dslams[i].getIp()+" oid:"+oid.getName());
								TreeUtils treeUtils = new TreeUtils(snmp, new MyDefaultPDUFactory(PDU.GETBULK, contextEngineId));
								treeUtils.setMaxRepetitions(oid.getGetBulkSize());
								treeUtils.getSubtree(targets[i], oid.getOid(), i, treelistener);
								haveError = true;
								oiderror = true;
							} else {
								logger.debug("Max retry count is reached for dslam:"+dslams[i].getIp()+" oid:"+oid.getName()+
										" retrycount:"+dslams[i].getToDslamOidTables(oid.getOid().toString()).getRetrycount());	
							}
						}


					}
					if (oiderror) {
						logger.debug("Waiting at least "+oid.getAvgresponsetime()+" miliseconds for the requesst");
						TotalOidAvgResponseTime = TotalOidAvgResponseTime + oid.getAvgresponsetime();
						Thread.sleep(oid.getAvgresponsetime());
					}
				}
				if (!haveError) {
					logger.debug("We do not have any oids to retry or we do not have any error at all");	
					errorCheckFinished = true;
				} else {
					boolean finished = false;
					while (!finished) {
						boolean treelistenersFinished = true;
						for (TreeResponseListener treelistener : treelisteners) {
							logger.debug("is treelistenersFinished?:"+treelistenersFinished);
							logger.debug("Checking snmp listener for dslam:"+treelistener.getDslam().getIp()+" oid:"+treelistener.getOidtable().getOid().getName()+
									" finished?:"+treelistener.isFinished() +
									" is treelistenersFinished?:"+treelistenersFinished);
							long now = Instant.now().getEpochSecond();
							long runtime = now-treelistener.getStartTime();
							if (!treelistener.isFinished()) {
								if (runtime > (treelistener.getOidtable().getOid().getAvgresponsetime()*2/1000)) {
									treelistener.stopWalk();
									logger.error("Long runtime:"+ runtime + " for treelistener of dslam:"+treelistener.getDslam().getIp()+" for oid:"+
											treelistener.getOidtable().getOid().getName()+
											" error:"+treelistener.getOidtable().getError() );
								}
							}
							if (treelistenersFinished == true && treelistener.isFinished()) {
								treelistenersFinished = true;
							} else {
								treelistenersFinished = treelistener.isFinished();
							}
						}
						if (treelistenersFinished == true) {
							logger.debug("All snmp listners are finished, ending check");
							finished=treelistenersFinished;
						} else {
							logger.debug("All snmp listners are not FINISHED, will retry check");
							Thread.sleep(10000);
						}
					}

				}
			}
		}
		snmp.close();
		Timestamp EndTime = new Timestamp(System.currentTimeMillis());
		long runtime = EndTime.getTime()- StarTime.getTime();
		logger.info("Endtime, timestamp:"+EndTime.toString()+" total runtime:"+runtime + " total wait time for oids:"+TotalOidAvgResponseTime);




		for (int i = 0; i < dslams.length; i++) {
			boolean logerror = false;
			String summary="Summary for DSLAM:"+dslams[i].getIp();
			Hashtable<String, OidTable> oidtables = dslams[i].getDslamOidTables();
			for (String tableoid : oidtables.keySet()) {
				OidTable oidtable=oidtables.get(tableoid);
				if (oidtable.getError()!=0) {
					logerror = true;
				}
				summary=summary+" oid:"+oidtable.getOid().getName()+" size:"+oidtable.getIndexToValue().size();

			}
			if (logerror) {
				logger.error(summary);
			} else {
				logger.info(summary);
			}

		}
		for (int i = 0; i < dslams.length; i++) {
			Hashtable<String, OidTable> oidtables = dslams[i].getDslamOidTables();
			for (String tableoid : oidtables.keySet()) {
				OidTable oidtable=oidtables.get(tableoid);
				for (String ifindex : oidtable.getIndexToValue().keySet() ) {
					logger.trace("dslam:"+dslams[i].getIp()+" table:"+oidtable.getOid().getName()+ " ifindex:"+ifindex+" value:"+oidtable.getIndexToValue().get(ifindex));
				}

			}

		}

		logger.debug("Start to save db process");
		Timestamp DBStartTime = new Timestamp(System.currentTimeMillis());
		logger.info("DBStartTime, timestamp:"+DBStartTime.toString());

		SaveToDatabase db = new SaveToDatabase();
		for (int i = 0; i < dslams.length; i++) {
			String sql = "INSERT INTO `FiXdsl`.`dsllines_temp` (username,date,month,day,msan,cardtype,card,port,ifindex,status,profile,"
					+ "maxdown,maxup,atuc,atur,snrd,snru,"
					+ "adslAtucPerfCurr1DayLols,adslAtucPerfCurr1DayInits,adslAtucPerfPrev1DayLoss,"
					+ "adslAtucPerfPrev1DayInits,AtucPerfCurr1DayTimeElapsed_Loss,AtucPerfCurr1DayTimeElapsed_Inits,"
					+ "attenuationDS,attenuationUS,VDSLTransmissionMode) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? )";
			PreparedStatement ps = db.getConnection().prepareStatement(sql);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String date = sdf.format(new Date());

			int month;
			GregorianCalendar cdate = new GregorianCalendar();      
			month = cdate.get(Calendar.MONTH);
			month = month+1;
			int day = cdate.get(Calendar.DATE);


			for (String ifindex : dslams[i].getDslamOidTables().get("1.3.6.1.2.1.31.1.1.1.1").getIndexToValue().keySet() ) {

				String cardtype = null;
				String[] portdesc = null;
				String card=null;
				String port=null;
				String username = null;
				String msan = null;
				String status = null;
				String profile = null; 
				String maxdown = null;
				String maxup = null;
				String attenuationDS = null;
				String attenuationUS = null;
				String atuc = null;
				String atur = null;
				String snrd = null;
				String snru = null;

				String adslAtucPerfCurr1DayLols = null;
				String adslAtucPerfCurr1DayInits = null;

				String AtucPerfCurr1DayTimeElapsed_Loss = null;
				String AtucPerfCurr1DayTimeElapsed_Inits = null;

				String AtucPerfPrev1DayInits = null;
				String AtucPerfPrev1DayLoss = null;

				String TransmissionMode = null;




				String[] if_descriptions = dslams[i].getDslamOidTables().get("1.3.6.1.2.1.31.1.1.1.1").getIndexToValue().get(ifindex).split("_");
				cardtype=if_descriptions[0];
				if (cardtype.contentEquals("vdsl")) {
					try {
						portdesc = if_descriptions[1].split("/");
						card=portdesc[1];
						port=portdesc[2];
						username = dslams[i].getDslamOidTables().get("1.3.6.1.2.1.2.2.1.2").getIndexToValue().get(ifindex);
						msan = dslams[i].getIp();
						status = dslams[i].getDslamOidTables().get("1.3.6.1.2.1.10.251.1.1.1.1.14").getIndexToValue().get(ifindex); 
						profile = dslams[i].getDslamOidTables().get("1.3.6.1.4.1.3902.1082.130.2.2.1.2.1.9").getIndexToValue().get(ifindex); 

						maxdown = dslams[i].getDslamOidTables().get("1.3.6.1.2.1.10.251.1.1.1.1.20").getIndexToValue().get(ifindex); 
						maxup = dslams[i].getDslamOidTables().get("1.3.6.1.2.1.10.251.1.1.1.1.21").getIndexToValue().get(ifindex);

						attenuationDS = dslams[i].getDslamOidTables().get("1.3.6.1.2.1.10.251.1.1.2.1.2").getIndexToValue().get(ifindex+".1");
						attenuationUS = dslams[i].getDslamOidTables().get("1.3.6.1.2.1.10.251.1.1.2.1.2").getIndexToValue().get(ifindex+".2");

						atuc = dslams[i].getDslamOidTables().get("1.3.6.1.2.1.10.251.1.2.2.1.2").getIndexToValue().get(ifindex+".1");
						atur = dslams[i].getDslamOidTables().get("1.3.6.1.2.1.10.251.1.2.2.1.2").getIndexToValue().get(ifindex+".2");

						snrd = dslams[i].getDslamOidTables().get("1.3.6.1.2.1.10.251.1.1.2.1.4").getIndexToValue().get(ifindex+".1");
						snru = dslams[i].getDslamOidTables().get("1.3.6.1.2.1.10.251.1.1.2.1.4").getIndexToValue().get(ifindex+".2");

						adslAtucPerfCurr1DayLols=dslams[i].getDslamOidTables().get("1.3.6.1.2.1.10.251.1.4.1.1.1.16").getIndexToValue().get(ifindex);
						adslAtucPerfCurr1DayInits=dslams[i].getDslamOidTables().get("1.3.6.1.2.1.10.251.1.4.1.2.1.11").getIndexToValue().get(ifindex);

						AtucPerfCurr1DayTimeElapsed_Loss=dslams[i].getDslamOidTables().get("1.3.6.1.2.1.10.251.1.4.1.1.1.12").getIndexToValue().get(ifindex);
						AtucPerfCurr1DayTimeElapsed_Inits=dslams[i].getDslamOidTables().get("1.3.6.1.2.1.10.251.1.4.1.2.1.10").getIndexToValue().get(ifindex);

						AtucPerfPrev1DayInits=dslams[i].getDslamOidTables().get("1.3.6.1.2.1.10.251.1.4.1.6.1.3").getIndexToValue().get(ifindex);
						AtucPerfPrev1DayLoss=dslams[i].getDslamOidTables().get("1.3.6.1.2.1.10.251.1.4.1.4.1.7").getIndexToValue().get(ifindex);
						TransmissionMode=dslams[i].getDslamOidTables().get("1.3.6.1.2.1.10.251.1.1.1.1.13").getIndexToValue().get(ifindex);

					} catch (NullPointerException e){
						logger.warn("NullPointerException, means the table oid or the index do not ex"+e.toString());
					}
					logger.debug("msan:"+msan+" username:"+username+" status:"+status+" cardtype:"+cardtype+" card:"+card+" port:"+port+
							" profile:"+profile+" maxdown:"+maxdown+" maxup:"+maxup+" attenuationDS:"+attenuationDS+" attenuationUS:"+attenuationUS+
							" atuc:"+atuc+ " atur:"+atur+" snrd:"+snrd+" snru:"+snru+" adslAtucPerfCurr1DayLols:"+adslAtucPerfCurr1DayLols+
							" adslAtucPerfCurr1DayInits:"+adslAtucPerfCurr1DayInits+" AtucPerfCurr1DayTimeElapsed_Loss:"+AtucPerfCurr1DayTimeElapsed_Loss+
							" AtucPerfCurr1DayTimeElapsed_Inits:"+AtucPerfCurr1DayTimeElapsed_Inits+" AtucPerfPrev1DayInits:"+AtucPerfPrev1DayInits+
							" AtucPerfPrev1DayLoss:"+AtucPerfPrev1DayLoss);
					ps.setString(1, username);
					ps.setString(2, date);
					ps.setString(3, Integer.toString(month));
					ps.setString(4, Integer.toString(day));
					ps.setString(5, msan);
					ps.setString(6, cardtype);
					ps.setString(7, card);
					ps.setString(8, port);
					ps.setString(9, ifindex);
					ps.setString(10, status);
					ps.setString(11, profile);
					ps.setString(12, maxdown);
					ps.setString(13, maxup);
					ps.setString(14, atuc);
					ps.setString(15, atur);
					ps.setString(16, snrd);
					ps.setString(17, snru);
					ps.setString(18, adslAtucPerfCurr1DayLols);
					ps.setString(19, adslAtucPerfCurr1DayInits);
					ps.setString(20, AtucPerfPrev1DayLoss);
					ps.setString(21, AtucPerfPrev1DayInits);
					ps.setString(22, AtucPerfCurr1DayTimeElapsed_Loss);
					ps.setString(23, AtucPerfCurr1DayTimeElapsed_Inits);
					ps.setString(24, attenuationDS);
					ps.setString(25, attenuationUS);
					ps.setString(26, TransmissionMode);


					ps.addBatch();
					logger.debug(ps.toString());
				} else {
					logger.trace("uninterested cartype:"+cardtype);
					continue;
				}

			}
			int[] count = db.batchUpdate(ps);
			if (count != null ) logger.info(count.length+" number of records are inserted into db");
		}

		db.CloseConnection();
		logger.debug("Start to save db process");
		Timestamp DBEndTime = new Timestamp(System.currentTimeMillis());
		long dbruntime = DBEndTime.getTime()- DBStartTime.getTime();
		logger.info("DBEndTime, timestamp:"+DBEndTime.toString()+" total runtime:"+dbruntime);
	}
}

package collect.info;

import org.snmp4j.PDU;
import org.snmp4j.Target;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OctetString;
import org.snmp4j.util.DefaultPDUFactory;

class MyDefaultPDUFactory extends DefaultPDUFactory {  
    private OctetString contextEngineId = null;  
      
    public MyDefaultPDUFactory(int pduType, OctetString contextEngineId) {  
        super(pduType);  
        this.setContextEngineId(contextEngineId);  
    }  

    @Override  
    public PDU createPDU(Target target) {  
        PDU pdu = super.createPDU(target); 
        if (target.getVersion() == SnmpConstants.version3) {  
          //  ((ScopedPDU)pdu).setContextEngineID(contextEngineId);  bu alanı çıkardım sorun yaratıyor. Sanırım uygun formatta contextid yaratamıyor. asnyc calısma için gerekli.

        }  
        return pdu;  
    }

	public OctetString getContextEngineId() {
		return contextEngineId;
	}

	public void setContextEngineId(OctetString contextEngineId) {
		this.contextEngineId = contextEngineId;
	}         
}

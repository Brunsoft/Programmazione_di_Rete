
import server.central.*;
import server.login.*;
import client.bootstrap.*;
import client.login.*;
import client.mobileA.*;
import client.mobileS.*;
import lib.*;

import java.io.*;
import java.net.*;
import java.rmi.*;
import java.rmi.activation.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.Context;


public final class Setup {
	
	public static void main(String args[]) {

		System.out.println("");
		System.out.println(" Inizia la procedura main di Setup");

		// INIZIALIZZAZIONI
		String policyGroup = System.getProperty("multe.policy");
		String implCodebase = System.getProperty("multe.impl.codebase");
		String classeserver = System.getProperty("multe.classeserver");
		String leasePeriod = System.getProperty("java.rmi.dgc.leaseValue");
		
		// LANCIO IL SECURITY MANAGER
		System.setSecurityManager(new RMISecurityManager());
		try {
			Properties prop = new Properties();
			prop.put("java.security.policy", policyGroup);
			prop.put("multe.impl.codebase", implCodebase);
			prop.put("java.class.path", "no_classpath");
			prop.put("java.security.policy", "/home/luca/javarmi/multe/policy/policy");
			prop.put("javax.net.ssl.keyStore", "/home/luca/javarmi/multe/cert/Server_Keystore");
			prop.put("javax.net.ssl.keyStorePassword", "servermulte");
			prop.put("javax.net.ssl.trustStore", "/home/luca/javarmi/multe/cert/Server_Truststore");
			prop.put("javax.net.ssl.trustStorePassword", "servermulte");
			System.out.println(" -Creo il gruppo di attivazione.");

			// FASE 1: CREAZIONE DEL GRUPPO DI ATTIVAZIONE
			ActivationGroupDesc groupDesc = new ActivationGroupDesc(prop,null);
			System.out.println(" -Gruppo Attivazione Creato");

			// FASE 2: REGISTRAZIONE DEL GRUPPO DI ATTIVAZIONE
			ActivationGroupID groupID = ActivationGroup.getSystem().registerGroup(groupDesc);
			System.out.println(" -Gruppo Attivazione registrato col S.A., Identificativo: "+groupID);

			// FASE 3: CREAZIONE DELL'ACTIVATION DESCRIPTOR ASSOCIATO AL SERVER
			ActivationDesc actDesc = new ActivationDesc(groupID, classeserver, implCodebase, null);

			// FASE 4: REGISTRAZIONE DEL SERVER ATTIVABILE COL SISTEMA D'ATTIVAZIONE
			ICentralServer stub_server = (ICentralServer)Activatable.register(actDesc);

			System.out.println(" -Activation Descriptor Creato, registrato col demone d'attivazione");
			System.out.println(" -Accesso al Server Attivabile tramite lo stub: "+stub_server);
			System.out.println(" -RemoteRef dello stub è a null");

			// FASE 5: BINDING DEL SERVER ATTIVABILE SUL REGISTRO RMI
			System.out.println(" -Binding stub server attivabile nel registro RMI alla porta 1098\n  dove gia' si trova registrato il sistema di attivazione ");
			
			Naming.rebind("//:1098/CentralServer", stub_server);
			System.out.println(" -E' terminato il Setup");
		}
		catch (Exception e){
			e.printStackTrace();
			System.out.println(e);
		}
	}
}

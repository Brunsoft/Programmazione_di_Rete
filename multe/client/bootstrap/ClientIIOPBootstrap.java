package client.bootstrap;

import java.rmi.server.RMIClassLoader;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

public class ClientIIOPBootstrap {

	static final String serverIP = System.getenv("SERVERIP");
	static final String codebase = "http://"+serverIP+":8000/multe/";
	static final String clientClass = "client.login.AutenticatoreIIOP";
	
	public static void main(String[] args) throws Exception {
	
		System.out.println(" - Sono il Minimale Client IIOP.");
		System.setSecurityManager(new RMISecurityManager());
		try{
			Class<?> classClient = RMIClassLoader.loadClass(codebase, clientClass);
			System.out.println(" - Ho recuperato il client di autenticazione. Ora lo eseguo.");
			Runnable client = (Runnable) classClient.newInstance();
			client.run();	
		}catch(Exception e){
			System.out.println(" - Impossibile caricare la classe! Codebase offline\n   Riprovare più tardi..");
		}
     	}
}

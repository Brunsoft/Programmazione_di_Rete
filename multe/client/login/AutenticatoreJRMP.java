package client.login;

import server.login.ILogin;
import server.central.ICAutovelox;
import server.central.ICCaserma;
import client.mobileA.Autovelox;
import client.mobileS.Caserma;
import server.login.ILAutovelox;

import java.io.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.RMISecurityManager;
import javax.rmi.PortableRemoteObject;
import javax.naming.*;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.Serializable;
import java.rmi.MarshalledObject;

public class AutenticatoreJRMP implements Runnable {

	private String serverIP;
	private String localIP;
	private String codebase;
	private ILogin objL = null;
	private ILAutovelox objLA = null;
	private ICAutovelox objA = null;
	private Object client = null;
	private Object objRef = null;

	public void run() {

		try {
			System.out.println(" - Sono nel codice di AutenticatoreJRMP.");
			System.setSecurityManager(new RMISecurityManager());
			serverIP = System.getenv("SERVERIP");
			localIP = System.getenv("LOCALIP");
			System.out.println(" - IP SERVER: "+serverIP+"\n - IP LOCALE: "+localIP);
			codebase = "http://" + serverIP + ":8000/multe/";

			Properties pr = new Properties();
			pr.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.rmi.registry.RegistryContextFactory");
			pr.put(Context.PROVIDER_URL, "rmi://" + serverIP + ":2222");
			InitialContext ic = new InitialContext(pr);

			objRef = ic.lookup("LoginServer");
			System.out.println(" - Ho recuperato lo stub dell'LoginServer.");
			objL = (ILogin) PortableRemoteObject.narrow(objRef, ILogin.class);

		} catch (NamingException e) {
			System.out.println(" - Problema con Naming.");
		}
		try{
			String user, pwd;
			boolean a = false;
			boolean b = false;
			MarshalledObject<Object> mo = null;
			do{
				if(a){
					System.out.println();
					System.out.println("\033[31m"+" Autenticazione fallita!"+" \033[0m");
					if(!b)
						System.out.println("\033[31m"+" Utente già autenticato!"+" \033[0m");
					System.in.read();
				}
				clear();
				System.out.print("\033[1;34m"+" Username: "+" \033[0m");
				user = inserimento();
				System.out.println();
 				System.out.print("\033[1;34m"+" Password: "+" \033[0m");
				pwd = inserimento();
				a = true;
				if(!user.equals("luca")){
					if(objL.chekUser(user) == null)
						b = true;
					else
						b = false;
					mo = objL.login(user, pwd);
				}else
					System.out.println("\033[31m"+" Con questo Client puoi solo autenticarti come MobileAgent!"+" \033[0m");
	
			}while(mo==null || !b);
		
			Object client = mo.get();
			
			Properties pr1 = new Properties();
			pr1.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
			pr1.put(Context.PROVIDER_URL, "rmi://" + serverIP + ":1098");
			InitialContext ic1 = new InitialContext(pr1);
	
			Autovelox au = (Autovelox) client;
			objA = (ICAutovelox)ic1.lookup("CentralServer");		// Lookup CentralServer con Interfaccia Autovelox
			objLA = (ILAutovelox) PortableRemoteObject.narrow(objRef, ILAutovelox.class);
			au.runMobile(objA, user, objLA, localIP);		// Eseguo il MA

		}catch(RemoteException re){
			System.out.println(" - Server offline!\n   Riprovare più tardi..");
		}catch(Exception e){
			System.out.println(" - Server offline!\n   Riprovare più tardi..");
		}
	}
	private static String inserimento(){
		String i;
		String input = "";
		try{	
			while (!(i = ""+(char)System.in.read()).equals(""+(char)10))
           			input += i;
		}catch (Exception e) {
			System.out.println(" Client exception: " + e.getMessage());
			e.printStackTrace();
		}
		return input;
	}

	private void clear(){
		try{
			Runtime run = Runtime.getRuntime();
        		Process proc = run.exec(new String[]{"/bin/sh", "-c", "clear"});
        		proc.waitFor();
       			BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
       			while(br.ready())
            			System.out.println(br.readLine());
			System.out.println("---------------------------- CLIENT AUTENTICAZIONE ----------------------------");

		}catch (Exception e) {
			System.out.println(" Errore clear bash!");
		}
	}
}

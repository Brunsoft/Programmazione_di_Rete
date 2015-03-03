package server.login;

import client.mobileS.MobileServer;
import client.mobileA.Autovelox;
import client.mobileS.Caserma;
import server.login.ILAutovelox;
import server.login.ILCaserma;
import lib.*;

import java.rmi.*;
import java.rmi.server.*;
import java.util.*;
import javax.naming.*;
import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import java.rmi.server.UnicastRemoteObject;
import javax.rmi.PortableRemoteObject;
import java.rmi.MarshalledObject;
import java.lang.System.*;


public class LoginServer implements ILogin, ILAutovelox, ILCaserma, Unreferenced{
	
	private MobileServer stubMS = null;					// Stub MobileServer
	private ArrayList<Ref> refs;						// Lista Utenti attivi
	private Timer timer;
	private TimerTask task;
	private Calendar c;
	private static String ip;
	private static InitialContext RMIRegistry;
	private static InitialContext CosNaming;
	private boolean b = false;						 

	public LoginServer() throws RemoteException {
		UnicastRemoteObject.exportObject(this, 32000);			// Export JRMP
		PortableRemoteObject.exportObject(this);			// Export IIOP

		timer = new Timer();
		refs = new ArrayList<Ref>();
		task = new CountingReference();
		timer.schedule(task, 5000, 1000);
		c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.MINUTE, 2);	
	}
	
	// IMPLEMENTAZIONE DEI METODI REMOTI: getClient(), login(), ricerca()
	
	public MarshalledObject<Object> login(String user, String pwd) {				// METODO LOGIN PER GLI UTENTI
		FileReader fr = null;
		BufferedReader br;
		boolean b = false;
		char c=' ';
		try {	
			if(user.equals("luca") && stubMS != null)
				return null;

			fr = new FileReader("/home/luca/javarmi/multe/db/utenti");
			br = new BufferedReader(fr);
			String s;
			while ((s = br.readLine()) != null && !b) {
				StringTokenizer st = new StringTokenizer(s, ";");
				if (st.nextToken().equals(user) && st.nextToken().equals(pwd)) {
					b = true;
					c = st.nextToken().charAt(0);
				}
			}
			if (c == 'a')
				return new MarshalledObject<Object>(new Autovelox());
			if (c == 'c')
				return new MarshalledObject<Object>(new Caserma());
		}
		catch (IOException e)
		{
			System.out.println("Errore: " + e);
		}		
		return null;
				
	}

	private boolean ricerca(String user) {					// RICERCA UTENTE NEL DB
		FileReader fr=null;
		BufferedReader br;
		boolean trovato = false;
		try {
			fr = new FileReader("/home/luca/javarmi/multe/db/utenti");
			br = new BufferedReader(fr);

			String s;
			s = br.readLine();
			while (s != null) {
				StringTokenizer st = new StringTokenizer(s, ";");
				if(user.equals(st.nextToken()))
					trovato = true;
				s = br.readLine();
			}
		}
		catch (IOException e)
		{
			System.out.println("Errore: " + e);
		}
		return trovato;
	}

	// METODI DI GESTIONE REFERENZA MS
	
	public void resetMSRef() throws RemoteException {     			// RESET REFERENZA MOBILE SERVER
		clear();
		task.cancel();
		this.stubMS = null;
		System.out.println("\033[1;31m - Cancellazione referenza MobileServer\033[0m");
		System.out.println("\033[1;31m - Disconnessione Caserma\033[0m");
		task = new CountingReference();
		timer.schedule(task, 6000, 1000);
	}

	public void setMSRef(MarshalledObject<MobileServer> stubMS) throws RemoteException {     	// SET REFERENZA MOBILE SERVER
		clear();
		task.cancel();
		try{
			this.stubMS = stubMS.get();
		}
		catch(Exception e){}
		System.out.println("\033[1;32m - Salvataggio referenza MobileServer\033[0m");
		System.out.println(" - Recupero Informazioni sull'host utilizzando un suo metodo remoto: "+this.stubMS.ping());
		task = new CountingReference();
		timer.schedule(task, 6000, 1000);
	}
	
	public MobileServer getStubMS() throws RemoteException {
		return stubMS;
	}

	// METODI GESTIONE UTENTI

	public void addRef(Ref ref) throws RemoteException {			// AGGIUNGE UTENTE ALLA LISTA
		try {
			refs.add(ref);
			Calendar cal = Calendar.getInstance();
			System.out.println(" "+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.HOUR_OF_DAY)+"\033[1;32m Connessione di "+ref.getUser()+"\033[0m");
		} catch (Exception e) {}
	}

	public void removeRef(Ref ref) throws RemoteException {			// RIMUOVE UTENTE DALLA LISTA
		try {	
			Ref ref1 = null;
			for(int j = 0 ; j < refs.size() ; j++){		
				ref1 = refs.get(j);
				if (ref1.getUser().equals(ref.getUser())){
					refs.remove(ref1);
					Calendar cal = Calendar.getInstance();
					System.out.println(" "+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+"\033[1;31m Disconnessione di "+ref.getUser()+"\033[0m");
					return;
				}
			}
			
		} catch (Exception e) {}
		System.out.println("\033[1;31m Disconnessione di "+ref.getUser()+" non eseguita, ERRORE\033[0m");
	}

	public void setUserBlock(Ref user){					// BLOCCA UTENTE IN LISTA
		try {
			Ref ref = null;
			for(int j = 0 ; j < refs.size() ; j++){		
				ref = refs.get(j);
				if (user.equals(ref))
					ref.setBlock(true);
			}
			System.out.println("\033[1;31m"+" User: "+ref.getUser()+" bloccato dal MS"+" \033[0m");
		}catch (Exception e) {}
	}

	public void setUserUnblock(Ref user){					// SBLOCCA UTENTE IN LISTA
		try {
			Ref ref = null;
			for(int j = 0 ; j < refs.size() ; j++){		
				ref = refs.get(j);
				if (ref.equals(ref))
					ref.setBlock(false);
			}
			System.out.println("\033[1;32m"+" User: "+ref.getUser()+" sbloccato dal MS"+" \033[0m");
		}catch (Exception e) {}
	}

	public boolean chekUserBlock(Ref user){					// CONTROLLO UTENTE BLOCCATO
		try {
			Ref ref = null;
			for(int j = 0 ; j < refs.size() ; j++){		
				ref = refs.get(j);
				if ((ref.getUser()).equals(user.getUser())){
					if(!ref.getBlock())
						ref.setDate(user.getDate());
					return ref.getBlock();
				}
			}	
			return true;
		}catch (Exception e) {}
		return false;
	}

	public Ref chekUser(String user){					// RITORNA REF DELLA LISTA UTENTI, DATO UN NOME UTENTE
		try {
			Ref ref = null;
			for(int j = 0 ; j < refs.size() ; j++){		
				ref = refs.get(j);
				if ((ref.getUser()).equals(user))
					return ref;
			}
		}catch (Exception e) {}
		return null;
	}

	public ArrayList<Ref> getRefs(){
		return refs;
	}
	
	// METODI DI SISTEMA

	private class CountingReference extends TimerTask{
		public void run(){
			Ref ref = null;
			Date currentDate = new Date();
			Date userDate;
			Calendar c1 = Calendar.getInstance();
			c1.setTime(currentDate);
			Calendar c2 = Calendar.getInstance();
			clear();
			Calendar cal = Calendar.getInstance();

			for(int i = 0 ; i < refs.size() ; i++){
				ref = refs.get(i);
				userDate = ref.getDate();
				c2.setTime(userDate);
				c2.add(Calendar.MINUTE, 5);
				
				if (c2.before(c1)) {
					System.out.println("\n Utente: "+ref.getUser()+"\t \033[1;31m Espulso per inattività!\033[0m");
					refs.remove(ref);
					i--;
				}else{
					System.out.println("\n Utente: "+ref.getUser()+"\t Ultimo accesso ore: "+cal.get(Calendar.HOUR_OF_DAY)+
					":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND));
				}
				c.setTime(new Date());
				c.add(Calendar.MINUTE, 2);		
			}

			try{
				System.out.println("-------------------------------------------------------------------------------");
				System.out.println(" - Prossimo tentativo di spegnimento LoginServer alle ore " 
								+c.get(Calendar.HOUR_OF_DAY)+":"
								+c.get(Calendar.MINUTE)+":"
								+c.get(Calendar.SECOND));

				System.out.println("\n Ore: "	+cal.get(Calendar.HOUR_OF_DAY)+":"
								+cal.get(Calendar.MINUTE)+":"
								+cal.get(Calendar.SECOND)+"\n - Autovelox connessi: "
								+refs.size());
				try{
					stubMS.ping();
					System.out.println(" - Caserma ONLINE");
				}catch (Exception e){
					System.out.println(" - Caserma OFFLINE");
				}
			
			}catch (Exception e){}

			if(c.before(c1))		// Se per 5 minuti non ci sono utenti attivi richiamo Unreferenced()
				unreferenced();
		}
	}

	private void clear(){				// PULISCE BASH -> Ctrl + L
		try{
			Runtime run = Runtime.getRuntime();
        		Process proc = run.exec(new String[]{"/bin/sh", "-c", "clear"});
        		proc.waitFor();
       			BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
       			while(br.ready())
            			System.out.println(br.readLine());
			System.out.println("-------------------------------- LOGIN SERVER ---------------------------------");

		}catch (Exception e) {
			System.out.println(" Errore clear bash!");
		}
	}

	public static void main(String[] args) {
		try {
			ip=args[0];
			System.setProperty("java.rmi.dgc.leaseValue", "120000");
			System.setProperty("java.rmi.server.hostname", ip);
				
			LoginServer svr = new LoginServer();

			//BINDING SUL REGISTRO RMI
			Properties prop1 = new Properties();
			prop1.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
			prop1.put(Context.PROVIDER_URL, "rmi://"+ip+":2222");
			RMIRegistry = new InitialContext(prop1);

			RMIRegistry.rebind("LoginServer", svr);
			System.out.println("\n Fatta la bind sul registro RMI alla porta 2222");

			// BINDING SUL COSNaming
			Properties prop2 = new Properties();
			prop2.put("java.naming.factory.initial", "com.sun.jndi.cosnaming.CNCtxFactory");
			prop2.put("java.naming.provider.url", "iiop://"+ip+":5555");
			CosNaming = new InitialContext(prop2);

			CosNaming.rebind("LoginServer", svr);
			System.out.println(" Fatta bind col CosNaming alla porta 5555");
		}
		catch (Exception e) {
			System.out.println(" - Errore di esportazione LoginServer");
		}
	}

	@Override
	public void unreferenced(){				// Invoca la GC quando non ci son più utenti JRMP (Autovelox) online
		
		try{
			if(stubMS != null)
				b = stubMS.TimeOutLoginServer();
			
			if(b){
				clear();
				System.out.println("\033[1;36m----------------------------- METODO UNREFERENCED  -----------------------------\033[0m");
				task.cancel();
				System.out.println(" - Timeout Gestore Utenti");

				RMIRegistry.unbind("LoginServer");
				System.out.println(" - Eseguita unbind sul RmiRegistry alla porta 2222");

				CosNaming.unbind("LoginServer");
				System.out.println(" - Eseguita unbind sul CosNaming alla porta 5555");

				UnicastRemoteObject.unexportObject(this,true);
				System.out.println(" - UnExport URO eseguita!");

				PortableRemoteObject.unexportObject(this);
				System.out.println(" - UnExport PRO eseguita!");

				System.gc();
				System.out.println(" - System.gc() invocata\n");
			}else{
				c.setTime(new Date());
				c.add(Calendar.MINUTE, 2);
			}
				
		}catch (Exception e) {
			System.out.println("Errore: " + e);
		}

	}

}

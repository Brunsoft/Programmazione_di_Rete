package client.mobileS;

import server.central.ICCaserma;
import server.login.ILCaserma;
import lib.*;

import java.io.*;
import javax.naming.*;
import java.rmi.RemoteException;
import java.rmi.RMISecurityManager;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.Serializable;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.UnknownHostException;
import java.net.InetAddress; 
import javax.swing.JOptionPane;
import javax.swing.*;
import java.awt.BorderLayout;
import javax.swing.JComponent.*;
import java.util.Timer;
import java.awt.Dimension;
import javax.imageio.ImageIO;
import javax.swing.UIManager;
import java.awt.image.BufferedImage;
import javax.rmi.PortableRemoteObject;
import java.rmi.MarshalledObject;


public class Caserma extends JFrame implements MobileServer, Serializable {

	static final long serialVersionUID = 42L;
		
	private ICCaserma CentralServer = null;
	private ILCaserma LoginServer = null;
	private String user = "";
	private MobileServer stubMS = null;
	private Ref ref = null;
	private JLabel label;
	private JPanel pnl;
	private Timer timer;
	private TimerTask task;
	private String serverIP;
	private String localIP;
	private boolean exit = false;

	public Caserma() throws RemoteException {}

	public void runMobile(ICCaserma CentralServer, ILCaserma LoginServer, String user) throws RemoteException {
		
		this.CentralServer = CentralServer;
		this.LoginServer = LoginServer;
		this.user = user;
		try{
			serverIP = System.getenv("SERVERIP");
			localIP = System.getenv("LOCALIP");
			System.setSecurityManager(new RMISecurityManager());
			System.setProperty("java.rmi.server.hostname", localIP);
		
			stubMS = (MobileServer)UnicastRemoteObject.exportObject(this, 37000);
			ref = new Ref(user, localIP, new Date(), false);
			
			// Lookup Login Server IIOP
			LoginServer.setMSRef(new MarshalledObject<MobileServer>(this));		// Invio lo stub al LoginServer

			CentralServer.printInfo("\033[1;32m - Connessione MobileServer \033[0m"+user);	// Invoco CentralServer

		}catch(Exception e){
			e.printStackTrace();
    			System.out.println("Errore.");
    			System.exit(0);
		}
			
		try {
			String i="";
			// Create and install a security manager
			if (System.getSecurityManager() == null)
				System.setSecurityManager(new RMISecurityManager());

			label = new JLabel("Pannello Infrazioni gravi in diretta.");

			JLabel pic = new JLabel(UIManager.getIcon("OptionPane.warningIcon"));
        		pnl = (JPanel) getContentPane();
			pnl.add(pic, BorderLayout.WEST);
       			pnl.add(label, BorderLayout.EAST);
			pnl.setPreferredSize(new Dimension(450,50));
        		pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			
        		setTitle("Caserma");
        		setLocationRelativeTo(null);
			setUndecorated(true);
			setLocation(0, 0);
			pack();
			timer = new Timer();
			task = new hidePanel();
			timer.schedule(task, 3000, 3000);


			try {
				int op = 0;
				while(!exit){
					clear();
					System.out.println("------------------------------------------------------------------------");
					System.out.println("");
					System.out.println("\033[1;34m 1) Ricercare Multe ATTIVE");
					System.out.println(" 2) Ricercare Multa nello STORICO");
					System.out.println(" 3) Paga Multa");
					System.out.println(" 4) Gestione Utenti");
					System.out.println(" e) Uscita \033[0m");
					System.out.println("");
					System.out.print("\033[1;37m"+" Scelta: "+" \033[0m");
					String line;
       					i = ""+(System.in.read()-48);
					System.in.read();
					clear();
					if(i.equals("1")){

						multeAttive();

					}if(i.equals("2")){
						
						multeStorico();
				
					}if(i.equals("3")){
						
						pagaMulta();

					}if(i.equals("4")){
						 
						gestUtenti();

					}if(i.equals("53")){
						clear();
						exit = true;
					}
				
				}
				System.out.println("");
				System.out.println("\033[1;31m"+" Termine Caserma"+" \033[0m");
				try{	
					LoginServer.resetMSRef();
				}
				catch(RemoteException re){
					System.out.println("\033[1;31m"+" LoginServer Offline!"+" \033[0m");
				}
				try{	
					CentralServer.printInfo("\033[1;31m - Disconnessione MobileServer \033[0m"+user);		// Invoco CentralServer
				}
				catch(RemoteException re){
					System.out.println("\033[1;31m"+" CentralServer Offline!"+" \033[0m");
				}
			
				timer.cancel();
				UnicastRemoteObject.unexportObject(this,true);
				System.in.read();
			} catch (Exception e) {}
		}
		catch (Exception ex) {
			System.out.println(ex.getMessage()); 
		}
		System.exit(0);
	}

	private void multeAttive(){
		int op = 1;
		System.out.println("\033[1;36m--------------------- Sezione RICERCA MULTA ATTIVA ---------------------\033[0m");
		System.out.println("");
		System.out.print("\033[1;37m Inserisci termine di ricerca: \033[0m");
		String i="";
		String input = inserimento();
		while(!(input.equals("..")) && !(input.equals("p"))){
			clear();
			System.out.println("\033[1;36m--------------------- Sezione RICERCA MULTA ATTIVA ---------------------\033[0m");
			System.out.println("");
			ArrayList<Multa> a = new ArrayList<Multa>();
			try{
				a = CentralServer.ricerca(input, op);
				if (a.isEmpty() )
					System.out.println("\033[1;31m"+" Nessun risultato per: \033[0m"+ input);
				for(Multa m:a) {
         				System.out.println(" "+m.toString());
       				} 
			}catch(RemoteException e){
				System.out.println(" - CentralServer Offline!");
				exit = true;
			}
			System.out.println("");
			System.out.println("\033[1;34m"+" -> Inserisci nuovo parametro di ricerca"+" \033[0m");
			System.out.println("\033[1;34m"+" -> p  per pagare una multa"+" \033[0m");
			System.out.println("\033[1;34m"+" -> .. per tornare al Menu"+" \033[0m"); 
			System.out.println("");
			System.out.print("\033[1;37m"+" Scelta: "+" \033[0m");
			input = inserimento();
		}
		if((input.equals("p")))
			pagaMulta();
	}

	private void multeStorico(){
		int op = 2;
		System.out.println("\033[1;36m--------------------- Sezione RICERCA MULTA STORICO --------------------\033[0m");
		System.out.println("");	
		System.out.print("\033[1;37m Inserisci termine di ricerca: \033[0m");
		String i="";
		String input = inserimento();
		ArrayList<Multa> a = new ArrayList<Multa>();
		while(!(input.equals(".."))){
			clear();
			System.out.println("\033[1;36m--------------------- Sezione RICERCA MULTA STORICO --------------------\033[0m");
			System.out.println("");
			try{
				a = CentralServer.ricerca(input, op);
			
				if (a.isEmpty() )
					System.out.println("\033[1;31m"+" Nessun risultato per: \033[0m"+ input);
				for(Multa m:a) {
            				System.out.println(" "+m.toString());
        			} 
			}catch(RemoteException e){
				System.out.println(" - CentralServer Offline!");
				exit = true;
			}
			System.out.println("");
			System.out.println("\033[1;34m"+" -> Inserisci nuovo parametro di ricerca"+" \033[0m");
			System.out.println("\033[1;34m"+" -> .. per tornare al Menu"+" \033[0m"); 
			System.out.println("");
			System.out.print("\033[1;37m"+" Scelta: "+" \033[0m");
			input = inserimento();
		}
	}	
	
	private void pagaMulta(){
		System.out.println("");
		System.out.println("\033[36m--------------------- Sezione PAGAMENTO INFRAZIONE ---------------------\033[0m");
		System.out.println("");
		System.out.print("\033[37m Inserisci targa:  \033[0m");
		String i="";
		String input = inserimento();
		ArrayList<Multa> a = new ArrayList<Multa>();
		try{
			a = CentralServer.ricerca(input, 1);

			int c=1;
			System.out.println("");	
			if (a.isEmpty() )
				System.out.println("\033[1;31m"+" Nessun risultato per: \033[0m"+ input);				
			for(Multa m:a) {
            			System.out.println("\033[1;37m Infrazione num: "+c+" \033[0m"+m.toString());
				c++;
        		} 
		
			System.out.println("");
			System.out.print("\033[1;37m Inserisci num infrazione \033[0m");
			int n = Integer.parseInt(inserimento());
			boolean b = CentralServer.pagaMulta(input, n);
			if (b){
				CentralServer.printInfo(" - Multa di "+input+" num. "+n+" pagata con successo!");	
				System.out.println("\033[1;32m"+" Multa pagata con successo!"+" \033[0m");
			}else
				System.out.println("\033[1;31m"+" Errore durante il processo di pagamento!"+" \033[0m");
			System.in.read();
		}catch(RemoteException e){
			System.out.println(" - CentralServer Offline!");
			exit = true;
		}catch(Exception e){}
	}

	private void gestUtenti(){
		boolean b=true;
		while(b){
			repaint();
			System.out.println("\033[1;36m----------------------- Sezione GESTIONE UTENTI ------------------------\033[0m");
			System.out.println("");
			try{
				ArrayList<Ref> refs = LoginServer.getRefs();
				Ref ref = null;
				for(int j = 0 ; j < refs.size() ; j++){		
					ref = refs.get(j);
					if(ref.getBlock())
						System.out.println(" ---\033[1;31m Username: "+ref.getUser()+"\tHost IP :"+ref.getHost()+" \033[0m");
					else
						System.out.println(" ---\033[1;32m Username: "+ref.getUser()+"\tHost IP :"+ref.getHost()+" \033[0m");
				}
			}catch(Exception e){
				System.out.println(" - LoginServer Offline!");
			}
		
			System.out.println("");
			System.out.println("\033[1;34m"+" -> b Per BLOCCARE un utente."+" \033[0m");
			System.out.println("\033[1;34m"+" -> s Per SBLOCCARE un utente."+" \033[0m");
			System.out.println("\033[1;34m"+" -> k Per KILLARE un utente."+" \033[0m");
			System.out.println("\033[1;34m"+" -> .. per tornare al Menu"+" \033[0m"); 
			System.out.println("");
			System.out.print("\033[1;37m"+" Scelta: "+" \033[0m");
			String input = inserimento();
			if(input.equals("b")){
				bloccaUtente();
			}else 
			if(input.equals("s")){
				sbloccaUtente();
			}else
			if(input.equals("k")){
				killUtente();
			}else
			if(input.equals(".."))
				b=false;
			clear();
		}
	}

	private void bloccaUtente(){		
		System.out.println("");
		System.out.println("\033[1;36m------------------------ Sezione BLOCCA UTENTE -------------------------\033[0m");
		System.out.println("");
		System.out.print(" Chi vuoi bloccare? ");
		String input = inserimento();
		try{
			Ref user = LoginServer.chekUser(input);
			if(user != null)
				if(!(ref.getUser()).equals(user.getUser())){
					LoginServer.setUserBlock(user);
					CentralServer.printInfo(" - Utente "+user.getUser()+"\033[1;31m bloccato\033[0m dal MS");
				}else{
					System.out.println("Non puoi bloccare te stesso");
					System.in.read();
			}else
				System.out.println("Nessun utente corrispondente");
		}catch(RemoteException e){
			System.out.println(" - LoginServer Offline!");
			System.out.println(" - ERRORE BLOCCO UTENTE");
		}catch(Exception e){}
	}

	private void sbloccaUtente(){		
		System.out.println("");
		System.out.println("\033[1;36m------------------------ Sezione SBLOCCA UTENTE ------------------------\033[0m");
		System.out.println("");
		System.out.print(" Chi vuoi sbloccare? ");
		String input = inserimento();
		try{
			Ref user = LoginServer.chekUser(input);
			if(user != null)
				if(!(ref.getUser()).equals(user.getUser())){
					LoginServer.setUserUnblock(user);
					CentralServer.printInfo(" - Utente "+user.getUser()+"\033[1;32m sbloccato\033[0m dal MS");
				}else{
					System.out.println("Non puoi sbloccare te stesso");
					System.in.read();
			}else
				System.out.print("Nessun utente corrispondente");
		}catch(RemoteException e){
			System.out.println(" - CentralServer Offline!");
			System.out.println(" - ERRORE SBLOCCO UTENTE");	
		}catch(Exception e){}
	}

	private void killUtente(){		
		System.out.println("");
		System.out.println("\033[1;36m------------------------- Sezione KILL UTENTE --------------------------\033[0m");
		System.out.println("");
		System.out.print(" Chi vuoi killare? ");
		String input = inserimento();
		try{
			Ref user = LoginServer.chekUser(input);
			if(user != null){
				System.out.print(" Sicuro di voler killare "+user.getUser()+" s/n \t");
				input = inserimento();
				if (input.equals("s"))
					if(!(ref.getUser()).equals(user.getUser())){
						LoginServer.removeRef(user);
						CentralServer.printInfo(" - Utente "+user.getUser()+"\033[1;31m espulso \033[0mdal MS");
					}else{
						System.out.println("Non puoi killare te stesso");
						System.in.read();
			}else
					System.out.println(" Operazione anullata!");
			}else
				System.out.print("Nessun utente corrispondente");
		}catch(RemoteException e){
			System.out.println(" - CentralServer Offline!");
			System.out.println(" - ERRORE SBLOCCO UTENTE");	
		}catch(Exception e){}
	}
	
	public String ping(){

		String Info = "";
		String thisIp = null;

		thisIp = localIP;
		Info += "\n\n --- Username:\t\t"+System.getProperty("user.name")+"\n";
		Info += " --- Sistema Operativo:\t"+System.getProperty("os.name")+" "+System.getProperty("os.version")+"\n";
		Info += " --- Hostname/IP:\t"+thisIp+"\n";
		Info += " --- Paese:\t\t"+System.getProperty("user.country")+"\n\n";
		return Info;
	}
	
	public void notificaMulta (Multa m, Ref ref) {
		label.setText("Multa grave "+m.getTarga()+" | "+m.getVelocita()+" Km/h "+" | Eseguita da: "+ref.getUser());
		repaint();
		task.cancel();
		task = new hidePanel();
		timer.schedule(task, 3000, 3000);
		setVisible(true);
	}
		
	public boolean TimeOutLoginServer(){
		int n = JOptionPane.showConfirmDialog(null,
			"Il LoginServer è entrato nel metodo Unreferenced(), sta tentando di spegnersi, intendi proseguire?",
			"Spegnimento Login Server",JOptionPane.YES_NO_OPTION);
		try{
			if (n == 1 || n == -1){
				CentralServer.printInfo("\033[1;33m - Unreferenced LoginServer posticipata di 5 min.\033[0m");
				return false;
			}else{
				int m = JOptionPane.showConfirmDialog(null,
					"Il LoginServer sarà spento, perciò il ServerCentrale non sarà più raggiungibile dagli Autovelox.\n"+
					" Intendi spegnere anche il Server Centrale?",
					"Spegnimento Central Server",JOptionPane.YES_NO_OPTION);
				if( m == JOptionPane.YES_OPTION)
					CentralServer.powerOff(true);

				CentralServer.printInfo("\033[1;33m - Unreferenced LoginServer in corso..\033[0m");
				return true;
			}
		}catch(RemoteException e){
			System.out.println(" - CentralServer Offline!");
		}
		return true;
	}

	private class hidePanel extends TimerTask{
		public void run(){
			setVisible(false);
			repaint();
		}
	}

	private static String inserimento(){
		String i;
		String input = "";
		try{	
			while (!(i = ""+(char)System.in.read()).equals(""+(char)10))
           			input += i;
		}catch (Exception e) {
			System.out.println(" Client exception: "+ e.getMessage());
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
			System.out.println("---------------------------- CLIENT CASERMA ----------------------------");
			System.out.println(" Utente: "+user+"\t\tIP: "+localIP);
			
		}catch (Exception e) {
			System.out.println(" - Errore clear bash!");
			e.printStackTrace();
		}
	}
}

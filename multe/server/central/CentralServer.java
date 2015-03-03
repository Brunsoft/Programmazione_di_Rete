package server.central;

import client.mobileS.*;
import server.login.*;
import lib.*;

import java.io.*;
import javax.naming.*;
import java.rmi.*;
import java.rmi.activation.*;
import java.rmi.server.*;
import java.util.*;
import javax.rmi.PortableRemoteObject;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import java.util.Timer;
import java.net.InetAddress;
import java.lang.Object.*;
import java.util.Timer;

public class CentralServer extends Activatable implements ICentralServer, ICAutovelox, ICCaserma, Unreferenced {
	
	static final long serialVersionUID = 42L;
	private boolean powerOff = false;

	public CentralServer(ActivationID id, MarshalledObject<Object> m) throws ActivationException, IOException, ClassNotFoundException, RemoteException {
		
		super(id, 35000, new SslRMIClientSocketFactory(), new SslRMIServerSocketFactory());

		System.out.println(" MainServer esportato.");
	}
	
	// IMPLEMENTAZIONE DEI METODI SERVER MULTE

	public void printInfo(String info){
		System.out.println(info);
	}

	public void scriviMulta (Multa m, Ref user) {

		try {
			int deltaV = m.getVelocita()-m.getLimite();
			System.out.println(" - Ricev. multa da "+user.getUser()+" Info: "+m.getTarga()+"  +"+deltaV+" km/h   "+m.getMarca());
			
          		FileOutputStream prova = new FileOutputStream("/home/luca/javarmi/multe/db/multe_attive", true);
          		PrintStream scrivi = new PrintStream(prova);
          		scrivi.println(m.toString1()+";"+user.getUser());
		
      		}catch (IOException e) {
         		System.out.println("Errore: " + e);
      		}
	}

	public ArrayList<Multa> ricerca (String parametro, int db) {
		System.out.println(" - Ricerca multa per parametro: "+parametro+" da Luca.");
		ArrayList<Multa> multe = new ArrayList<Multa>();
		FileReader f=null;
		BufferedReader b;
		try {
			if (db == 1)
				f = new FileReader("/home/luca/javarmi/multe/db/multe_attive");
			if (db == 2)
				f = new FileReader("/home/luca/javarmi/multe/db/storico_multe");
			
			b = new BufferedReader(f);
 
			String s;
			s = b.readLine();
			while (s != null) {
				if (s.toLowerCase().contains(parametro.toLowerCase())) {
					StringTokenizer st = new StringTokenizer(s, ";");
					Multa m = new Multa (st.nextToken(), st.nextToken(), st.nextToken(), 
						Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), st.nextToken(), st.nextToken());
					multe.add(m);
				}
				s = b.readLine();
			}
		} catch (IOException e) {
			System.out.println("Errore: " + e);
		}
		return multe;
	}

	public boolean pagaMulta (String targa, int n) {
		System.out.println(" - Pagamento multa per la targa: "+targa+" num. "+n+" in corso..");
		ArrayList<Multa> multe = new ArrayList<Multa>();
		multe = ricerca(targa, 1);
		int i = 1;
		for (Multa m : multe) {
			if (i==n && m.getTarga().equals(targa)) {
				m.paga();
				spostaMulta(m);
				//System.out.println("Multa "+m.toString());
				return true;
			}
			i++;
		}
		return false;
	}

	private void spostaMulta (Multa m) {

		FileReader fr=null;
		FileWriter fwa=null;
		FileWriter fws=null;
		BufferedReader br=null;
		BufferedWriter bwa=null;
		BufferedWriter bws=null;

		try {
			fr = new FileReader("/home/luca/javarmi/multe/db/multe_attive");
			br = new BufferedReader(fr);
			
			String s, sa="", ss="";
			while ((s = br.readLine()) != null) {
				if (s.contains(m.toString1())) {
					ss+=s+"\n";
				} else {
					sa+=s+"\n";
				}
			}
			fwa = new FileWriter("/home/luca/javarmi/multe/db/multe_attive");
			fws = new FileWriter("/home/luca/javarmi/multe/db/storico_multe", true);
			bwa = new BufferedWriter(fwa);
			bws = new BufferedWriter(fws);
			bwa.write(sa);
			bws.write(ss);
		} catch (IOException e) {
			System.out.println("Errore: " + e);
		}
	
		try {
			bwa.flush();
			bwa.close();
			bws.flush();
			bws.close();
		} catch (IOException e) {
			System.out.println("Errore: " + e);
		}
	}

	public void powerOff(boolean powerOff){
		this.powerOff = powerOff;
		System.out.println("\033[1;33m - N.B. Il server verrà de-registrato alla prossima unreferenced\033[0m");
	}

	// IMPLEMENTAZIONE DEL METODO UNREFERENCED
	@Override
	public void unreferenced(){
		try {
			System.out.println("\n\033[1;36m--------------------- METODO UNREFERENCED  ---------------------\033[0m");

			Calendar cal = Calendar.getInstance();
			System.out.println("\n Ore: "+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE));

			if(Activatable.unexportObject(this,true)){
				Activatable.inactive(getID());
				System.out.println(" - Il Server e' stato disattivato");
				if(powerOff){
					Activatable.unregister(getID());
					System.out.println(" - Il Server e' stato de-registrato, non si riattiverà!");
				}
			}

			System.gc();
			System.out.println(" - System.gc() invocata\n");
			

		} catch (Exception e) {
			System.out.println("unreferenced: "+e);
			System.out.close();
		}
	}
}

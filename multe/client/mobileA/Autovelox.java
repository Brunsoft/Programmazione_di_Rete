package client.mobileA;

import server.central.ICAutovelox;
import server.login.ILAutovelox;
import lib.*;
import client.mobileS.MobileServer;

import java.io.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.RMISecurityManager;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.Serializable;
import java.rmi.UnknownHostException;
import java.net.InetAddress;
import javax.swing.JOptionPane;
import javax.swing.*;
import java.awt.BorderLayout;
import javax.swing.JComponent.*;
import java.util.Timer.*;
import java.awt.Dimension;
import javax.imageio.ImageIO;
import javax.swing.UIManager;
import java.awt.image.BufferedImage;
import java.awt.GridLayout;
import java.awt.*;
import java.awt.Toolkit;



public class Autovelox extends JFrame implements MobileAgent, Serializable {
	
	static final long serialVersionUID = 42L;

	private ICAutovelox CentralServer= null;
	private ILAutovelox LoginServer = null;
	private MobileServer stubMS = null;

	private String user = "";
	private Ref ref = null;
	private JLabel label;
	private JPanel pnl;
	private JLabel caserma;
	private JLabel block;
	private JLabel utente;
	private String localIp;

	public Autovelox(){}

	public void runMobile(ICAutovelox CentralServer, String user, ILAutovelox LoginServer, String localIp){
		
		this.CentralServer = CentralServer;
		this.LoginServer = LoginServer;
		this.user = user;
		this.localIp = localIp;
		try{

			ref = new Ref(user, localIp, new Date(), false);
			LoginServer.addRef(ref);						// Aggiungo utente alla lista in LoginServer
			CentralServer.printInfo("\033[1;32m - Connessione MobileAgent \033[0m"+user);		// Invoco CentralServer

		}
		catch(RemoteException re){
    			System.out.println(" Impossibile contattare il server. L'applicazione verrà terminata.");
    			System.exit(0);
		}
		catch(Exception e){
    			System.out.println(" Errore");	
			e.printStackTrace();
    			System.exit(0);
		}
		pnl = new JPanel(new GridLayout(2,2));
		pnl.setLayout(new GridLayout(1, 3));
		utente = new JLabel("Utente "+user);
		utente.setForeground (Color.WHITE);
		caserma = new JLabel("MS OFFLINE");
		caserma.setForeground (new Color(255,34,0));		//rosso
		block = new JLabel("ABILITATO");
		block.setForeground (new Color(102,205,0));		//verde

		utente.setHorizontalAlignment(JLabel.CENTER);
		caserma.setHorizontalAlignment(JLabel.CENTER);
		block.setHorizontalAlignment(JLabel.CENTER);
		pnl.setBackground (new Color(127,127,127));		//grigio

		pnl.add(utente);
		pnl.add(caserma);
      		pnl.add(block);
		add(pnl);
		setPreferredSize(new Dimension(500,50));
        		
        	setTitle("Autovelox");
        	setLocationRelativeTo(null);
		setUndecorated(true);
		setLocation((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2)-250, 0);
		pack();
		setVisible(true);
		
		try{
			clear();
			System.out.println("");
			System.out.print(" Inserisci limite di velocità: ");
			int lim = Integer.parseInt(inserimento());
			clear();
			System.out.println("");

			Random random = new Random();
			Multa m;
			boolean b;
			int delay, casual_car, casual_color, v_car;
			String targa, patt_data = "MM/dd/yyyy", patt_ora = "HH:mm";
			SimpleDateFormat format_data = new SimpleDateFormat(patt_data);
			SimpleDateFormat format_ora = new SimpleDateFormat(patt_ora);
			String car[] = {"Alfa Romeo", "Audi", "BMW", "Citroen", "Dacia", "Fiat", "Ford", "Honda", "Hyundai", "Infiniti", "Jaguar", 
					"Jeep", "Kia", "Lancia", "Land Rover", "Lexus", "Maserati", "Mazda", "Mercedes", "Mini", "Mitsubishi", "Nissan", 
					"Opel", "Peugeot", "Porsche", "Renault", "Seat", "Skoda", "Smart", "Subaru", "Suzuki", "Toyota", "Volkswagen", "Volvo"};

			String color[] = {"Bianca", "Nera", "Grigia", "Gialla", "Verde", "Blu"};

			// Create and install a security manager
			if (System.getSecurityManager() == null)
				System.setSecurityManager(new RMISecurityManager());
			boolean kill = false;
			for(int i=0;i<100;i++){
				try{
					stubMS = LoginServer.getStubMS();
				}catch (RemoteException re){
					System.out.println(" - ServerLogin Offline!");
				}
				if( stubMS == null ){
					caserma.setText("MS OFFLINE");
					caserma.setForeground (Color.RED);
				}else{
					caserma.setText("MS ONLINE");
					caserma.setForeground (Color.GREEN);

				}
				if (LoginServer.chekUserBlock(ref)){
					block.setText("BLOCCATO");
					block.setForeground (Color.RED);				
				}else{
					block.setText("ABILITATO");
					block.setForeground (Color.GREEN);
				}
				
				repaint();
					
				casual_car = random.nextInt(33);
				casual_color = random.nextInt(5);
				delay = random.nextInt(27)+3;	
	
				Thread.sleep(delay*100);
				v_car = random.nextInt(((int)(lim*0.6)) + random.nextInt((int)(lim*0.8))) + (int)(lim*0.4);	// vel_max 80% vel_min 40%
				targa = (""+(char)(random.nextInt(25)+65)+""+(char)(random.nextInt(25)+65)+""+random.nextInt(9)+""+random.nextInt(9)+""
					+random.nextInt(9)+""+(char)(random.nextInt(25)+65)+""+(char)(random.nextInt(25)+65));

    				String data = format_data.format(new Date());
				String ora = format_ora.format(new Date());

				int v_car_e = v_car;
				if(v_car >= 100) v_car_e = v_car - ((int)(v_car*0.05));
				if(v_car < 100) v_car_e = v_car - 5;
			
				if (LoginServer.chekUser(ref.getUser()) != null){
					if(v_car_e > lim){
					
						m = new Multa(data, ora, targa, v_car, lim, car[casual_car], color[casual_color]);
						if(v_car > lim+40){
							System.out.print("\033[1;34m ");
							if (stubMS != null && !LoginServer.chekUserBlock(ref))
								try{
									stubMS.notificaMulta(m, ref);
								
								}
								catch (Exception e){
									caserma.setText("OFFLINE");
									System.out.println(" - Caserma Offline!");	
								}
						
						}else
							System.out.print("\033[1;36m ");
						ref.setDate(new Date());
						if (!LoginServer.chekUserBlock(ref)){
							System.out.println(data+" "+ora+" "+targa+" "+v_car+" Km/h "+car[casual_car]+" "+color[casual_color]+
									" -> Multa "+" \033[0m");
							CentralServer.scriviMulta(m, ref);
						}else
							System.out.println(data+" "+ora+" "+targa+" "+v_car+" Km/h "+car[casual_car]+" "+color[casual_color]+
									" -> Multa\033[31m Utente Bloccato"+" \033[0m");
					
					}else
						System.out.println("\033[1;32m "+data+" "+ora+" "+targa+" "+v_car+" Km/h "+car[casual_car]+" "+color[casual_color] +
									" \033[0m");	
				}else{
					System.out.println(" Utente Espulso");
					System.exit(0);
				}
			}
			System.out.println("");
			System.out.println("\033[1;31m"+" Termine Autovelox"+" \033[0m");
			LoginServer.removeRef(ref);
			CentralServer.printInfo("\033[1;32m - Disconnessione MobileAgent \033[0m"+user);			// Invoco CentralServer
			setVisible(false);
			System.in.read();
		}
		catch (RemoteException re) {
			System.out.println(" Server OffLine!"); 
			re.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}	
		System.exit(0);
	}
	private void clear(){
		try{
			Runtime run = Runtime.getRuntime();
        		Process proc = run.exec(new String[]{"/bin/sh", "-c", "clear"});
        		proc.waitFor();
       			BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
       			while(br.ready())
            			System.out.println(br.readLine());

			System.out.println("---------------------------- CLIENT AUTOVELOX ----------------------------");
				
		}catch (Exception e) {
			System.out.println(" Errore clear bash!");
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
}

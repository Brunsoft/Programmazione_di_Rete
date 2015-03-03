package client.mobileS;

import server.central.ICCaserma;
import server.login.ILCaserma; 
import lib.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.UnknownHostException;

public interface MobileServer extends Remote {
	public void runMobile(ICCaserma CentralServer, ILCaserma LoginServer, String user) throws UnknownHostException, RemoteException;
	public String ping() throws RemoteException;
	public void notificaMulta (Multa m, Ref ref) throws RemoteException;
	public boolean TimeOutLoginServer() throws RemoteException;
}

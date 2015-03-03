package client.mobileA;

import server.central.ICAutovelox;
import server.login.ILAutovelox;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.UnknownHostException;

public interface MobileAgent extends Remote {
	public void runMobile(ICAutovelox CentralServer, String user, ILAutovelox LoginServer, String localIp) throws UnknownHostException, RemoteException;
}

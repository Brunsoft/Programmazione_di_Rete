package server.login;

import server.central.ICentralServer;
import lib.*;
import client.mobileS.MobileServer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;
import java.rmi.MarshalledObject;

public interface ILoginServer extends Remote {
	public MarshalledObject<Object> login(String user, String pwd) throws RemoteException;
	public ArrayList<Ref> getRefs() throws RemoteException;
	public void setMSRef(MarshalledObject<MobileServer> stubMS) throws RemoteException;
	public void addRef(Ref ref) throws RemoteException;
	public void removeRef(Ref ref) throws RemoteException;
	public void setUserBlock(Ref ref) throws RemoteException;
	public void setUserUnblock(Ref ref) throws RemoteException;
	public boolean chekUserBlock(Ref user) throws RemoteException;
	public Ref chekUser(String user) throws RemoteException;
}

package server.login;

import client.mobileS.MobileServer;
import lib.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;
import java.rmi.MarshalledObject;

public interface ILCaserma extends Remote {
	//public MarshalledObject login(String user, String pwd) throws RemoteException;
	public void setMSRef(MarshalledObject<MobileServer> stubMS) throws RemoteException;
	public void resetMSRef() throws RemoteException;
	public ArrayList<Ref> getRefs() throws RemoteException;
	public Ref chekUser(String user) throws RemoteException;
	public void setUserBlock(Ref user) throws RemoteException;
	public void setUserUnblock(Ref user) throws RemoteException;
	public boolean chekUserBlock(Ref user) throws RemoteException;
	public void removeRef(Ref ref) throws RemoteException;	
}

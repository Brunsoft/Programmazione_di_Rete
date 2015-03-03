package server.login;

import client.mobileS.MobileServer;
import lib.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;

public interface ILAutovelox extends Remote {
	public void addRef(Ref ref) throws RemoteException;
	public MobileServer getStubMS() throws RemoteException;
	public Ref chekUser(String user) throws RemoteException;
	public boolean chekUserBlock(Ref user) throws RemoteException;
	public void removeRef(Ref ref) throws RemoteException;
}

package server.login;

import lib.*;
import client.mobileS.MobileServer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;
import java.rmi.MarshalledObject;

public interface ILogin extends Remote {
	public MobileServer getStubMS() throws RemoteException;
	public void setMSRef(MarshalledObject<MobileServer> stubMS) throws RemoteException;
	public MarshalledObject<Object> login(String user, String pwd) throws RemoteException;
	public Ref chekUser(String user) throws RemoteException;
}

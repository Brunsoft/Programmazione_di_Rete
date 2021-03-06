package server.central;

import lib.*;
import client.mobileS.*;

import java.io.*;
import java.net.*;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.activation.*;
import java.util.ArrayList;

public interface ICentralServer extends Remote {
	public void printInfo(String info) throws RemoteException;
	public void scriviMulta (Multa m, Ref ref) throws RemoteException;
	public ArrayList<Multa> ricerca (String parametro, int db) throws RemoteException;
	public boolean pagaMulta (String targa, int n) throws RemoteException;
	public void powerOff(boolean powerOff) throws RemoteException;	
}

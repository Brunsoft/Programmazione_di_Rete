package server.central;

import lib.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;
import java.util.ArrayList;

public interface ICAutovelox extends Remote {
	public void printInfo(String info) throws RemoteException;
	public void scriviMulta (Multa m, Ref ref) throws RemoteException;
}

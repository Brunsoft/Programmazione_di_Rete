package lib;

import java.io.Serializable;
import java.util.Date;

public class Ref implements Serializable{

	static final long serialVersionUID = 42L;
	
	private String user;				/** Utente che detiene la referenza remota */
	private String host;				/** Indirizzo IP dell'host che detiene la refernza */
	private Date connDate;				/** Data della referenza */
	private boolean block;				/** Blocca l'utente in caso di ban */

	public Ref(){
		this.user="";
		this.host="";
		this.connDate=new Date();
	}
	
	public Ref(String user,String host,Date connDate, boolean b){
		this.user=user;
		this.host=host;
		this.connDate=connDate;
		this.block=b;
	}

	public void setUser(String user){
		this.user=user;
	}

	public void setHost(String host){
		this.user=host;
	}
	
	public void setDate(Date d){
		this.connDate = d;
	}
	
	public void setBlock(boolean b){
		this.block = b;
	}
	
	public String getUser(){
		return this.user;
	}

	public String getHost(){
		return this.host;
	}
	
	public Date getDate(){
		return this.connDate;
	}

	public boolean getBlock(){
		return block;
	}

	public boolean equals(Object o){
		Ref ref = (Ref)o;
		return (this.user.equals(ref.getUser()) && this.host.equals(ref.getHost()) 
				&& this.connDate.equals(ref.getDate()));	
	}
}

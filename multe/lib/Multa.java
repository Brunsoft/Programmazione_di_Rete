package lib;
import java.io.Serializable; 

public class Multa implements Serializable {

	static final long serialVersionUID = 42L;

	public String data, ora, targa, marca, colore;
	public int velocita, limite;
	public boolean pagata = false;

	public Multa (String data, String ora, String targa, int velocita, int limite, String marca, String colore) {
		this.data = data;
		this.ora = ora;
		this.targa = targa;
		this.velocita = velocita;
		this.limite = limite;
		this.marca = marca;
		this.colore = colore;
	}
	
	public void paga () {
		pagata = true;
	}

	public String getData () {
		return data;
	}

	public String getOra () {
		return ora;
	}

	public String getTarga () {
		return targa;
	}

	public int getVelocita () {
		return velocita;
	}
	
	public int getLimite () {
		return limite;
	}

	public String getMarca () {
		return marca;
	}

	public String getColore () {
		return colore;
	}

	public boolean getPagata () {
		return pagata;
	}

	public String toString () {
		return data+" "+ora+" "+targa+" "+velocita+" "+limite+" "+marca+" "+colore;
	}

	public String toString1 () {
		return data+";"+ora+";"+targa+";"+velocita+";"+limite+";"+marca+";"+colore;
	}

}

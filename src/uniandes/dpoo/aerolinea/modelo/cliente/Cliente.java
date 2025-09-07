package uniandes.dpoo.aerolinea.modelo.cliente;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uniandes.dpoo.aerolinea.modelo.Vuelo;
import uniandes.dpoo.aerolinea.tiquetes.Tiquete;

public abstract class Cliente {

	private List<Tiquete> tiquetesSinUsar;
	private List<Tiquete> tiquetesUsados;
	
	
	public Cliente() {
		List<Tiquete> tiquetesSinUsar = new ArrayList<Tiquete>();
		List<Tiquete> tiquetesUsados = new ArrayList<Tiquete>();
	}
	
	public abstract String getTipoCliente();
	
	public abstract String getIdentificador();
	
	public void agregarTiquete(Tiquete tiquete) {
		this.tiquetesSinUsar.add(tiquete);
	}
	
	public int calcularValorTotalTiquetes() {
		int total = 0;
		for (Tiquete t: this.tiquetesSinUsar) {
			total += t.getTarifa();
		}
		return total;
	}
	
	public void usarTiquetes(Vuelo vuelo) {
		Iterator<Tiquete> it = this.tiquetesSinUsar.iterator();
        while (it.hasNext()) {
            Tiquete t = it.next();
            if (vuelo.equals(t.getVuelo())) {
                if (!t.esUsado()) {
                    t.marcarComoUsado();
                }
                it.remove();
                this.tiquetesUsados.add(t);
            }
        }
	}
	
}

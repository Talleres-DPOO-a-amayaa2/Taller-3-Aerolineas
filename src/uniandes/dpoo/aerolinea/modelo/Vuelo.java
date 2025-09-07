package uniandes.dpoo.aerolinea.modelo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import uniandes.dpoo.aerolinea.exceptions.VueloSobrevendidoException;
import uniandes.dpoo.aerolinea.modelo.cliente.Cliente;
import uniandes.dpoo.aerolinea.modelo.tarifas.CalculadoraTarifas;
import uniandes.dpoo.aerolinea.tiquetes.GeneradorTiquetes;
import uniandes.dpoo.aerolinea.tiquetes.Tiquete;

public class Vuelo {

	private String fecha;
	private Ruta ruta;
	private Avion avion;
	private HashMap<String, Tiquete> tiquetes;
	
	public Vuelo(String fecha, Ruta ruta, Avion avion) {
		this.fecha = fecha;
		this.ruta = ruta;
		this.avion = avion;
		this.tiquetes = new HashMap<String, Tiquete>();
	}

	public String getFecha() {
		return fecha;
	}

	public Ruta getRuta() {
		return ruta;
	}

	public Avion getAvion() {
		return avion;
	}

	public HashMap<String, Tiquete> getTiquetes() {
		return tiquetes;
	}
	
	public int venderTiquetes(Cliente cliente, CalculadoraTarifas calculadora, int cantidad) throws VueloSobrevendidoException {
		// TODO 
		if (cantidad <= 0) return 0;
		
		int vendidos = this.tiquetes.size();
		int capacidad = this.avion.getCapacidad();
		if (vendidos + cantidad > capacidad) {
			throw new VueloSobrevendidoException(this);
		} else {
			int total = 0;
			for (int i = 0; i < cantidad; i++) {
	            int tarifa = calculadora.calcularTarifa(this, cliente);
	            Tiquete tiq = GeneradorTiquetes.generarTiquete(this, cliente, tarifa);
	            this.tiquetes.put(tiq.getCodigo(), tiq);
	            cliente.agregarTiquete(tiq);
	            total += tarifa;
	        }
	        return total;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		return false;
	}
	
	
	
	
}

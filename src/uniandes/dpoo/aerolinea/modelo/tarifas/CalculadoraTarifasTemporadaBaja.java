package uniandes.dpoo.aerolinea.modelo.tarifas;

import uniandes.dpoo.aerolinea.modelo.Ruta;
import uniandes.dpoo.aerolinea.modelo.Vuelo;
import uniandes.dpoo.aerolinea.modelo.cliente.Cliente;
import uniandes.dpoo.aerolinea.modelo.cliente.ClienteCorporativo;

public class CalculadoraTarifasTemporadaBaja extends CalculadoraTarifas{

	protected int COSTO_POR_KM_NATURAL = 600;
	protected int COSTO_POR_KM_CORPORATIVO = 900;
	protected double DESCUENTO_PEQ = 0.02;
	protected double DESCUENTO_MEDIANAS = 0.02;
	protected double DESCUENTO_GRANDES = 0.02;
	
	
	@Override
	protected int calcularCostoBase(Vuelo vuelo, Cliente cliente) {
		// TODO Auto-generated method stub
		Ruta ruta = vuelo.getRuta();
		int distancia = calcularDistanciaVuelo(ruta);
		String tipoCliente = cliente.getTipoCliente();
		int costoKm;
		
		if (tipoCliente.equals("Natural")) {
			costoKm = COSTO_POR_KM_NATURAL;
		} else {
			costoKm = COSTO_POR_KM_CORPORATIVO;
		}
		
		int costo = distancia * costoKm;
		return costo;
		
	}

	@Override
	protected double calcularPorcentajeDescuento(Cliente cliente) {
		// TODO Auto-generated method stub
		String tipoCliente = cliente.getTipoCliente();
		if (tipoCliente.equals("Natural")) {
			return 0.0;
		} else {
			ClienteCorporativo corp = (ClienteCorporativo) cliente;
            int tam = corp.getTamanoEmpresa();
            switch (tam) {
                case 1:
                    return DESCUENTO_GRANDES;
                case 2:
                    return DESCUENTO_MEDIANAS;
                case 3: 
                    return DESCUENTO_PEQ;
                default:
                    return 0.0;
            }
		}
	}
}


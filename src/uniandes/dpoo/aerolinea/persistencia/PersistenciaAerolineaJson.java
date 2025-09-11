package uniandes.dpoo.aerolinea.persistencia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import uniandes.dpoo.aerolinea.exceptions.AeropuertoDuplicadoException;
import uniandes.dpoo.aerolinea.modelo.Aerolinea;
import uniandes.dpoo.aerolinea.modelo.Aeropuerto;
import uniandes.dpoo.aerolinea.modelo.Avion;
import uniandes.dpoo.aerolinea.modelo.Ruta;

public class PersistenciaAerolineaJson implements IPersistenciaAerolinea
{
    private static final String AVIONES = "aviones";
    private static final String NOMBRE_AVION = "nombre";
    private static final String CAPACIDAD = "capacidad";

    private static final String RUTAS = "rutas";
    private static final String CODIGO_RUTA = "codigoRuta";
    private static final String HORA_SALIDA = "horaSalida";
    private static final String HORA_LLEGADA = "horaLlegada";
    private static final String ORIGEN = "origen";
    private static final String DESTINO = "destino";

    private static final String VUELOS = "vuelos";
    private static final String FECHA = "fecha";
    private static final String AVION = "avion";

    private static final String NOMBRE_AEROPUERTO = "nombre";
    private static final String CODIGO_AEROPUERTO = "codigo";
    private static final String NOMBRE_CIUDAD = "nombreCiudad";
    private static final String LATITUD = "latitud";
    private static final String LONGITUD = "longitud";

    @Override
    public void cargarAerolinea( String archivo, Aerolinea aerolinea ) throws JSONException, AeropuertoDuplicadoException
    {
        try ( BufferedReader br = Files.newBufferedReader(Paths.get(archivo), StandardCharsets.UTF_8) )
        {
            JSONObject root = new JSONObject( new JSONTokener( br ) );

            // Aviones
            if ( root.has( AVIONES ) )
            {
                JSONArray jAviones = root.getJSONArray( AVIONES );
                for ( int i = 0; i < jAviones.length(); i++ )
                {
                    JSONObject jA = jAviones.getJSONObject( i );
                    String nombre = String.valueOf( jA.get( NOMBRE_AVION ) ).trim();
                    int capacidad = jA.getInt( CAPACIDAD );
                    aerolinea.agregarAvion( new Avion( nombre, capacidad ) );
                }
            }

            // Rutas
            if ( root.has( RUTAS ) )
            {
                JSONArray jRutas = root.getJSONArray( RUTAS );
                for ( int i = 0; i < jRutas.length(); i++ )
                {
                    JSONObject jR = jRutas.getJSONObject( i );
                    //System.out.println(jR);

                    String codigo = (String) jR.get( CODIGO_RUTA );
                    //System.out.println(codigo);
                    String hs = String.valueOf( jR.get( HORA_SALIDA ) ).trim();
                    String hl = String.valueOf( jR.get( HORA_LLEGADA ) ).trim();

                    Aeropuerto org = leerAeropuerto( jR.getJSONObject( ORIGEN ) );
                    Aeropuerto des = leerAeropuerto( jR.getJSONObject( DESTINO ) );

                    aerolinea.agregarRuta( new Ruta( org, des, hs, hl, codigo ) );
                    //System.out.println(aerolinea.getRuta("4558"));
                    
                }
            }

            // Vuelos
            if ( root.has( VUELOS ) )
            {
                JSONArray jVuelos = root.getJSONArray( VUELOS );
                for ( int i = 0; i < jVuelos.length(); i++ )
                {
                    JSONObject jV = jVuelos.getJSONObject( i );
                    String fecha = String.valueOf( jV.get( FECHA ) ).trim();
                    String codigoRuta = String.valueOf( jV.get( CODIGO_RUTA ) ).trim();
                    String nombreAvion = String.valueOf( jV.get( AVION ) ).trim();
                    
                    boolean existe = false;
                    for ( Avion a : aerolinea.getAviones() ) {
                        if ( a != null && nombreAvion.equals( a.getNombre() ) ) { existe = true; break; }
                    }
                    if ( !existe ) {
                        aerolinea.agregarAvion( new Avion( nombreAvion, 180 ) );
                    }
                    
                    try {
                        aerolinea.programarVuelo( fecha, codigoRuta, nombreAvion );
                    } catch ( Exception e ) {
                     
                    }
                }
            }
        }
        catch ( IOException ioe )
        {
            
        }
    }

    @Override
    public void salvarAerolinea( String archivo, Aerolinea aerolinea )
    {
        try ( PrintWriter pw = new PrintWriter( Files.newBufferedWriter(Paths.get(archivo), StandardCharsets.UTF_8) ) )
        {
            JSONObject root = new JSONObject();

            // Aviones
            JSONArray jAviones = new JSONArray();
            for ( Avion a : aerolinea.getAviones() )
            {
                JSONObject j = new JSONObject();
                j.put( NOMBRE_AVION, a.getNombre() );
                j.put( CAPACIDAD, a.getCapacidad() );
                jAviones.put( j );
            }
            root.put( AVIONES, jAviones );

            // Rutas
            JSONArray jRutas = new JSONArray();
            for ( Ruta r : aerolinea.getRutas() )
            {
                JSONObject j = new JSONObject();
                j.put( CODIGO_RUTA, r.getCodigoRuta() );
                j.put( HORA_SALIDA, r.getHoraSalida() );
                j.put( HORA_LLEGADA, r.getHoraLlegada() );
                j.put( ORIGEN,  escribirAeropuerto( r.getOrigen() ) );
                j.put( DESTINO, escribirAeropuerto( r.getDestino() ) );
                jRutas.put( j );
            }
            root.put( RUTAS, jRutas );

            // Vuelos
            JSONArray jVuelos = new JSONArray();
            for ( uniandes.dpoo.aerolinea.modelo.Vuelo v : aerolinea.getVuelos() )
            {
                JSONObject j = new JSONObject();
                j.put( FECHA, v.getFecha() );
                j.put( CODIGO_RUTA, v.getRuta().getCodigoRuta() );
                j.put( AVION, v.getAvion().getNombre() );
                jVuelos.put( j );
            }
            root.put( VUELOS, jVuelos );

            root.write( pw, 2, 0 );
        }
        catch ( IOException ioe )
        {
      
        }
    }

    private Aeropuerto leerAeropuerto( JSONObject j ) throws JSONException, AeropuertoDuplicadoException
    {
        return new Aeropuerto(
            String.valueOf(j.get( NOMBRE_AEROPUERTO )).trim(),
            String.valueOf(j.get( CODIGO_AEROPUERTO )).trim(),
            String.valueOf(j.get( NOMBRE_CIUDAD )).trim(),
            j.getDouble( LATITUD ),
            j.getDouble( LONGITUD )
        );
    } // Me voy a poner a llorar porque no lo lee T.T

    private JSONObject escribirAeropuerto( Aeropuerto a )
    {
        JSONObject j = new JSONObject();
        j.put( NOMBRE_AEROPUERTO, a.getNombre() );
        j.put( CODIGO_AEROPUERTO, a.getCodigo() );
        j.put( NOMBRE_CIUDAD, a.getNombreCiudad() );
        j.put( LATITUD, a.getLatitud() );
        j.put( LONGITUD, a.getLongitud() );
        return j;
    }
}

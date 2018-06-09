package grupo4.com.core.manejadores;

import grupo4.com.servidor.database.BD;
import grupo4.com.util.Log;

import java.util.ArrayList;
import java.util.List;

import grupo4.com.core.modelJEE.EventoGCabezal;
import grupo4.com.core.modelJEE.EventoGConf;

public class ManejadorEventos {

	public 	List<EventoGConf> getConfEG(Log log, long idEvento){
		List<EventoGConf> configuraciones = new ArrayList<EventoGConf>();
		try {
			BD base = new BD();
			configuraciones = base.getConfEG(log, idEvento);
		}catch(Throwable t) {
			log.log("-->ERROR");
		}
		return configuraciones;
	}
	List<EventoGConf> configuraciones = new ArrayList<EventoGConf>();
	public List<EventoGCabezal> getListaEventosG (Log log){
		List<EventoGCabezal> eventos = null;
		try {
			BD base = new BD();
			eventos =  base.getListaEventosG(log);
		}catch(Throwable t) {
			log.log("-->ERROR");
		}
		return eventos;
	}
	public boolean configurarEG(Log log, String nombreEvento, int tipo, int nivel, String alerta) {
		boolean creado = false;
		try {
			BD base = new BD();
			long id_evento = base.getIDEventoG(log, nombreEvento);
			if(id_evento > 0 ) {
				creado = base.configurarEG(log, id_evento, tipo, nivel, alerta);	
			}else {
				log.log("No es posible configurar EVENTO GLOBAL , id evento incorrecto");
			}
		}catch(Throwable t) {
			log.log("-->ERROR");
		}
		return creado;
	}
	
	public boolean crearEG(Log log, String nombreEvento, String adminAlta) {
		boolean creado = false;
		try {
			BD base = new BD();
			creado = base.crearEG(log, nombreEvento, adminAlta);
		}catch(Throwable t) {
			log.log("-->ERROR");
		}
		return creado;
	}
	
	public boolean activarEG(Log log, String id_evento) {
		boolean activado = false;
		try {
			BD base = new BD();
			activado = base.activarEG(log,id_evento);
		}catch(Throwable t) {
			log.log("-->ERROR");
		}
		return activado;
	}
}

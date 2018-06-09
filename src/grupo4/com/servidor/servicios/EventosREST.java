package grupo4.com.servidor.servicios;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import grupo4.com.core.manejadores.ManejadorEventos;
import grupo4.com.core.modelJEE.EventoGCabezal;
import grupo4.com.core.modelJEE.EventoGConf;
import grupo4.com.servidor.rest.seguridad.anotacion.Secured;
import grupo4.com.servidor.servicios.modelos.MetodosToken;
import grupo4.com.util.Constantes;
import grupo4.com.util.Log;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@SuppressWarnings("unused")
@Path("/eventos")
public class EventosREST {

	ManejadorEventos meve = new ManejadorEventos();
	
	@Secured
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getConfEG/{idEvento}")
	public Response getConfEG (@HeaderParam(Constantes.HEADER_TOKEN) String token, @PathParam("idEvento") long idEvento) {
		Log log = null;
		List<EventoGConf> configuraciones = new ArrayList<EventoGConf>();
		try {
			log = new Log("EventosREST.log", true);
			log.log("Comienza recuperacion de eventos globales ]");
			configuraciones = meve.getConfEG(log, idEvento);
			if(configuraciones != null) {
				return Response.ok(configuraciones).build();	
			}else {
				return Response.ok("CONFIGURACION VACIA").build();	
			}
		}catch(Throwable t) {
			log.log("Error recuperando conf  de  EVENTO GLOBAL["+idEvento+"] debido a["+t.getMessage()+"]", t);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			log.log("Termina recuperacion  de configuracion del  EVENTO GLOBAL :[" + idEvento + "]");
			Log.cerrar(log);
		}
	}
	
	@Secured
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getListaEventosG")
	public Response getListaEventosG(@HeaderParam(Constantes.HEADER_TOKEN) String token) {
		Log log = null;
		List<EventoGCabezal> eventos = new ArrayList<EventoGCabezal>();
		try {
			log = new Log("EventosREST.log", true);
			log.log("Comienza recuperacion de eventos globales ]");
			eventos = meve.getListaEventosG(log);
			if(!eventos.isEmpty() && eventos!= null) {
				return Response.ok(eventos).build();	
			}else {
				return Response.ok("NO EXISTEN EVENTOS").build();	
			}
		} catch (Throwable t) {
			log.log("Error recuperando lista de  EVENTO GLOBAL debido a["+t.getMessage()+"]", t);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		} finally {
			log.log("Termina recuperacion  de EVENTOS GLOBAL :[" + eventos.toString() + "]");
			Log.cerrar(log);
		}
	}
	
	@Secured
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/confEventoG/{nombreEvento}/{tipo}/{nivel}/{alerta}")
	public Response configurarEventoG(@HeaderParam(Constantes.HEADER_TOKEN) String token, @PathParam("nombreEvento") String nombreEvento,@PathParam("tipo") int tipo,@PathParam("nivel") int nivel, @PathParam("alerta") String alerta ) {
		Log log = null;
		boolean configurado = false;
		try {
			log = new Log("EventosREST.log", true);
			log.log("-> [Comienza configuracion del EVENTO GLOBAL ["+nombreEvento+"]");
			String rol = MetodosToken.getRol(log, token);
			String adminAlta = MetodosToken.getUsuario(log, token);
			if(rol.equals(Constantes.ADMINISTRADOR_COMUN)||rol.equals(Constantes.ADMINISTRADOR_PRIVILEGIOS)||rol.equals(Constantes.ADMINISTRADOR_SOLO_NODOS) ||rol.equals(Constantes.SUPERVISOR) ) {
				configurado = meve.configurarEG(log,nombreEvento, tipo, nivel, alerta );
				return Response.ok(configurado).build();	
			}else {
				return Response.ok("No tienes suficiente nivel de acceso para configurar el evento eventos").build();	
			}
		} catch (Throwable t) {
			log.log("Error configurando EVENTO GLOBAL debido a["+t.getMessage()+"]", t);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		} finally {
			log.log("Termina configuracion de EVENTO GLOBAL :[" + nombreEvento + "] resultado ["+configurado+"]");
			Log.cerrar(log);
		}
	}
	 
	@Secured
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/crearEG/{nombre}")
	public Response crear(@HeaderParam(Constantes.HEADER_TOKEN) String token, @PathParam("nombre") String nombreEvento) {
		Log log = null;
		boolean creado = false;
		try {
			log = new Log("EventosREST.log", true);
			log.log("-> [Comienza creacion de EVENTO GLOBAL .");
			String rol = MetodosToken.getRol(log, token);
			String adminAlta = MetodosToken.getUsuario(log, token);
			if(rol.equals(Constantes.ADMINISTRADOR_COMUN)||rol.equals(Constantes.ADMINISTRADOR_PRIVILEGIOS)||rol.equals(Constantes.ADMINISTRADOR_SOLO_NODOS) ||rol.equals(Constantes.SUPERVISOR) ) {
				creado = meve.crearEG(log,nombreEvento, adminAlta);
				return Response.ok(creado).build();	
			}else {
				return Response.ok("No tienes suficiente nivel de acceso para crear eventos").build();	
			}
		} catch (Throwable t) {
			log.log("Error creando EVENTO GLOBAL debido a["+t.getMessage()+"]", t);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		} finally {
			log.log("Termina creacion de EVENTO GLOBAL :[" + creado + "] ");
			Log.cerrar(log);
		}
	}	
	
	@Secured
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/activarEG/{id_evento}")
	public Response activarEG(@HeaderParam(Constantes.HEADER_TOKEN) String token, @PathParam("id_evento") String id_evento) {
		Log log = null;
		boolean creado = false;
		try {
			log = new Log("EventosREST.log", true);
			log.log("-> [Comienza activacion de EVENTO GLOBAL ["+id_evento+"]");
			String rol = MetodosToken.getRol(log, token);
			String adminAlta = MetodosToken.getUsuario(log, token);
			if(rol.equals(Constantes.ADMINISTRADOR_COMUN)||rol.equals(Constantes.ADMINISTRADOR_PRIVILEGIOS)||rol.equals(Constantes.ADMINISTRADOR_SOLO_NODOS) ||rol.equals(Constantes.SUPERVISOR) ) {
				creado = meve.activarEG(log,id_evento);
				return Response.ok(creado).build();	
			}else {
				return Response.ok("No tienes suficiente nivel de acceso para activar eventos").build();	
			}
		} catch (Throwable t) {
			log.log("Error activando EVENTO GLOBAL["+id_evento+"] debido a["+t.getMessage()+"]", t);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		} finally {
			log.log("Termina activacion de EVENTO GLOBAL :[" + creado + "] ");
			Log.cerrar(log);
		}
	}
}

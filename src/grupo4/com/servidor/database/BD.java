package grupo4.com.servidor.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import grupo4.com.core.modelJEE.EventoGCabezal;
import grupo4.com.core.modelJEE.EventoGConf;
import grupo4.com.util.Constantes;
import grupo4.com.util.Log;
import grupo4.com.util.UtilBase;

public class BD {

	public 	List<EventoGConf> getConfEG(Log log, long idEvento){
		String sql		= "";
		List<EventoGConf> configuraciones = null;
		Connection c 	= null;
		Statement st 	= null;
		ResultSet rs	= null;
		try {
			log.log("Recuperando lista de eventos en BD....");
			configuraciones = new ArrayList<EventoGConf>();
			c = Conexion.getInstancia().getConexion(log, UtilBase.DATASOURCE);
			sql = "SELECT * FROM conf_eventos_globales WHERE id_evento ="+idEvento;
			st = c.createStatement();
			rs = st.executeQuery(sql);
			//TODO : hacer el while y setear, antes de que se me apagara lo hice
			while(rs.next()){
				EventoGConf conf = new EventoGConf();
				conf.setIdEvento(idEvento);
				conf.setAlerta(rs.getString("alerta"));
				conf.setFechaMod(rs.getString("fecha_ultima_mod"));
				conf.setNivel(rs.getInt("nivel"));
				conf.setTipo(rs.getInt("tipo"));
				
				configuraciones.add(conf);
			}
		}catch(Throwable t) {
			log.log("No fue posible recuperar lista de configuraciones en BD. SQL ["+sql+"]. debido a ["+t.getMessage()+"]");
		}finally {
			UtilBase.cerrarComponentes(log, rs, st, c);
		}
		return configuraciones;
	}
	public List<EventoGCabezal> getListaEventosG (Log log){
		String sql		= "";
		List<EventoGCabezal> eventos = null;
		Connection c 	= null;
		Statement st 	= null;
		ResultSet rs	= null;
		try {
			log.log("Recuperando lista de eventos en BD....");
			eventos = new ArrayList<EventoGCabezal>();
			c = Conexion.getInstancia().getConexion(log, UtilBase.DATASOURCE);
			sql = "SELECT id_evento, nombre_evento, activo FROM eventos_globales ";
			st = c.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next()) {
				EventoGCabezal eg = new EventoGCabezal();
				eg.setIdEvento(rs.getLong("id_evento"));
				eg.setNombreEvento(rs.getString("nombre_evento"));
				eg.setActivo(rs.getBoolean("activo"));
				eventos.add(eg);
			}
		}catch(Throwable t) {
			log.log("No fue posible recuperar lista de eventos en BD. SQL ["+sql+"]. debido a ["+t.getMessage()+"]");
		}finally {
			UtilBase.cerrarComponentes(log, rs, st, c);
		}
		return eventos;
	}
	
	public long getIDEventoG(Log log, String nombreEvento) {
		String sql		= "";
		long idEvento		= 0;
		Connection c 	= null;
		Statement st 	= null;
		ResultSet rs	= null;
		try {
			c = Conexion.getInstancia().getConexion(log, UtilBase.DATASOURCE);
			sql = "SELECT id_evento FROM eventos_globales WHERE nombre_evento='"+nombreEvento+"'";
			st = c.createStatement();
			rs = st.executeQuery(sql);
			if (rs.next()) {
				idEvento = rs.getLong("id_evento");
			}
		} catch(Throwable e) {
    		log.log("ERROR al buscar id de  EVENTO GLOBAL ["+nombreEvento+"]. SQL:["+sql+"]. Error:["+e.getMessage()+"]", e);
		} finally {
			UtilBase.cerrarComponentes(log, rs, st, c);
		}
		return idEvento;
	}
	
	public boolean configurarEG(Log log, long idEvento, int tipo, int nivel, String alerta) {
		String sql		= "";
		String delete	= "";
		Connection c 	= null;
		Statement st 	= null;
		boolean configurado = false;
		try {
			c = Conexion.getInstancia().getConexion(log, UtilBase.DATASOURCE);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String now = sdf.format(new Date());
			
			delete = "DELETE FROM conf_eventos_globales WHERE id_evento="+idEvento;
			
			sql = "INSERT INTO conf_eventos_globales  (id_evento, tipo, nivel, alerta, fecha_ultima_mod) "
					+ "VALUES ("+idEvento+", "+tipo+", "+nivel+", '"+alerta+"', '"+now+"')";
			st = c.createStatement();
			
			//hago el delete y compruebo si es modificacion o configuracion por primera vez
			boolean existe = (st.executeUpdate(delete) > 0);
			
    		String logearQ = (existe) ? "Id Evento:["+idEvento+"] se modifica. " : "Id Evento:["+idEvento+"] se configura. primera vez. ";
    		
    		//Hago insert de la congiuracion o modificaion dependiendo que sea que
    		int res = st.executeUpdate(sql);
    		if(res > 0) {
    			configurado = true;
    			log.log(logearQ);
    		} else {
    			log.log(logearQ+"No se pudo configurar.");
    		}
		} catch(Throwable e) {
    		log.log("ERROR al crear EVENTO GLOBAL ["+idEvento+"]. SQL:["+sql+"]. Error:["+e.getMessage()+"]", e);
		} finally {
			UtilBase.cerrarComponentes(log, null, st, c);
		}
		return configurado;
	}
	
	public boolean crearEG(Log log, String nombreEvento, String adminAlta) {
		String sql		= "";
		Connection c 	= null;
		Statement st 	= null;
		int rs 	= 0;
		boolean creado = false;
		try {
			c = Conexion.getInstancia().getConexion(log, UtilBase.DATASOURCE);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String now = sdf.format(new Date());
			//despues modificar la tabla eventos globales y agregar lo de que se sume el id para que sea unico
			sql = "INSERT INTO eventos_globales ( nombre_evento, activo, fecha_creacion, admin_alta) "
					+ "VALUES ('"+nombreEvento+"', false, '"+now+"', '"+adminAlta+"') ";
			st = c.createStatement();
			rs = st.executeUpdate(sql);
			if (rs>0) {
				creado = true;
			}
		} catch(Throwable e) {
    		log.log("ERROR al crear EVENTO GLOBAL o ["+nombreEvento+"]. SQL:["+sql+"]. Error:["+e.getMessage()+"]", e);
		} finally {
			UtilBase.cerrarComponentes(log, null, st, c);
		}
		return creado;
	}
	
	
	public String getRolUsuario(String user) {
		String rol		= "";
		String sql		= "";
		Connection c 	= null;
		Statement st 	= null;
		ResultSet rs 	= null;
		Log log 		= null;
		try {
			log = new Log("Obtener_rol.log",true);
    		log.log("Comienza obtencion de rol  del  Usuario:["+user+"]");
    		sql	= 	"SELECT niver_acceso " +
    				"FROM usuarios "+
    				"WHERE username='"+user+"';";

    		c = Conexion.getInstancia().getConexion(log, UtilBase.DATASOURCE);
    		st = c.createStatement();
    		rs = st.executeQuery(sql);
    		if(rs.next() ) {
    			rol = rs.getString("niver_acceso");
    		} else {
        		rol = Constantes.SIN_ROL;
    		}
		} catch(Throwable e) {
    		log.log("ERROR al obtener rol del usuario ["+user+"]. SQL:["+sql+"]. Error:["+e.getMessage()+"]", e);
		} finally {
			UtilBase.cerrarComponentes(log, rs, st, c);
		}
		return rol;
	}
	
	/**
	 * Valida token en la base de postgres
	 */
	public boolean validarToken(Log log, String user, String token) {
		Connection c 	= null;
		Statement st 	= null;
		ResultSet rs 	= null;
		String sql		= "";
		try {
    		log.log("Comienza validacion del token. Usuario:["+user+"], Token:["+token+"]");

    		sql	= 	"SELECT username, token " +
    				"FROM autenticacion "+
    				"WHERE username='"+user+"';";

    		c = Conexion.getInstancia().getConexion(log, UtilBase.DATASOURCE);
    		st = c.createStatement();
    		rs = st.executeQuery(sql);

    		if(rs.next() ) {
    			String tok = rs.getString("token");
        		if(token.equals(tok)) {
            		log.log("Se valida correctamente el token. Usuario:["+user+"], Token:["+token+"]");
            		return true;
        		} else {
            		log.log("ERROR NO se valida correctamente el token. Usuario:["+user+"], Token:["+token+"]");
            		return false;
        		}	
    		} else {
        		log.log(" No existe registro para  Usuario:["+user+"]");
        		return false;
    		}
		} catch(Throwable e) {
    		log.log("ERROR al validar token. SQL:["+sql+"]. Error:["+e.getMessage()+"]", e);
    		return false;
		} finally {
			UtilBase.cerrarComponentes(log, rs, st, c);
		}
	}
	
	/**
	 * Valida usuario en la BD de postgres
	 */
	public boolean validarUsuario(String username, String password) {
		Connection c = null;
		Statement st = null;
		ResultSet rs = null;
		String sql = "";
		boolean valido = false;
		Log log = null;
		try {
			log = new Log("basic_auth.log", true);
			log.log("Se pide validacion de Usuario ["+username+"], Encriptada:["+password+"]");
			c = Conexion.getInstancia().getConexion(log, UtilBase.DATASOURCE);
			sql = 	"SELECT username "+
					"FROM usuarios "+
					"WHERE username='"+username+"' AND password='"+password+"'";
			st = c.createStatement();
			rs = st.executeQuery(sql);
			valido = (rs.next()) ? true : false;
			log.log("Validacion de usuario ["+username+"] retorna ["+String.valueOf(valido)+"]. SQL:["+sql+"]");
		} catch(Throwable t) {
			log.log("No es posible validar el usuario. Usuario ["+username+"], Password:["+password+"] Error:["+t.getMessage()+"]", t);
		} finally {
			UtilBase.cerrarComponentes(log, rs, st, c);
		}
		return valido;
	}


	public boolean activarEG(Log log, String id_evento) {
		boolean activado = false;
		Connection c = null;
		Statement st = null;
		int  rs = 0;
		String sql = "";
		try {
			log.log("Se activa Evento GLOBAL :["+id_evento+"]");
			c = Conexion.getInstancia().getConexion(log, UtilBase.DATASOURCE);
			sql = 	"UPDATE eventos_globales  "+
					"SET activo = true "+
					"WHERE  id_evento = "+id_evento;
			st = c.createStatement();
			rs = st.executeUpdate(sql);
			activado = (rs > 0) ? true : false;
			log.log("Evento  ["+id_evento+"] ACTIVADO");
		} catch(Throwable t) {
			log.log("No es posible activar EVENTO GLOBAL ["+id_evento+"] Error:["+t.getMessage()+"].SQL ["+sql+"]", t);
		} finally {
			UtilBase.cerrarComponentes(log, null, st, c);
		}
		return activado;
	}
	
	/**
	public String getRoles(Log log, String username) {
		String result 			= ""; 
		return result;
	}*/
	
}

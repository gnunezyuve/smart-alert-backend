package grupo4.com.servidor.servicios;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.util.Calendar;
import javax.ws.rs.core.Response;

import grupo4.com.servidor.database.BD;
import grupo4.com.servidor.database.Persistencia;
import grupo4.com.util.Constantes;
import grupo4.com.util.Log;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

@SuppressWarnings("unused")
@Path("/usuario")
public class UsuarioREST {
	
	 public Response login(@HeaderParam(HttpHeaders.AUTHORIZATION) String aut) {
			Log log = null;
	        try {
	    		log = new Log("login.log", true);
	    		log.log("-> [Comienza login y solicitud de JWT]");

	    		if ((aut != null) && (aut.length() > 6)) {
	    			aut = aut.substring(6);
	        	} else {
	        		log.log("No se recibio header de Basic Atuh:["+HttpHeaders.AUTHORIZATION+"]. Imposible continuar");
	                return Response.status(Response.Status.UNAUTHORIZED).build();
	        	}
	        	
				byte [] bdecoded = new sun.misc.BASE64Decoder().decodeBuffer(aut);
				String decoded = new String(bdecoded);
				String [] ps = decoded.split(":");
				String user = ps[0];

				Persistencia p		= new Persistencia();
	    		BD metodosBase		= new BD();
	    		//String roles 		= metodosBase.getRoles(log, user);
	    		

				// se genera token jwt
	            String jwt = crearJWT(user);

	    		log.log("Usuario:["+user+"], Se genero token:["+jwt+"]");
	    		p.persistirToken(log, user, jwt);
	    		
	            return Response.ok().header(Constantes.HEADER_TOKEN, jwt).build();
	 
	        } catch (Throwable t) {
	        	log.log("Error inesperado generando token", t);
	            return Response.status(Response.Status.UNAUTHORIZED).build();
	        } finally {
	        	Log.cerrar(log);
	        }
	    }
	 
	 private String crearJWT(String user) throws UnsupportedEncodingException {
		 	//Le metemos 120 minutos de expiracion al token, que seria lo valido de la sesion
	    	Date fecha_ahora = new Date();
	    	Calendar calendario = Calendar.getInstance();
	    	calendario.setTime(fecha_ahora);
	    	calendario.add(Calendar.MINUTE, 120);
	        Date fecha_exp = calendario.getTime();
	        
	        BD base = new BD();
	        String rol = base.getRolUsuario(user);
			//Se meten las claims del JWT
	        String token = Jwts.builder()
	        		.claim(Constantes.ROL, rol)
	        		.claim(Constantes.TOKEN_USER, user)
	                .setSubject("Token valido")
	                .setIssuer("Doits")
	                .setIssuedAt(fecha_ahora)
	                .setExpiration(fecha_exp)
	                .signWith(SignatureAlgorithm.HS512, Constantes.KEY.getBytes("UTF-8")).compact();
	                
	        return token;
	    }
	 
	//Para Test estado de Servidor
	@GET
	@Path("/prueba")
	public String probar() {
		return "Proyecto-2018 Server is Up!";
	}

}

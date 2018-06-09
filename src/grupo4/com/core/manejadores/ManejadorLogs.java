package grupo4.com.core.manejadores;

import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import com.mongodb.client.model.Filters;

import grupo4.com.core.modelJEE.InfoCabezalNodo;
import grupo4.com.core.modelJEE.InfoDisco;
import grupo4.com.core.modelJEE.InfoMemoria;
import grupo4.com.util.Log;
import  grupo4.com.util.UtilMongo;

@SuppressWarnings("unused")
public class ManejadorLogs {

	/**
	 * Devuelve info de cabezal de un nodo
	 * @param log
	 * @param nodo
	 * @return
	 */
	public InfoCabezalNodo getInfoCabezalNodo(Log log, String nodo) {
		MongoClient mongoClient = null;
		InfoCabezalNodo inf = null;
		try {
			String con = UtilMongo.ConMongoLogtek();
			MongoClientURI connectionString = new MongoClientURI(con);
			mongoClient = new MongoClient(connectionString);
			MongoDatabase database = mongoClient.getDatabase("logtek");
			MongoCollection<Document> col = database.getCollection(nodo);
			
			FindIterable<Document> ultimo =   col.find().limit(1);
			for (Document docIte : ultimo) {
				inf = new InfoCabezalNodo();
				inf.setDistro(docIte.getString("distro"));
				String cantCpus = docIte.getString("NumberOfCPUs");
				inf.setCantCpus(Integer.parseInt(cantCpus));
				inf.setIpAddress(docIte.getString("IPAddress"));
				inf.setIpPublica(docIte.getString("PublicIP"));
				String totS = docIte.getString("TotalMemoryMB");
				long tot = Long.parseLong(totS);
				int totMB = (int) (tot/1024);
				inf.setTotalRAM(totMB);
			}
		}catch(Throwable t){
			log.log("No es posible devolver cabezal del nodo ["+nodo+"] debido a ["+t.getMessage()+"]");
		}finally {
			mongoClient.close();
		}
		return inf;
	}
	
	/**
	 * Devuelve memoria libre de un nodo
	 * @param log
	 * @param nodo
	 * @return
	 */
	public InfoMemoria getMemoriaLibre(Log log, String nodo) {
		InfoMemoria resultado = null;
		MongoClient mongoClient = null;
		try {
			resultado = new InfoMemoria();
			String con = UtilMongo.ConMongoLogtek();
			MongoClientURI connectionString = new MongoClientURI(con);
			mongoClient = new MongoClient(connectionString);
			MongoDatabase database = mongoClient.getDatabase("logtek");
			MongoCollection<Document> col = database.getCollection(nodo);
			
			FindIterable<Document> ultimo =   col.find().sort(new Document("_id", -1)).limit(1);
			int auxRes =0;
			int auxMemTotal = 0;
			int auxMemEnUso = 0;
			for (Document docIte : ultimo) {
				String ult = docIte.getString("FreeMemory");
				String ultiMemoriaTotal = docIte.getString("TotalMemoryMB");
				String ultMemEnUso = docIte.getString("MemoryInUse");
				int auxBytes = Integer.parseInt(ult);
				auxRes = auxBytes/1024;
				auxMemTotal = Integer.parseInt(ultiMemoriaTotal);
				auxMemTotal = auxMemTotal/1024;
				auxMemEnUso = Integer.parseInt(ultMemEnUso);
				auxMemEnUso = auxMemEnUso/1024;
			}
			resultado.setMemoriaEnUso(auxMemEnUso);
			resultado.setMemoriaLibre(auxRes);
			resultado.setMemoriaTotal(auxMemTotal);
		}catch(Throwable t) {
			log.log("No es posible devolver memoria libre debido a ["+t.getMessage()+"]");
		}finally {
			mongoClient.close();
		}
		return resultado;
	}
	
	/**
	 * Retorna espacio libre en disco de un nodo
	 * @param log
	 * @param nodo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public InfoDisco getEspacioEnDisco (Log log, String nodo) {
		InfoDisco resultado = null;
		MongoClient mongoClient = null;
		try {
			String con = UtilMongo.ConMongoLogtek();
			MongoClientURI connectionString = new MongoClientURI(con);
			mongoClient = new MongoClient(connectionString);
			MongoDatabase database = mongoClient.getDatabase("logtek");
			MongoCollection<Document> col = database.getCollection(nodo);
			
			List<Document> ue = (List<Document>) col.find().into(new ArrayList<Document>());
			for (Document entrada : ue) {	 
				List<Document> detalles = (List<Document>) entrada.get("DiskArray");
				for (Document detalle : detalles) {
					resultado = new InfoDisco();
					resultado.setMount(detalle.getString("mount"));
					String espacioDispo = detalle.getString("spaceavail");
					String espacioTotal = detalle.getString("spacetotal");
					String spliEsDispo = espacioDispo.split("G")[0];
					String spliEsTot = espacioTotal.split("G")[0];
					resultado.setEspacioDisponible(Float.parseFloat(spliEsDispo));
					resultado.setEspacioTotal(Float.parseFloat(spliEsTot)); 
				}
			}
		}catch(Throwable t) {
			log.log("No es posible devolver espacio en disco libre debido a ["+t.getMessage()+"]");
		}finally {
			mongoClient.close();
		}
		return resultado;
	}
}

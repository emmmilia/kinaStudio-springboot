package com.kinastudio.webapp.presentadores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
@Scope("session")
public class ConexionNavegador{

    private final ObjectMapper objectMapper;

    private SseEmitter conexionSSE;

    ConexionNavegador(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void conectarSSE() {
        if (conexionSSE != null) {
            cerrarConexion();
        }
        long timeOut = 30 * 60 * 1000;
        conexionSSE = new SseEmitter(timeOut);
        
    }
    public void cerrarConexion(){
        try{
            if(conexionSSE!=null){
                conexionSSE.complete();
                conexionSSE = null;
            }
        }catch(Exception e){}
    }

    public SseEmitter getConexionSSE() {
        return conexionSSE;
    }
     
    public void enviarJSON(Object informacion) {
        try {
            String json = new ObjectMapper().writeValueAsString(informacion);
            enviarMensaje(json);
        } catch (JacksonException e) {
            System.out.println("Error al convertir a JSON:" + e.getMessage());
           
        }
   
    }
    public void enviarMensaje(String mensaje) {
       
	if(conexionSSE==null) return;
        try {	
	     	conexionSSE.send(mensaje);
						
	} catch (Throwable e) {
            System.out.println("Error al enviar mensaje:" + e.getMessage());
            cerrarConexion();
	}
    }
    
  

}
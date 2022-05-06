/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.primerProyecto.tinderMascota.servicios;

import com.primerProyecto.tinderMascota.entidades.Mascota;
import com.primerProyecto.tinderMascota.entidades.Voto;
import com.primerProyecto.tinderMascota.errores.ErrorServicio;
import com.primerProyecto.tinderMascota.repositorios.MascotasRepositorio;
import com.primerProyecto.tinderMascota.repositorios.VotoRepositorio;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 *
 * @author leedavidcuellar
 */
@Service
public class VotoServicio {
    
    @Autowired
    private MascotasRepositorio mascotaRepositorio;
    
    @Autowired
    private NotificacionServicio notificacionServicio;
    
    @Autowired
    private VotoRepositorio votoRepositorio;
    
  
   public void votar(String idUsuario, String idMascota1, String idMascota2) throws ErrorServicio{
    Voto voto = new Voto();
    voto.setFecha(new Date());
    
    if(idMascota1.equals(idMascota2)){
        throw new ErrorServicio("No puede votarse a si mismo");
    }
    
    Optional<Mascota> respuesta = mascotaRepositorio.findById(idMascota1);
    if(respuesta.isPresent()){
        Mascota mascota1 = respuesta.get();
        if(mascota1.getUsuario().getId().equals(idUsuario)){
            voto.setMascota1(mascota1);
        }else{
            throw new ErrorServicio("No tiene permiso para realizar esa operacion");
        }
    }else{
        throw new ErrorServicio("No existe una mascota que vota vinculada a ese id");
    }
        System.out.println(idMascota1);
       System.out.println(idMascota2);
    Optional<Mascota> respuesta2 = mascotaRepositorio.findById(idMascota2);
    if(respuesta2.isPresent()){
        Mascota mascota2 = respuesta2.get();
        voto.setMascota2(mascota2);
        
       notificacionServicio.enviar("Tu mascota ha sido votada", "Tinder de Mascota", mascota2.getUsuario().getMail());

        
    }else{
        throw new ErrorServicio("No existe una mascota Votada vinculada a ese id");
    }
    
    votoRepositorio.save(voto);
   }    
    
  
   public void responder(String idUsuario, String idVoto) throws ErrorServicio{
      Optional<Voto> respuesta = votoRepositorio.findById(idVoto);
      if(respuesta.isPresent()){
          Voto voto = respuesta.get();
          voto.setRespuesta(new Date());
          
          if(voto.getMascota2().getUsuario().getId().equals(idUsuario)){
            notificacionServicio.enviar("Tu voto fue correspondido", "Tinder de Mascota", voto.getMascota1().getUsuario().getMail());

              votoRepositorio.save(voto);
          }else{
             throw new ErrorServicio("No tiene permiso para realizar la operacion"); 
          }      
      }else{
          throw new ErrorServicio("No existe el voto solicitado");
      }
   }
   
      public void ignorar(String idUsuario, String idVoto) throws ErrorServicio{
      Optional<Voto> respuesta = votoRepositorio.findById(idVoto);
      if(respuesta.isPresent()){
          Voto voto = respuesta.get();
          
          if(voto.getMascota2().getUsuario().getId().equals(idUsuario)){
            notificacionServicio.enviar("Tu voto NO fue correspondido", "Tinder de Mascota", voto.getMascota1().getUsuario().getMail());

          }else{
             throw new ErrorServicio("No tiene permiso para realizar la operacion"); 
          }      
      }else{
          throw new ErrorServicio("No existe el voto solicitado");
      }
   }
   
   public List<Voto> buscarVotosPropios(String id){
       return votoRepositorio.buscarVotosPropios(id);
   }
   
   public List<Voto> buscarVotosRecibidos(String id){
       return votoRepositorio.buscarVotosRecibidos(id);
   }
   
}

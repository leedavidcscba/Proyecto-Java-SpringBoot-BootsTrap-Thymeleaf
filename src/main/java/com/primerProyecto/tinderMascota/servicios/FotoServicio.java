/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.primerProyecto.tinderMascota.servicios;

import com.primerProyecto.tinderMascota.entidades.Foto;
import com.primerProyecto.tinderMascota.errores.ErrorServicio;
import com.primerProyecto.tinderMascota.repositorios.FotoRepositorio;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


/**
 *
 * @author leedavidcuellar
 */
@Service
public class FotoServicio {
    @Autowired
    private FotoRepositorio fotoRepositorio;
    
    //para guardar foto, multipartFile lo hace
    @Transactional
    public Foto guardar(MultipartFile archivo) throws ErrorServicio{
      if(archivo != null && !archivo.isEmpty()){
        try{  // seteamos la foto y copiamos, si ocurre error, lo muestra
            Foto foto = new Foto();
            foto.setMime(archivo.getContentType());
            foto.setNombre(archivo.getName());
            foto.setContenido(archivo.getBytes());
            
            return fotoRepositorio.save(foto);
        }catch(Exception e){
            System.err.println(e.getMessage());
        }   
      }   
      return null;    
    }
    
    @Transactional // esto hace que si ejecuta el metodo guarda y deja comit, si hay error, no lo hace
    public Foto actualizar(String idFoto, MultipartFile archivo)throws ErrorServicio{//funciona para una nueva o no tenia nada
      if(archivo != null && !archivo.isEmpty()){
        try{  // seteamos la foto y copiamos, si ocurre error, lo muestra
            Foto foto = new Foto();
            if(idFoto != null){
                Optional<Foto> respuesta = fotoRepositorio.findById(idFoto);
                if(respuesta.isPresent()){
                    foto=respuesta.get();
                }
            }
            foto.setMime(archivo.getContentType());
            foto.setNombre(archivo.getName());
            foto.setContenido(archivo.getBytes());
            
            return fotoRepositorio.save(foto);
        }catch(Exception e){
            System.err.println(e.getMessage());
        }   
      }   
      return null;     
    }
    
}

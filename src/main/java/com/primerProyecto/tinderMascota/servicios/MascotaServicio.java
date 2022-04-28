/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.primerProyecto.tinderMascota.servicios;

import com.primerProyecto.tinderMascota.entidades.Foto;
import com.primerProyecto.tinderMascota.entidades.Mascota;
import com.primerProyecto.tinderMascota.entidades.Usuario;
import com.primerProyecto.tinderMascota.enumeraciones.Sexo;
import com.primerProyecto.tinderMascota.enumeraciones.Tipo;
import com.primerProyecto.tinderMascota.errores.ErrorServicio;
import com.primerProyecto.tinderMascota.repositorios.MascotasRepositorio;
import com.primerProyecto.tinderMascota.repositorios.UsuarioRepositorio;
import java.util.Date;
import java.util.List;
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
public class MascotaServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    
    @Autowired
    private MascotasRepositorio mascotasRepositorio;

    @Autowired
    private FotoServicio fotoServicio;
    
    @Transactional
    public void agregarMascota(MultipartFile archivo, String idUsuario, String nombre, Tipo tipo, Sexo sexo) throws ErrorServicio {

        Usuario usuario = usuarioRepositorio.findById(idUsuario).get();

        validar(nombre, sexo);
        
        Mascota mascota = new Mascota();
        mascota.setNombre(nombre);
        mascota.setSexo(sexo);
        mascota.setTipo(tipo);
        mascota.setAlta(new Date());
        mascota.setUsuario(usuario);
        
        Foto foto = fotoServicio.guardar(archivo);
        mascota.setFoto(foto);
        
        mascotasRepositorio.save(mascota);
    }

    @Transactional
    public void modificar(MultipartFile archivo, String idUsuario, String idMascota, String nombre, Tipo tipo, Sexo sexo) throws ErrorServicio{
        validar(nombre, sexo);
        
        Optional<Mascota> respuesta = mascotasRepositorio.findById(idMascota);
        if(respuesta.isPresent()){
            Mascota mascota = respuesta.get();
            if(mascota.getUsuario().getId().equals(idUsuario)){
                mascota.setNombre(nombre);
                mascota.setSexo(sexo);
                mascota.setTipo(tipo);
                String idFoto = null;
                if(mascota.getFoto()!= null){
                    idFoto = mascota.getFoto().getId();
                }
                
                Foto foto = fotoServicio.actualizar(idFoto, archivo);
                mascota.setFoto(foto);
                
                mascotasRepositorio.save(mascota);
            }else{
                throw new ErrorServicio("No tiene permisos suficientes para realizar esa operacion");
            }
        }else{
            throw new ErrorServicio("No existe una mascota con el identificador solicitado");
        }
    }
    
    @Transactional
    public void eliminar(String idUsuario, String idMascota) throws ErrorServicio{
        Optional<Mascota> respuesta = mascotasRepositorio.findById(idMascota);
        if(respuesta.isPresent()){
            Mascota mascota = respuesta.get();
            if(mascota.getUsuario().getId().equals(idUsuario)){
                mascota.setBaja(new Date());
                               
                mascotasRepositorio.delete(mascota);
            }else{
                throw new ErrorServicio("No tiene permisos suficientes para realizar esa operacion");
            }
        }else{
            throw new ErrorServicio("No existe una mascota con el identificador solicitado");
        }
    }
    
    public Mascota buscarPorId(String id) throws ErrorServicio{
        Optional<Mascota> respuesta = mascotasRepositorio.findById(id);
        if(respuesta.isPresent()){
            return respuesta.get();
        }else{
            throw new ErrorServicio("La mascota solicitada no existe");
        }
        
    }
    
     public List<Mascota> buscarPorIdUsuario(String id) throws ErrorServicio{
        List<Mascota> mascota = mascotasRepositorio.buscarMascotaPorUsuario(id);
        if(mascota !=null){
            return mascota;
        }else{
            throw new ErrorServicio("El usuario no tiene mascota registrada");
        }
        
    }
     
       public List<Mascota> listarMascotas(String id) throws ErrorServicio{
        List<Mascota> mascotas = mascotasRepositorio.listarTodasMascotas(id);
        if(mascotas !=null){
            return mascotas;
        }else{
            throw new ErrorServicio("No hay mascotas");
        }
        
    }
    
    public void validar(String nombre, Sexo sexo) throws ErrorServicio {
        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServicio("El nombre de mascota no puede ser vacio o nulo");
        }
        if (sexo == null){
            throw new ErrorServicio("El sexo de mascota no puede ser nulo");
        }
    }

}

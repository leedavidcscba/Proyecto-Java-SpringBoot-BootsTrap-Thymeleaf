/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.primerProyecto.tinderMascota.controladores;

import com.primerProyecto.tinderMascota.entidades.Mascota;
import com.primerProyecto.tinderMascota.entidades.Usuario;
import com.primerProyecto.tinderMascota.entidades.Zona;
import com.primerProyecto.tinderMascota.enumeraciones.Sexo;
import com.primerProyecto.tinderMascota.enumeraciones.Tipo;
import com.primerProyecto.tinderMascota.errores.ErrorServicio;
import com.primerProyecto.tinderMascota.repositorios.ZonaRepositorio;
import com.primerProyecto.tinderMascota.servicios.MascotaServicio;
import com.primerProyecto.tinderMascota.servicios.UsuarioServicio;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author leedavidcuellar
 */
@PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
@Controller
@RequestMapping("/mascota")
public class MascotaController {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private MascotaServicio mascotaServicio;

    
    @GetMapping("/editar-perfil")
    public String editarPerfil(HttpSession session, @RequestParam(required = false) String id, ModelMap model,@RequestParam(required = false) String accion ) {
        
        if(accion== null){
            accion="Crear";
        }
        
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if(login == null){
            return "redirect:/inicio";// si pasa tiempo y no hace nada para vuelva a inicio
        }
        
        //if()
        
        Mascota mascota = new Mascota();
        if(id!=null && !id.isEmpty()){
            try {
                mascota = mascotaServicio.buscarPorId(id);
            } catch (ErrorServicio ex) {
                Logger.getLogger(MascotaController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        model.put("perfil", mascota);
        model.put("accion", accion);
        model.put("sexos", Sexo.values());
        model.put("tipos", Tipo.values());
        return "mascota.html";
    }
    
    @GetMapping("/mis-mascotas")
    public String misMascotas(HttpSession session, ModelMap model) throws ErrorServicio {
        Usuario login = (Usuario) session.getAttribute("usuariosession");//recupero usuario logueado
        if(login == null){
            return "redirect:/login";// si pasa tiempo y no hace nada para vuelva a inicio
        }
        
        List<Mascota> mascotas = mascotaServicio.buscarPorIdUsuario(login.getId());
        if(mascotas.isEmpty() || mascotas == null){
            model.put("error", "No tenes ninguna mascota cargada");
                    model.put("mascotas", mascotas);    
                return "mascotas";
        }else{
        
        model.put("mascotas", mascotas);    
        return "mascotas";
        }
    }
    
    @PostMapping("/actualizar-perfil")
    public String actualizar(ModelMap modelo, HttpSession session, MultipartFile archivo, @RequestParam String id, @RequestParam String nombre, @RequestParam Tipo tipo, @RequestParam Sexo sexo, @RequestParam(required = false) String accion) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if(login == null){
            return "redirect:/login";// si pasa tiempo y no hace nada para vuelva a inicio
        }
        
        
        try {

//            Usuario login = (Usuario) session.getAttribute("usuariosession");
//            if (login == null || !login.getId().equals(id)) {
//                return "redirect:/inicio";
//            }
            
            if (id == null|| id.isEmpty()) {
                mascotaServicio.agregarMascota(archivo, login.getId(), nombre, tipo, sexo);
            } else {
               
                mascotaServicio.modificar(archivo, login.getId(), id, nombre, tipo, sexo);
            }
            
            return "redirect:/inicio";
        } catch (ErrorServicio ex) {
            Mascota mascota = new Mascota();
            
            mascota.setNombre(nombre);
            mascota.setId(id);
            mascota.setSexo(sexo);
            mascota.setTipo(tipo);
            modelo.put("accion", "Actualizar");
            modelo.put("sexos", Sexo.values());
            modelo.put("tipos", Tipo.values());
            modelo.put("error", ex.getMessage());
            modelo.put("perfil", mascota);
            return "perfil.html";
        }
    }
    
    @PostMapping("eliminar-perfil")
    public String eliminar(HttpSession session, String id){
        try {
            Usuario login = (Usuario) session.getAttribute("usuariosession");
            mascotaServicio.eliminar(login.getId(), id);
            
        } catch (ErrorServicio ex) {
            Logger.getLogger(MascotaController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "redirect:/mis-mascotas";
    }

}

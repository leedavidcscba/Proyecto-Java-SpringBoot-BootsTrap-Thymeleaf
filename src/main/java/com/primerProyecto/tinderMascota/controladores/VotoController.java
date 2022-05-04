/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.primerProyecto.tinderMascota.controladores;

import com.primerProyecto.tinderMascota.entidades.Mascota;
import com.primerProyecto.tinderMascota.entidades.Usuario;
import com.primerProyecto.tinderMascota.entidades.Voto;
import com.primerProyecto.tinderMascota.errores.ErrorServicio;
import com.primerProyecto.tinderMascota.repositorios.VotoRepositorio;
import com.primerProyecto.tinderMascota.servicios.MascotaServicio;
import com.primerProyecto.tinderMascota.servicios.UsuarioServicio;
import com.primerProyecto.tinderMascota.servicios.VotoServicio;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author leedavidcuellar
 */
@PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
@Controller
@RequestMapping("/voto")
public class VotoController {

    @Autowired
    private VotoServicio votoServicio;
    
    @Autowired
    private VotoRepositorio votoRepositorio;

//    @Autowired
//    private UsuarioServicio usuarioServicio;
//
    @Autowired
    private MascotaServicio mascotaServicio;
    
    
    @GetMapping("/panelparaVotar")
    public String panelParaVotar(HttpSession session, ModelMap model, @RequestParam String idMascota1){
        try {
            Usuario login = (Usuario) session.getAttribute("usuariosession");//recupero usuario logueado
        if(login == null){
            return "redirect:/login";// si pasa tiempo y no hace nada para vuelva a inicio
        }

            List<Mascota> listaMascotas = mascotaServicio.listarMascotas(login.getId());
            model.addAttribute("listaMascotas",listaMascotas);

            model.put("idMascota1",idMascota1);

        } catch (ErrorServicio ex) {
           model.put("mensaje", ex.getMessage());
           return "error.html";
        }
        return "panelVoto.html";
    }
    
    @GetMapping("/listaVotosPropios")
    public String votosPropios(HttpSession session, ModelMap model){
        
        
        Usuario login = (Usuario) session.getAttribute("usuariosession");
            if (login == null) {
                return "redirect:/inicio";// si pasa tiempo y no hace nada para vuelva a inicio
            }
            
            
        try {
            List<Mascota> mascotas = mascotaServicio.buscarPorIdUsuario(login.getId());
            List<Voto> listaVotos = new ArrayList<Voto>();
            for (Mascota mascota : mascotas) {
                listaVotos.addAll(votoServicio.buscarVotosPropios(mascota.getId()));
            }
            model.put("accion", "Propios");
            model.put("listaVotos", listaVotos);
            return "listavotos.html";
        } catch (ErrorServicio ex) {
            model.put("mensaje", ex.getMessage());
            return "error.html";
        }
           
        
    }
   
    @GetMapping("/listaVotosRecibidos")
    public String votosRecibido(HttpSession session, ModelMap model, @RequestParam(required = false) String accion){
        
        
        Usuario login = (Usuario) session.getAttribute("usuariosession");
            if (login == null) {
                return "redirect:/inicio";// si pasa tiempo y no hace nada para vuelva a inicio
            }
            
        try {
            List<Mascota> mascotas = mascotaServicio.buscarPorIdUsuario(login.getId());
            List<Voto> listaVotos = new ArrayList<Voto>();
            for (Mascota mascota : mascotas) {
                listaVotos.addAll(votoServicio.buscarVotosRecibidos(mascota.getId()));
            }
            model.put("accion", "Recibidos");
            model.put("listaVotos", listaVotos);
            
            return "listavotos.html";
        } catch (ErrorServicio ex) {
            model.put("mensaje", ex.getMessage());
            return "error.html";
        }
    }
    
    @PostMapping("/votar")
    public String votar(HttpSession session, ModelMap modelo, @RequestParam String idMascota1, @RequestParam String idMascota2) {
        try {
            Usuario login = (Usuario) session.getAttribute("usuariosession");
            if (login == null) {
                return "redirect:/login";// si pasa tiempo y no hace nada para vuelva a inicio
            }
            
            votoServicio.votar(login.getId(), idMascota1, idMascota2);
            modelo.put("exito", "Se realizo el voto");
            return "redirect:/inicio";
        } catch (ErrorServicio ex) {
            modelo.put("mensaje", ex.getMessage());
            return "error.html";
        }
        
    }

    @PostMapping("/responderVoto")
    public String responderVoto(HttpSession session, ModelMap modelo, @RequestParam String idVoto) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null) {
            return "redirect:/inicio";// si pasa tiempo y no hace nada para vuelva a inicio

        }
        
        try {
            votoServicio.responder(login.getId(), idVoto);
         modelo.put("exito", "Se respondio el voto");
        return "panelVoto.html";
        } catch (ErrorServicio ex) {
            modelo.put("mensaje", ex.getMessage());
            return "error.html";
        }

        
    }
    
    @GetMapping("/ignorar")
    public String responderVoto(HttpSession session, ModelMap modelo) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null) {
            return "redirect:/inicio";// si pasa tiempo y no hace nada para vuelva a inicio

        }
        
        modelo.put("exito", "Se ignoro el Voto");
        return "panelVoto.html";
        
    }
    
}

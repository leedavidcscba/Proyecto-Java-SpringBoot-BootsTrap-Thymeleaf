/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.primerProyecto.tinderMascota.controladores;

import com.primerProyecto.tinderMascota.entidades.Mascota;
import com.primerProyecto.tinderMascota.entidades.Usuario;
import com.primerProyecto.tinderMascota.entidades.Zona;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.primerProyecto.tinderMascota.errores.ErrorServicio;
import com.primerProyecto.tinderMascota.repositorios.ZonaRepositorio;
import com.primerProyecto.tinderMascota.servicios.MascotaServicio;
import com.primerProyecto.tinderMascota.servicios.UsuarioServicio;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author leedavidcuellar
 */
@Controller
@RequestMapping("/")// configura que url va tener
public class PortalControlador {
    
    @Autowired
    private UsuarioServicio usuarioServicio;
    
    @Autowired
    private ZonaRepositorio zonaRepositorio;
    
    @Autowired
    private MascotaServicio mascotaServicio;
    
// responde al get de http al ingresar "/"
    @GetMapping("/")
    public String index() {
        return "index.html";
    }
    
    @GetMapping("/login")
    public String login(@RequestParam(required=false)String error, ModelMap modelo,@RequestParam(required =false) String logout,  RedirectAttributes redirectAttrs) {
        if(error!= null){
            modelo.put("error","Usuario o Clave incorrecta");
        }
        
        if(logout != null){
            modelo.put("logout","Ha salido correctamente de la plataforma");
        }
        return "login.html";
    } 
    
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")    
    @GetMapping("/inicio")
    public String inicio(HttpSession session, ModelMap model) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");//recupero usuario logueado
        if(login == null){
            return "redirect:/login";// si pasa tiempo y no hace nada para vuelva a inicio
        }
        try {
            List<Mascota> listaMascotaPropia = mascotaServicio.buscarPorIdUsuario(login.getId());
            model.addAttribute("listaMascotasPropia",listaMascotaPropia);
        } catch (ErrorServicio ex) {
            Logger.getLogger(PortalControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        return "inicio.html";
    }
    
    @GetMapping("/registro")
    public String registro(ModelMap modelo) {
        List<Zona> zonas = zonaRepositorio.findAll();
        modelo.put("zonas", zonas);
        return "registro.html";
    }
    
    @GetMapping("/exito")
    public String exito() {
        return "exito.html";
    }
    
    
    //registrar, envia datos del formlario a base datos, form action="/registrar" method="POST"
    //RequestParam es para indicar que son necesarios para guardar y viajen  en metodo post
    //ModelMap guarda todo lo que neceistamos guardar temporariamente interfaz de usuario
    @PostMapping("/registrar")
    public String registrar(ModelMap modelo, MultipartFile archivo, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String mail,@RequestParam String idZona, @RequestParam String clave1, @RequestParam String clave2) {
        
        try {
            usuarioServicio.registrar(archivo, nombre, apellido, mail, idZona, clave1,clave2);  
        }catch (ErrorServicio ex) {
            modelo.put("Error", ex.getMessage());//en html registro add,<p th:if="${Error != null}" th:text="$(Error)" style=color:red;></p> que si en el modelo viene un error lo muestre
            modelo.put("nombre", nombre);// en html registro para que lo use en los input th:value="$(nombre)"
            modelo.put("apellido", apellido);
            modelo.put("mail", mail);
            modelo.put("clave1", clave1);
            modelo.put("clave2", clave2);
            List<Zona> zonas = zonaRepositorio.findAll();
            modelo.put("zonas", zonas);
            return "registro.html";
        }
        modelo.put("titulo", "Bienvenido al tinder de mascotas");
        modelo.put("descripcion", "Tu usuario fue registrado satisfactoriamente");
        return "exito.html";//en este html <h2 class="display-4" th:text="$(titulo)"></h2> <p th:text="$(descripcion)"></p>
    }
}

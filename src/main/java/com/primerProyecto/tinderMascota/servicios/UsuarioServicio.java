/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.primerProyecto.tinderMascota.servicios;
import com.primerProyecto.tinderMascota.entidades.Foto;
import com.primerProyecto.tinderMascota.entidades.Usuario;
import com.primerProyecto.tinderMascota.entidades.Zona;
import com.primerProyecto.tinderMascota.errores.ErrorServicio;
import com.primerProyecto.tinderMascota.repositorios.UsuarioRepositorio;
import com.primerProyecto.tinderMascota.repositorios.ZonaRepositorio;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;


/**
 *
 * @author leedavidcuellar
 */
@Service
public class UsuarioServicio implements UserDetailsService{//

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    
    @Autowired
    private NotificacionServicio notificacionServicio;
    
    @Autowired
    private FotoServicio fotoServicio;
    
    @Autowired
    private ZonaRepositorio zonaRepositorio;

    @Transactional
    public void registrar(MultipartFile archivo, String nombre, String apellido, String mail, String idZona,String clave1, String clave2) throws ErrorServicio {

        Zona zona= zonaRepositorio.getOne(idZona);
        validar(nombre, apellido, mail, zona,clave1, clave2);        
        
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setMail(mail);
        usuario.setZona(zona);
        
        String encriptada = new BCryptPasswordEncoder().encode(clave1);
        usuario.setClave(encriptada);
        usuario.setAlta(new Date());

        Foto foto = fotoServicio.guardar(archivo);
        usuario.setFoto(foto);
        usuarioRepositorio.save(usuario);
        
        notificacionServicio.enviar("Bienvenido al Tinder de Mascota", "Tinder de Mascota", usuario.getMail());
    }

    @Transactional
    public void modificar(MultipartFile archivo, String id, String nombre, String apellido, String mail,String idZona, String clave1, String clave2) throws ErrorServicio {

        Zona zona= zonaRepositorio.getOne(idZona);
        validar(nombre, apellido, mail, zona, clave1,clave2);

        //jpa nos devuelve un opcional usuario
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setMail(mail);
            usuario.setZona(zona);
            
            String encriptada = new BCryptPasswordEncoder().encode(clave1);
            usuario.setClave(encriptada);

            String idFoto = null;
            if(usuario.getFoto()!= null){
                idFoto = usuario.getFoto().getId();
            }
            
            Foto foto = fotoServicio.actualizar(idFoto, archivo);
            usuario.setFoto(foto);
            
            usuarioRepositorio.save(usuario);
        } else {
            throw new ErrorServicio("No se encontro el usuario solicitado");
        }

    }
    
    @Transactional
    public void deshabilitar(String id) throws ErrorServicio{
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuario.setBaja(new Date());

            usuarioRepositorio.save(usuario);
        } else {
            throw new ErrorServicio("No se encontro el usuario solicitado");
        }   
    }
    
    @Transactional
    public void habilitar(String id) throws ErrorServicio{
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuario.setBaja(null);

            usuarioRepositorio.save(usuario);
        } else {
            throw new ErrorServicio("No se encontro el usuario solicitado");
        }   
    }
    
    public Usuario buscarPorId(String id){
        return usuarioRepositorio.buscarPorId(id);
    }
    

    private void validar(String nombre, String apellido, String mail, Zona zona, String clave1, String clave2) throws ErrorServicio {
        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServicio("El nombre del usuario no puede ser nulo");
        }

        if (apellido == null || apellido.isEmpty()) {
            throw new ErrorServicio("El apellido del usuario no puede ser nulo");
        }

        if (mail == null || mail.isEmpty()) {
            throw new ErrorServicio("El mail del usuario no puede ser nulo");
        }

        if (clave1 == null || clave1.isEmpty() || clave1.length() < 6) {
            throw new ErrorServicio("El mail del usuario no puede ser nulo y no puede ser menor de 6 caracteres");
        }
        
        if (!clave1.equals(clave2)) {
            throw new ErrorServicio("Las claves tiene que ser iguales");
        }
        if(zona == null){
            throw new ErrorServicio("No se encontro la zona solicitada");
        }
    }

    @Override // usuario quiere autentificarse en plataforma, les da esos permisos
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepositorio.buscarPorMail(mail);
        if(usuario != null){
            List<GrantedAuthority> permisos = new ArrayList<>();
            
            GrantedAuthority p1 = new SimpleGrantedAuthority("ROLE_USUARIO_REGISTRADO");
            permisos.add(p1);
//            GrantedAuthority p2 = new SimpleGrantedAuthority("MODULO_MASCOTAS");
//            permisos.add(p2);
//            GrantedAuthority p3 = new SimpleGrantedAuthority("MODULO_VOTO");
//            permisos.add(p3);
            
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute("usuariosession", usuario);// aca guardo los datos del usuario

            User user = new User(usuario.getMail(), usuario.getClave(), permisos);
            return user;
        } else{
               return null;
        }
    }
}

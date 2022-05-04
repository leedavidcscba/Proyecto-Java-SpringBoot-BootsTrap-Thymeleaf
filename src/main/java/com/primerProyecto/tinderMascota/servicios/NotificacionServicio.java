/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.primerProyecto.tinderMascota.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *
 * @author leedavidcuellar
 */
@Service
public class NotificacionServicio {
   
    @Autowired
    private JavaMailSender mailSender;
    
    @Async // hace que se ejecute en segundo plano,sigue el progrma
    public void enviar(String cuerpo, String titulo, String mail){
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(mail);
        mensaje.setFrom("no-reply@tinderdemascotas.com");
        mensaje.setSubject(titulo);
        mensaje.setText(cuerpo);
        
        
        mailSender.send(mensaje);
    }

}
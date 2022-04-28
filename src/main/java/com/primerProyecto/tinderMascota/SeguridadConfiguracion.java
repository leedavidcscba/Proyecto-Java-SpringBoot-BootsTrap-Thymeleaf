/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.primerProyecto.tinderMascota;

import com.primerProyecto.tinderMascota.servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 *
 * @author leedavidcuellar
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class SeguridadConfiguracion extends WebSecurityConfigurerAdapter {
    
    @Autowired
    public UsuarioServicio usuarioServicio;
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)throws Exception{//indica un encriptador para claves para autentificar
        auth
                .userDetailsService(usuarioServicio)
                .passwordEncoder(new BCryptPasswordEncoder());
    }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.headers().frameOptions().sameOrigin().and()
                    .authorizeRequests()
                    .antMatchers("/css/*", "/js/*", "/img/*", "/**")//caulquiera pueda acceder a css, js, img
                    .permitAll()
                    
	.and().formLogin()    //configuramos el método login, a través d q url se va a acceder al login
                .loginPage("/login")                    
                        .loginProcessingUrl("/logincheck")  //configuramos cual es la url q va a usar SS para procesar o autenticar algún usuario --> esa url es la que hay q usar en el formulario de nstra pag en html
                        .usernameParameter("username")      //establecemos con que nombre van a viajar los parametros de nombre d usuario y clave	
                        .passwordParameter("password")
                        .defaultSuccessUrl("/inicio")
                    .permitAll()
                    
                    .and().logout()
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
         .and().csrf()//sin necesidad de estar logueado o tener algún permiso específico
         .disable();//sin necesidad de estar logueado o tener algún permiso específico
        }
    
    

}

    

//        @Override
//        public void configure(AuthenticationManagerBuilder auth) throws Exception {
//            auth.inMemoryAuthentication()
//                    .withUser("user").password("{noop}password").roles("USER").and()
//                    .withUser("admin").password("{noop}password").roles("ADMIN");
//        }
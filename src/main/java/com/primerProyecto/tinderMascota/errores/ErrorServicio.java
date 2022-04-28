/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.primerProyecto.tinderMascota.errores;

/**
 *
 * @author leedavidcuellar
 */
public class ErrorServicio extends Exception{
    // se crea para diferencias errores nuestra logica  de negocios de los del sistema
    public ErrorServicio(String msn){
        super(msn);
    }
}

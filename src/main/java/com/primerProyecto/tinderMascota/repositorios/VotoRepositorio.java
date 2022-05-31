/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.primerProyecto.tinderMascota.repositorios;

import com.primerProyecto.tinderMascota.entidades.Voto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


/**
 *
 * @author leedavidcuellar
 */
@Repository
public interface VotoRepositorio extends JpaRepository<Voto, String> {
    
    @Query("SELECT c FROM Voto c WHERE c.mascota1.id = :id ORDER BY c.fecha DESC")
    public List<Voto> buscarVotosPropios(@Param("id") String id);
    
    @Query("SELECT c FROM Voto c WHERE c.mascota2.id = :id ORDER BY c.fecha DESC")
    public List<Voto> buscarVotosRecibidos(@Param("id") String id);
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.primerProyecto.tinderMascota.repositorios;

import com.primerProyecto.tinderMascota.entidades.Mascota;
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
public interface MascotasRepositorio extends JpaRepository<Mascota, String>{
    @Query("SELECT c FROM Mascota c WHERE c.usuario.id = :id AND c.baja IS NULL")
    public List<Mascota> buscarMascotaPorUsuario(@Param("id") String id);
    
    @Query("SELECT c FROM Mascota c WHERE c.baja IS NULL AND c.usuario.id != :id")
    public List<Mascota> listarTodasMascotas(@Param("id") String id);
}
    
package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario,Long> {
    @Override
    Optional<Usuario> findById(Long usuario_id);

    @Query("select u from Usuario u where u.nombre_usuario = ?1")
    Optional<Usuario> findByNombre(String nombre_usuario);

    @Query("select u from Usuario u where u.email_usuario = ?1")
    Optional<Usuario> findByEmail(String email_usuario);
}

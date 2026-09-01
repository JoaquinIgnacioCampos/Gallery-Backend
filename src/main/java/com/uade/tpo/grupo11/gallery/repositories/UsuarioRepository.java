package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario,Long> {
    @Override
    Optional<Usuario> findById(Long usuario_id);
    Optional<Usuario> findByNombre(String nombre_usuario);
    Optional<Usuario> findByEmail(String email_usuario);
}

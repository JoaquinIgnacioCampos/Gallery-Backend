package com.uade.tpo.grupo11.gallery.repositories;

import com.uade.tpo.grupo11.gallery.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Habla con la base. Al extender JpaRepository, el CRUD basico ya viene hecho.
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario,Long> {
    // Sobrescribimos el metodo de JpaRepository para traer tambien el usuario en la misma consulta.
    @Override
    Optional<Usuario> findById(Long usuario_id);

    // Busca un usuario por su nombre de usuario. Se usa para el login y para no repetir datos.
    @Query("select u from Usuario u where u.nombre_usuario = ?1")
    Optional<Usuario> findByNombre(String nombre_usuario);

    // Busca un usuario por su email. Se usa para el login y para no repetir datos.
    @Query("select u from Usuario u where u.email_usuario = ?1")
    Optional<Usuario> findByEmail(String email_usuario);
}

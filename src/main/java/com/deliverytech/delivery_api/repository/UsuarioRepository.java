package com.deliverytech.delivery_api.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.deliverytech.delivery_api.model.Usuario;

public interface UsuarioRepository  extends JpaRepository<Usuario,Long>{

    UserDetails findByEmail(String email);
}

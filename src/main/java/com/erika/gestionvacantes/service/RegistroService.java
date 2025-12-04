package com.erika.gestionvacantes.service;

import com.erika.gestionvacantes.dto.RegistroDTO;
import com.erika.gestionvacantes.model.Aspirante;
import com.erika.gestionvacantes.model.Empleador;
import com.erika.gestionvacantes.model.Usuario;
import com.erika.gestionvacantes.model.enums.Role;
import com.erika.gestionvacantes.repository.AspiranteRepository;
import com.erika.gestionvacantes.repository.EmpleadorRepository;
import com.erika.gestionvacantes.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistroService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AspiranteRepository aspiranteRepository;

    @Autowired
    private EmpleadorRepository empleadorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean existeCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }

    @Transactional
    public Usuario registrarUsuario(RegistroDTO registroDTO) {
        // Validar que el correo no exista
        if (existeCorreo(registroDTO.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        // Validar que las contraseñas coincidan
        if (!registroDTO.getContrasena().equals(registroDTO.getConfirmarContrasena())) {
            throw new RuntimeException("Las contraseñas no coinciden");
        }

        Usuario usuario;

        if (registroDTO.getRol() == Role.ADMIN.ASPIRANTE) {
            usuario = crearAspirante(registroDTO);
        } else if (registroDTO.getRol() == Role.EMPLEADOR) {
            usuario = crearEmpleador(registroDTO);
        } else {
            throw new RuntimeException("Rol no válido para registro público");
        }

        return usuario;
    }

    private Aspirante crearAspirante(RegistroDTO dto) {
        Aspirante aspirante = new Aspirante();
        aspirante.setNombre(dto.getNombre());
        aspirante.setCorreo(dto.getCorreo());
        aspirante.setContrasenaHash(passwordEncoder.encode(dto.getContrasena()));
        aspirante.setRol(Role.ASPIRANTE);
        aspirante.setHabilidades(dto.getHabilidades());
        aspirante.setActivo(true);

        return aspiranteRepository.save(aspirante);
    }

    private Empleador crearEmpleador(RegistroDTO dto) {
        if (dto.getEmpresa() == null || dto.getEmpresa().trim().isEmpty()) {
            throw new RuntimeException("El nombre de la empresa es obligatorio para empleadores");
        }

        Empleador empleador = new Empleador();
        empleador.setNombre(dto.getNombre());
        empleador.setCorreo(dto.getCorreo());
        empleador.setContrasenaHash(passwordEncoder.encode(dto.getContrasena()));
        empleador.setRol(Role.EMPLEADOR);
        empleador.setEmpresa(dto.getEmpresa());
        empleador.setActivo(true);

        return empleadorRepository.save(empleador);
    }
}

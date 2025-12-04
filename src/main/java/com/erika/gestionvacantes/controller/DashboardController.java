package com.erika.gestionvacantes.controller;

import com.erika.gestionvacantes.model.Usuario;
import com.erika.gestionvacantes.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/dashboard-redirect")
    public String redirectToDashboard(Authentication authentication) {
        String correo = authentication.getName();
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return switch (usuario.getRol()) {
            case EMPLEADOR -> "redirect:/empleador/dashboard";
            case ASPIRANTE -> "redirect:/aspirante/dashboard";
            case ADMIN -> "redirect:/admin/dashboard";
        };
    }
}

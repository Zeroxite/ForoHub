package com.zagirox.ForoHub.controller;

import com.zagirox.ForoHub.dto.LoginRequest;
import com.zagirox.ForoHub.dto.RegisterRequest;
import com.zagirox.ForoHub.model.Usuario;
import com.zagirox.ForoHub.repository.UsuarioRepository;
import com.zagirox.ForoHub.security.JwtTokenUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail());
        if (usuario != null && passwordEncoder.matches(loginRequest.getContrasena(), usuario.getContrasena())) {
            String token = jwtTokenUtil.generateToken(usuario);
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.status(401).body("Credenciales inválidas");
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        if (usuarioRepository.findByEmail(registerRequest.getEmail()) != null) {
            return ResponseEntity.badRequest().body("El email ya está en uso");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(registerRequest.getNombre());
        usuario.setEmail(registerRequest.getEmail());
        usuario.setContrasena(passwordEncoder.encode(registerRequest.getContrasena()));
        usuario.setRol("USER");
        usuarioRepository.save(usuario);
        return ResponseEntity.ok("Usuario registrado con éxito");
    }
}
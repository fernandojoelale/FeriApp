package com.feriapp.backend.service

import com.feriapp.backend.config.JwtService
import com.feriapp.backend.dto.AuthRequest
import com.feriapp.backend.dto.AuthResponse
import com.feriapp.backend.dto.RegisterRequest
import com.feriapp.backend.model.Usuario
import com.feriapp.backend.repository.UsuarioRepository
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class AuthService(
    private val usuarioRepository: UsuarioRepository,
    private val jwtService: JwtService,
    private val passwordEncoder: PasswordEncoder
) {
    fun registrarUsuario(request: RegisterRequest): AuthResponse {
        if (usuarioRepository.findByEmail(request.email) != null) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "El email ya está registrado")
        }

        val hashedPassword = passwordEncoder.encode(request.password)
            ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al encriptar contraseña")

        val nuevoUsuario = usuarioRepository.save(
            Usuario(
                nombre = request.nombre,
                email = request.email,
                password = hashedPassword,
                rol = request.rol,
                rubro = request.rubro
            )
        )

        val token = jwtService.generarToken(nuevoUsuario)
        return AuthResponse(token = token, rol = nuevoUsuario.rol.name)
    }

    fun autenticarUsuario(request: AuthRequest): AuthResponse {
        // 1. Buscar usuario en PostgreSQL
        val usuario = usuarioRepository.findByEmail(request.email)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas")

        // 2. Verificar la contraseña encriptada
        if (!passwordEncoder.matches(request.password, usuario.password)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas")
        }

        // 3. Generar token y armar respuesta
        val token = jwtService.generarToken(usuario)
        return AuthResponse(token = token, rol = usuario.rol.name)
    }
}

package com.feriapp.backend.controller

import com.feriapp.backend.exception.SolicitudInvalidaException
import com.feriapp.backend.model.Usuario
import com.feriapp.backend.repository.UsuarioRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class TokenPushRequest(val token: String)

/** Guarda o borra el token de Firebase del celular del usuario logueado (para RF04). */
@RestController
@RequestMapping("/api/usuarios/me/fcm-token")
class DispositivoController(
    private val usuarioRepository: UsuarioRepository
) {

    @PutMapping
    @Transactional
    fun registrar(
        @AuthenticationPrincipal usuario: Usuario,
        @RequestBody request: TokenPushRequest
    ): ResponseEntity<Void> {
        val token = request.token.trim()
        if (token.isEmpty()) throw SolicitudInvalidaException("El token es obligatorio")
        // Un celular recibe los avisos de una sola cuenta: se le quita el token a las demás.
        usuarioRepository.findByFcmToken(token)
            .filter { it.id != usuario.id }
            .forEach { usuarioRepository.save(it.copy(fcmToken = null)) }
        usuarioRepository.save(usuario.copy(fcmToken = token))
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping
    fun borrar(@AuthenticationPrincipal usuario: Usuario): ResponseEntity<Void> {
        usuarioRepository.save(usuario.copy(fcmToken = null))
        return ResponseEntity.noContent().build()
    }
}

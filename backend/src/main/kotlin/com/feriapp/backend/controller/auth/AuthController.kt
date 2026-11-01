package com.feriapp.backend.controller.auth

import com.feriapp.backend.dto.AuthRequest
import com.feriapp.backend.dto.AuthResponse
import com.feriapp.backend.dto.RegisterRequest
import com.feriapp.backend.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        val response = authService.registrarUsuario(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/login")
    fun login(@RequestBody request: AuthRequest): ResponseEntity<AuthResponse> {
        val response = authService.autenticarUsuario(request)
        return ResponseEntity.ok(response)
    }
}

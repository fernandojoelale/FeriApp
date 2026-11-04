package com.feriapp.backend.config

import com.feriapp.backend.model.Usuario
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.security.Key
import java.util.Date

@Service
class JwtService(
    @Value("\${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private val secret: String,
    @Value("\${jwt.expiration:86400000}")
    private val jwtExpiration: Long
) {
    private val key: Key by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))
    }

    fun generarToken(usuario: Usuario): String {
        return Jwts.builder()
            .setSubject(usuario.email)
            .claim("rol", usuario.rol.name)
            .claim("nombre", usuario.nombre)
            .claim("id", usuario.id)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun extraerEmail(token: String): String? {
        return extraerClaims(token)?.subject
    }

    fun extraerRol(token: String): String? {
        return extraerClaims(token)?.get("rol", String::class.java)
    }

    fun esTokenValido(token: String, usuario: Usuario): Boolean {
        val email = extraerEmail(token)
        return email == usuario.email && !estaExpirado(token)
    }

    private fun estaExpirado(token: String): Boolean {
        val expiration = extraerClaims(token)?.expiration ?: return true
        return expiration.before(Date())
    }

    fun extraerClaims(token: String): Claims? {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body
        } catch (e: Exception) {
            null
        }
    }
}

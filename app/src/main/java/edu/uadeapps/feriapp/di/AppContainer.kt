package edu.uadeapps.feriapp.di

import android.content.Context
import edu.uadeapps.feriapp.data.local.FeriasCache
import edu.uadeapps.feriapp.data.local.SessionManager
import edu.uadeapps.feriapp.data.remote.FeriAppApi
import edu.uadeapps.feriapp.data.remote.RetrofitClient
import edu.uadeapps.feriapp.data.repository.AuthRepositoryImpl
import edu.uadeapps.feriapp.data.repository.DetalleFeriaRepositoryImpl
import edu.uadeapps.feriapp.data.repository.FeriaRepository
import edu.uadeapps.feriapp.data.repository.ReservaRepositoryImpl
import edu.uadeapps.feriapp.domain.repository.AuthRepository
import edu.uadeapps.feriapp.domain.repository.DetalleFeriaRepository
import edu.uadeapps.feriapp.domain.repository.ReservaRepository
import edu.uadeapps.feriapp.domain.usecase.CerrarSesionUseCase
import edu.uadeapps.feriapp.domain.usecase.CrearFeriaUseCase
import edu.uadeapps.feriapp.domain.usecase.ObtenerMisFeriasUseCase
import edu.uadeapps.feriapp.domain.usecase.IniciarSesionUseCase
import edu.uadeapps.feriapp.domain.usecase.ObservarSesionUseCase
import edu.uadeapps.feriapp.domain.usecase.ObtenerDetalleFeriaUseCase
import edu.uadeapps.feriapp.domain.usecase.ObtenerMisReservasUseCase
import edu.uadeapps.feriapp.domain.usecase.ObtenerReservaUseCase
import edu.uadeapps.feriapp.domain.usecase.RegistrarUsuarioUseCase
import edu.uadeapps.feriapp.domain.usecase.ReservarPuestoUseCase
import kotlinx.coroutines.flow.firstOrNull

/** Inyección de dependencias manual: se crea una vez en FeriApp y la usan los ViewModels. */
interface AppContainer {
    val feriaRepository: FeriaRepository
    val iniciarSesionUseCase: IniciarSesionUseCase
    val registrarUsuarioUseCase: RegistrarUsuarioUseCase
    val observarSesionUseCase: ObservarSesionUseCase
    val cerrarSesionUseCase: CerrarSesionUseCase
    val obtenerDetalleFeriaUseCase: ObtenerDetalleFeriaUseCase
    val reservarPuestoUseCase: ReservarPuestoUseCase
    val obtenerMisReservasUseCase: ObtenerMisReservasUseCase
    val obtenerReservaUseCase: ObtenerReservaUseCase
    val crearFeriaUseCase: CrearFeriaUseCase
    val obtenerMisFeriasUseCase: ObtenerMisFeriasUseCase
}

class AppDataContainer(context: Context) : AppContainer {

    private val sessionManager = SessionManager(context.applicationContext)

    private val retrofitClient = RetrofitClient(
        tokenProvider = { sessionManager.session.firstOrNull()?.token }
    )

    private val api: FeriAppApi = retrofitClient.feriaApi

    private val authRepository: AuthRepository =
        AuthRepositoryImpl(retrofitClient.authApi, sessionManager)
    private val detalleFeriaRepository: DetalleFeriaRepository = DetalleFeriaRepositoryImpl(api)
    private val reservaRepository: ReservaRepository = ReservaRepositoryImpl(api)

    override val feriaRepository: FeriaRepository by lazy { FeriaRepository(api, FeriasCache(context.applicationContext)) }

    override val iniciarSesionUseCase by lazy { IniciarSesionUseCase(authRepository) }
    override val registrarUsuarioUseCase by lazy { RegistrarUsuarioUseCase(authRepository) }
    override val observarSesionUseCase by lazy { ObservarSesionUseCase(authRepository) }
    override val cerrarSesionUseCase by lazy { CerrarSesionUseCase(authRepository) }
    override val obtenerDetalleFeriaUseCase by lazy { ObtenerDetalleFeriaUseCase(detalleFeriaRepository) }
    override val reservarPuestoUseCase by lazy { ReservarPuestoUseCase(reservaRepository, authRepository) }
    override val obtenerMisReservasUseCase by lazy { ObtenerMisReservasUseCase(reservaRepository) }
    override val obtenerReservaUseCase by lazy { ObtenerReservaUseCase(reservaRepository) }
    override val crearFeriaUseCase by lazy { CrearFeriaUseCase(detalleFeriaRepository, authRepository) }
    override val obtenerMisFeriasUseCase by lazy { ObtenerMisFeriasUseCase(detalleFeriaRepository) }
}

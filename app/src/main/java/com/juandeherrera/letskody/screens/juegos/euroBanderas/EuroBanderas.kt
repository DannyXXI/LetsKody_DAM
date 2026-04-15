package com.juandeherrera.letskody.screens.juegos.euroBanderas

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.room.Room
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.juandeherrera.letskody.R
import com.juandeherrera.letskody.localdb.AppDB
import com.juandeherrera.letskody.localdb.Estructura
import com.juandeherrera.letskody.metodosAuxiliares.componentes.BarraNavegacionInferior
import com.juandeherrera.letskody.metodosAuxiliares.componentes.BarraSuperiorSinMenuLateral
import com.juandeherrera.letskody.metodosAuxiliares.componentes.MensajeSnackbarHost
import com.juandeherrera.letskody.metodosAuxiliares.componentes.ModalInactividadJuego
import com.juandeherrera.letskody.metodosAuxiliares.componentes.ModalPuntuacionEuroBanderas
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.cerrarSesionUsuario
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.juegos.AccionPuntuacion
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.juegos.GestorPuntuacionEuroBanderas
import com.juandeherrera.letskody.navigation.AppScreens
import com.juandeherrera.letskody.viewModels.euroBanderas.EstadoJuego
import com.juandeherrera.letskody.viewModels.euroBanderas.EuroBanderasViewModel
import com.juandeherrera.letskody.viewModels.euroBanderas.EuroBanderasViewModelFactory
import kotlinx.coroutines.launch

// ═════════════════════════════════════════════════════════════════════════════
// PANTALLA PRINCIPAL — EURO BANDERAS
//
// Este Composable es el punto de entrada de la pantalla. Su única
// responsabilidad es:
//   1. Obtener el usuario y la instancia de la base de datos.
//   2. Crear (o recuperar) el ViewModel.
//   3. Observar los flows del ViewModel y pasarlos a los componentes visuales.
//   4. Reaccionar a eventos de navegación (inactividad, fin de partida...).
//   5. Mostrar el Snackbar con el mensaje del resultado del guardado.
//
// TODA la lógica del juego vive en EuroBanderasViewModel.
// TODOS los componentes visuales reutilizables están en ComponentesEuroBanderas.
// ═════════════════════════════════════════════════════════════════════════════
@SuppressLint("DefaultLocale")
@RequiresApi(value = Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PantallaEuroBanderas(controladorNavegacion: NavController) {

    val badcomic          = FontFamily(Font(R.font.badcomic))
    val context           = LocalContext.current



    // ── Base de datos ─────────────────────────────────────────────────────────
    // Se envuelve en "remember" para que solo se cree una vez y no se
    // reconstruya en cada recomposición de la pantalla.
    val db = remember {
        Room.databaseBuilder(context, AppDB::class.java, Estructura.DB.NAME)
            .allowMainThreadQueries()
            .build()
    }

    // ── Usuario ───────────────────────────────────────────────────────────────
    // collectAsState convierte el Flow de la BD en un State de Compose.
    // La pantalla se recompone automáticamente cuando el usuario cambia.
    // initial = null significa que mientras carga, el valor es null.
    val usuario by db.usuarioDao().getUser().collectAsState(initial = null)

    // Si el usuario aún no ha cargado, mostrar spinner y salir
    if (usuario == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // ── ViewModel ─────────────────────────────────────────────────────────────
    // viewModel() recupera el ViewModel existente o lo crea si no existe.
    // La Factory le indica cómo crearlo pasándole la base de datos.
    // El ViewModel sobrevive a recomposiciones y rotaciones de pantalla.
    val viewModel: EuroBanderasViewModel = viewModel(
        factory = EuroBanderasViewModelFactory(db)
    )

    // ── Observar los flows del ViewModel ──────────────────────────────────────
    // Cada vez que alguno de estos valores cambia en el ViewModel,
    // Compose redibuja automáticamente las partes de la UI que los usan.
    val estado                 by viewModel.estado.collectAsState()
    val mostrarDialogoInactivo by viewModel.mostrarModalInactividad.collectAsState()
    val cuentaAtras            by viewModel.cuentaAtrasInactividad.collectAsState()
    val resultado              by viewModel.resultado.collectAsState()

    // Estado local solo para la UI (no pertenece a la lógica del juego)
    var abrirToolbar by remember { mutableStateOf(false) }

    // ── Efecto de inicio: arrancar el juego al entrar en la pantalla ──────────
    // LaunchedEffect con key = Unit se ejecuta UNA SOLA VEZ cuando el Composable
    // entra en composición. Es el lugar correcto para disparar acciones de inicio.
    LaunchedEffect(Unit) {
        viewModel.iniciarJuego()
    }

    // ── Efecto de vigilancia: si la cuenta atrás llega a 0 → salir ───────────
    // Se re-ejecuta cada vez que cambia "cuentaAtras" o "mostrarDialogoInactivo".
    // Si el diálogo está visible Y la cuenta llegó a 0, navega al Inicio.
    LaunchedEffect(cuentaAtras, mostrarDialogoInactivo) {
        if (mostrarDialogoInactivo && cuentaAtras <= 0) {
            controladorNavegacion.navigate(AppScreens.Inicio.route) {
                popUpTo(id = 0) { inclusive = true }  // limpiar el backstack completo
            }
        }
    }

    Scaffold(
        topBar = {
            BarraSuperiorSinMenuLateral(
                titulo                = "Euro-banderas",
                fuenteTipografica     = badcomic,
                controladorNavegacion = controladorNavegacion,
                estadoMenuDesplegable = abrirToolbar,
                abrirMenuDesplegable  = { abrirToolbar = true },
                cerrarMenuDesplegable = { abrirToolbar = false },
                cerrarSesionUsuario   = {
                    cerrarSesionUsuario(db = db, usuario = usuario!!)
                    controladorNavegacion.navigate(AppScreens.Login.route) {
                        popUpTo(id = 0) { inclusive = true }
                    }
                }
            )
        },
        bottomBar = {
            BarraNavegacionInferior(
                fuenteTipografica     = badcomic,
                controladorNavegacion = controladorNavegacion,
                selectInicio          = false,
                selectMaterias        = true,
                selectPerfil          = false
            )
        }
    ) { innerPadding ->

        // ── Contenedor principal con detección de actividad ───────────────────
        // pointerInput captura CUALQUIER evento táctil sobre este Box.
        // Al detectar un toque, avisa al ViewModel para reiniciar el timer de inactividad.
        // awaitPointerEventScope + awaitPointerEvent forman un bucle infinito que
        // escucha eventos táctiles sin consumirlos (la UI sigue recibiendo los toques).
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFC2DAFD))
                .padding(paddingValues = innerPadding)
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            awaitPointerEvent()              // esperar cualquier toque
                            viewModel.registrarActividad()   // notificar al ViewModel
                        }
                    }
                }
        ) {

            // ── Reaccionar al estado del juego ────────────────────────────────
            // "when" sobre una sealed class: el compilador exige cubrir todos los casos
            when (val s = estado) {

                // Cargando: se está leyendo la BD → mostrar spinner
                is EstadoJuego.Cargando -> {
                    Box(
                        modifier         = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                // Error: no hay suficientes banderas en la BD → mensaje de error
                is EstadoJuego.ErrorSinBanderas -> {
                    Box(
                        modifier         = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = "No hay suficientes banderas en la base de datos.",
                            fontFamily = badcomic,
                            color      = Color.Red,
                            fontSize   = 16.sp
                        )
                    }
                }

                // Jugando: mostrar la pregunta actual con su bandera y opciones
                is EstadoJuego.Jugando -> {
                    ContenidoJugando(
                        estado            = s,
                        fuenteTipografica = badcomic,
                        onResponder       = { opcion -> viewModel.responder(opcion) }
                    )
                }

                // JuegoTerminado: pantalla vacía mientras se muestra el AlertDialog
                is EstadoJuego.JuegoTerminado -> {
                    Box(modifier = Modifier.fillMaxSize())
                }
            }

            // ── Diálogo de inactividad (superpuesto sobre el contenido) ───────
            // Se muestra encima del juego si el usuario lleva 20s sin tocar la pantalla.
            // El cronómetro del juego sigue corriendo por debajo.
            if (mostrarDialogoInactivo) {
                ModalInactividadJuego(
                    cuentaAtras = cuentaAtras,
                    fuenteTipografica = badcomic,
                    continuar = {
                        viewModel.cerrarModalInactividad()   // cierra el diálogo y reinicia el timer
                    }
                )
            }

            // ── Diálogo de resultado final (superpuesto al terminar) ──────────
            // Solo se muestra cuando el estado es JuegoTerminado Y ya hay resultado calculado.
            if (estado is EstadoJuego.JuegoTerminado && resultado != null) {
                ModalPuntuacionEuroBanderas(
                    resultado         = resultado!!,
                    fuenteTipografica = badcomic,
                    repetir         = {
                        // Reiniciar el juego desde 0 sin salir de la pantalla
                        viewModel.iniciarJuego()
                    },
                    guardarYsalir   = {
                        // ── Guardar puntuación en local y Firebase ────────────
                        // guardarPuntuacion devuelve un AccionPuntuacion con
                        // el campo ".mensaje" que contiene el texto exacto a mostrar.
                        val accion = GestorPuntuacionEuroBanderas.guardarPuntuacion(
                            db          = db,
                            uidUsuario  = usuario!!.uidUsuario,
                            puntos      = resultado!!.puntos,
                            tiempoTotal = resultado!!.tiempoTotal
                        )



                        // Navegar al Inicio tras guardar
                        controladorNavegacion.navigate(AppScreens.Inicio.route) {
                            popUpTo(id = 0) { inclusive = true }
                        }
                    }
                )







            }
        }
    }
}


// ═════════════════════════════════════════════════════════════════════════════
// CONTENIDO DURANTE LA PARTIDA
//
// Composable privado que agrupa todo lo que se ve mientras el juego está
// en marcha: cabecera, imagen de la bandera, pregunta y los 4 botones.
//
// Se separó de PantallaEuroBanderas para mantener esa función más limpia
// y para que este bloque solo se recomponga cuando el estado "Jugando" cambie.
// ═════════════════════════════════════════════════════════════════════════════
@Composable
private fun ContenidoJugando(
    estado            : EstadoJuego.Jugando,  // estado actual del juego con todos los datos
    fuenteTipografica : FontFamily,
    onResponder       : (String) -> Unit       // callback que envía la respuesta al ViewModel
) {
    Column(
        modifier            = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── Cabecera: cronómetro, progreso y puntos ───────────────────────────
        CabeceraJuego(
            segundos          = estado.tiempoCronometro,
            numeroPregunta    = estado.numeroPregunta,
            totalPreguntas    = 12,
            puntos            = estado.puntos,
            fuenteTipografica = fuenteTipografica
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ── Imagen de la bandera ──────────────────────────────────────────────
        ImagenBandera(urlBandera = estado.preguntaActual.urlBandera)

        Spacer(modifier = Modifier.height(8.dp))

        // ── Texto de la pregunta ──────────────────────────────────────────────
        Text(
            text       = "¿De qué país es esta bandera?",
            fontFamily = fuenteTipografica,
            fontSize   = 20.sp,
            color      = Color(0xFF0D1B2A)
        )

        // ── Los 4 botones de respuesta ────────────────────────────────────────
        // Se recorre la lista de opciones (ya mezclada por el ViewModel)
        // y se crea un botón para cada una.
        Column(
            modifier            = Modifier.padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            estado.opciones.forEach { opcion ->
                BotonOpcion(
                    texto                 = opcion,
                    respuestaSeleccionada = estado.respuestaSeleccionada,  // null si no respondió aún
                    opcionCorrecta        = estado.opcionCorrecta,
                    fuenteTipografica     = fuenteTipografica,
                    onClick               = { onResponder(opcion) }        // enviar respuesta al ViewModel
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
package com.juandeherrera.letskody.screens.miscelanea

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.room.Room
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.juandeherrera.letskody.R
import com.juandeherrera.letskody.localdb.AppDB
import com.juandeherrera.letskody.localdb.Estructura
import com.juandeherrera.letskody.metodosAuxiliares.componentes.BarraNavegacionInferior
import com.juandeherrera.letskody.metodosAuxiliares.componentes.BarraSuperiorSinMenuLateral
import com.juandeherrera.letskody.metodosAuxiliares.componentes.miscelanea.LienzoInteractivo
import com.juandeherrera.letskody.metodosAuxiliares.operaciones.cerrarSesionUsuario
import com.juandeherrera.letskody.navigation.AppScreens

@SuppressLint("DefaultLocale")
@RequiresApi(value = Build.VERSION_CODES.TIRAMISU) // solo se permite Android 13 o superior (API 33+)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PantallaDrawArena(controladorNavegacion: NavController) {
    val badcomic = FontFamily(Font(R.font.badcomic))  // fuente tipográfica por defecto

    val scope = rememberCoroutineScope() // variable que crea un ámbito de corrutinas que se mantienen en la recomposición de la interfaz

    val context = LocalContext.current // variable que obtiene el contexto actual

    // instancia a la base de datos local (en el mismo hilo)
    val db = Room.databaseBuilder(context, klass = AppDB::class.java, name = Estructura.DB.NAME).allowMainThreadQueries().build()

    val usuario by db.usuarioDao().getUser().collectAsState(initial = null)  // variable que contiene los datos del usuario

    when (usuario) {
        null -> {
            // si no existe el usuario o esta cargando el usuario
            Box(
                modifier = Modifier.fillMaxSize(),  // ocupa la pantalla completa
                contentAlignment = Alignment.Center // contenido centrado en el medio
            ) {
                CircularProgressIndicator() // spinner girando de manera infinita
            }
            return
        }
        else -> { /* aquí ya existe el usuario */ }
    }

    var abrirToolbar by remember { mutableStateOf(value = false) } // variable para el estado (abrir/cerrar) del menu desplegable del toolbar

    Scaffold(
        // BARRA SUPERIOR
        topBar = {
            BarraSuperiorSinMenuLateral(
                titulo = "Draw Arena",
                fuenteTipografica = badcomic,
                controladorNavegacion = controladorNavegacion,
                estadoMenuDesplegable = abrirToolbar,
                abrirMenuDesplegable = { abrirToolbar = true },
                cerrarMenuDesplegable = { abrirToolbar = false },
                cerrarSesionUsuario = {
                    cerrarSesionUsuario(db = db, usuario = usuario!!)  // se cierra la sesión de Firebase y se borran los datos locales
                    controladorNavegacion.navigate(route = AppScreens.Login.route) { popUpTo(id = 0) {inclusive = true} }
                }
            )
        },
        // BARRA INFERIOR
        bottomBar = {
            BarraNavegacionInferior(
                fuenteTipografica = badcomic,
                controladorNavegacion = controladorNavegacion,
                selectInicio = false,
                selectMaterias = true,
                selectPerfil = false
            )
        }
    ){
        innerPadding ->

        Column(
            modifier = Modifier.fillMaxSize()                    // se ocupa la pantalla completa
                .background(Color(0xFFC2DAFD))                   // color de fondo
                .padding(paddingValues = innerPadding)           // padding por defecto
                .padding(horizontal = 10.dp),                    // padding horizontal adicional
            horizontalAlignment = Alignment.CenterHorizontally,  // centrado horizontal
            verticalArrangement = Arrangement.Top                // centrado vertical
        ){
            // lienzo interactivo con guardado en galería
            LienzoInteractivo(context = context, scope = scope, fuenteTipografica = badcomic)
        }
    }
}
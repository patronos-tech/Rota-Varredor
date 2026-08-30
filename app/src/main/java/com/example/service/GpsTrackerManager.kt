package com.example.service

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import com.example.data.TrechoEntity
import com.example.model.PontoGps
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GpsTrackerManager(private val context: Context) {

    private val _posicaoAtual = MutableStateFlow<PontoGps>(
        PontoGps(lat = -23.55050, lon = -46.63330) // Posição inicial Depósito Sé
    )
    val posicaoAtual: StateFlow<PontoGps> = _posicaoAtual.asStateFlow()

    private val _modoSimulador = MutableStateFlow(true)
    val modoSimulador: StateFlow<Boolean> = _modoSimulador.asStateFlow()

    private val _rastreamentoAtivo = MutableStateFlow(true)
    val rastreamentoAtivo: StateFlow<Boolean> = _rastreamentoAtivo.asStateFlow()

    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null

    init {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    }

    fun setModoSimulador(simulador: Boolean) {
        _modoSimulador.value = simulador
        if (simulador) {
            pararGpsReal()
        } else {
            iniciarGpsReal()
        }
    }

    fun setPosicaoManual(lat: Double, lon: Double) {
        _posicaoAtual.value = PontoGps(lat = lat, lon = lon)
    }

    fun simularAproximacao(trecho: TrechoEntity) {
        // Coloca o gari a ~50 metros antes do início do trecho
        val offsetLat = (trecho.pontoFinalLat - trecho.pontoInicialLat) * 0.35
        val offsetLon = (trecho.pontoFinalLon - trecho.pontoInicialLon) * 0.35
        _posicaoAtual.value = PontoGps(
            lat = trecho.pontoInicialLat - offsetLat,
            lon = trecho.pontoInicialLon - offsetLon
        )
    }

    fun simularEntrarNaRua(trecho: TrechoEntity) {
        // Coloca o gari exatamente no ponto inicial do trecho
        _posicaoAtual.value = PontoGps(
            lat = trecho.pontoInicialLat,
            lon = trecho.pontoInicialLon
        )
    }

    fun simularAvancoNoTrecho(trecho: TrechoEntity, progresso0a1: Float) {
        val lat = trecho.pontoInicialLat + (trecho.pontoFinalLat - trecho.pontoInicialLat) * progresso0a1
        val lon = trecho.pontoInicialLon + (trecho.pontoFinalLon - trecho.pontoInicialLon) * progresso0a1
        _posicaoAtual.value = PontoGps(lat = lat, lon = lon)
    }

    fun simularDesvioForaRota(trecho: TrechoEntity) {
        // Afasta o gari ~180 metros perpendicularmente
        _posicaoAtual.value = PontoGps(
            lat = trecho.pontoInicialLat + 0.00160,
            lon = trecho.pontoInicialLon + 0.00160
        )
    }

    @SuppressLint("MissingPermission")
    private fun iniciarGpsReal() {
        try {
            locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    if (!_modoSimulador.value) {
                        _posicaoAtual.value = PontoGps(
                            lat = location.latitude,
                            lon = location.longitude,
                            precisaoMetros = location.accuracy
                        )
                    }
                }
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }

            locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000L,
                3.0f,
                locationListener!!
            )
        } catch (e: SecurityException) {
            // Em caso de falta de permissão, mantém modo simulador
            _modoSimulador.value = true
        }
    }

    private fun pararGpsReal() {
        locationListener?.let {
            locationManager?.removeUpdates(it)
        }
    }
}

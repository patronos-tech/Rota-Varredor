package com.example.service

import com.example.data.TrechoEntity
import com.example.model.PontoGps
import com.example.model.StatusGpsAlerta
import kotlin.math.*

object LocationMatcher {

    /**
     * Calculates distance in meters between two coordinates using Haversine formula
     */
    fun calcularDistanciaMetros(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0 // Earth radius in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    fun calcularDistancia(p1: PontoGps, p2: PontoGps): Double {
        return calcularDistanciaMetros(p1.lat, p1.lon, p2.lat, p2.lon)
    }

    /**
     * Calculates distance from a point to a line segment (initial -> final)
     */
    fun distanciaPontoParaSegmento(
        pontoLat: Double, pontoLon: Double,
        inicioLat: Double, inicioLon: Double,
        fimLat: Double, fimLon: Double
    ): Double {
        val distInicio = calcularDistanciaMetros(pontoLat, pontoLon, inicioLat, inicioLon)
        val distFim = calcularDistanciaMetros(pontoLat, pontoLon, fimLat, fimLon)
        val compSegmento = calcularDistanciaMetros(inicioLat, inicioLon, fimLat, fimLon)

        if (compSegmento < 1.0) return distInicio

        // Projection factor t
        val dx = fimLon - inicioLon
        val dy = fimLat - inicioLat
        val t = ((pontoLon - inicioLon) * dx + (pontoLat - inicioLat) * dy) / (dx * dx + dy * dy)

        return when {
            t <= 0.0 -> distInicio
            t >= 1.0 -> distFim
            else -> {
                val projLat = inicioLat + t * dy
                val projLon = inicioLon + t * dx
                calcularDistanciaMetros(pontoLat, pontoLon, projLat, projLon)
            }
        }
    }

    /**
     * Evaluates the GPS alert status for the active segment
     */
    fun avaliarStatusGps(
        posicaoAtual: PontoGps?,
        trechoAtivo: TrechoEntity?,
        proximoTrecho: TrechoEntity? = null
    ): Pair<StatusGpsAlerta, Double> {
        if (posicaoAtual == null || (trechoAtivo == null && proximoTrecho == null)) {
            return Pair(StatusGpsAlerta.CINZA, 0.0)
        }

        val target = trechoAtivo ?: proximoTrecho!!
        val distParaSegmento = distanciaPontoParaSegmento(
            posicaoAtual.lat, posicaoAtual.lon,
            target.pontoInicialLat, target.pontoInicialLon,
            target.pontoFinalLat, target.pontoFinalLon
        )

        val distParaInicio = calcularDistanciaMetros(
            posicaoAtual.lat, posicaoAtual.lon,
            target.pontoInicialLat, target.pontoInicialLon
        )

        val distanciaRelevante = min(distParaSegmento, distParaInicio)

        return when {
            distanciaRelevante <= 28.0 -> Pair(StatusGpsAlerta.VERDE, distanciaRelevante)
            distanciaRelevante <= 95.0 -> Pair(StatusGpsAlerta.AMARELO, distanciaRelevante)
            else -> Pair(StatusGpsAlerta.VERMELHO, distanciaRelevante)
        }
    }
}

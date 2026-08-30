package com.example.service

import com.example.data.SetorEntity
import com.example.data.TrechoEntity
import com.example.model.PontoGps

object RoutingOptimizer {

    /**
     * Reorders pending segments greedily (Nearest Neighbor heuristic)
     * starting from current worker location or cart depot, ending near cart dropoff point.
     */
    fun otimizarSequenciaTrechos(
        posicaoOrigem: PontoGps,
        setor: SetorEntity,
        trechosPendentes: List<TrechoEntity>
    ): List<TrechoEntity> {
        if (trechosPendentes.size <= 1) return trechosPendentes

        val naoVisitados = trechosPendentes.toMutableList()
        val resultado = mutableListOf<TrechoEntity>()
        var pontoAtual = posicaoOrigem

        while (naoVisitados.isNotEmpty()) {
            var melhorTrecho: TrechoEntity? = null
            var menorDistancia = Double.MAX_VALUE

            for (trecho in naoVisitados) {
                val dist = LocationMatcher.calcularDistanciaMetros(
                    pontoAtual.lat, pontoAtual.lon,
                    trecho.pontoInicialLat, trecho.pontoInicialLon
                )
                if (dist < menorDistancia) {
                    menorDistancia = dist
                    melhorTrecho = trecho
                }
            }

            if (melhorTrecho != null) {
                naoVisitados.remove(melhorTrecho)
                resultado.add(melhorTrecho)
                pontoAtual = PontoGps(melhorTrecho.pontoFinalLat, melhorTrecho.pontoFinalLon)
            } else {
                break
            }
        }

        return resultado.mapIndexed { index, trecho ->
            trecho.copy(ordemPlanejada = index + 1)
        }
    }

    fun calcularDistanciaTotalPlanejada(
        setor: SetorEntity,
        trechos: List<TrechoEntity>
    ): Double {
        if (trechos.isEmpty()) return 0.0

        var total = 0.0
        // Distância do ponto de retirada do carrinho até o 1º trecho
        total += LocationMatcher.calcularDistanciaMetros(
            setor.pontoRetiradaLat, setor.pontoRetiradaLon,
            trechos.first().pontoInicialLat, trechos.first().pontoInicialLon
        )

        // Distância de cada trecho + conexões
        for (i in trechos.indices) {
            val t = trechos[i]
            total += t.distanciaEstimadaMetros

            if (i < trechos.size - 1) {
                val proximo = trechos[i + 1]
                total += LocationMatcher.calcularDistanciaMetros(
                    t.pontoFinalLat, t.pontoFinalLon,
                    proximo.pontoInicialLat, proximo.pontoInicialLon
                )
            }
        }

        // Conexão do último trecho até a devolução do carrinho
        total += LocationMatcher.calcularDistanciaMetros(
            trechos.last().pontoFinalLat, trechos.last().pontoFinalLon,
            setor.pontoDevolucaoLat, setor.pontoDevolucaoLon
        )

        return total
    }
}

package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.StatusSetor
import com.example.model.StatusTrecho
import com.example.model.TipoOcorrencia
import com.example.model.TipoPausa

@Entity(tableName = "setores")
data class SetorEntity(
    @PrimaryKey val id: String,
    val codigo: String,
    val nome: String,
    val descricao: String,
    val pontoRetiradaLat: Double,
    val pontoRetiradaLon: Double,
    val pontoRetiradaNome: String,
    val pontoDevolucaoLat: Double,
    val pontoDevolucaoLon: Double,
    val pontoDevolucaoNome: String,
    val status: StatusSetor = StatusSetor.PRONTO,
    val dataCriacao: Long = System.currentTimeMillis(),
    val dataAtualizacao: Long = System.currentTimeMillis()
)

@Entity(tableName = "ruas")
data class RuaEntity(
    @PrimaryKey val id: String,
    val setorId: String,
    val nome: String,
    val ordemSugerida: Int,
    val geometriaJson: String = "[]",
    val status: String = "PENDENTE"
)

@Entity(tableName = "trechos")
data class TrechoEntity(
    @PrimaryKey val id: String,
    val ruaId: String,
    val setorId: String,
    val nomeRua: String,
    val ordemPlanejada: Int,
    val pontoInicialLat: Double,
    val pontoInicialLon: Double,
    val pontoFinalLat: Double,
    val pontoFinalLon: Double,
    val numInicialEstimado: Int,
    val numFinalEstimado: Int,
    val distanciaEstimadaMetros: Double,
    val status: StatusTrecho = StatusTrecho.PENDENTE,
    val geometriaJson: String = "[]"
)

@Entity(tableName = "execucoes_trecho")
data class ExecucaoTrechoEntity(
    @PrimaryKey val id: String,
    val trechoId: String,
    val setorId: String,
    val ruaId: String,
    val nomeRua: String,
    val ordemPlanejada: Int,
    val trabalhadorNome: String = "Gari Operacional",
    val numeroInicial: Int? = null,
    val numeroFinal: Int? = null,
    val horaInicial: Long = System.currentTimeMillis(),
    val horaFinal: Long? = null,
    val gpsInicialLat: Double? = null,
    val gpsInicialLon: Double? = null,
    val gpsFinalLat: Double? = null,
    val gpsFinalLon: Double? = null,
    val distanciaPercorridaMetros: Double = 0.0,
    val duracaoSegundos: Long = 0,
    val status: String = "INICIADO",
    val rotaRealJson: String = "[]"
)

@Entity(tableName = "ocorrencias")
data class OcorrenciaEntity(
    @PrimaryKey val id: String,
    val setorId: String,
    val trechoId: String?,
    val nomeRua: String?,
    val tipo: TipoOcorrencia,
    val observacao: String,
    val lat: Double,
    val lon: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val resolvido: Boolean = false
)

@Entity(tableName = "pausas")
data class PausaEntity(
    @PrimaryKey val id: String,
    val setorId: String,
    val motivo: TipoPausa,
    val observacao: String = "",
    val horaInicio: Long = System.currentTimeMillis(),
    val horaFim: Long? = null,
    val duracaoSegundos: Long = 0,
    val lat: Double,
    val lon: Double
)

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey val id: String,
    val tipoEntidade: String,
    val payloadJson: String,
    val statusSync: String = "PENDENTE", // PENDENTE, SINCRONIZADO, ERRO
    val timestamp: Long = System.currentTimeMillis(),
    val tentativas: Int = 0
)

@Entity(tableName = "historico_sessoes")
data class HistoricoSessaoEntity(
    @PrimaryKey val id: String,
    val setorId: String,
    val nomeSetor: String,
    val dataInicio: Long,
    val dataFim: Long,
    val totalTrechos: Int,
    val trechosCompletados: Int,
    val distanciaTotalMetros: Double,
    val tempoTotalSegundos: Long,
    val totalPausas: Int,
    val totalOcorrencias: Int,
    val eficienciaScore: Int // 0-100
)

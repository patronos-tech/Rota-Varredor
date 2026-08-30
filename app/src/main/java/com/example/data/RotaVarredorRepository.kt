package com.example.data

import android.content.Context
import com.example.model.*
import com.example.service.RoutingOptimizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.UUID

class RotaVarredorRepository(private val db: AppDatabase) {

    val todosSetores: Flow<List<SetorEntity>> = db.setorDao().getAllSetores()
    val pendingSyncCount: Flow<Int> = db.syncQueueDao().getPendingCount()
    val historicoSessoes: Flow<List<HistoricoSessaoEntity>> = db.historicoDao().getAllHistorico()

    fun getRuas(setorId: String): Flow<List<RuaEntity>> = db.ruaDao().getRuasBySetor(setorId)
    fun getTrechos(setorId: String): Flow<List<TrechoEntity>> = db.trechoDao().getTrechosBySetor(setorId)
    fun getExecucoes(setorId: String): Flow<List<ExecucaoTrechoEntity>> = db.execucaoTrechoDao().getExecucoesBySetor(setorId)
    fun getAllExecucoes(): Flow<List<ExecucaoTrechoEntity>> = db.execucaoTrechoDao().getAllExecucoes()
    fun getOcorrencias(setorId: String): Flow<List<OcorrenciaEntity>> = db.ocorrenciaDao().getOcorrenciasBySetor(setorId)
    fun getPausas(setorId: String): Flow<List<PausaEntity>> = db.pausaDao().getPausasBySetor(setorId)

    suspend fun inicializarDadosDemoSeNecessario() = withContext(Dispatchers.IO) {
        val setoresExistentes = db.setorDao().getAllSetores().firstOrNull()
        if (!setoresExistentes.isNullOrEmpty()) return@withContext

        // Criar Setor Demo 01: Setor Centro Histórico e Comercial
        val setorId = "setor_demo_centro_01"
        val setor = SetorEntity(
            id = setorId,
            codigo = "SC-001",
            nome = "Setor 01 — Centro Comercial",
            descricao = "Área comercial e histórica com calçadas largas, comércio e fluxo moderado a pé.",
            pontoRetiradaLat = -23.55050,
            pontoRetiradaLon = -46.63330,
            pontoRetiradaNome = "Depósito Operacional Sé (Retirada do Carrinho)",
            pontoDevolucaoLat = -23.55320,
            pontoDevolucaoLon = -46.63680,
            pontoDevolucaoNome = "Ponto de Descarte & Devolução Boa Vista",
            status = StatusSetor.PRONTO
        )
        db.setorDao().insertSetor(setor)

        // Criar Ruas
        val rua1 = RuaEntity("rua_01", setorId, "Rua Direita", 1)
        val rua2 = RuaEntity("rua_02", setorId, "Rua Quinze de Novembro", 2)
        val rua3 = RuaEntity("rua_03", setorId, "Rua São Bento", 3)
        val rua4 = RuaEntity("rua_04", setorId, "Praça do Patriarca", 4)
        val rua5 = RuaEntity("rua_05", setorId, "Rua Álvares Penteado", 5)
        val rua6 = RuaEntity("rua_06", setorId, "Rua Boa Vista", 6)
        db.ruaDao().insertRuas(listOf(rua1, rua2, rua3, rua4, rua5, rua6))

        // Criar Trechos detalhados com coordenadas geográficas reais em São Paulo
        val trechos = listOf(
            TrechoEntity(
                id = "trecho_01",
                ruaId = "rua_01",
                setorId = setorId,
                nomeRua = "Rua Direita",
                ordemPlanejada = 1,
                pontoInicialLat = -23.55050,
                pontoInicialLon = -46.63330,
                pontoFinalLat = -23.55120,
                pontoFinalLon = -46.63400,
                numInicialEstimado = 1,
                numFinalEstimado = 120,
                distanciaEstimadaMetros = 135.0,
                status = StatusTrecho.PENDENTE
            ),
            TrechoEntity(
                id = "trecho_02",
                ruaId = "rua_01",
                setorId = setorId,
                nomeRua = "Rua Direita",
                ordemPlanejada = 2,
                pontoInicialLat = -23.55120,
                pontoInicialLon = -46.63400,
                pontoFinalLat = -23.55200,
                pontoFinalLon = -46.63480,
                numInicialEstimado = 121,
                numFinalEstimado = 250,
                distanciaEstimadaMetros = 140.0,
                status = StatusTrecho.PENDENTE
            ),
            TrechoEntity(
                id = "trecho_03",
                ruaId = "rua_02",
                setorId = setorId,
                nomeRua = "Rua Quinze de Novembro",
                ordemPlanejada = 3,
                pontoInicialLat = -23.55200,
                pontoInicialLon = -46.63480,
                pontoFinalLat = -23.55150,
                pontoFinalLon = -46.63580,
                numInicialEstimado = 1,
                numFinalEstimado = 90,
                distanciaEstimadaMetros = 120.0,
                status = StatusTrecho.PENDENTE
            ),
            TrechoEntity(
                id = "trecho_04",
                ruaId = "rua_02",
                setorId = setorId,
                nomeRua = "Rua Quinze de Novembro",
                ordemPlanejada = 4,
                pontoInicialLat = -23.55150,
                pontoInicialLon = -46.63580,
                pontoFinalLat = -23.55080,
                pontoFinalLon = -46.63660,
                numInicialEstimado = 91,
                numFinalEstimado = 180,
                distanciaEstimadaMetros = 130.0,
                status = StatusTrecho.PENDENTE
            ),
            TrechoEntity(
                id = "trecho_05",
                ruaId = "rua_03",
                setorId = setorId,
                nomeRua = "Rua São Bento",
                ordemPlanejada = 5,
                pontoInicialLat = -23.55080,
                pontoInicialLon = -46.63660,
                pontoFinalLat = -23.54980,
                pontoFinalLon = -46.63550,
                numInicialEstimado = 200,
                numFinalEstimado = 320,
                distanciaEstimadaMetros = 160.0,
                status = StatusTrecho.PENDENTE
            ),
            TrechoEntity(
                id = "trecho_06",
                ruaId = "rua_04",
                setorId = setorId,
                nomeRua = "Praça do Patriarca",
                ordemPlanejada = 6,
                pontoInicialLat = -23.54980,
                pontoInicialLon = -46.63550,
                pontoFinalLat = -23.54890,
                pontoFinalLon = -46.63460,
                numInicialEstimado = 1,
                numFinalEstimado = 60,
                distanciaEstimadaMetros = 110.0,
                status = StatusTrecho.PENDENTE
            ),
            TrechoEntity(
                id = "trecho_07",
                ruaId = "rua_05",
                setorId = setorId,
                nomeRua = "Rua Álvares Penteado",
                ordemPlanejada = 7,
                pontoInicialLat = -23.54890,
                pontoInicialLon = -46.63460,
                pontoFinalLat = -23.55010,
                pontoFinalLon = -46.63370,
                numInicialEstimado = 1,
                numFinalEstimado = 110,
                distanciaEstimadaMetros = 145.0,
                status = StatusTrecho.PENDENTE
            ),
            TrechoEntity(
                id = "trecho_08",
                ruaId = "rua_06",
                setorId = setorId,
                nomeRua = "Rua Boa Vista",
                ordemPlanejada = 8,
                pontoInicialLat = -23.55010,
                pontoInicialLon = -46.63370,
                pontoFinalLat = -23.55320,
                pontoFinalLon = -46.63680,
                numInicialEstimado = 1,
                numFinalEstimado = 280,
                distanciaEstimadaMetros = 220.0,
                status = StatusTrecho.PENDENTE
            )
        )
        db.trechoDao().insertTrechos(trechos)

        // Criar Histórico Prévio para alimentar aprendizado estatístico
        val sessaoOntem = HistoricoSessaoEntity(
            id = "sessao_ontem_01",
            setorId = setorId,
            nomeSetor = "Setor 01 — Centro Comercial",
            dataInicio = System.currentTimeMillis() - 86400000L * 1 - 7200000L,
            dataFim = System.currentTimeMillis() - 86400000L * 1,
            totalTrechos = 8,
            trechosCompletados = 8,
            distanciaTotalMetros = 1180.0,
            tempoTotalSegundos = 6840L,
            totalPausas = 2,
            totalOcorrencias = 1,
            eficienciaScore = 94
        )
        val sessaoAnteontem = HistoricoSessaoEntity(
            id = "sessao_anteontem_01",
            setorId = setorId,
            nomeSetor = "Setor 01 — Centro Comercial",
            dataInicio = System.currentTimeMillis() - 86400000L * 2 - 7600000L,
            dataFim = System.currentTimeMillis() - 86400000L * 2,
            totalTrechos = 8,
            trechosCompletados = 7,
            distanciaTotalMetros = 1050.0,
            tempoTotalSegundos = 7200L,
            totalPausas = 3,
            totalOcorrencias = 2,
            eficienciaScore = 88
        )
        db.historicoDao().insertHistorico(sessaoOntem)
        db.historicoDao().insertHistorico(sessaoAnteontem)
    }

    suspend fun iniciarTrecho(
        trecho: TrechoEntity,
        numeroInicial: Int,
        gps: PontoGps
    ) = withContext(Dispatchers.IO) {
        val execucaoId = UUID.randomUUID().toString()
        val execucao = ExecucaoTrechoEntity(
            id = execucaoId,
            trechoId = trecho.id,
            setorId = trecho.setorId,
            ruaId = trecho.ruaId,
            nomeRua = trecho.nomeRua,
            ordemPlanejada = trecho.ordemPlanejada,
            numeroInicial = numeroInicial,
            horaInicial = System.currentTimeMillis(),
            gpsInicialLat = gps.lat,
            gpsInicialLon = gps.lon,
            status = "INICIADO"
        )
        db.execucaoTrechoDao().insertExecucao(execucao)
        db.trechoDao().updateTrecho(trecho.copy(status = StatusTrecho.EM_EXECUCAO))

        // Enqueue sync event
        db.syncQueueDao().enqueueItem(
            SyncQueueEntity(
                id = UUID.randomUUID().toString(),
                tipoEntidade = "INICIAR_TRECHO",
                payloadJson = "{\"trechoId\":\"${trecho.id}\",\"numInicial\":$numeroInicial,\"lat\":${gps.lat},\"lon\":${gps.lon}}"
            )
        )
    }

    suspend fun finalizarTrecho(
        trecho: TrechoEntity,
        numeroFinal: Int,
        gps: PontoGps,
        distanciaPercorrida: Double,
        duracaoSegundos: Long
    ) = withContext(Dispatchers.IO) {
        val execucaoAtiva = db.execucaoTrechoDao().getExecucaoAtiva(trecho.id)
        if (execucaoAtiva != null) {
            val atualizada = execucaoAtiva.copy(
                numeroFinal = numeroFinal,
                horaFinal = System.currentTimeMillis(),
                gpsFinalLat = gps.lat,
                gpsFinalLon = gps.lon,
                distanciaPercorridaMetros = distanciaPercorrida,
                duracaoSegundos = duracaoSegundos,
                status = "CONCLUIDO"
            )
            db.execucaoTrechoDao().updateExecucao(atualizada)
        }
        db.trechoDao().updateTrecho(trecho.copy(status = StatusTrecho.CONCLUIDO))

        // Enqueue sync event
        db.syncQueueDao().enqueueItem(
            SyncQueueEntity(
                id = UUID.randomUUID().toString(),
                tipoEntidade = "FINALIZAR_TRECHO",
                payloadJson = "{\"trechoId\":\"${trecho.id}\",\"numFinal\":$numeroFinal,\"duracaoSegundos\":$duracaoSegundos,\"distancia\":$distanciaPercorrida}"
            )
        )
    }

    suspend fun registrarOcorrencia(
        setorId: String,
        trechoId: String?,
        nomeRua: String?,
        tipo: TipoOcorrencia,
        observacao: String,
        gps: PontoGps
    ) = withContext(Dispatchers.IO) {
        val ocorrencia = OcorrenciaEntity(
            id = UUID.randomUUID().toString(),
            setorId = setorId,
            trechoId = trechoId,
            nomeRua = nomeRua,
            tipo = tipo,
            observacao = observacao,
            lat = gps.lat,
            lon = gps.lon
        )
        db.ocorrenciaDao().insertOcorrencia(ocorrencia)
        db.syncQueueDao().enqueueItem(
            SyncQueueEntity(
                id = UUID.randomUUID().toString(),
                tipoEntidade = "OCORRENCIA",
                payloadJson = "{\"tipo\":\"${tipo.name}\",\"obs\":\"$observacao\",\"lat\":${gps.lat},\"lon\":${gps.lon}}"
            )
        )
    }

    suspend fun registrarPausa(
        setorId: String,
        motivo: TipoPausa,
        observacao: String,
        gps: PontoGps
    ) = withContext(Dispatchers.IO) {
        val pausa = PausaEntity(
            id = UUID.randomUUID().toString(),
            setorId = setorId,
            motivo = motivo,
            observacao = observacao,
            lat = gps.lat,
            lon = gps.lon
        )
        db.pausaDao().insertPausa(pausa)
    }

    suspend fun retomarPausa(setorId: String) = withContext(Dispatchers.IO) {
        val ativa = db.pausaDao().getPausaAtiva(setorId)
        if (ativa != null) {
            val fim = System.currentTimeMillis()
            val duracao = (fim - ativa.horaInicio) / 1000
            db.pausaDao().updatePausa(ativa.copy(horaFim = fim, duracaoSegundos = duracao))
        }
    }

    suspend fun sincronizarFilaOffline() = withContext(Dispatchers.IO) {
        // Simula upload confiável com garantia de idempotência e ordenação
        db.syncQueueDao().markAllAsSynced()
    }

    suspend fun resetarSetorDemo() = withContext(Dispatchers.IO) {
        val setorId = "setor_demo_centro_01"
        val trechos = db.trechoDao().getTrechosBySetorSync(setorId)
        val resetados = trechos.map { it.copy(status = StatusTrecho.PENDENTE) }
        db.trechoDao().insertTrechos(resetados)
    }
}

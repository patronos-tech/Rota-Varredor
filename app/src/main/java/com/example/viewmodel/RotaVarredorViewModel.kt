package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.model.*
import com.example.service.AnalyticsLearningService
import com.example.service.GpsTrackerManager
import com.example.service.LocationMatcher
import com.example.service.RoutingOptimizer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RotaVarredorViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = RotaVarredorRepository(db)
    val gpsManager = GpsTrackerManager(application)

    val todosSetores = repository.todosSetores.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    private val _setorIdSelecionado = MutableStateFlow("setor_demo_centro_01")
    val setorIdSelecionado: StateFlow<String> = _setorIdSelecionado.asStateFlow()

    val setorAtivo: StateFlow<SetorEntity?> = combine(todosSetores, _setorIdSelecionado) { setores, id ->
        setores.find { it.id == id } ?: setores.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val ruas = _setorIdSelecionado.flatMapLatest { id ->
        repository.getRuas(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trechos = _setorIdSelecionado.flatMapLatest { id ->
        repository.getTrechos(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val execucoes = _setorIdSelecionado.flatMapLatest { id ->
        repository.getExecucoes(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ocorrencias = _setorIdSelecionado.flatMapLatest { id ->
        repository.getOcorrencias(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pausas = _setorIdSelecionado.flatMapLatest { id ->
        repository.getPausas(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val historicoSessoes = repository.historicoSessoes.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val pendingSyncCount = repository.pendingSyncCount.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )

    val posicaoAtual = gpsManager.posicaoAtual
    val modoSimulador = gpsManager.modoSimulador

    // Trecho em execução ou próximo a executar
    val trechoAtivo: StateFlow<TrechoEntity?> = trechos.map { lista ->
        lista.find { it.status == StatusTrecho.EM_EXECUCAO }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val proximoTrechoSugerido: StateFlow<TrechoEntity?> = trechos.map { lista ->
        lista.find { it.status == StatusTrecho.EM_EXECUCAO }
            ?: lista.find { it.status == StatusTrecho.PENDENTE }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Status do Alerta GPS em tempo real (VERDE, AMARELO, VERMELHO, CINZA)
    val statusGpsInfo: StateFlow<Pair<StatusGpsAlerta, Double>> = combine(
        posicaoAtual, trechoAtivo, proximoTrechoSugerido
    ) { pos, ativo, proximo ->
        LocationMatcher.avaliarStatusGps(pos, ativo, proximo)
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000),
        Pair(StatusGpsAlerta.CINZA, 0.0)
    )

    val emPausa: StateFlow<Boolean> = pausas.map { lista ->
        lista.any { it.horaFim == null }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Cronômetro da sessão e do trecho ativo
    private val _tempoTrechoSegundos = MutableStateFlow(0L)
    val tempoTrechoSegundos: StateFlow<Long> = _tempoTrechoSegundos.asStateFlow()

    private val _distanciaSessaoMetros = MutableStateFlow(0.0)
    val distanciaSessaoMetros: StateFlow<Double> = _distanciaSessaoMetros.asStateFlow()

    private var timerJob: Job? = null

    // Resumo e Métricas
    val resumoSetor: StateFlow<ResumoSetor> = combine(
        trechos, execucoes, ocorrencias, pausas
    ) { listTrechos, listExec, listOcorr, listPausas ->
        val total = listTrechos.size
        val concluidos = listTrechos.count { it.status == StatusTrecho.CONCLUIDO }
        val pendentes = total - concluidos
        val pct = if (total > 0) (concluidos.toFloat() / total) * 100f else 0f
        val distTotal = listExec.sumOf { it.distanciaPercorridaMetros }
        val tempoTotal = listExec.sumOf { it.duracaoSegundos }

        ResumoSetor(
            totalTrechos = total,
            trechosConcluidos = concluidos,
            trechosPendentes = pendentes,
            percentualConcluido = pct,
            distanciaTotalMetros = distTotal,
            tempoTotalSegundos = tempoTotal,
            totalOcorrencias = listOcorr.size,
            totalPausas = listPausas.size
        )
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000),
        ResumoSetor(8, 0, 8, 0f, 0.0, 0L, 0, 0)
    )

    val estatisticasRuas: StateFlow<List<EstatisticaRua>> = combine(
        trechos, execucoes
    ) { t, e ->
        AnalyticsLearningService.gerarEstatisticasPorRua(t, e)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sugestoesAprendizado: StateFlow<List<SugestaoAprendizado>> = combine(
        historicoSessoes, execucoes
    ) { h, e ->
        AnalyticsLearningService.gerarSugestoesAprendizado(h, e)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.inicializarDadosDemoSeNecessario()
        }
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (trechoAtivo.value != null && !emPausa.value) {
                    _tempoTrechoSegundos.value += 1
                }
            }
        }
    }

    fun selecionarSetor(id: String) {
        _setorIdSelecionado.value = id
    }

    fun iniciarTrecho(trecho: TrechoEntity, numeroInicial: Int) {
        viewModelScope.launch {
            _tempoTrechoSegundos.value = 0L
            repository.iniciarTrecho(trecho, numeroInicial, posicaoAtual.value)
            // Se em simulador, coloca gari no início da rua
            if (modoSimulador.value) {
                gpsManager.simularEntrarNaRua(trecho)
            }
        }
    }

    fun finalizarTrecho(trecho: TrechoEntity, numeroFinal: Int) {
        viewModelScope.launch {
            val duracao = _tempoTrechoSegundos.value
            val distancia = trecho.distanciaEstimadaMetros
            _distanciaSessaoMetros.value += distancia
            repository.finalizarTrecho(trecho, numeroFinal, posicaoAtual.value, distancia, duracao)
            _tempoTrechoSegundos.value = 0L

            // Preparar próximo trecho no simulador
            val pendentes = trechos.value.filter { it.id != trecho.id && it.status == StatusTrecho.PENDENTE }
            val proximo = pendentes.firstOrNull()
            if (proximo != null && modoSimulador.value) {
                gpsManager.simularAproximacao(proximo)
            }
        }
    }

    fun registrarOcorrencia(tipo: TipoOcorrencia, observacao: String) {
        viewModelScope.launch {
            val ativo = trechoAtivo.value ?: proximoTrechoSugerido.value
            repository.registrarOcorrencia(
                setorId = _setorIdSelecionado.value,
                trechoId = ativo?.id,
                nomeRua = ativo?.nomeRua,
                tipo = tipo,
                observacao = observacao,
                gps = posicaoAtual.value
            )
        }
    }

    fun alternarPausa(motivo: TipoPausa = TipoPausa.DESCANSO, obs: String = "") {
        viewModelScope.launch {
            if (emPausa.value) {
                repository.retomarPausa(_setorIdSelecionado.value)
            } else {
                repository.registrarPausa(_setorIdSelecionado.value, motivo, obs, posicaoAtual.value)
            }
        }
    }

    fun otimizarSequenciaRota() {
        viewModelScope.launch {
            val setor = setorAtivo.value ?: return@launch
            val listaTrechos = trechos.value
            val pendentes = listaTrechos.filter { it.status == StatusTrecho.PENDENTE }
            val otimizados = RoutingOptimizer.otimizarSequenciaTrechos(posicaoAtual.value, setor, pendentes)

            db.trechoDao().insertTrechos(otimizados)
        }
    }

    fun sincronizarFilaOffline() {
        viewModelScope.launch {
            repository.sincronizarFilaOffline()
        }
    }

    fun reiniciarSetorDemo() {
        viewModelScope.launch {
            repository.resetarSetorDemo()
            _tempoTrechoSegundos.value = 0L
            _distanciaSessaoMetros.value = 0.0
            val primeiro = trechos.value.firstOrNull()
            if (primeiro != null) {
                gpsManager.simularAproximacao(primeiro)
            }
        }
    }

    // Controles do Simulador de GPS
    fun simularAproximacao() {
        val alvo = trechoAtivo.value ?: proximoTrechoSugerido.value ?: return
        gpsManager.simularAproximacao(alvo)
    }

    fun simularEntrarNaRua() {
        val alvo = trechoAtivo.value ?: proximoTrechoSugerido.value ?: return
        gpsManager.simularEntrarNaRua(alvo)
    }

    fun simularDesvioForaRota() {
        val alvo = trechoAtivo.value ?: proximoTrechoSugerido.value ?: return
        gpsManager.simularDesvioForaRota(alvo)
    }

    fun simularAvancarMeioTrecho() {
        val alvo = trechoAtivo.value ?: proximoTrechoSugerido.value ?: return
        gpsManager.simularAvancoNoTrecho(alvo, 0.5f)
    }

    fun simularAvancarFimTrecho() {
        val alvo = trechoAtivo.value ?: proximoTrechoSugerido.value ?: return
        gpsManager.simularAvancoNoTrecho(alvo, 1.0f)
    }

    fun alternarModoSimulador(simulador: Boolean) {
        gpsManager.setModoSimulador(simulador)
    }
}

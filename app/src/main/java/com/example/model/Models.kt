package com.example.model

enum class StatusSetor(val label: String) {
    RASCUNHO("Rascunho"),
    PRONTO("Pronto para Iniciar"),
    EM_EXECUCAO("Em Execução"),
    CONCLUIDO("Concluído")
}

enum class StatusTrecho(val label: String) {
    PENDENTE("Pendente"),
    EM_EXECUCAO("Em Varrição"),
    CONCLUIDO("Concluído"),
    BLOQUEADO("Bloqueado")
}

enum class StatusGpsAlerta(val label: String, val descricao: String) {
    VERDE("RUA CORRETA", "Você está no trecho planejado da rota"),
    AMARELO("APROXIMANDO-SE", "Aproximando-se do início do próximo trecho"),
    VERMELHO("FORA DA ROTA", "Atenção: você se afastou da rota planejada"),
    CINZA("GPS AGUARDANDO", "Inicie a varrição ou ative o simulador")
}

enum class TipoOcorrencia(val label: String, val iconName: String) {
    RUA_BLOQUEADA("Rua Bloqueada", "Block"),
    OBRA_NA_VIA("Obra na Via", "Construction"),
    ACESSO_IMPOSSIVEL("Acesso Impossível", "Warning"),
    CHUVA_FORTE("Chuva Forte / Temporal", "Cloud"),
    RISCO_SEGURANCA("Risco à Segurança", "Shield"),
    PROBLEMA_LOCAL("Problema no Local", "Report"),
    OUTRO("Outro Motivo", "Info")
}

enum class TipoPausa(val label: String) {
    DESCANSO("Descanso Regulamentar"),
    ALMOCO("Horário de Almoço"),
    CHUVA("Parada por Chuva"),
    HIDRATACAO("Pausa Hidratação"),
    MANUTENCAO_CARRINHO("Manutenção do Carrinho"),
    OUTRO("Outro")
}

data class PontoGps(
    val lat: Double,
    val lon: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val precisaoMetros: Float = 5.0f
)

data class ResumoSetor(
    val totalTrechos: Int,
    val trechosConcluidos: Int,
    val trechosPendentes: Int,
    val percentualConcluido: Float,
    val distanciaTotalMetros: Double,
    val tempoTotalSegundos: Long,
    val totalOcorrencias: Int,
    val totalPausas: Int
)

data class EstatisticaRua(
    val nomeRua: String,
    val totalTrechos: Int,
    val trechosConcluidos: Int,
    val tempoMedioSegundos: Long,
    val distanciaMediaMetros: Double,
    val sentidoMaisEficiente: String
)

data class SugestaoAprendizado(
    val titulo: String,
    val descricao: String,
    val ganhoEstimado: String,
    val tipo: String
)

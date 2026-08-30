package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SetorEntity
import com.example.data.TrechoEntity
import com.example.model.PontoGps
import com.example.model.ResumoSetor
import com.example.model.StatusGpsAlerta
import com.example.model.StatusTrecho
import com.example.ui.components.*
import com.example.viewmodel.RotaVarredorViewModel

@Composable
fun ExecutionScreen(
    viewModel: RotaVarredorViewModel,
    modifier: Modifier = Modifier
) {
    val setorAtivo by viewModel.setorAtivo.collectAsState()
    val trechos by viewModel.trechos.collectAsState()
    val trechoAtivo by viewModel.trechoAtivo.collectAsState()
    val proximoTrecho by viewModel.proximoTrechoSugerido.collectAsState()
    val statusGpsInfo by viewModel.statusGpsInfo.collectAsState()
    val posicaoAtual by viewModel.posicaoAtual.collectAsState()
    val emPausa by viewModel.emPausa.collectAsState()
    val tempoSegundos by viewModel.tempoTrechoSegundos.collectAsState()
    val resumo by viewModel.resumoSetor.collectAsState()
    val modoSimulador by viewModel.modoSimulador.collectAsState()

    var modalNumeroInicialVisivel by remember { mutableStateOf(false) }
    var modalNumeroFinalVisivel by remember { mutableStateOf(false) }
    var modalOcorrenciaVisivel by remember { mutableStateOf(false) }
    var modalPausaVisivel by remember { mutableStateOf(false) }
    var simuladorToolbarExpandida by remember { mutableStateOf(true) }

    val trechoFoco = trechoAtivo ?: proximoTrecho

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Interactive Canvas Map
        InteractiveSectorMap(
            setor = setorAtivo,
            trechos = trechos,
            posicaoAtual = posicaoAtual,
            statusGps = statusGpsInfo.first,
            trechoAtivo = trechoFoco
        )

        // 2. High-Contrast GPS Alert Banner
        GpsAlertBanner(
            status = statusGpsInfo.first,
            distanciaMetros = statusGpsInfo.second,
            nomeRua = trechoFoco?.nomeRua
        )

        // 3. Pause Alert if active
        AnimatedVisibility(visible = emPausa) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.PauseCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Varrição em Pausa Operacional",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = { viewModel.alternarPausa() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Retomar")
                    }
                }
            }
        }

        // 4. Active Trecho Cockpit Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("cockpit_trecho_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (trechoAtivo != null) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = if (trechoAtivo != null) "EM VARRIÇÃO ATIVA" else "PRÓXIMO TRECHO",
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = if (trechoAtivo != null) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = "Trecho ${trechoFoco?.ordemPlanejada ?: 1} de ${trechos.size}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = trechoFoco?.nomeRua ?: "Selecione um trecho",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Numeração estimada: nº ${trechoFoco?.numInicialEstimado ?: 1} até nº ${trechoFoco?.numFinalEstimado ?: 100} • Distância: ${trechoFoco?.distanciaEstimadaMetros?.toInt() ?: 120}m",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stats row: Timer, Distance, Progress
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Tempo", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        val minutos = tempoSegundos / 60
                        val segs = tempoSegundos % 60
                        Text(
                            text = "%02d:%02d".format(minutos, segs),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Divider(
                        modifier = Modifier
                            .height(32.dp)
                            .width(1.dp)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Progresso Setor", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "${resumo.trechosConcluidos}/${resumo.totalTrechos}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Divider(
                        modifier = Modifier
                            .height(32.dp)
                            .width(1.dp)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Conclusão", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "${resumo.percentualConcluido.toInt()}%",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF059669)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Primary Buttons: Iniciar vs Finalizar
                if (trechoAtivo == null) {
                    Button(
                        onClick = { modalNumeroInicialVisivel = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF059669)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("btn_iniciar_trecho")
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "INICIAR ESTE TRECHO",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    Button(
                        onClick = { modalNumeroFinalVisivel = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0284C7)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("btn_finalizar_trecho")
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "FINALIZAR TRECHO (INSERIR Nº FINAL)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Secondary operational buttons: Pausa & Ocorrência
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (emPausa) viewModel.alternarPausa() else modalPausaVisivel = true
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(
                            if (emPausa) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (emPausa) "Retomar" else "Pausar", fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = { modalOcorrenciaVisivel = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(
                            Icons.Filled.ReportProblem,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Problema", fontSize = 13.sp)
                    }
                }
            }
        }

        // 5. GPS Simulator Toolbar (Crucial for testings & demonstrations)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Tune,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Simulador de GPS & Testes",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Switch(
                        checked = modoSimulador,
                        onCheckedChange = { viewModel.alternarModoSimulador(it) }
                    )
                }

                if (modoSimulador) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Simule situações reais de campo instantaneamente:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilledTonalButton(
                            onClick = { viewModel.simularAproximacao() },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Text("Aproximar (Amarelo)", fontSize = 11.sp, textAlign = TextAlign.Center)
                        }

                        FilledTonalButton(
                            onClick = { viewModel.simularEntrarNaRua() },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Text("Na Rua (Verde)", fontSize = 11.sp, textAlign = TextAlign.Center)
                        }

                        FilledTonalButton(
                            onClick = { viewModel.simularDesvioForaRota() },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Text("Desvio (Vermelho)", fontSize = 11.sp, textAlign = TextAlign.Center)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.simularAvancarMeioTrecho() },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Text("Meio do Trecho (50%)", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = { viewModel.simularAvancarFimTrecho() },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Text("Fim do Trecho (100%)", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }

    // Modal para Inserir Número Inicial
    ModalNumero(
        visivel = modalNumeroInicialVisivel,
        titulo = "Iniciar Varrição do Trecho",
        subtitulo = "Confirme o número do primeiro imóvel do trecho (${trechoFoco?.nomeRua})",
        valorSugerido = trechoFoco?.numInicialEstimado ?: 1,
        onConfirmar = { num ->
            trechoFoco?.let { viewModel.iniciarTrecho(it, num) }
            modalNumeroInicialVisivel = false
        },
        onCancelar = { modalNumeroInicialVisivel = false }
    )

    // Modal para Inserir Número Final
    ModalNumero(
        visivel = modalNumeroFinalVisivel,
        titulo = "Finalizar Varrição do Trecho",
        subtitulo = "Confirme o número do último imóvel varrido (${trechoFoco?.nomeRua})",
        valorSugerido = trechoFoco?.numFinalEstimado ?: 100,
        onConfirmar = { num ->
            trechoFoco?.let { viewModel.finalizarTrecho(it, num) }
            modalNumeroFinalVisivel = false
        },
        onCancelar = { modalNumeroFinalVisivel = false }
    )

    // Modal de Registro de Ocorrência
    ModalOcorrencia(
        visivel = modalOcorrenciaVisivel,
        onRegistrar = { tipo, obs ->
            viewModel.registrarOcorrencia(tipo, obs)
        },
        onFechar = { modalOcorrenciaVisivel = false }
    )

    // Modal de Pausa
    ModalPausa(
        visivel = modalPausaVisivel,
        onIniciarPausa = { motivo, obs ->
            viewModel.alternarPausa(motivo, obs)
            modalPausaVisivel = false
        },
        onFechar = { modalPausaVisivel = false }
    )
}

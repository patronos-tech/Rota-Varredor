package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.TrechoEntity
import com.example.model.StatusGpsAlerta
import com.example.model.TipoOcorrencia
import com.example.model.TipoPausa

@Composable
fun GpsAlertBanner(
    status: StatusGpsAlerta,
    distanciaMetros: Double,
    nomeRua: String?,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = when (status) {
            StatusGpsAlerta.VERDE -> Color(0xFF059669)   // Green 600
            StatusGpsAlerta.AMARELO -> Color(0xFFD97706) // Amber 600
            StatusGpsAlerta.VERMELHO -> Color(0xFFDC2626) // Red 600
            StatusGpsAlerta.CINZA -> Color(0xFF475569)   // Slate 600
        },
        label = "alert_color"
    )

    val icon = when (status) {
        StatusGpsAlerta.VERDE -> Icons.Filled.CheckCircle
        StatusGpsAlerta.AMARELO -> Icons.Filled.NearMe
        StatusGpsAlerta.VERMELHO -> Icons.Filled.Warning
        StatusGpsAlerta.CINZA -> Icons.Filled.GpsFixed
    }

    // Pulse animation for active alerts
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (status == StatusGpsAlerta.VERDE || status == StatusGpsAlerta.VERMELHO) 1.08f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("gps_alert_banner"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .scale(pulseScale)
                    .background(Color.White.copy(alpha = 0.25f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = status.label,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = status.label,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = if (nomeRua != null) "$nomeRua • ${status.descricao}" else status.descricao,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    lineHeight = 16.sp
                )
            }

            if (distanciaMetros > 0 && status != StatusGpsAlerta.CINZA) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.Black.copy(alpha = 0.25f),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = "${distanciaMetros.toInt()}m",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ModalNumero(
    visivel: Boolean,
    titulo: String,
    subtitulo: String,
    valorSugerido: Int? = null,
    onConfirmar: (Int) -> Unit,
    onCancelar: () -> Unit
) {
    if (!visivel) return

    var textoInput by remember(valorSugerido) {
        mutableStateOf(valorSugerido?.toString() ?: "")
    }

    Dialog(onDismissRequest = onCancelar) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("modal_numero_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.Tag,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = titulo,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = subtitulo,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = textoInput,
                    onValueChange = { novo ->
                        if (novo.isEmpty() || novo.all { it.isDigit() }) {
                            textoInput = novo
                        }
                    },
                    label = { Text("Número do Imóvel") },
                    placeholder = { Text("Ex: 120") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_numero_field")
                )

                // Quick buttons (+10, +50, etc.)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val atual = textoInput.toIntOrNull() ?: 0
                    SuggestionChip(
                        onClick = { textoInput = (atual + 1).toString() },
                        label = { Text("+1") }
                    )
                    SuggestionChip(
                        onClick = { textoInput = (atual + 10).toString() },
                        label = { Text("+10") }
                    )
                    SuggestionChip(
                        onClick = { textoInput = (atual + 50).toString() },
                        label = { Text("+50") }
                    )
                    SuggestionChip(
                        onClick = { textoInput = "" },
                        label = { Text("Limpar") }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancelar,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            val num = textoInput.toIntOrNull() ?: 1
                            onConfirmar(num)
                        },
                        enabled = textoInput.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("btn_confirmar_numero")
                    ) {
                        Text("Confirmar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ModalOcorrencia(
    visivel: Boolean,
    onRegistrar: (TipoOcorrencia, String) -> Unit,
    onFechar: () -> Unit
) {
    if (!visivel) return

    var tipoSelecionado by remember { mutableStateOf(TipoOcorrencia.RUA_BLOQUEADA) }
    var observacao by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onFechar) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.ReportProblem,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Registrar Ocorrência",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Selecione o motivo:",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                TipoOcorrencia.values().forEach { tipo ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { tipoSelecionado = tipo }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (tipoSelecionado == tipo),
                            onClick = { tipoSelecionado = tipo }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = tipo.label, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = observacao,
                    onValueChange = { observacao = it },
                    label = { Text("Observações adicionais") },
                    placeholder = { Text("Ex: Caçamba obstruindo sarjeta") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onFechar,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            onRegistrar(tipoSelecionado, observacao)
                            onFechar()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Registrar")
                    }
                }
            }
        }
    }
}

@Composable
fun ModalPausa(
    visivel: Boolean,
    onIniciarPausa: (TipoPausa, String) -> Unit,
    onFechar: () -> Unit
) {
    if (!visivel) return

    var motivoSelecionado by remember { mutableStateOf(TipoPausa.DESCANSO) }
    var obs by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onFechar) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.PauseCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Registrar Pausa Operacional",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                TipoPausa.values().forEach { motivo ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { motivoSelecionado = motivo }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (motivoSelecionado == motivo),
                            onClick = { motivoSelecionado = motivo }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = motivo.label, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onFechar,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Voltar")
                    }

                    Button(
                        onClick = {
                            onIniciarPausa(motivoSelecionado, obs)
                            onFechar()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Iniciar Pausa")
                    }
                }
            }
        }
    }
}

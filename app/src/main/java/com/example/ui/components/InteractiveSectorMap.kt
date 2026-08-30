package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SetorEntity
import com.example.data.TrechoEntity
import com.example.model.PontoGps
import com.example.model.StatusGpsAlerta
import com.example.model.StatusTrecho
import kotlin.math.max
import kotlin.math.min

@Composable
fun InteractiveSectorMap(
    setor: SetorEntity?,
    trechos: List<TrechoEntity>,
    posicaoAtual: PontoGps,
    statusGps: StatusGpsAlerta,
    trechoAtivo: TrechoEntity?,
    modifier: Modifier = Modifier
) {
    if (setor == null) return

    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val radarRadius by infiniteTransition.animateFloat(
        initialValue = 12f,
        targetValue = 38f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_radius"
    )
    val radarAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .testTag("interactive_sector_map"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)), // Slate 900 canvas
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // Calculate bounding box of all points
                val allLats = mutableListOf(
                    setor.pontoRetiradaLat, setor.pontoDevolucaoLat, posicaoAtual.lat
                )
                val allLons = mutableListOf(
                    setor.pontoRetiradaLon, setor.pontoDevolucaoLon, posicaoAtual.lon
                )
                trechos.forEach {
                    allLats.add(it.pontoInicialLat)
                    allLats.add(it.pontoFinalLat)
                    allLons.add(it.pontoInicialLon)
                    allLons.add(it.pontoFinalLon)
                }

                val minLat = allLats.minOrNull() ?: -23.55320
                val maxLat = allLats.maxOrNull() ?: -23.54890
                val minLon = allLons.minOrNull() ?: -46.63680
                val maxLon = allLons.maxOrNull() ?: -46.63330

                val latSpan = max(0.0001, maxLat - minLat)
                val lonSpan = max(0.0001, maxLon - minLon)

                val canvasW = size.width
                val canvasH = size.height

                fun toCanvasOffset(lat: Double, lon: Double): Offset {
                    val x = ((lon - minLon) / lonSpan).toFloat() * canvasW
                    // Invert Y so North is Up
                    val y = (1.0f - ((lat - minLat) / latSpan).toFloat()) * canvasH
                    return Offset(x, y)
                }

                // 1. Draw Street Segments (Background road grid)
                trechos.forEach { trecho ->
                    val p1 = toCanvasOffset(trecho.pontoInicialLat, trecho.pontoInicialLon)
                    val p2 = toCanvasOffset(trecho.pontoFinalLat, trecho.pontoFinalLon)

                    val isAtivo = trecho.id == trechoAtivo?.id
                    val color = when (trecho.status) {
                        StatusTrecho.CONCLUIDO -> Color(0xFF10B981) // Emerald Green
                        StatusTrecho.EM_EXECUCAO -> Color(0xFF0284C7) // Sky Blue
                        StatusTrecho.BLOQUEADO -> Color(0xFFEF4444) // Red
                        StatusTrecho.PENDENTE -> if (isAtivo) Color(0xFFF59E0B) else Color(0xFF64748B) // Slate 500
                    }

                    // Road casing (dark outline)
                    drawLine(
                        color = Color(0xFF1E293B),
                        start = p1,
                        end = p2,
                        strokeWidth = if (isAtivo) 18f else 12f,
                        cap = StrokeCap.Round
                    )

                    // Road fill
                    drawLine(
                        color = color,
                        start = p1,
                        end = p2,
                        strokeWidth = if (isAtivo) 12f else 7f,
                        cap = StrokeCap.Round
                    )

                    // Segment start point node
                    drawCircle(
                        color = color,
                        radius = if (isAtivo) 7f else 4f,
                        center = p1
                    )
                }

                // 2. Draw Cart Pickup Depot Point
                val pRetirada = toCanvasOffset(setor.pontoRetiradaLat, setor.pontoRetiradaLon)
                drawCircle(
                    color = Color(0xFF0284C7),
                    radius = 11f,
                    center = pRetirada
                )
                drawCircle(
                    color = Color.White,
                    radius = 5f,
                    center = pRetirada
                )

                // 3. Draw Cart Return Point
                val pDevolucao = toCanvasOffset(setor.pontoDevolucaoLat, setor.pontoDevolucaoLon)
                drawCircle(
                    color = Color(0xFFEA580C),
                    radius = 11f,
                    center = pDevolucao
                )
                drawCircle(
                    color = Color.White,
                    radius = 5f,
                    center = pDevolucao
                )

                // 4. Draw Live Worker GPS Pin with Radar Ping
                val pGari = toCanvasOffset(posicaoAtual.lat, posicaoAtual.lon)
                val radarColor = when (statusGps) {
                    StatusGpsAlerta.VERDE -> Color(0xFF10B981)
                    StatusGpsAlerta.AMARELO -> Color(0xFFF59E0B)
                    StatusGpsAlerta.VERMELHO -> Color(0xFFEF4444)
                    StatusGpsAlerta.CINZA -> Color(0xFF94A3B8)
                }

                // Expanding radar circle
                drawCircle(
                    color = radarColor.copy(alpha = radarAlpha),
                    radius = radarRadius,
                    center = pGari
                )

                // Core worker pin
                drawCircle(
                    color = Color.White,
                    radius = 12f,
                    center = pGari
                )
                drawCircle(
                    color = radarColor,
                    radius = 8f,
                    center = pGari
                )
            }

            // Top-left map badges
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1E293B).copy(alpha = 0.9f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFF0284C7), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Retirada Carrinho",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1E293B).copy(alpha = 0.9f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFFEA580C), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Devolução",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Bottom-right GPS position indicator
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF1E293B).copy(alpha = 0.9f),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.MyLocation,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "GPS: %.4f, %.4f".format(posicaoAtual.lat, posicaoAtual.lon),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

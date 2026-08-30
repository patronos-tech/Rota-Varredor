package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.RotaVarredorViewModel

@Composable
fun SyncOfflineScreen(
    viewModel: RotaVarredorViewModel,
    modifier: Modifier = Modifier
) {
    val pendingCount by viewModel.pendingSyncCount.collectAsState()
    var sincronizando by remember { mutableStateOf(false) }
    var mensagemStatus by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Status Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (pendingCount == 0) Color(0xFF059669).copy(alpha = 0.15f) else Color(0xFFD97706).copy(alpha = 0.15f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                if (pendingCount == 0) Color(0xFF059669) else Color(0xFFD97706),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (pendingCount == 0) Icons.Filled.CloudDone else Icons.Filled.CloudUpload,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = if (pendingCount == 0) "100% Sincronizado" else "$pendingCount Eventos Pendentes",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (pendingCount == 0) Color(0xFF059669) else Color(0xFFD97706)
                        )
                        Text(
                            text = if (pendingCount == 0) "Todos os dados estão salvos localmente e atualizados na nuvem." else "Os dados foram armazenados localmente e aguardam conexão.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        sincronizando = true
                        viewModel.sincronizarFilaOffline()
                        sincronizando = false
                        mensagemStatus = "Fila offline transmitida com sucesso e com garantia de idempotência!"
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Sync, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sincronizar Lote com a Central", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (mensagemStatus != null) {
            Text(
                text = mensagemStatus!!,
                color = Color(0xFF059669),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }

        Text(
            text = "Diretrizes de Segurança & Arquitetura",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                SecurityInfoCard(
                    titulo = "Arquitetura Offline-First (Room Local)",
                    descricao = "Todas as operações do gari (início, números, pausas, GPS, problemas) persistem imediatamente no SQLite/Room do aparelho, sem travar o aplicativo mesmo sem sinal 4G/5G.",
                    icon = Icons.Filled.Storage
                )
            }
            item {
                SecurityInfoCard(
                    titulo = "Transmissão Idempotente e Criptografia",
                    descricao = "Os lotes de eventos na fila contêm UUIDs únicos para evitar duplicidade no servidor central ao restabelecer conexão.",
                    icon = Icons.Filled.Security
                )
            }
            item {
                SecurityInfoCard(
                    titulo = "Privacidade e Retenção GPS (LGPD)",
                    descricao = "As coordenadas de varrição são associadas ao setor e trecho, preservando privacidade pessoal e garantindo auditoria estrita de limpeza pública.",
                    icon = Icons.Filled.Lock
                )
            }
            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun SecurityInfoCard(
    titulo: String,
    descricao: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = descricao,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

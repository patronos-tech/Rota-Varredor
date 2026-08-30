package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.StatusGpsAlerta
import com.example.viewmodel.RotaVarredorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: RotaVarredorViewModel
) {
    var abaSelecionada by remember { mutableStateOf(0) }
    val statusGpsInfo by viewModel.statusGpsInfo.collectAsState()
    val modoSimulador by viewModel.modoSimulador.collectAsState()
    val setorAtivo by viewModel.setorAtivo.collectAsState()
    val pendingSync by viewModel.pendingSyncCount.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ROTA VARREDOR",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (modoSimulador) Color(0xFFD97706) else Color(0xFF059669)
                            ) {
                                Text(
                                    text = if (modoSimulador) "SIMULADOR" else "GPS REAL",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = setorAtivo?.nome ?: "Assistente de Varrição",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        when (statusGpsInfo.first) {
                                            StatusGpsAlerta.VERDE -> Color(0xFF059669)
                                            StatusGpsAlerta.AMARELO -> Color(0xFFD97706)
                                            StatusGpsAlerta.VERMELHO -> Color(0xFFDC2626)
                                            StatusGpsAlerta.CINZA -> Color(0xFF64748B)
                                        },
                                        CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Gari Operacional",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_navigation_bar"),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = (abaSelecionada == 0),
                    onClick = { abaSelecionada = 0 },
                    icon = {
                        Icon(
                            if (abaSelecionada == 0) Icons.Filled.DirectionsWalk else Icons.Outlined.DirectionsWalk,
                            contentDescription = "Varrição"
                        )
                    },
                    label = { Text("Varrição", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    modifier = Modifier.testTag("nav_item_varricao")
                )

                NavigationBarItem(
                    selected = (abaSelecionada == 1),
                    onClick = { abaSelecionada = 1 },
                    icon = {
                        Icon(
                            if (abaSelecionada == 1) Icons.Filled.Map else Icons.Outlined.Map,
                            contentDescription = "Setor"
                        )
                    },
                    label = { Text("Setor", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    modifier = Modifier.testTag("nav_item_setor")
                )

                NavigationBarItem(
                    selected = (abaSelecionada == 2),
                    onClick = { abaSelecionada = 2 },
                    icon = {
                        Icon(
                            if (abaSelecionada == 2) Icons.Filled.Analytics else Icons.Outlined.Analytics,
                            contentDescription = "Estatísticas"
                        )
                    },
                    label = { Text("IA & Métricas", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    modifier = Modifier.testTag("nav_item_metricas")
                )

                NavigationBarItem(
                    selected = (abaSelecionada == 3),
                    onClick = { abaSelecionada = 3 },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (pendingSync > 0) {
                                    Badge { Text("$pendingSync") }
                                }
                            }
                        ) {
                            Icon(
                                if (abaSelecionada == 3) Icons.Filled.SyncLock else Icons.Outlined.SyncLock,
                                contentDescription = "Sincronização"
                            )
                        }
                    },
                    label = { Text("Sync Offline", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    modifier = Modifier.testTag("nav_item_sync")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (abaSelecionada) {
                0 -> ExecutionScreen(viewModel = viewModel)
                1 -> SectorDetailScreen(viewModel = viewModel, onNavegarParaExecucao = { abaSelecionada = 0 })
                2 -> HistoryAnalyticsScreen(viewModel = viewModel)
                3 -> SyncOfflineScreen(viewModel = viewModel)
            }
        }
    }
}

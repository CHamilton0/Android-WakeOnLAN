package com.privateparking.wakeonlan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.privateparking.wakeonlan.ui.theme.WakeOnLANTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WakeOnLANTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text("Wake-on-LAN") })
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    MainScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var deviceName by remember { mutableStateOf("") }
    var ipAddress by remember { mutableStateOf("") }
    var macAddress by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val (name, mac, ip) = WakePreferences.load(context)
        deviceName = name ?: ""
        macAddress = mac ?: ""
        ipAddress = ip ?: ""
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextField(
            value = deviceName,
            onValueChange = { deviceName = it },
            label = { Text("Device Name") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = ipAddress,
            onValueChange = { ipAddress = it },
            label = { Text("IP Address") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = macAddress,
            onValueChange = { macAddress = it },
            label = { Text("MAC Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                scope.launch {
                    try {
                        WakePreferences.save(context, deviceName, macAddress, ipAddress)
                        withContext(Dispatchers.IO) {
                            sendWakeOnLan(macAddress, ipAddress)
                        }
                        println("Sent WOL to $macAddress via $ipAddress")
                    } catch (e: Exception) {
                        println("WOL error: ${e.message}")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Wake Device")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    WakeOnLANTheme {
        MainScreen()
    }
}

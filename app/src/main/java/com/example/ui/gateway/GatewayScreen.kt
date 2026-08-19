package com.example.ui.gateway

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GatewayScreen(onEnterClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "SUPI",
            color = Color(0xFFFF5722),
            fontSize = 48.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 4.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Komm erst mal an.",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Anstand • Respekt • Zwischenmenschlichkeit",
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 64.dp)
        )
        
        TritEinButton(
            onClick = onEnterClick
        )
        
        Spacer(modifier = Modifier.height(64.dp))
        
        Text(
            text = "Ein Produkt von ALLESEASYworld",
            color = Color(0xFF1A3AFF),
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 32.dp)
        )
    }
}

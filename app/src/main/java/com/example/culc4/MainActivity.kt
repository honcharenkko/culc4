package com.example.culc4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment

import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


import kotlin.math.sqrt


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShortCircuitCalculatorScreen()
        }
    }
}

// Network voltage constant for 10 kV (10,000 V)
const val NETWORK_VOLTAGE = 10000

@Composable
fun ShortCircuitCalculatorScreen() {
    var transformerPower by remember { mutableStateOf("") }
    var equivalentImpedance by remember { mutableStateOf("") }
    var cableType by remember { mutableStateOf("") }
    var cableArea by remember { mutableStateOf("") }
    var currentDensity by remember { mutableStateOf("") }
    var dynamicCoefficient by remember { mutableStateOf("") }
    var phaseCurrent by remember { mutableStateOf("") }
    var thermalStability by remember { mutableStateOf("") }
    var dynamicStability by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Short-Circuit Current Calculator", fontSize = 20.sp)

        OutlinedTextField(
            value = transformerPower,
            onValueChange = { transformerPower = it },
            label = { Text("Transformer Power (MVA)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = equivalentImpedance,
            onValueChange = { equivalentImpedance = it },
            label = { Text("Equivalent Impedance (Ohms)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = cableType,
            onValueChange = { cableType = it },
            label = { Text("Cable Type (e.g., Copper, Aluminum)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = cableArea,
            onValueChange = { cableArea = it },
            label = { Text("Cable Area (mm²)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = currentDensity,
            onValueChange = { currentDensity = it },
            label = { Text("Current Density (A/mm²)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = dynamicCoefficient,
            onValueChange = { dynamicCoefficient = it },
            label = { Text("Dynamic Stability Coefficient") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = {
            val transformerPowerValue = transformerPower.toDoubleOrNull() ?: 0.0
            val equivalentImpedanceValue = equivalentImpedance.toDoubleOrNull() ?: 0.0
            val cableAreaValue = cableArea.toDoubleOrNull() ?: 0.0
            val currentDensityValue = currentDensity.toDoubleOrNull() ?: 0.0
            val dynamicCoefficientValue = dynamicCoefficient.toDoubleOrNull() ?: 0.0

            val calculatedPhaseCurrent = calculateThreePhaseShortCircuit(transformerPowerValue, equivalentImpedanceValue)
            phaseCurrent = calculatedPhaseCurrent.toString()

            thermalStability = if (checkThermalStability(calculatedPhaseCurrent, currentDensityValue, cableAreaValue)) {
                "Passed"
            } else {
                "Failed"
            }

            dynamicStability = if (checkDynamicStability(calculatedPhaseCurrent, dynamicCoefficientValue, cableType)) {
                "Passed"
            } else {
                "Failed"
            }
        }) {
            Text("Calculate Short-Circuit Current")
        }

        Text("Three-Phase Short-Circuit Current: $phaseCurrent A", fontSize = 18.sp)
        Text("Thermal Stability: $thermalStability", fontSize = 18.sp)
        Text("Dynamic Stability: $dynamicStability", fontSize = 18.sp)
    }
}

fun calculateThreePhaseShortCircuit(transformerPower: Double, equivalentImpedance: Double): Double {
    return transformerPower * 1_000_000 / (sqrt(3.0) * NETWORK_VOLTAGE * equivalentImpedance)
}

fun checkThermalStability(shortCircuitCurrent: Double, maxCurrentDensity: Double, cableArea: Double): Boolean {
    val maxAllowedCurrent = maxCurrentDensity * cableArea
    return shortCircuitCurrent <= maxAllowedCurrent
}

fun checkDynamicStability(shortCircuitCurrent: Double, dynamicCoefficient: Double, cableType: String): Boolean {
    val dynamicLimit = when (cableType.lowercase()) {
        "copper" -> 2.0 * dynamicCoefficient
        "aluminum" -> 1.8 * dynamicCoefficient
        else -> dynamicCoefficient
    }
    return shortCircuitCurrent <= dynamicLimit
}

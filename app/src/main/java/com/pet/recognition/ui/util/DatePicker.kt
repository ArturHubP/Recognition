package com.pet.recognition.ui.util

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.commandiron.wheel_picker_compose.WheelDatePicker
import com.commandiron.wheel_picker_compose.core.WheelPickerDefaults

import java.time.LocalDate

@Composable
fun DatePickerComponent(
    startDate: LocalDate,
    onSnap: (LocalDate) -> Unit,
    onDismiss: () -> Unit
){
    Dialog(
        onDismissRequest = { onDismiss.invoke() }
    ) {
        WheelDatePicker(
            startDate = startDate,
            rowCount = 3,
            size = DpSize(300.dp, 150.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 30.sp,
            ),
            selectorProperties = WheelPickerDefaults.selectorProperties(
                enabled = true,
                color = MaterialTheme.colorScheme.secondaryContainer,
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            )
        ){snappedTime -> onSnap(snappedTime) }
    }
}

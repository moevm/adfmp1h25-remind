package com.example.remind.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.remind.R
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp


@Composable
fun NewTaskLayout() {
    Column(
        modifier = Modifier
            .statusBarsPadding()
            .padding(horizontal = 40.dp)
            .verticalScroll(rememberScrollState())
            .safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.task_name),
            fontSize = 20.sp,
            modifier = Modifier
                .padding(bottom = 16.dp, top = 40.dp)
                .align(alignment = Alignment.Start)
        )
        EditNumberField(modifier = Modifier.padding(bottom = 32.dp).fillMaxWidth(),name = R.string.task_name)
        Text(
            text = stringResource(R.string.category_name),
            fontSize = 20.sp,
            modifier = Modifier
                .padding(bottom = 13.dp, top = 20.dp)
                .align(alignment = Alignment.Start)
        )
        EditNumberField(modifier = Modifier.padding(bottom = 32.dp).fillMaxWidth(), name = R.string.category_name)
//        Spacer(modifier = Modifier.height(150.dp))
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxSize()
                .size(53.dp),
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB0E57C))
        ) {
            Text(
                fontSize = 20.sp,
                text = stringResource(R.string.button_add_task),
            )
        }
    }
}


@Composable
fun EditNumberField(modifier: Modifier = Modifier, name: Int) {
    var nameInput by remember { mutableStateOf("") }
    OutlinedTextField(
        value = nameInput,
        onValueChange = {nameInput = it},
        singleLine = true,

//        label = { Text(stringResource(name)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        modifier = modifier
    )
}



@Preview(showBackground = true)
@Composable
fun Preview() {
    NewTaskLayout()
}
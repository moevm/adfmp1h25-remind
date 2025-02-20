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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.sp
import kotlin.math.exp


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
        EditField(modifier = Modifier
            .padding(bottom = 32.dp)
            .fillMaxWidth(),name = R.string.task_name)
        Text(
            text = stringResource(R.string.category_name),
            fontSize = 20.sp,
            modifier = Modifier
                .padding(bottom = 13.dp, top = 20.dp)
                .align(alignment = Alignment.Start)
        )
        dropMenu(modifier = Modifier
            .padding(bottom = 32.dp)
            .fillMaxWidth(), name = R.string.category_name)
        Spacer(modifier = Modifier.height(150.dp))
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
fun EditField(modifier: Modifier = Modifier, name: Int) {
    var nameInput by remember { mutableStateOf("") }
    OutlinedTextField(
        value = nameInput,
        onValueChange = {nameInput = it},
        singleLine = true,
        textStyle = TextStyle.Default.copy(fontSize = 20.sp),

//        label = { Text(stringResource(name)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        modifier = modifier
    )
}

@Composable
fun dropMenu(modifier: Modifier = Modifier, name: Int) {
    var expanded by remember { mutableStateOf(false) }
    val items = listOf("Быт", "Учеба", "Работа")
    var selectedItem by remember{ mutableStateOf(items[0]) }
    var searchQuery by remember{ mutableStateOf("") }
    val filteredItems = items.filter { it.contains(searchQuery, ignoreCase = true) }
    Box{
        OutlinedButton(
            onClick = {expanded=true} ,

            modifier = Modifier
                .fillMaxSize()
                .size(53.dp)
                .align(alignment = Alignment.CenterStart),
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0x0))


        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text(
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start,
                    text = selectedItem,
                    fontSize = 20.sp,
                    color = Color(0xFF000000),
                    style = TextStyle.Default.copy(fontSize = 20.sp)
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Развернуть список",
                    tint = Color.Black,
                    modifier = Modifier.size(30.dp)
                )
            }

        }
        DropdownMenu(
            expanded= expanded,
            onDismissRequest = {expanded=false},
            offset= DpOffset(0.dp, 0.dp)
        ) {
            TextField(
                value = searchQuery,
                onValueChange = {searchQuery = it}
            )
            filteredItems.forEach {item ->
            DropdownMenuItem(
                text = {Text(item)},
                onClick = {
                    selectedItem = item
                    expanded = false
                }
                )
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun Preview() {
    NewTaskLayout()
}
package com.example.remind.ui.pages

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.remind.R
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.remind.data.FileManager
import com.example.remind.ui.models.Task


@Composable
fun NewTaskLayout(navController: NavController) {
    var taskName by remember { mutableStateOf("") }

    val fileManager = FileManager()
    val context = LocalContext.current
    val existingCategories = fileManager.loadCategoriesFromFile(context).toMutableList()
    var category by remember { mutableStateOf(existingCategories[0]) }

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .padding(horizontal = 40.dp)
            .verticalScroll(rememberScrollState())
            .safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextButton(
            onClick = { navController.navigate("main") },
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад",
                    tint = Color.Black,
                    modifier = Modifier
                        .padding(end = 15.dp)
                        .size(25.dp)
                )
                Text("Добавить задачу",
                    fontSize = 23.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Text(
            text = stringResource(R.string.task_name),
            fontSize = 20.sp,
            modifier = Modifier
                .padding(bottom = 16.dp, top = 40.dp)
                .align(alignment = Alignment.Start)
        )
        EditField(
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth(),
            name = R.string.task_name,
            value = taskName,
            onValueChange = { taskName = it }
        )

        Text(
            text = stringResource(R.string.category_name),
            fontSize = 20.sp,
            modifier = Modifier
                .padding(bottom = 13.dp, top = 20.dp)
                .align(alignment = Alignment.Start)
        )
        DropMenu(
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth(),
            selectedItem = category,
            onItemSelected = { category = it },
            items = existingCategories
        )

        AddCategory(context=context, fileManager=fileManager, onCreateCategory = {data-> category = data})
        Spacer(modifier = Modifier.height(50.dp))
        Button(
            onClick = {
                val newTask = Task(
                    id = generateId(),
                    title = taskName,
                    isCompleted = false,
                    completedAt = null,
                    category = category,
                    image = null,
                    imageDate = null
                )
                val existingTasks = fileManager.loadTasksFromFile(context).toMutableList()
                existingTasks.add(newTask)
                fileManager.saveTasksToFile(context = context, tasks = existingTasks)
                navController.navigate("main")
            },
            modifier = Modifier
                .fillMaxSize()
                .size(53.dp),
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB0E57C))
        ) {
            Text(
                fontSize = 20.sp,
                color = Color.Black,
                text = stringResource(R.string.button_add_task),
            )
        }
    }
}




@Composable
fun EditField(modifier: Modifier = Modifier, name: Int, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle.Default.copy(fontSize = 18.sp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        modifier = modifier
    )
}


@Composable
fun DropMenu(modifier: Modifier = Modifier, items: List<String>, selectedItem: String, onItemSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
//    val items = listOf("Быт", "Учеба", "Работа")
    val sortedItems = items.sortedBy { it.toString() }

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier
                .fillMaxSize()
                .size(53.dp)
                .align(alignment = Alignment.CenterStart),
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0x00000000))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    textAlign = TextAlign.Start,
                    text = selectedItem,
                    fontSize = 18.sp,
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
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            sortedItems.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = item,
                            style = TextStyle.Default.copy(fontSize = 20.sp)
                        )
                    },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

fun generateId(): Int {
    return (Math.random() * 10000).toInt()
}


@Composable
fun AddCategory(
    context: Context,
    fileManager: FileManager,
    onCreateCategory: (String) -> Unit
){
    val openDialog = remember { mutableStateOf(false) }
    var newCategory by remember { mutableStateOf("") }
    TextButton(
        onClick = { openDialog.value = true },
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = "Добавить",
                tint = Color(0xFF8C9A80),
                modifier = Modifier
                    .padding(end = 5.dp)
                    .size(30.dp)
            )
            Text(
                text = "Добавить категорию",
                color = Color(0xFF728066),
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal

                )
            }
    }
    if(openDialog.value){
        Dialog(onDismissRequest = { openDialog.value = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(0.dp),
                shape = RectangleShape,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = "Новая категория",
                        modifier = Modifier.padding(start = 20.dp, top=20.dp),
                        fontSize = 23.sp
                    )
                    EditField(modifier = Modifier
                        .padding(bottom = 5.dp, top = 10.dp, start = 20.dp)
                        .width(280.dp),
                        value = newCategory,
                        name = R.string.category_name,
                        onValueChange = { newCategory = it })
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        TextButton(
                            onClick = { openDialog.value = false },
                            modifier = Modifier.padding(15.dp),
                        ) {
                            Text(
                                text = "ОТМЕНА",
                                fontSize = 15.sp,
                                color = Color.Black)
                        }
                        TextButton(
                            onClick = {
                                openDialog.value = false
                                val existingCategories = fileManager.loadCategoriesFromFile(context).toMutableList()
                                existingCategories.add(newCategory)
                                fileManager.saveCategoriesToFile(context = context, categories = existingCategories)
                                onCreateCategory(newCategory)
                                newCategory = ""
                                },
                            modifier = Modifier.padding(15.dp),
                        ) {
                            Text(
                                text = "СОХРАНИТЬ",
                                fontSize = 15.sp,
                                color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun Preview() {
    val navController = rememberNavController()
    NewTaskLayout(navController)
}
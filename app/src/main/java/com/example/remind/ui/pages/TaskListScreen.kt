package com.example.remind.ui.pages

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.remind.R
import com.example.remind.ui.components.TaskCategory
import com.example.remind.ui.models.Task
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.remind.data.FileManager
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Composable
fun TaskListScreen(
    tasks: List<Task>,
    onUpdateTasks: (List<Task>) -> Unit,
    onOpenCamera: (Int) -> Unit,
    fileManager: FileManager,
    context: Context
) {
    var sortDesk by remember { mutableStateOf(false) }
    var filter by remember { mutableStateOf<TaskFilter?>(null) }
    var searchQuery by remember { mutableStateOf<String>("") }
    val tasksState = remember(tasks) { tasks.toMutableStateList() }
    val hasCompletedTasks = tasksState.any { it.isCompleted }
    //val groupedTasks = tasksState.groupBy { it.category }
    val filteredTasks = applyFilter(tasksState, filter)
    val searchedTasks = filteredTasks.filter{it.title.contains(searchQuery, true) || it.category.contains(searchQuery, true)}
    val groupedTasks = searchedTasks.groupBy { it.category }

    val savedSortState = remember { fileManager.loadSortState(context) }
    var selectedSortOption by rememberSaveable { mutableStateOf(savedSortState) }

    LaunchedEffect(selectedSortOption) {
        fileManager.saveSortState(context, selectedSortOption)
    }
    fun deleteTask(task: Task) {
        tasksState.remove(task)
        onUpdateTasks(tasksState.toList())
    }
    fun sortTask(desc: Int) {
        tasksState.sortWith(Comparator{a: Task, b: Task->
            if(a.completedAt.isNullOrBlank()) {
                return@Comparator 1             //опускаем неотмеченные дела вниз
            }
            if( b.completedAt.isNullOrBlank()){
                return@Comparator -1           //опускаем неотмеченные дела вниз
            }
            if(a.completedAt!! < b.completedAt!!){
                return@Comparator 1*desc
            }
            if(a.completedAt!! > b.completedAt!!){
                return@Comparator -1*desc
            }
            return@Comparator 0
        })
    }
    fun unsortTask(){
        tasksState.clear()
        tasksState.addAll(tasks)
    }

    LaunchedEffect(selectedSortOption) {
        when (selectedSortOption) {
            "Сначала новые отметки" -> sortTask(1)
            "Сначала старые отметки" -> sortTask(-1)
            "Сбросить сортировку" -> unsortTask()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Список дел",
                style = MaterialTheme.typography.headlineSmall
            )

            Row {
                FilterBottomSheet { selectedFilter ->
                    filter = selectedFilter
                }
                askBottomSheet(radioOptions = listOf("Сначала новые отметки", "Сначала старые отметки", "Сбросить сортировку"),
                    functionOptions = listOf(
                        {
                            sortTask(1)
                            selectedSortOption = "Сначала новые отметки"
                        },
                        {
                            sortTask(-1)
                            selectedSortOption = "Сначала старые отметки"
                        },
                        {
                            unsortTask()
                            selectedSortOption = "Сбросить сортировку"
                        }
                    ),selectedOption = selectedSortOption)
            }
        }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = searchQuery,
            onValueChange = {
                searchQuery = it
            },
            singleLine = true,
            textStyle = TextStyle.Default.copy(fontSize = 18.sp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            placeholder = {Text("Поиск")}
        )
        TextButton(
            onClick = {
                tasksState.replaceAll {
                    it.copy(
                        isCompleted = false,
                        completedAt = null,
                        imageDate = null,
                        image = null
                    )
                }
                onUpdateTasks(tasksState)
            },
            enabled = hasCompletedTasks
        ) {
            Text("Сбросить все отметки")
        }

        LazyColumn {
            groupedTasks.forEach { (category, tasksInCategory) ->
                item(key = category) {
                    TaskCategory(
                        category = category,
                        tasks = tasksInCategory,
                        onTaskUpdated = { updatedTask ->
                            val index = tasksState.indexOfFirst { it.id == updatedTask.id }
                            if (index != -1) {
                                tasksState[index] = updatedTask.apply {
                                    completedAt =
                                        if (isCompleted) LocalDateTime.now().toString() else null
                                }
                                onUpdateTasks(tasksState.toList())
                            }
                        },
                        onTaskDeleted = { task -> deleteTask(task) },
                        onOpenCamera = { taskId -> onOpenCamera(taskId) }
                    )
                }
            }
        }
    }

    LaunchedEffect(tasksState) {
        onUpdateTasks(tasksState.toList())
    }
}

enum class TaskFilter {
    WITH_PHOTO, COMPLETED, NOT_COMPLETED
}
fun applyFilter(tasks: List<Task>, filter: TaskFilter?): List<Task> {
    return when (filter) {
        TaskFilter.WITH_PHOTO -> tasks.filter { it.image != null }
        TaskFilter.COMPLETED -> tasks.filter { it.isCompleted }
        TaskFilter.NOT_COMPLETED -> tasks.filter { !it.isCompleted }
        null -> tasks
    }
}

@Composable
fun iconSortTime() {
    Box(
        modifier = Modifier.size(width = 30.dp, height = 29.dp)
    ) {
        Image(
            modifier = Modifier.size(28.dp),
            painter = painterResource(id = R.drawable.sort2),
            contentDescription = "Сортировка"
        )

        Image(
            modifier = Modifier
                .padding(start = 18.dp, top = 17.dp)
                .size(12.dp),
            painter = painterResource(id = R.drawable.clock),
            contentDescription = "Время"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun askBottomSheet(
    radioOptions: List<String>,
    functionOptions: List<()->Unit>,
    selectedOption: String
){
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    var skipPartiallyExpanded by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
//    val (selectedOption, onOptionSelected) = remember { mutableStateOf("") }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)
    IconButton(onClick = { openBottomSheet = !openBottomSheet }) {
        Image(
            painter = painterResource(id = R.drawable.sort),
            contentDescription = "Сортировка"
        )
    }
    if(openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(Modifier.selectableGroup()) {
                Row(
                    Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp, bottom = 16.dp, start = 150.dp),

                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically){
                    Text(
                        text = "Сортировка",
                        fontSize = 23.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                    TextButton(
                        modifier = Modifier.padding(end = 15.dp),
                        onClick = {scope
                            .launch{ bottomSheetState.hide() }
                            .invokeOnCompletion {
                                if (!bottomSheetState.isVisible) {
                                    openBottomSheet = false
                                }
                            }}
                    ) {
                        Text("Закрыть",
                            fontSize = 15.sp,
                            color = Color.Gray
                            )
                    }
                }
                HorizontalDivider()
                radioOptions.forEachIndexed{id, text ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = (text == selectedOption),
                                onClick = {
                                    openBottomSheet = false
                                    functionOptions[id]()
                                          },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            text = text,
                            style = TextStyle.Default.copy(fontSize = 18.sp),
                            modifier = Modifier.padding(start = 16.dp)
                        )
                        RadioButton(
                            selected = (text == selectedOption),
                            onClick = null

                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    onFilterSelected: (TaskFilter?) -> Unit
) {
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    var skipPartiallyExpanded by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val (selectedOption, onOptionSelected) = remember { mutableStateOf<TaskFilter?>(null) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)

    IconButton(onClick = { openBottomSheet = !openBottomSheet }) {
        Image(
            painter = painterResource(id = R.drawable.filter),
            contentDescription = "Фильтрация"
        )
    }

    if (openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(Modifier.selectableGroup()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 0.dp, bottom = 16.dp, start = 150.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Фильтрация",
                        fontSize = 23.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                    TextButton(
                        modifier = Modifier.padding(end = 15.dp),
                        onClick = {
                            scope
                                .launch { bottomSheetState.hide() }
                                .invokeOnCompletion {
                                    if (!bottomSheetState.isVisible) {
                                        openBottomSheet = false
                                    }
                                }
                        }
                    ) {
                        Text(
                            "Закрыть",
                            fontSize = 15.sp,
                            color = Color.Gray
                        )
                    }
                }
                HorizontalDivider()
                listOf(
                    "Только с фото" to TaskFilter.WITH_PHOTO,
                    "Только отмеченные" to TaskFilter.COMPLETED,
                    "Только не отмеченные" to TaskFilter.NOT_COMPLETED,
                    "Без фильтров" to null
                ).forEach { (text, filter) ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = (filter == selectedOption),
                                onClick = {
                                    onOptionSelected(filter)
                                    openBottomSheet = false
                                    onFilterSelected(filter)
                                },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = text,
                            style = TextStyle.Default.copy(fontSize = 18.sp),
                            modifier = Modifier.padding(start = 16.dp)
                        )
                        RadioButton(
                            selected = (filter == selectedOption),
                            onClick = null
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTaskList() {
    iconSortTime()
}
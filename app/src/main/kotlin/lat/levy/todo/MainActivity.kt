package lat.levy.todo

import android.database.sqlite.SQLiteOpenHelper
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import lat.levy.todo.ui.theme.Purple40
import lat.levy.todo.ui.theme.Purple80
import lat.levy.todo.ui.theme.TodoTheme
import tech.turso.libsql.Database
import tech.turso.libsql.Libsql
import java.io.File

class MainActivity : ComponentActivity() {
    private val db: Database by lazy { Libsql.open(this.filesDir.path + "/todo.db") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = this.db
        db.connect().use {
            it.execute("create table if not exists todos(id integer primary key autoincrement, content text)");
        }

        enableEdgeToEdge()
        setContent {
            val padding = 24.dp
            var text by remember { mutableStateOf("") }
            var todos by remember {
                var todos = mutableListOf<String>()
                db.connect().use {
                    it.query("select (content) from todos").use { rows ->
                        while (true) {
                            val row = rows.next()
                            if (row.isEmpty()) break
                            todos.add(row[0] as String);
                        }
                    }
                }

                mutableStateOf(todos as List<String>)
            }


            TodoTheme {
                Box(
                    Modifier.background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xff15262b),
                                Color(0xff2e5857)
                            )
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = padding, vertical = padding * Math.E.toFloat()),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextField(
                                singleLine = true,
                                value = text,
                                onValueChange = { text = it },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp)),
                            )
                            Spacer(modifier = Modifier.padding(10.dp))
                            IconButton(
                                colors = IconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    disabledContentColor = Color.Unspecified,
                                    disabledContainerColor = Color.Unspecified
                                ),
                                modifier = Modifier
                                    .size(42.dp)
                                    .aspectRatio(1f),
                                onClick = {
                                    db.connect().use {
                                        it.execute("delete from todos", text)
                                        it.query("select (content) from todos").use { rows ->
                                            var new_todos = mutableListOf<String>()
                                            while (true) {
                                                val row = rows.next()
                                                if (row.isEmpty()) break
                                                new_todos.add(row[0] as String);
                                            }
                                            todos = new_todos
                                        }
                                    }
                                }) {
                                Icon(Icons.Default.Clear, contentDescription = null)
                            }
                            Spacer(modifier = Modifier.padding(10.dp))
                            IconButton(
                                colors = IconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    disabledContentColor = Color.Unspecified,
                                    disabledContainerColor = Color.Unspecified
                                ),
                                modifier = Modifier
                                    .size(42.dp)
                                    .aspectRatio(1f),
                                onClick = {
                                    if (text.isNotEmpty()) {
                                        db.connect().use {
                                            it.execute(
                                                "insert into todos (content) values (?)",
                                                text
                                            )
                                            it.query("select (content) from todos").use { rows ->
                                                var new_todos = mutableListOf<String>()
                                                while (true) {
                                                    val row = rows.next()
                                                    if (row.isEmpty()) break
                                                    new_todos.add(row[0] as String);
                                                }
                                                todos = new_todos
                                            }
                                        }
                                        text = ""
                                    }
                                }) {
                                Icon(Icons.Default.Add, contentDescription = null)
                            }
                        }
                        Spacer(Modifier.size(padding))
                        LazyColumn {
                            items(todos) { todo ->
                                Text(todo, color = MaterialTheme.colorScheme.onBackground)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        db.close()
    }

}
package ru.beryukhov.coffeegram.pages

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.semantics.accessibilityLabel
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.format.TextStyle
import ru.beryukhov.coffeegram.TABLE_PAGE
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.toInt
import ru.beryukhov.coffeegram.view.MonthTable


@Composable
fun TableAppBar(
    yearMonth: YearMonth,
    navController: NavController
) {
    TopAppBar(title = {
        Row(horizontalArrangement = Arrangement.Center) {
            Text(
                modifier = Modifier.weight(1f),
                text = AnnotatedString(
                    text = yearMonth.month.getDisplayName(
                        TextStyle.FULL,
                        ContextAmbient.current.resources.configuration.locale
                    ),
                    paragraphStyle = ParagraphStyle(textAlign = TextAlign.Center)
                )
            )

        }
    },
        navigationIcon = {
            IconButton(
                onClick = { navController.navigate("$TABLE_PAGE/${yearMonth.minusMonths(1).toInt()}")},
                modifier = Modifier.semantics {
                    accessibilityLabel = "ArrowLeft"
                }) { Icon(Icons.Default.KeyboardArrowLeft) }
        },
        actions = {
            IconButton(
                onClick = { navController.navigate("$TABLE_PAGE/${yearMonth.plusMonths(1).toInt()}")},
                modifier = Modifier.semantics {
                    testTag = "ArrowRight"
                }) { Icon(Icons.Default.KeyboardArrowRight) }
        }
    )
}

@Composable
fun TablePage(
    yearMonth: YearMonth,
    daysCoffeesStore: DaysCoffeesStore,
    navController: NavController
) {
    val coffeesState by daysCoffeesStore.state.collectAsState()

    Column(horizontalAlignment = Alignment.End) {
        MonthTable(
            yearMonth,
            coffeesState.coffees.filter { entry: Map.Entry<LocalDate, DayCoffee> -> entry.key.year == yearMonth.year && entry.key.month == yearMonth.month }
                .mapKeys { entry: Map.Entry<LocalDate, DayCoffee> -> entry.key.dayOfMonth }
                .mapValues { entry: Map.Entry<Int, DayCoffee> -> entry.value.getIconId() },
            navController,
            modifier = Modifier.weight(1f)
        )
        Text("${yearMonth.year}", modifier = Modifier.padding(16.dp))
    }
}
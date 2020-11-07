package ru.beryukhov.coffeegram.pages

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.ui.tooling.preview.Preview
import org.threeten.bp.LocalDate
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.model.*
import ru.beryukhov.coffeegram.view.CoffeeTypeItem


@Composable
fun CoffeeListAppBar(navController: NavController){
    TopAppBar(title = { Text("Add drink") },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    Icons.Default.ArrowBack
                )
            }
        }
    )
}

@Composable
fun CoffeeListPage(daysCoffeesStore: DaysCoffeesStore, date: LocalDate) {
    CoffeeList(
        date,
        daysCoffeesStore
    )
}

@Composable
fun CoffeeList(
    localDate: LocalDate,
    daysCoffeesStore: DaysCoffeesStore,
    modifier: Modifier = Modifier
) {
    val dayCoffeeState: DaysCoffeesState by daysCoffeesStore.state.collectAsState()
    val dayCoffee = dayCoffeeState.coffees[localDate]?:DayCoffee()
    LazyColumnFor(items = dayCoffee.coffeeCountMap.toList(),
        modifier = modifier.fillMaxHeight(),
        itemContent = { pair ->
            CoffeeTypeItem(localDate, pair.first, pair.second, daysCoffeesStore)
        })
}

@Preview
@Composable
private fun preview() {
    CoffeeList(
        LocalDate.now(),
        DaysCoffeesStore(),
        modifier = Modifier
    )
}
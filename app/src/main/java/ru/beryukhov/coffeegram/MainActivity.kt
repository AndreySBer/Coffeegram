package ru.beryukhov.coffeegram

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.transition
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawOpacity
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import androidx.ui.tooling.preview.Preview
import org.threeten.bp.LocalDate
import ru.beryukhov.coffeegram.animations.*
import ru.beryukhov.coffeegram.app_ui.CoffeegramTheme
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.pages.*

import org.threeten.bp.YearMonth


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var splashShown by remember { mutableStateOf(SplashState.Shown) }
            val transition = transition(splashTransitionDefinition, splashShown)
            Box {
                LandingPage(
                    modifier = Modifier.drawOpacity(transition[splashAlphaKey]),
                    onTimeout = { splashShown = SplashState.Completed }
                )
                PagesContent(
                    modifier = Modifier.drawOpacity(transition[contentAlphaKey]),
                    topPadding = transition[contentTopPaddingKey],
                    daysCoffeesStore = DaysCoffeesStore()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PagesContent(daysCoffeesStore = DaysCoffeesStore())
}

@Composable
fun PagesContent(
    modifier: Modifier = Modifier,
    topPadding: Dp = 0.dp,
    daysCoffeesStore: DaysCoffeesStore
) {
    val navController = rememberNavController()

    CoffeegramTheme {
        NavHost(navController, startDestination = "$TABLE_PAGE/{$YEAR_MONTH_PARAM}") {
            composable("$TABLE_PAGE/{$YEAR_MONTH_PARAM}",
                arguments = listOf(navArgument(YEAR_MONTH_PARAM) { type = NavType.IntType })
            ) { backStackEntry ->
                val yearMonth = backStackEntry.arguments!!.getInt(YEAR_MONTH_PARAM).let {
                    Log.d("NAV_T", "it$it")
                    if (it%12 == 0) YearMonth.now()
                    else it.toYearMonth()
                }//?:YearMonth.now()
                Log.d("NAV_T", yearMonth.toString())
                MyScaffold(modifier = modifier, topPadding = topPadding, topBar = {TableAppBar(
                    yearMonth,
                    navController
                )}) {
                    TablePage(
                        yearMonth,
                        daysCoffeesStore,
                        navController
                    )
                }
            }
            composable("$COFFEELIST_PAGE/{$DAY_OF_MONTH_PARAM}") {backStackEntry ->
                val localDate = LocalDate.ofEpochDay(
                    backStackEntry.arguments!!.getString(
                        DAY_OF_MONTH_PARAM
                    )!!.toLong()
                )
                Log.d("NAV_C", localDate.toString())
                MyScaffold(modifier = modifier, topPadding = topPadding, topBar =
                {CoffeeListAppBar(navController)})
                {
                    CoffeeListPage(
                        daysCoffeesStore,
                        localDate
                    )
                }
            }
        }
    }
}

@Composable
fun MyScaffold(modifier: Modifier = Modifier, topPadding: Dp = 0.dp, topBar: @Composable () -> Unit = emptyContent(), bodyContent: @Composable () -> Unit){
    Scaffold(
        modifier = modifier, topBar = topBar
    ){
        Column {
            Spacer(Modifier.padding(top = topPadding).align(Alignment.CenterHorizontally))
            bodyContent()
        }
    }
}

fun Int.toYearMonth(): YearMonth = YearMonth.of((this-1) / 12, (this-1) % 12)
fun YearMonth.toInt(): Int = this.year * 12 + this.monthValue

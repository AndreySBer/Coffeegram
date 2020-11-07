package ru.beryukhov.coffeegram.view

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import androidx.ui.tooling.preview.Preview
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.format.TextStyle
import ru.beryukhov.coffeegram.COFFEELIST_PAGE
import ru.beryukhov.coffeegram.R
import ru.beryukhov.coffeegram.app_ui.CoffeegramTheme
import ru.beryukhov.coffeegram.times
import java.text.DateFormatSymbols
import java.util.*

data class DayItem(
    val day: String,
    @DrawableRes val iconId: Int? = null,
    val dayOfMonth: LocalDate? = null
)

@Composable
fun DayCell(
    dayItem: DayItem,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier =
    if (dayItem.dayOfMonth == null) modifier else
        modifier.clickable(onClick = {
            navController.navigate("$COFFEELIST_PAGE/${dayItem.dayOfMonth.toEpochDay()}")
        })
    ) {
        with(dayItem) {
            if (iconId != null) {
                Image(
                    vectorResource(id = iconId),
                    modifier = Modifier
                        .preferredSize(32.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                )
            } else {
                Icon(
                    Icons.Default.Delete,
                    tint = Color.Transparent,
                    modifier = Modifier.preferredSize(32.dp)
                )
            }

            Text(
                AnnotatedString(
                    text = day,
                    paragraphStyle = ParagraphStyle(textAlign = TextAlign.Center)
                )
            )
        }
    }
}

@Composable
fun WeekRow(dayItems: List<DayItem?>, navController: NavController) {
    val weekDaysItems = dayItems.toMutableList()
    weekDaysItems.addAll(listOf(DayItem("")) * (7 - weekDaysItems.size))
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth()) {
            for (dayItem in weekDaysItems) {
                DayCell(
                    dayItem = dayItem
                        ?: DayItem(""),
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Divider()
    }

}

@Composable
fun MonthTableAdjusted(
    weekItems: List<List<DayItem?>>,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        weekItems.map { WeekRow(dayItems = it, navController = navController) }
    }
}

data class WeekDayVectorPair(
    val day: LocalDate,
    val weekDay: DayOfWeek,
    @DrawableRes var iconId: Int? = null
) {
    fun toDayItem(): DayItem =
        DayItem(day.dayOfMonth.toString(), iconId, day)
}

@Composable
fun MonthTable(
    yearMonth: YearMonth,
    filledDayItemsMap: Map<Int, Int?>,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val weekDays: List<DayItem> = getWeekDaysNames(
        ContextAmbient.current
    ).map { DayItem(it) }
    val days1to31 = mutableListOf<Int>()
    for (i in 1 until 31) {
        days1to31.add(i)
    }
    val days = days1to31.filter { yearMonth.isValidDay(it) }
        .associateBy(
            { it },
            {
                WeekDayVectorPair(
                    yearMonth.atDay(it),
                    yearMonth.atDay(it).dayOfWeek
                )
            })
        .toMutableMap()
    filledDayItemsMap.forEach { days[it.key]?.iconId = it.value }
    val weekDaysStrings =
        getWeekDaysNames(ContextAmbient.current)
    val numberOfFirstDay = weekDaysStrings.indexOf(
        days[1]!!.weekDay.getDisplayName(
            TextStyle.SHORT,
            ContextAmbient.current.resources.configuration.locale
        )
    )
    val daysList: List<WeekDayVectorPair> = days.toList().sortedBy { it.first }.map { it.second }
    val firstWeek: List<DayItem> =
        listOf(DayItem("")) * (numberOfFirstDay) + daysList.take(7 - numberOfFirstDay)
            .map(WeekDayVectorPair::toDayItem)
    val secondToSixWeeks: List<List<DayItem>> = listOf(2, 3, 4, 5, 6).map {
        daysList.drop(7 * (it - 1) - numberOfFirstDay).take(7)
    }.filterNot { it.isEmpty() }
        .map { it.map(WeekDayVectorPair::toDayItem) }

    val weekItems = mutableListOf(
        weekDays,
        firstWeek
    )
    weekItems.addAll(secondToSixWeeks)
    return MonthTableAdjusted(
        weekItems,
        navController,
        modifier = modifier
    )
}


@Preview(showBackground = true)
@Composable
fun TablePreview() {
    CoffeegramTheme {
        /*Row {
            DayCell(DayItem("Пн"))
            DayCell(DayItem("1"))
            DayCell(DayItem("2", Icons.Default.Call))
        }*/
        /*WeekRow(
            listOf(
                null,
                DayItem("Пн"),
                DayItem("1"),
                DayItem("2", Icons.Default.Call)
            )
        )*/
        SampleTable()
        //Text(getWeekDaysNames(ContextAmbient.current).toString())
    }
}

@Composable
fun SampleTable(modifier: Modifier = Modifier) =
    MonthTable(
        YearMonth.of(2020, 7),
        mapOf(2 to R.drawable.coffee),
        modifier = modifier,
        navController = NavController(ContextAmbient.current)
    )


fun getWeekDaysNames(context: Context): List<String> =
    getWeekDaysNames(context.resources.configuration.locale)

fun getWeekDaysNames(locale: Locale): List<String> {
    val list = DateFormatSymbols(locale).shortWeekdays.toMutableList()
    // this fun adds empty string at the beginning
    list.removeAt(0)
    return list
}

fun getEmptyWeek(start: Int, end: Int): List<DayItem> {
    val list = mutableListOf<DayItem>()
    for (i in start until end + 1) {
        list.add(DayItem("$i"))
    }
    return list
}
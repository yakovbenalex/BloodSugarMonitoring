package ru.opalevapps.EveryGlic

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AlertDialog
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import ru.opalevapps.EveryGlic.ui.AboutActivity
import ru.opalevapps.EveryGlic.ui.AgreementActivity
import ru.opalevapps.EveryGlic.ui.PreferencesActivity
import java.math.BigDecimal
import java.util.*

// for different aspect of application work
object MyUtill {
    private const val TAG = "myLog"

    // round float number to scale
    fun roundUp(value: Float, digits: Int): BigDecimal {
        return BigDecimal(value.toString()).setScale(digits, BigDecimal.ROUND_HALF_EVEN)
    }

    // get text with set accuracy after specified symbol(separator)
    fun getStringNumberWithAccuracy(
        str: String,
        scale: Int,
        separator: Char,
        fillZero: Boolean
    ): String {
        var s = str
        val sLength = s.length
        val sepIndex = s.indexOf(separator)
        if (sLength == 1 && s[0] == separator) {
            return "0$s"
        }
        // conditions of if:
        //  1 - length of string must be greater than 1
        //  2 - is the number needs to be set accuracy
        //  3 - is the number contains stated symbol
        if (sLength > 1 && sLength - 1 != sepIndex + scale && s.contains(separator.toString())) {
            // (sLength - 1 - sepIndex) - number of digits after the separator
            if (sLength - 1 - sepIndex > scale) {
                // get substring with specified number of digits after separator
                s = s.substring(0, sepIndex + 1 + scale)
            } else {
                // filling zeros
                if (fillZero) {
                    while (s.length - 1 < sepIndex + scale) {
                        s += "0"
                    }
                }
            }
        }
        return s
    }

    // checking of number for included in the specified range.
    fun numberInRange(number: Float, lowerBound: Float, upperBound: Float): Boolean {
        return !(number < lowerBound || number > upperBound)
    }

    // checking fields on empty end return true if so
    fun isEmpty(et: EditText): Boolean {
        return et.text.toString().trim { it <= ' ' }.isEmpty()
    }

    // set focus and additional option: clear editText if needs
    fun clearET(et: EditText) {
        et.setText("")
    }

    // request focus for empty required fields and return true if so
    fun requiredFiledEmpty(et: EditText): Boolean {
        if (isEmpty(et)) {
            et.requestFocus()
            return true
        }
        return false
    }

    // show alert dialog with specified title and text
    fun showDialog(context: Context?, title: String?, text: String?) {
        val builder = AlertDialog.Builder(
            context!!
        )
        builder.setTitle(title)
            .setMessage(text)
            .setCancelable(true)
            .setPositiveButton(
                Resources.getSystem().getString(android.R.string.ok)
            ) { dialog: DialogInterface, which: Int -> dialog.cancel() }.show()
    }

    // for parse menu item info on ActionBar (all items)
    fun parseMenuItemMain(context: Context, item: MenuItem) {
        when (item.itemId) {
            R.id.action_settings -> {
                val intentPreferencesActivity = Intent(context, PreferencesActivity::class.java)
                context.startActivity(intentPreferencesActivity)
            }
            R.id.action_agreement -> {
                val intentAgreementActivity = Intent(context, AgreementActivity::class.java)
                context.startActivity(intentAgreementActivity)
            }
            R.id.action_about -> {
                val intentSettingsActivity = Intent(context, AboutActivity::class.java)
                context.startActivity(intentSettingsActivity)
            }
            else -> {}
        }
        Log.d(TAG, "parseMenuItemInfo: $context ")
    }

    // for parse menu item info on ActionBar (all items)
    fun parseMenuItemInfo(context: Context, item: MenuItem): Boolean {
        when (item.itemId) {
            PreferencesActivity.IDM_INFO -> {
                // vars for show info dialog
                var dialogTitle = ""
                var dialogText = ""
                var showDialog = true
                when (context.javaClass.simpleName) {
                    "MainActivity" -> {
                        dialogTitle = "Главный экран"
                        dialogText =
                            "На этом экране будут отображаться 3 последних измерения. А также через главный экран осуществляется навигация по приложению."
                    }
                    "PreferencesActivity" -> {
                        dialogTitle = "Экран настроек"
                        dialogText = """
                            Здесь можно настроить различные предпочтения.
                            Настройки, относящиеся к диабету ограничены референсными интервалами.
                            """.trimIndent()
                    }
                    "AddOrChangeMeasurementActivity" -> {
                        dialogTitle = "Экран добавления/изменения измерения"
                        dialogText = """
                            Для добавления уровня сахара введите в соответствующее поле, комментарий для измерения не обязателен.
                            Выберете дату и время нажав на кнопки «Дата» и «Время», после чего они будут отображаться в зеленом поле.
                            Сохранить измерение можно нажав на кнопку «Сохранить».
                            
                            """.trimIndent()
                    }
                    "StatisticsActivity" -> {
                        dialogTitle = "Экран статистики"
                        dialogText =
                            """Здесь будет отображаться статистика сахаров за разные периоды времени:

 – «Текущий» период (неделя, месяц) означает, что отсчет ведется от начала периода (недели, месяца).
– «Последний» период (неделя, месяц) означает, что отсчет ведется за последние 30 дней (в случае месяца) и за 7 дней (в случае недели).
"""
                    }
                    "MeasurementsActivity" -> {
                        dialogTitle = "Экран измерений"
                        dialogText = """
                            Здесь можно просмотреть все добавленные измерения.
                            
                            Чтобы изменить или удалить измерение необходимо на него нажать. Откроется окно, в котором можно изменить уровень сахара, дату и время, после чего для сохранения нажать на кнопку «Сохранить» или для удаления на кнопку «Удалить».
                            
                            """.trimIndent()
                    }
                    "CalculatorCarbsActivity" -> {
                        dialogTitle = "Калькулятор углеводов"
                        dialogText =
                            """Калькулятор углеводов предназначен для подсчета хлебных единиц (ХЕ), зная которые можно рассчитать дозу инсулина.

Здесь можно подсчитать:
 - Количество хлебных единиц в продукте. 
Для этого нужно ввести сначала кол-во углеводов в 100 граммах продукта, затем сколько кол-во грамм продукта;
 - Количество грамм продукта, в котором содержится n хлебных единиц.
Для этого нужно ввести сначала кол-во углеводов в 100 граммах продукта, затем кол-во ХЕ.

Также можно подсчитать общее количество хлебных единиц. Нажав на кнопку «Добавить» количество хлебных единиц добавится ко счету.
"""
                    }
                    "InfoActivity" -> {
                        dialogTitle = "Экран полезного"
                        dialogText =
                            "Позволяет просматривать полезную информацию, связанную с заболеванием и не только."
                    }
                    else -> {
                        Toast.makeText(
                            context,
                            R.string.there_is_no_information_for_this_screen,
                            Toast.LENGTH_SHORT
                        ).show()
                        showDialog = false
                    }
                }

                // show dialog if help for screen is available
                if (showDialog) {
                    if (Locale.getDefault().language != "ru") {
                        dialogText += """

!!!
In current test version app - help is only available on russian language, sorry man.
!!!"""
                    }
                    showDialog(context, dialogTitle, dialogText)
                }
            }
            else -> {}
        }
        return true
    }

    // for parse menu item on ActionBar (info item only)
    fun createInfoItemInActionBar(menu: Menu): Boolean {
        menu.add(Menu.NONE, PreferencesActivity.IDM_INFO, Menu.NONE, R.string.info)
            .setIcon(R.drawable.ic_action_info)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return true
    }

    // scroll to bottom of textView
    fun scrollToBottomOfTextView(tv: TextView) {
        tv.post {
            if (tv.lineCount > tv.maxLines) {
                tv.scrollTo(0, tv.lineHeight * (tv.lineCount - tv.maxLines) + 2)
            }
        }
    }
}
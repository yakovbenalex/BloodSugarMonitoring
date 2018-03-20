package com.example.jason.EveryGlic;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;

import static com.example.jason.EveryGlic.PreferencesActivity.IDM_INFO;


class MyWorks {

    private static final String TAG = "myLog";

    // round float number to scale
    static BigDecimal roundUp(float value, int digits) {
        return new BigDecimal(String.valueOf(value)).setScale(digits, BigDecimal.ROUND_HALF_EVEN);
    }

    // get text with set accuracy after specified symbol(separator)
    static String getStringNumberWithAccuracy(String s, int scale, Character separator, boolean fillZero) {
        int sLength = s.length();
        int sepIndex = s.indexOf(separator);

        if ((sLength == 1) && (s.charAt(0) == separator)) {
            return "0" + s;
        }
        // conditions of if:
        //  1 - length of string must be greater than 1
        //  2 - is the number needs to be set accuracy
        //  3 - is the number contains stated symbol
        if ((sLength > 1) && (sLength - 1 != sepIndex + scale) && (s.contains(separator.toString()))) {
            // (sLength - 1 - sepIndex) - number of digits after the separator
            if ((sLength - 1 - sepIndex > scale)) {
                // get substring with specified number of digits after separator
                s = s.substring(0, sepIndex + 1 + scale);
            } else {
                // filling zeros
                if (fillZero) {
                    while (s.length() - 1 < sepIndex + scale) {
                        s += "0";
                    }
                }
            }
        }
        return s;
    }

    // checking of number for included in the specified range.
    static boolean numberInRange(float number, float lowerBound, float upperBound) {
        return !(number < lowerBound || number > upperBound);
    }

    // checking fields on empty end return true if so
    static boolean isEmpty(EditText et) {
        return et.getText().toString().trim().length() == 0;
    }

    // set focus and additional option: clear editText if needs
    static void clearET(EditText et) {
        et.setText("");
    }

    // request focus for empty required fields and return true if so
    static boolean requiredFiledEmpty(EditText et) {
        if (isEmpty(et)) {
            et.requestFocus();
            return true;
        }
        return false;
    }

    // show alert dialog with setted title and text
    static void showDialog(Context context, String title, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title)
                .setMessage(text)
                .setCancelable(true)
                .setPositiveButton(Resources.getSystem().getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
    }

    // for parse menu item info on ActionBar (all items)
    static boolean parseMenuItemMain(Context context, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intentPreferencesActivity = new Intent(context, PreferencesActivity.class);
                context.startActivity(intentPreferencesActivity);
                break;

            /*case R.id.action_info:
            Toast.makeText(context, "Test", Toast.LENGTH_SHORT).show();
            // for identify called activity
            switch (context.getClass().getSimpleName()) {
                case "MainActivity":
                    Toast.makeText(context, "MainActivity", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(context, "There is no information for this screen", Toast.LENGTH_SHORT).show();
                    break;
            }*/

            // start agreement activity
            case R.id.action_agreement:
                Intent intentAgreementActivity = new Intent(context, AgreementActivity.class);
                context.startActivity(intentAgreementActivity);
                break;

            case R.id.action_about:
                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.activity_info_about);
                dialog.setTitle(R.string.about);
                dialog.show();
                break;

            default:
                break;
        }
        Log.d(TAG, "parseMenuItemInfo: " + context + " ");
        return true;
    }

    // for parse menu item info on ActionBar (all items)
    static boolean parseMenuItemInfo(Context context, MenuItem item) {
        switch (item.getItemId()) {
            case IDM_INFO:
                // vars for show info dialog
                String dialogTitle = "";
                String dialogText = "";
                boolean showDialog = true;

                // for identify called activity
                // IN THE FUTURE IT WILL BE IN DATABASE //
                switch (context.getClass().getSimpleName()) {
                    case "MainActivity":
                        dialogTitle = "Главный экран";
                        dialogText = "На этом экране будут отображаться 3 последних измерения. А также через главный экран осуществляется навигация по приложению.";
                        break;

                    case "PreferencesActivity":
                        dialogTitle = "Экран настроек";
                        dialogText = "Здесь можно настроить различные предпочтения.\n" +
                                "Настройки, относящиеся к диабету ограничены референсными интервалами.";
                        break;

                    case "AddOrChangeMeasurementActivity":
                        dialogTitle = "Экран добавления/изменения измерения";
                        dialogText = "Для добавления уровня сахара введите в соответствующее поле, комментарий для измерения не обязателен.\n" +
                                "Выберете дату и время нажав на кнопки «Дата» и «Время», после чего они будут отображаться в зеленом поле.\n" +
                                "Сохранить измерение можно нажав на кнопку «Сохранить».\n";
                        break;

                    case "StatisticsActivity":
                        dialogTitle = "Экран статистики";
                        dialogText = "Здесь будет отображаться статистика сахаров за разные периоды времени:\n" +
                                "\n" +
                                " – «Текущий» период (неделя, месяц) означает, что отсчет ведется от начала периода (недели, месяца).\n" +
                                "– «Последний» период (неделя, месяц) означает, что отсчет ведется за последние 30 дней (в случае месяца) и за 7 дней (в случае недели).\n";
                        break;

                    case "MeasurementsActivity":
                        dialogTitle = "Экран измерений";
                        dialogText = "Здесь можно просмотреть все добавленные измерения.\n" +
                                "\nЧтобы изменить или удалить измерение необходимо на него нажать. " +
                                "Откроется окно, в котором можно изменить уровень сахара, дату и время, " +
                                "после чего для сохранения нажать на кнопку «Сохранить» или для удаления на кнопку «Удалить».\n";
                        break;

                    case "CalculatorCarbsActivity":
                        dialogTitle = "Калькулятор углеводов";
                        dialogText = "Калькулятор углеводов предназначен для подсчета хлебных единиц (ХЕ), зная которые можно рассчитать дозу инсулина.\n" +
                                "\n" +
                                "Здесь можно подсчитать:\n" +
                                " - Количество хлебных единиц в продукте. \n" +
                                "Для этого нужно ввести сначала кол-во углеводов в 100 граммах продукта, затем сколько кол-во грамм продукта;\n" +
                                " - Количество грамм продукта, в котором содержится n хлебных единиц.\n" +
                                "Для этого нужно ввести сначала кол-во углеводов в 100 граммах продукта, затем кол-во ХЕ.\n" +
                                "\n" +
                                "Также можно подсчитать общее количество хлебных единиц. Нажав на кнопку «Добавить» количество хлебных единиц добавится ко счету.\n";
                        break;

                    case "InfoActivity":
                        dialogTitle = "Экран полезного";
                        dialogText = "Позволяет просматривать полезную информацию, связанную с заболеванием и не только.";
                        break;

                    default:
                        Toast.makeText(context, R.string.there_is_no_information_for_this_screen, Toast.LENGTH_SHORT).show();
                        showDialog = false;
                        break;
                }

                // show dialog if help for screen is available
                if (showDialog) {
                    dialogText += "\n\n!!!\nВ текущей тестовой версии приложения " +
                            "справка имется только на русском языке.\n!!!";
                    showDialog(context, dialogTitle, dialogText);
                }
                break;

            default:
                break;
        }
//        Log.d(TAG, "parseMenuItemInfo: " + context + " ");
        return true;
    }

    // for parse menu item on ActionBar (info item only)
    static boolean createInfoItemInActionBar(Menu menu) {
        menu.add(Menu.NONE, IDM_INFO, Menu.NONE, R.string.info)
                .setIcon(R.drawable.ic_action_info)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    // scroll to bottom of textView
    static void scrollToBottomOfTextView(final TextView tv) {
        tv.post(new Runnable() {
            public void run() {
                if (tv.getLineCount() > tv.getMaxLines()) {
                    tv.scrollTo(0, tv.getLineHeight() * (tv.getLineCount() - tv.getMaxLines()) + 2);
                }
            }
        });
    }
}
package com.example.jason.EveryGlic;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
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
    static boolean numberInRange(Float number, float lowerBound, float upperBound) {
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

    // for parse menu item on ActionBar (all items)
    static boolean parseMenuItemMain(Context context, String calledActivityName, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intentPreferencesActivity = new Intent(context, PreferencesActivity.class);
                context.startActivity(intentPreferencesActivity);
                break;

            case R.id.action_info:
                Toast.makeText(context, "Test", Toast.LENGTH_SHORT).show();
                switch (calledActivityName){
                    case "MainActivity":
                        Toast.makeText(context, "MainActivity", Toast.LENGTH_SHORT).show();
                        break;

                    case "PreferencesActivity":
                        Toast.makeText(context, "PreferencesActivity", Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        Toast.makeText(context, "There is no information for this screen", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;

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
        Log.d(TAG, "parseMenuItemMain: " + context + " ");
        return true;
    }

    // for parse menu item on ActionBar (info item only)
    static boolean createInfoItemInActionBar(Menu menu) {
        menu.add(Menu.NONE, IDM_INFO, Menu.NONE, R.string.info)
                .setIcon(R.drawable.ic_action_info)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

}
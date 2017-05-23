package com.example.jason.bloodGlucoseMonitoring;

import android.widget.EditText;

import java.math.BigDecimal;


class MyWorks {

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
        if (number < lowerBound || number > upperBound) {
            return false;
        }
        return true;
    }

    // checking fields on empty
    static boolean isEmpty(EditText et) {
        return et.getText().toString().trim().length() == 0;
    }

    // set focus and additional option: show message, clear editText if needs
    static void setFocus(EditText et, boolean clearET) {
        if (clearET) et.setText("");
        et.requestFocus();
    }

    // request focus for empty required fields and return true if so
    static boolean requiredFiledEmpty(EditText et) {
        if (isEmpty(et)) {
            et.requestFocus();
            return true;
        }
        return false;
    }
}

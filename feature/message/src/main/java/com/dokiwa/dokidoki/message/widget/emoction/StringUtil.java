package com.dokiwa.dokidoki.message.widget.emoction;

import android.text.TextUtils;

import java.util.Locale;
import java.util.UUID;

public class StringUtil {

    public static int counterChars(String str) {
        // return
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            int tmp = (int) str.charAt(i);
            if (tmp > 0 && tmp < 127) {
                count += 1;
            } else {
                count += 2;
            }
        }
        return count;
    }
}

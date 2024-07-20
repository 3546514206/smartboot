package org.smartboot.smart.flow.admin.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author qinluo
 * @date 2023/3/5 23:31
 * @since 1.0.0
 */
public class DateUtils {

    public static String format(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sdf.format(date);
    }
}

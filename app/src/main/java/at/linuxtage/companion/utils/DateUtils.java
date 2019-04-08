package at.linuxtage.companion.utils;

import android.content.Context;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.Nullable;

public class DateUtils {

	private static final TimeZone AUSTRIA_TIME_ZONE = TimeZone.getTimeZone("Europe/Vienna");

	public static TimeZone getAustriaTimeZone() {
		return AUSTRIA_TIME_ZONE;
	}

	public static DateFormat withAustrianTimeZone(DateFormat format) {
		format.setTimeZone(AUSTRIA_TIME_ZONE);
		return format;
	}

	public static DateFormat getTimeDateFormat(Context context) {
		return withAustrianTimeZone(android.text.format.DateFormat.getTimeFormat(context));
	}

	public static int getYear(long timestamp) {
		return getYear(timestamp, null);
	}

	public static int getYear(long timestamp, @Nullable Calendar calendar) {
		if (calendar == null) {
			calendar = Calendar.getInstance(DateUtils.getAustriaTimeZone(), Locale.US);
		}
		calendar.setTimeInMillis(timestamp);
		return calendar.get(Calendar.YEAR);
	}
}

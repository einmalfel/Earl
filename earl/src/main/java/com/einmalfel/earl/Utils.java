package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

class Utils {
  static final String ITUNES_NAMESPACE = "http://www.itunes.com/dtds/podcast-1.0.dtd";

  private static final String TAG = "E.UTL";
  private static final DateFormat rfc822DateTimeFormat = new SimpleDateFormat(
      "EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);

  private static final DateFormat[] itunesDurationFormats = {
      new SimpleDateFormat("HH:mm:ss", Locale.US),
      new SimpleDateFormat("H:mm:ss", Locale.US),
      new SimpleDateFormat("   mm:ss", Locale.US),
      new SimpleDateFormat("    m:ss", Locale.US),
      new SimpleDateFormat("ssssssss", Locale.US),};

  @Nullable
  static Date parseRFC822Date(@NonNull String dateString) {
    try {
      return rfc822DateTimeFormat.parse(dateString);
    } catch (ParseException exception) {
      Log.w(TAG, "Malformed date " + dateString);
      return null;
    }
  }

  @Nullable
  static Long parseItunesDuration(@NonNull String dateString) {
    for (DateFormat format : itunesDurationFormats) {
      try {
        Date date = format.parse(dateString);
        Calendar calendar = GregorianCalendar.getInstance(Locale.US);
        calendar.setTime(date);
        return calendar.getTimeInMillis() / 1000;
      } catch (ParseException ignored) {}
    }
    return null;
  }

  /**
   * Copied from http://developer.android.com/training/basics/network-ops/xml.html#skip
   */
  static void skipTag(@NonNull XmlPullParser parser) throws XmlPullParserException, IOException {
    if (parser.getEventType() != XmlPullParser.START_TAG) {
      throw new IllegalStateException();
    }
    int depth = 1;
    while (depth != 0) {
      switch (parser.next()) {
        case XmlPullParser.END_TAG:
          depth--;
          break;
        case XmlPullParser.START_TAG:
          depth++;
          break;
      }
    }
  }

}

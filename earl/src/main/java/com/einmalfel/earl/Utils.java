package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
      new SimpleDateFormat("    m:ss", Locale.US),};

  @Nullable
  static Date parseRFC822Date(@NonNull String dateString) {
    try {
      return rfc822DateTimeFormat.parse(dateString);
    } catch (ParseException exception) {
      Log.w(TAG, "Malformed date " + dateString);
      return null;
    }
  }

  /**
   * sourced from: http://cokere.com/RFC3339Date.txt
   */
  public static java.util.Date parseRFC3339Date(String datestring) throws java.text.ParseException, IndexOutOfBoundsException{
    Date d = new Date();

    //if there is no time zone, we don't need to do any special parsing.
    if(datestring.endsWith("Z")){
      try{
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");//spec for RFC3339
        d = s.parse(datestring);
      }
      catch(java.text.ParseException pe){//try again with optional decimals
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");//spec for RFC3339 (with fractional seconds)
        s.setLenient(true);
        d = s.parse(datestring);
      }
      return d;
    }

    //step one, split off the timezone.
    String firstpart = datestring.substring(0,datestring.lastIndexOf('-'));
    String secondpart = datestring.substring(datestring.lastIndexOf('-'));

    //step two, remove the colon from the timezone offset
    secondpart = secondpart.substring(0,secondpart.indexOf(':')) + secondpart.substring(secondpart.indexOf(':')+1);
    datestring  = firstpart + secondpart;
    SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");//spec for RFC3339
    try{
      d = s.parse(datestring);
    }
    catch(java.text.ParseException pe){//try again with optional decimals
      s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");//spec for RFC3339 (with fractional seconds)
      s.setLenient(true);
      d = s.parse(datestring);
    }
    return d;
  }

  /**
   * @param dateString time string to parse
   * @return episode duration in seconds or null if parsing fails
   */
  @Nullable
  static Integer parseItunesDuration(@NonNull String dateString) {
    for (DateFormat format : itunesDurationFormats) {
      try {
        Date date = format.parse(dateString);
        return (int)(date.getTime() / 1000);
      } catch (ParseException ignored) {}
    }
    // if none of formats match, this could be an integer value in seconds
    return tryParseInt(dateString);
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

  @Nullable
  static Integer tryParseInt(@Nullable String string) {
    if (string == null) {
      Log.w(TAG, "Null value while parsing integer", new NullPointerException());
      return null;
    } else {
      try {
        return Integer.valueOf(string);
      } catch (NumberFormatException exception) {
        Log.w(TAG, "Error parsing integer value '" + string, exception);
        return null;
      }
    }
  }

  @NonNull
  static Integer nonNullInt(@Nullable String string) {
    Integer result = tryParseInt(string);
    if (result == null) {
      Log.w(TAG, "Malformed integer string replaced with '-1'");
      return -1;
    } else {
      return result;
    }
  }

  @NonNull
  static String nonNullString(@Nullable String string) {
    if (string == null) {
      Log.w(TAG, "Unexpectedly got null string. Replaced with empty", new NullPointerException());
      return "";
    } else {
      return string;
    }
  }

  @Nullable
  static URL tryParseUrl(@Nullable String string) {
    if (string == null) {
      Log.w(TAG, "Null value while parsing url", new NullPointerException());
      return null;
    } else {
      try {
        return new URL(string);
      } catch (MalformedURLException exception) {
        Log.w(TAG, "Error parsing url value '" + string, exception);
        return null;
      }
    }
  }

  @NonNull
  static URL nonNullUrl(@Nullable String string) {
    URL result = tryParseUrl(string);
    if (result == null) {
      Log.w(TAG, "Malformed URL replaced with 'http://'");
      try {
        result = new URL("http://");
      } catch (MalformedURLException ignored) {throw new AssertionError("Should never get here");}
    }
    return result;
  }
}

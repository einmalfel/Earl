package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.einmalfel.earl.tools.NPTParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

class Utils {
  static final String ATOM_NAMESPACE = "http://www.w3.org/2005/Atom";
  static final String MEDIA_NAMESPACE = "http://search.yahoo.com/mrss/";
  static final String ITUNES_NAMESPACE = "http://www.itunes.com/dtds/podcast-1.0.dtd";

  private static final String TAG = "Earl.Utils";
  private static final DateFormat rfc822DateTimeFormat = new SimpleDateFormat(
      "EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);

  @Nullable
  static Date parseRFC822Date(@NonNull String dateString) {
    try {
      return rfc822DateTimeFormat.parse(dateString);
    } catch (ParseException exception) {
      Log.w(TAG, "Malformed date " + dateString);
      return null;
    }
  }

  private static DateFormat RFC3339;
  private static DateFormat RFC3339Ms;
  private static DateFormat RFC3339Tz;
  private static DateFormat RFC3339TzMs;

  static void setupRFC3339() {
    RFC3339 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
    RFC3339Ms = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US);
    RFC3339Ms.setLenient(true);
    RFC3339Tz = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
    RFC3339TzMs = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ", Locale.US);
    RFC3339TzMs.setLenient(true);
  }

  /**
   * based on: http://cokere.com/RFC3339Date.txt
   */
  @Nullable
  public static java.util.Date parseRFC3339Date(@NonNull String string) {
    if (RFC3339 == null) {
      setupRFC3339();
    }
    try {
      Date date;

      //if there is no time zone, we don't need to do any special parsing.
      if (string.endsWith("Z")) {
        try {
          date = RFC3339.parse(string);
        } catch (java.text.ParseException pe) {//try again with optional decimals
          date = RFC3339Ms.parse(string);
        }
        return date;
      }

      //step one, split off the timezone.
      String firstPart = string.substring(0, string.lastIndexOf('-'));
      String secondPart = string.substring(string.lastIndexOf('-'));

      //step two, remove the colon from the timezone offset
      secondPart = secondPart.substring(0, secondPart.indexOf(':')) + secondPart
          .substring(secondPart.indexOf(':') + 1);
      string = firstPart + secondPart;
      try {
        date = RFC3339Tz.parse(string);
      } catch (java.text.ParseException pe) {//try again with optional decimals
        date = RFC3339TzMs.parse(string);
      }
      return date;
    } catch (ParseException exception) {
      Log.w(TAG, "Failed to parse RFC3339 string " + string, exception);
      return null;
    }
  }

  @Nullable
  static Integer parseRFC2326NPT(@NonNull String string) {
    try {
      return (int) new NPTParser(string).parse();
    } catch (ParseException exception) {
      Log.w(TAG, "Failed to parse media:rating time", exception);
      return null;
    }
  }

  private static DateFormat[] itunesDurationFormats = null;

  static void setupItunesDateFormats() {
    itunesDurationFormats = new DateFormat[]{
        new SimpleDateFormat("HH:mm:ss", Locale.US),
        new SimpleDateFormat("H:mm:ss", Locale.US),
        new SimpleDateFormat("mm:ss", Locale.US),
        new SimpleDateFormat("m:ss", Locale.US),};

    TimeZone utc = TimeZone.getTimeZone("UTC");
    for (DateFormat format : itunesDurationFormats) {
      format.setTimeZone(utc);
    }
  }

  /**
   * @param dateString time string to parse
   * @return episode duration in seconds or null if parsing fails
   */
  @Nullable
  static Integer parseItunesDuration(@NonNull String dateString) {
    if (itunesDurationFormats == null) {
      setupItunesDateFormats();
    }
    for (DateFormat format : itunesDurationFormats) {
      try {
        Date date = format.parse(dateString);
        return (int) (date.getTime() / 1000);
      } catch (ParseException ignored) {}
    }
    // if none of formats match, this could be an integer value in seconds
    return tryParseInt(dateString);
  }

  @Nullable
  static Integer parseMediaRssTime(@NonNull String time) {
    // MRSS spec doesn't always clarify which time format is used.
    // In examples it looks quite like itunes duration.
    Integer result = Utils.parseItunesDuration(time);
    if (result == null) {
      result = Utils.parseRFC2326NPT(time);
    } else {
      // Itunes duration is in [s]
      result *= 1000;
    }
    return result;
  }

  /**
   * Fast-forward parser to the end of current tag (last tag whose START_TAG we passed),
   * skipping all nested tags.
   *
   * @throws XmlPullParserException
   * @throws IOException
   */
  static void finishTag(@NonNull XmlPullParser parser) throws XmlPullParserException, IOException {
    while (parser.getEventType() != XmlPullParser.END_TAG) {
      if (parser.getEventType() == XmlPullParser.START_TAG) {
        Utils.skipTag(parser);
      }
      parser.next();
    }
  }

  /**
   * Skip next tag (we are currently at its START_TAG).
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
        Log.w(TAG, "Error parsing integer value '" + string + "'", exception);
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
        Log.w(TAG, "Error parsing url value '" + string + "'", exception);
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

  @Nullable
  static URI tryParseUri(@Nullable String string) {
    if (string == null) {
      Log.w(TAG, "Null value while parsing uri", new NullPointerException());
      return null;
    } else {
      try {
        return new URI(string);
      } catch (URISyntaxException exception) {
        Log.w(TAG, "Error parsing uri value '" + string + "'", exception);
        return null;
      }
    }
  }

  @NonNull
  static URI nonNullUri(@Nullable String string) {
    URI result = tryParseUri(string);
    if (result == null) {
      Log.w(TAG, "Malformed URI replaced with 'http://'");
      try {
        result = new URI("http:///");
      } catch (URISyntaxException ignored) {throw new AssertionError("Should never get here");}
    }
    return result;
  }
}

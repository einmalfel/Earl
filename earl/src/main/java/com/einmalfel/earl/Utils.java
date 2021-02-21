package com.einmalfel.earl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

final class Utils {
  static final String ATOM_NAMESPACE = "http://www.w3.org/2005/Atom";
  static final String MEDIA_NAMESPACE = "http://search.yahoo.com/mrss/";
  static final String ITUNES_NAMESPACE = "http://www.itunes.com/dtds/podcast-1.0.dtd";
  static final String CONTENT_NAMESPACE = "http://purl.org/rss/1.0/modules/content/";

  private static final String TAG = "Earl.Utils";
  private static final DateFormat rfc822DateTimeFormat = new SimpleDateFormat(
  "EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
  private static final DateFormat iso8601DateTimeFormat = new SimpleDateFormat(
  "yyyy-MM-dd'T'HH:mm:ss.SSSz", Locale.ENGLISH);
  private static final DateFormat RFC3339Tz = new SimpleDateFormat(
  "yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
  private static final DateFormat RFC3339TzMs = new SimpleDateFormat(
  "yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ", Locale.ENGLISH);
  private static final DateFormat[] itunesDurationFormats = {
  new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH),
  new SimpleDateFormat("H:mm:ss", Locale.ENGLISH),
  new SimpleDateFormat("mm:ss", Locale.ENGLISH),
  new SimpleDateFormat("m:ss", Locale.ENGLISH),
  };

  static {
	RFC3339TzMs.setLenient(true);

	final TimeZone utc = TimeZone.getTimeZone("UTC");
	for (DateFormat format : itunesDurationFormats) {
	  format.setTimeZone(utc);
	}
  }

  private Utils() {
  }

  /**
   * One single function to parse all types of date strings. Due to the non-standard nature of
   * users of the Internet, let alone developers, standards are easily broken by developers and
   * we have to try our best at figuring out what those dates are.
   *
   * @param dateString date (as string) to parse
   * @return parsed date on success, NULL otherwise
   */
  @Nullable
  static Date parseDate(@NonNull String dateString) {
	final String trimmedDate = dateString.trim();
	Date date = parseRFC822Date(trimmedDate);
	if (null == date) {
	  date = parseISO8601Date(trimmedDate);
	}
	if (null == date) {
	  date = parseRFC3339Date(trimmedDate);
	}
	if (null == date) {
	  Log.w(TAG, "Malformed date " + dateString);
	}
	return date;
  }

  @Nullable
  private static Date parseRFC822Date(@NonNull String dateString) {
	try {
	  return rfc822DateTimeFormat.parse(dateString);
	} catch (ParseException ignored) {
	  return null;
	}
  }

  @Nullable
  private static Date parseISO8601Date(@NonNull String dateString) {
	try {
	  return iso8601DateTimeFormat.parse(patchISO8601Date(dateString));
	} catch (ParseException ignored) {
	  return null;
	}
  }

  /**
   * Patches the specified date due to non-standardization when it comes to ISO 8601. In
   * particular, this will normalize the string so that our {@link DateFormat} could work properly
   * and parse the date. We need this to support pre-1.7 Java versions.
   * <p>
   * The function supports these formats:
   * <p>
   * 2016-01-01T01:01:01.001Z
   * 2016-01-01T0101:01.001Z
   * 2016-01-01T01:01:01.001+01:01
   * 2016-01-01T0101:01.001+01:01
   *
   * @param dateString date (as string) to parse
   * @return patched date
   */
  @NonNull
  private static String patchISO8601Date(@NonNull String dateString) {
	final int dateLength = dateString.length();
	if (19 < dateLength) {
	  // If zero time, add TZ indicator.
	  if (dateString.endsWith("Z")) {
		dateString = dateString.substring(0, dateLength - 1) + "GMT-00:00";
	  } else {
		if (!dateString.substring(0, dateLength - 9).startsWith("GMT")) {
		  // Prefix "+01:00" with "GMT" so it the formatter can work properly.
		  String preTimeZone = dateString.substring(0, dateLength - 6);
		  String timeZone = dateString.substring(dateLength - 6, dateLength);
		  dateString = preTimeZone + "GMT" + timeZone;
		}
	  }

	  // Detect if there a colon missing between hour and minute.
	  if (':' != dateString.charAt(13)) {
		// Insert a colon between hour and minute.
		dateString = dateString.substring(0, 13) + ":" + dateString.substring(13);
	  }
	}
	return dateString;
  }

  /**
   * based on: http://cokere.com/RFC3339Date.txt
   */
  @Nullable
  private static Date parseRFC3339Date(@NonNull String string) {
	try {
	  //if there is no time zone, we don't need to do any special parsing.
	  if (string.endsWith("Z")) {
		string = string.replace("Z", "+00:00");
	  } else {
		char timezoneSign = string.contains("+") ? '+' : '-';

		//step one, split off the timezone.
		String firstPart = string.substring(0, string.lastIndexOf(timezoneSign));
		String secondPart = string.substring(string.lastIndexOf(timezoneSign));

		//step two, remove the colon from the timezone offset
		secondPart = secondPart.substring(0, secondPart.indexOf(':')) + secondPart
		.substring(secondPart.indexOf(':') + 1);
		string = firstPart + secondPart;
	  }

	  try {
		return RFC3339Tz.parse(string);
	  } catch (ParseException ignored) { // try again with optional decimals
		return RFC3339TzMs.parse(string);
	  }
	} catch (ParseException ignored) {
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

  /**
   * @param dateString time string to parse
   * @return episode duration in seconds or null if parsing fails
   */
  @Nullable
  static Integer parseItunesDuration(@NonNull String dateString) {
	for (DateFormat format : itunesDurationFormats) {
	  try {
		final Date date = format.parse(dateString);
		return (int) (date.getTime() / 1000);
	  } catch (ParseException ignored) {
		// ignore exceptions: if date won't match any format, test if it is integer value in seconds
	  }
	}
	return tryParseInt(dateString);
  }

  @Nullable
  static Integer parseMediaRssTime(@NonNull String time) {
	// MRSS spec doesn't always clarify which time format is used.
	// In examples it looks quite like itunes duration.
	Integer result = parseItunesDuration(time);
	if (result == null) {
	  result = parseRFC2326NPT(time);
	} else {
	  // Itunes duration is in [s]
	  result *= 1000;
	}
	return result;
  }

  /**
   * Fast-forward parser to the end of current tag (last tag whose START_TAG we passed),
   * skipping all nested tags.
   */
  static void finishTag(@NonNull XmlPullParser parser) throws XmlPullParserException, IOException {
	while (parser.getEventType() != XmlPullParser.END_TAG) {
	  if (parser.getEventType() == XmlPullParser.START_TAG) {
		skipTag(parser);
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
	  throw new IllegalStateException("Unexpected parser event " + parser.getEventType());
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
		default: // ignore other tags
	  }
	}
  }

  @Nullable
  static Integer tryParseInt(@Nullable String string) {
	if (string == null) {
	  return null;
	} else {
	  try {
		return Integer.valueOf(string);
	  } catch (NumberFormatException exception) {
		Log.w(TAG, "Error parsing integer value '" + string + '\'', exception);
		return null;
	  }
	}
  }

  @NonNull
  static Integer nonNullInt(@Nullable String string) {
	if (string == null) {
	  Log.w(TAG, "Unexpectedly got null string. -1 returned", new NullPointerException());
	  return -1;
	}
	try {
	  return Integer.valueOf(string);
	} catch (NumberFormatException exception) {
	  Log.w(TAG, "Malformed integer string replaced with '-1'", exception);
	  return -1;
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
		Log.w(TAG, "Error parsing url value '" + string + '\'', exception);
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
	  } catch (MalformedURLException ignored) {
		throw new AssertionError("Should never get here");
	  }
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
		Log.w(TAG, "Error parsing uri value '" + string + '\'', exception);
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
	  } catch (URISyntaxException ignored) {
		throw new AssertionError("Should never get here");
	  }
	}
	return result;
  }
}

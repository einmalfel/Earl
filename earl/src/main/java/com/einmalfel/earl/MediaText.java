package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class MediaText {
  static final String XML_TAG = "text";

  @Nullable
  public final String type;
  @Nullable
  public final String lang;
  @Nullable
  public final Integer start;
  @Nullable
  public final Integer end;
  @NonNull
  public final String value;

  @NonNull
  static MediaText read(XmlPullParser parser) throws XmlPullParserException, IOException {
    parser.require(XmlPullParser.START_TAG, null, XML_TAG);
    String start = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "start");
    String end = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "end");
    return new MediaText(
        parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "type"),
        parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "lang"),
        start == null ? null : Utils.parseRFC2326NPT(start),
        end == null ? null : Utils.parseRFC2326NPT(end),
        parser.nextText());
  }

  public MediaText(@Nullable String type, @Nullable String lang, @Nullable Integer start,
                   @Nullable Integer end, @NonNull String value) {
    this.type = type;
    this.lang = lang;
    this.start = start;
    this.end = end;
    this.value = value;
  }
}

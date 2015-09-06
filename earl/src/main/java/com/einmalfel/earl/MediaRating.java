package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URI;

public class MediaRating {
  static final String XML_TAG = "rating";

  @Nullable
  public final URI scheme;
  @NonNull
  public final String value;

  @NonNull
  static MediaRating read(XmlPullParser parser) throws XmlPullParserException, IOException {
    parser.require(XmlPullParser.START_TAG, null, XML_TAG);
    String uri = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "uri");
    return new MediaRating(uri == null ? null : Utils.tryParseUri(uri), parser.nextText());
  }

  public MediaRating(@Nullable URI scheme, @NonNull String value) {
    this.scheme = scheme;
    this.value = value;
  }
}

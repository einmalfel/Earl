package com.einmalfel.earl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public final class MediaLocation {
  static final String XML_TAG = "location";

  @Nullable
  public final String description;
  @Nullable
  public final Integer start;
  @Nullable
  public final Integer end;

  @NonNull
  static MediaLocation read(@NonNull XmlPullParser parser) throws XmlPullParserException, IOException {
    parser.require(XmlPullParser.START_TAG, null, XML_TAG);
    final String start = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "start");
    final String end = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "end");
    final MediaLocation result = new MediaLocation(
      parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "description"),
      start == null ? null : Utils.parseMediaRssTime(start),
      end == null ? null : Utils.parseMediaRssTime(end));
    Utils.skipTag(parser); // TODO geoRSS
    return result;
  }

  public MediaLocation(@Nullable String description, @Nullable Integer start,
                       @Nullable Integer end) {
    this.description = description;
    this.start = start;
    this.end = end;
  }
}

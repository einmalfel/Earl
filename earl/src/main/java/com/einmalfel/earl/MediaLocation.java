package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class MediaLocation {
  static final String XML_TAG = "location";

  @Nullable
  public final String description;
  @Nullable
  public final Integer start;
  @Nullable
  public final Integer end;

  @NonNull
  static MediaLocation read(XmlPullParser parser) throws XmlPullParserException, IOException {
    parser.require(XmlPullParser.START_TAG, null, XML_TAG);
    String start = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "start");
    String end = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "end");
    MediaLocation result = new MediaLocation(
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

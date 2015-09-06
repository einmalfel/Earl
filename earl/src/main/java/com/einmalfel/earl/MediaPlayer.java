package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URL;

public class MediaPlayer {
  static final String XML_TAG = "player";

  @NonNull
  public final URL url;
  @Nullable
  public final Integer height;
  @Nullable
  public final Integer width;

  @NonNull
  static MediaPlayer read(XmlPullParser parser) throws XmlPullParserException, IOException {
    parser.require(XmlPullParser.START_TAG, null, XML_TAG);
    String width = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "width");
    String height = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "height");
    MediaPlayer result = new MediaPlayer(
        Utils.nonNullUrl(parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "url")),
        width == null ? null : Utils.tryParseInt(width),
        height == null ? null : Utils.tryParseInt(height));
    parser.nextTag();
    return result;
  }

  public MediaPlayer(@NonNull URL url, @Nullable Integer height, @Nullable Integer width) {
    this.url = url;
    this.height = height;
    this.width = width;
  }
}

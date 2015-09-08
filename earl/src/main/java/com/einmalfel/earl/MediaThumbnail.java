package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URL;

public class MediaThumbnail {
  static final String XML_TAG = "thumbnail";
  private static final String TAG = "Earl.MediaThumbnail";

  @NonNull
  public final URL url;
  @Nullable
  public final Integer width;
  @Nullable
  public final Integer height;
  /**
   * In milliseconds
   */
  @Nullable
  public final Integer time;

  @NonNull
  static MediaThumbnail read(XmlPullParser parser) throws XmlPullParserException, IOException {
    parser.require(XmlPullParser.START_TAG, null, XML_TAG);
    String width = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "width");
    String height = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "height");
    String time = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "time");
    MediaThumbnail result = new MediaThumbnail(
        Utils.nonNullUrl(parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "url")),
        width == null ? null : Utils.tryParseInt(width),
        height == null ? null : Utils.tryParseInt(height),
        time == null ? null : Utils.parseRFC2326NPT(time));
    parser.nextTag();
    return result;
  }

  public MediaThumbnail(@NonNull URL url, @Nullable Integer width, @Nullable Integer height,
                        @Nullable Integer time) {
    this.url = url;
    this.width = width;
    this.height = height;
    this.time = time;
  }
}

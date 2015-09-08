package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RSSImage {
  static final String XML_TAG = "image";
  private static final String TAG = "Earl.RSSImage";

  private enum ST {title, description, link, url, width, height}

  @NonNull
  public final String title;
  @Nullable
  public final String description;
  @NonNull
  public final URL link;
  @NonNull
  public final URL url;
  @Nullable
  public final Integer width;
  @Nullable
  public final Integer height;

  @NonNull
  static RSSImage read(@NonNull XmlPullParser parser) throws IOException, XmlPullParserException {
    parser.require(XmlPullParser.START_TAG, XmlPullParser.NO_NAMESPACE, XML_TAG);
    Map<ST, String> map = new HashMap<>();
    while (parser.nextTag() == XmlPullParser.START_TAG) {
      try {
        map.put(ST.valueOf(parser.getName()), parser.nextText());
      } catch (IllegalArgumentException ignored) {
        Log.w(TAG, "Unknown RSS image tag " + parser.getName());
        Utils.skipTag(parser);
      }
      Utils.finishTag(parser);
    }
    return new RSSImage(
        Utils.nonNullString(map.remove(ST.title)),
        map.remove(ST.description),
        Utils.nonNullUrl(map.remove(ST.link)),
        Utils.nonNullUrl(map.remove(ST.url)),
        map.containsKey(ST.width) ? Utils.tryParseInt(map.remove(ST.width)) : null,   //default
        map.containsKey(ST.height) ? Utils.tryParseInt(map.remove(ST.height)) : null);//values 88X31
  }

  public RSSImage(@NonNull String title, @Nullable String description, @NonNull URL link,
                  @NonNull URL url, @Nullable Integer width, @Nullable Integer height) {
    this.title = title;
    this.description = description;
    this.link = link;
    this.url = url;
    this.width = width;
    this.height = height;
  }
}

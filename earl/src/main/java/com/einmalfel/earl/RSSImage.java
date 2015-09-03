package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RSSImage {
  static final String XML_TAG = "image";
  private static final String TAG = "E.RSI";

  public final String title;
  public final String description;
  public final URL link;
  public final URL url;
  public final Integer width;
  public final Integer height;

  @NonNull
  static RSSImage read(@NonNull XmlPullParser parser) throws IOException, XmlPullParserException {
    parser.require(XmlPullParser.START_TAG, XmlPullParser.NO_NAMESPACE, XML_TAG);
    Map<String, String> map = new HashMap<>();
    while (parser.next() != XmlPullParser.END_TAG || !XML_TAG.equals(parser.getName())) {
      if (parser.getEventType() == XmlPullParser.START_TAG) {
        map.put(parser.getName(), parser.nextText());
      }
    }
    RSSImage result = new RSSImage(
        Utils.nonNullString(map.remove("title")),
        map.remove("description"),
        map.containsKey("link") ? new URL(map.remove("link")) : null,
        map.containsKey("url") ? new URL(map.remove("url")) : null,
        map.containsKey("width") ? Utils.tryParseInt(map.remove("width")) : null,   // default
        map.containsKey("height") ? Utils.tryParseInt(map.remove("height")) : null);// values 88X31

    for (String tag : map.keySet()) {
      Log.w(TAG, "Unknown image tag: " + tag);
    }

    return result;
  }

  public RSSImage(String title, String description, URL link, URL url, Integer width, Integer height) {
    this.title = title;
    this.description = description;
    this.link = link;
    this.url = url;
    this.width = width;
    this.height = height;
  }
}

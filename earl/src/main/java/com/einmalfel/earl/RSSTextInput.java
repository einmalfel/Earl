package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RSSTextInput {
  static final String XML_TAG = "textInput";
  private static final String TAG = "E.RTI";

  public final String title;
  public final String description;
  public final String name;
  public final URL link;

  @NonNull
  static RSSTextInput read(@NonNull XmlPullParser parser)
      throws IOException, XmlPullParserException {
    Map<String, String> map = new HashMap<>();
    while (parser.next() != XmlPullParser.END_TAG || !XML_TAG.equals(parser.getName())) {
      if (parser.getEventType() == XmlPullParser.START_TAG) {
        map.put(parser.getName(), parser.nextText());
      }
    }
    RSSTextInput result = new RSSTextInput(
        Utils.nonNullString(map.remove("title")),
        Utils.nonNullString(map.remove("description")),
        Utils.nonNullString(map.remove("name")),
        Utils.nonNullUrl(map.remove("link")));

    for (String tag : map.keySet()) {
      Log.w(TAG, "Unknown textInput tag: " + tag);
    }

    return result;
  }

  public RSSTextInput(String title, String description, String name, URL link) {
    this.title = title;
    this.description = description;
    this.name = name;
    this.link = link;
  }
}

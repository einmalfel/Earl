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
  private static final String TAG = "Earl.RSSTextInput";

  private enum ST {title, description, name, link}

  @NonNull
  public final String title;
  @NonNull
  public final String description;
  @NonNull
  public final String name;
  @NonNull
  public final URL link;

  @NonNull
  static RSSTextInput read(@NonNull XmlPullParser parser)
      throws IOException, XmlPullParserException {
    Map<ST, String> map = new HashMap<>();
    while (parser.nextTag() == XmlPullParser.START_TAG) {
      try {
        map.put(ST.valueOf(parser.getName()), parser.nextText());
      } catch (IllegalArgumentException ignored) {
        Log.w(TAG, "Unknown RSS TextInput tag " + parser.getName());
        Utils.skipTag(parser);
      }
      Utils.finishTag(parser);
    }
    return new RSSTextInput(
        Utils.nonNullString(map.remove(ST.title)),
        Utils.nonNullString(map.remove(ST.description)),
        Utils.nonNullString(map.remove(ST.name)),
        Utils.nonNullUrl(map.remove(ST.link)));
  }

  public RSSTextInput(@NonNull String title, @NonNull String description, @NonNull String name,
                      @NonNull URL link) {
    this.title = title;
    this.description = description;
    this.name = name;
    this.link = link;
  }
}

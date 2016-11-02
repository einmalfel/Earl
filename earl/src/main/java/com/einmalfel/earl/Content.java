package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Content {
  private static final String TAG = "Earl.Content";

  private enum ST {encoded}

  static class ContentBuilder {
    final Map<ST, String> map = new HashMap<>();

    void parseTag(@NonNull XmlPullParser parser) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      switch (tagName) {
        default:
          try {
            map.put(ST.valueOf(tagName), parser.nextText());
          } catch (IllegalArgumentException ignored) {
            Log.w(TAG, "Unknown Content feed tag '" + tagName + "', skipping..");
            Utils.skipTag(parser);
          }
      }
    }

    @NonNull
    Content build() {
      return new Content(
          map.remove(ST.encoded));
    }
  }

  @Nullable
  public final String encoded;

  public Content(@Nullable String encoded) {
    this.encoded = encoded;
  }
}

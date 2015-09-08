package com.einmalfel.earl;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ItunesItem {
  private static final String TAG = "Earl.ItunesItem";

  private enum ST {author, block, duration, explicit, isClosedCaptioned, order, subtitle, summary}

  static class ItunesItemBuilder {
    final Map<ST, String> map = new HashMap<>();
    URL image;
    List<String> keywords;

    void parseTag(@NonNull XmlPullParser parser) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      switch (tagName) {
        case "image":
          String imageStr = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "href");
          image = imageStr == null ? null : Utils.tryParseUrl(imageStr);
          parser.nextText();
          break;
        case "keywords":
          keywords = Arrays.asList(parser.nextText().split(" "));
          break;
        default:
          try {
            map.put(ST.valueOf(tagName), parser.nextText());
          } catch (IllegalArgumentException ignored) {
            Log.w(TAG, "Unknown itunes item tag " + tagName);
            Utils.skipTag(parser);
          }
      }
    }

    @NonNull
    ItunesItem build() {
      return new ItunesItem(
          map.remove(ST.author),
          map.containsKey(ST.block) ? "yes".equals(map.remove(ST.block)) : null,
          image,
          map.containsKey(ST.duration) ? Utils.parseItunesDuration(map.remove(ST.duration)) : null,
          map.remove(ST.explicit),
          map.containsKey(ST.isClosedCaptioned) ? " yes".equals(
              map.remove(ST.isClosedCaptioned)) : null,
          map.containsKey(ST.order) ? Utils.tryParseInt(map.remove(ST.order)) : null,
          map.remove(ST.subtitle),
          map.remove(ST.summary),
          keywords == null ? new LinkedList<String>() : keywords);
    }
  }

  @Nullable
  public final String author;
  @Nullable
  public final Boolean block;
  @Nullable
  public final URL image;
  @Nullable
  public final Integer duration;
  @Nullable
  public final String explicit;
  @Nullable
  public final Boolean isClosedCaptioned;
  @Nullable
  public final Integer order;
  @Nullable
  public final String subtitle;
  @Nullable
  public final String summary;
  /**
   * Allows users to search on text keywords
   * This one is now deprecated by Apple
   */
  @NonNull
  public final List<String> keywords;

  public ItunesItem(@Nullable String author, @Nullable Boolean block, @Nullable URL image,
                    @Nullable Integer duration, @Nullable String explicit,
                    @Nullable Boolean isClosedCaptioned, @Nullable Integer order,
                    @Nullable String subtitle, @Nullable String summary,
                    @NonNull List<String> keywords) {
    this.author = author;
    this.block = block;
    this.image = image;
    this.duration = duration;
    this.explicit = explicit;
    this.isClosedCaptioned = isClosedCaptioned;
    this.order = order;
    this.subtitle = subtitle;
    this.summary = summary;
    this.keywords = Collections.unmodifiableList(keywords);
  }
}

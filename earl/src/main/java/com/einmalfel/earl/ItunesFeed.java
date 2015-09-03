package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ItunesFeed {
  private static final String TAG = "E.ITF";

  static class ItunesFeedBuilder {
    final Map<String, String> map = new HashMap<>();
    final List<ItunesCategory> categories = new LinkedList<>();
    ItunesOwner owner;
    String image;

    void parseTag(@NonNull XmlPullParser parser) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      switch (tagName) {
        case ItunesCategory.XML_TAG:
          categories.add(ItunesCategory.read(parser));
          break;
        case ItunesOwner.XML_TAG:
          owner = ItunesOwner.read(parser);
          break;
        case "image":
          image = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "href");
          parser.nextToken();
          break;
        default:
          map.put(tagName, parser.nextText());
      }
    }

    @NonNull
    ItunesFeed build() throws MalformedURLException {
      ItunesFeed result = new ItunesFeed(
          map.remove("author"),
          map.containsKey("block") ? ("yes".equals(map.remove("block"))) : null,
          categories,
          image == null ? null : new URL(image),
          map.remove("explicit"),
          map.containsKey("complete") ? ("yes".equals(map.remove("block"))) : null,
          map.remove("new-feed-url"),
          owner,
          map.remove("subtitle"),
          map.remove("summary"));

      for (String tag : map.keySet()) {
        Log.w(TAG, "Unknown itunes feed tag: " + tag);
      }

      return result;
    }
  }

  @Nullable
  public final String author;
  @Nullable
  public final Boolean block;
  @NonNull
  public final List<ItunesCategory> categories;
  @Nullable
  public final URL image;
  @Nullable
  public final String explicit;
  @Nullable
  public final Boolean complete;
  @Nullable
  public final String newFeedURL;
  @Nullable
  public final ItunesOwner owner;
  @Nullable
  public final String subtitle;
  @Nullable
  public final String summary;

  public ItunesFeed(@Nullable String author, @Nullable Boolean block,
                    @NonNull List<ItunesCategory> categories, @Nullable URL image,
                    @Nullable String explicit, @Nullable Boolean complete,
                    @Nullable String newFeedURL, @Nullable ItunesOwner owner,
                    @Nullable String subtitle, @Nullable String summary) {
    this.author = author;
    this.block = block;
    this.categories = Collections.unmodifiableList(categories);
    this.image = image;
    this.explicit = explicit;
    this.complete = complete;
    this.newFeedURL = newFeedURL;
    this.owner = owner;
    this.subtitle = subtitle;
    this.summary = summary;
  }
}

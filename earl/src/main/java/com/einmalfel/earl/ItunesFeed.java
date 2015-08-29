package com.einmalfel.earl;

import android.support.annotation.NonNull;
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

  public final String author;
  public final Boolean block;
  public final List<ItunesCategory> categories;
  public final URL image;
  public final String explicit;
  public final Boolean complete;
  public final String newFeedURL;
  public final ItunesOwner owner;
  public final String subtitle;
  public final String summary;

  public ItunesFeed(String author, Boolean block, @NonNull List<ItunesCategory> categories, URL image, String explicit, Boolean complete, String newFeedURL, ItunesOwner owner, String subtitle, String summary) {
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

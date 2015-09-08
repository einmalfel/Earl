package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ItunesFeed {
  private static final String TAG = "Earl.ItunesFeed";

  private enum ST {author, block, explicit, complete, subtitle, summary}

  static class ItunesFeedBuilder {
    final Map<ST, String> map = new HashMap<>();
    final List<ItunesCategory> categories = new LinkedList<>();
    ItunesOwner owner;
    URL image;
    URL newFeedURL;

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
          image = Utils.tryParseUrl(parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "href"));
          parser.nextToken();
          break;
        case "new-feed-url":
          newFeedURL = Utils.tryParseUrl(parser.nextText());
          break;
        default:
          try {
            map.put(ST.valueOf(tagName), parser.nextText());
          } catch (IllegalArgumentException ignored) {
            Log.w(TAG, "Unknown Itunes feed tag " + tagName + " skipping..");
            Utils.skipTag(parser);
          }
      }
    }

    @NonNull
    ItunesFeed build() {
      return new ItunesFeed(
          map.remove(ST.author),
          map.containsKey(ST.block) ? ("yes".equals(map.remove(ST.block))) : null,
          categories,
          image,
          map.remove(ST.explicit),
          map.containsKey(ST.complete) ? ("yes".equals(map.remove(ST.complete))) : null,
          newFeedURL,
          owner,
          map.remove(ST.subtitle),
          map.remove(ST.summary));
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
  public final URL newFeedURL;
  @Nullable
  public final ItunesOwner owner;
  @Nullable
  public final String subtitle;
  @Nullable
  public final String summary;

  public ItunesFeed(@Nullable String author, @Nullable Boolean block,
                    @NonNull List<ItunesCategory> categories, @Nullable URL image,
                    @Nullable String explicit, @Nullable Boolean complete,
                    @Nullable URL newFeedURL, @Nullable ItunesOwner owner,
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

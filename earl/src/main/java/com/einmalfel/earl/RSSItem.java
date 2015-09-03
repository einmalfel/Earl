package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RSSItem implements Item {
  static final String XML_TAG = "item";
  private static final String TAG = "E.RIT";

  public final String title;
  public final URL link;
  public final String description;
  public final String author;
  public final List<RSSCategory> categories;
  public final URL comments;
  public final List<RSSEnclosure> enclosures;
  public final RSSGuid guid;
  public final Date pubDate;
  public final RSSSource source;
  public final ItunesItem itunes;

  @NonNull
  static RSSItem read(@NonNull XmlPullParser parser) throws IOException, XmlPullParserException {
    parser.require(XmlPullParser.START_TAG, XmlPullParser.NO_NAMESPACE, XML_TAG);
    Map<String, String> map = new HashMap<>(5);
    List<RSSEnclosure> enclosures = new LinkedList<>();
    List<RSSCategory> categories = new LinkedList<>();
    RSSGuid guid = null;
    RSSSource source = null;
    ItunesItem.ItunesItemBuilder itunesBuilder = null;
    while (parser.nextTag() == XmlPullParser.START_TAG) {
      switch (parser.getNamespace()) {
        case XmlPullParser.NO_NAMESPACE:
          String tagName = parser.getName();
          switch (tagName) {
            case RSSEnclosure.XML_TAG:
              enclosures.add(RSSEnclosure.read(parser));
              break;
            case RSSCategory.XML_TAG:
              categories.add(RSSCategory.read(parser));
              break;
            case RSSSource.XML_TAG:
              source = RSSSource.read(parser);
              break;
            case RSSGuid.XML_TAG:
              guid = RSSGuid.read(parser);
              break;
            default:
              map.put(tagName, parser.nextText());
          }
          break;
        case Utils.ITUNES_NAMESPACE:
          if (itunesBuilder == null) {
            itunesBuilder = new ItunesItem.ItunesItemBuilder();
          }
          itunesBuilder.parseTag(parser);
          break;
        default:
          Log.w(TAG, "Unknown namespace in RSS item " + parser.getNamespace());
          Utils.skipTag(parser);
      }
    }
    RSSItem result = new RSSItem(
        map.remove("title"),
        map.containsKey("link") ? Utils.tryParseUrl(map.remove("link")) : null,
        map.remove("description"),
        map.remove("author"),
        categories,
        map.containsKey("comments") ? Utils.tryParseUrl(map.remove("comments")) : null,
        enclosures,
        guid,
        map.containsKey("pubDate") ? Utils.parseRFC822Date(map.remove("pubDate")) : null,
        source,
        itunesBuilder == null ? null : itunesBuilder.build());

    for (String tag : map.keySet()) {
      Log.w(TAG, "Unknown RSS item tag: " + tag);
    }

    return result;
  }

  public RSSItem(String title, URL link, String description, String author, @NonNull List<RSSCategory> categories, URL comments, @NonNull List<RSSEnclosure> enclosures, RSSGuid guid, Date pubDate, RSSSource source, ItunesItem itunes) {
    this.title = title;
    this.link = link;
    this.description = description;
    this.author = author;
    this.categories = Collections.unmodifiableList(categories);
    this.comments = comments;
    this.enclosures = Collections.unmodifiableList(enclosures);
    this.guid = guid;
    this.pubDate = pubDate;
    this.source = source;
    this.itunes = itunes;
  }

  @Nullable
  @Override
  public String getLink() {
    return link == null ? null : link.toString();
  }

  @Nullable
  @Override
  public Date getPublicationDate() {
    return pubDate;
  }

  @Nullable
  @Override
  public String getTitle() {
    return title;
  }

  @Nullable
  @Override
  public String getDescription() {
    return description;
  }

  @Nullable
  @Override
  public String getImageLink() {
    return itunes == null ? null : itunes.image.toString();
  }

  @Nullable
  @Override
  public String getAuthor() {
    return author;
  }

  @NonNull
  @Override
  public List<? extends Enclosure> getEnclosures() {
    return enclosures;
  }
}

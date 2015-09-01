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

public class RSSFeed implements Feed {
  static final String XML_TAG = "channel";

  private static final String TAG = "E.RSF";

  public final String title;
  public final URL link;
  public final String description;
  public final String language;
  public final String copyright;
  public final String managingEditor;
  public final String webMaster;
  public final Date pubDate;
  public final Date lastBuildDate;
  public final List<RSSCategory> categories;
  public final String generator;
  public final URL docs;
  public final RSSCloud cloud;
  public final Integer ttl;
  public final String rating;
  public final RSSImage image;
  public final RSSTextInput textInput;
  public final List<Integer> skipHours;
  public final List<String> skipDays;
  public final List<RSSItem> items;

  public final ItunesFeed itunes;

  @NonNull
  static RSSFeed read(@NonNull XmlPullParser parser, int maxItems)
      throws IOException, XmlPullParserException {
    parser.require(XmlPullParser.START_TAG, XmlPullParser.NO_NAMESPACE, XML_TAG);

    Map<String, String> map = new HashMap<>(5);
    List<RSSItem> items = new LinkedList<>();
    List<RSSCategory> categories = new LinkedList<>();
    RSSCloud cloud = null;
    RSSImage image = null;
    RSSTextInput textInput = null;
    List<Integer> skipHours = new LinkedList<>();
    List<String> skipDays = new LinkedList<>();
    ItunesFeed.ItunesFeedBuilder itunesBuilder = null;

    while (parser.nextTag() == XmlPullParser.START_TAG && (maxItems < 1 || items.size() < maxItems)) {
      switch (parser.getNamespace()) {
        //RSS tags
        case XmlPullParser.NO_NAMESPACE:
          String tagName = parser.getName();
          switch (tagName) {
            case RSSItem.XML_TAG:
              items.add(RSSItem.read(parser));
              break;
            case RSSCategory.XML_TAG:
              categories.add(RSSCategory.read(parser));
              break;
            case RSSCloud.XML_TAG:
              cloud = RSSCloud.read(parser);
              break;
            case RSSImage.XML_TAG:
              image = RSSImage.read(parser);
              break;
            case RSSTextInput.XML_TAG:
              textInput = RSSTextInput.read(parser);
              break;
            case "skipHours":
              while (parser.nextTag() == XmlPullParser.START_TAG && "hour".equals(parser.getName())) {
                skipHours.add(Integer.valueOf(parser.nextText()));
              }
              break;
            case "skipDays":
              while (parser.nextTag() == XmlPullParser.START_TAG && "day".equals(parser.getName())) {
                skipDays.add(parser.nextText());
              }
              break;
            default:
              map.put(tagName, parser.nextText());
              break;
          }
          break;
        //Itunes tags
        case Utils.ITUNES_NAMESPACE:
          if (itunesBuilder == null) {
            itunesBuilder = new ItunesFeed.ItunesFeedBuilder();
          }
          itunesBuilder.parseTag(parser);
          break;
        //Unknown tags
        default:
          Log.w(TAG, "Unknown RSS feed extension " + parser.getNamespace());
          Utils.skipTag(parser);
      }
    }

    RSSFeed result = new RSSFeed(
        map.remove("title"),
        map.containsKey("link") ? new URL(map.remove("link")) : null,
        map.remove("description"),
        map.remove("language"),
        map.remove("copyright"),
        map.remove("managingEditor"),
        map.remove("webMaster"),
        map.containsKey("pubDate") ? Utils.parseRFC822Date(map.remove("pubDate")) : null,
        map.containsKey("lastBuildDate") ? Utils.parseRFC822Date(map.remove("lastBuildDate")) : null,
        categories,
        map.remove("generator"),
        map.containsKey("docs") ? new URL(map.remove("docs")) : null,
        cloud,
        map.containsKey("ttl") ? Integer.valueOf(map.remove("ttl")) : null,
        map.remove("rating"),
        image,
        textInput,
        skipHours,
        skipDays,
        items,
        itunesBuilder == null ? null : itunesBuilder.build());
    for (String key : map.keySet()) {
      Log.w(TAG, "Unknown RSS tag: " + key);
    }
    return result;
  }

  public RSSFeed(String title, URL link, String description, String language, String copyright, String managingEditor, String webMaster, Date pubDate, Date lastBuildDate, @NonNull List<RSSCategory> categories, String generator, URL docs, RSSCloud cloud, Integer ttl, String rating, RSSImage image, RSSTextInput textInput, @NonNull List<Integer> skipHours, @NonNull List<String> skipDays, @NonNull List<RSSItem> items, ItunesFeed itunes) {
    this.title = title;
    this.link = link;
    this.description = description;
    this.language = language;
    this.copyright = copyright;
    this.managingEditor = managingEditor;
    this.webMaster = webMaster;
    this.pubDate = pubDate;
    this.lastBuildDate = lastBuildDate;
    this.categories = Collections.unmodifiableList(categories);
    this.generator = generator;
    this.docs = docs;
    this.cloud = cloud;
    this.ttl = ttl;
    this.rating = rating;
    this.image = image;
    this.textInput = textInput;
    this.skipHours = Collections.unmodifiableList(skipHours);
    this.skipDays = Collections.unmodifiableList(skipDays);
    this.itunes = itunes;
    this.items = Collections.unmodifiableList(items);
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
  public String getCopyright() {
    return copyright;
  }

  @Nullable
  @Override
  public String getImageLink() {
    return (image == null || image.url == null) ? null : image.url.toString();
  }

  @Nullable
  @Override
  public String getAuthor() {
    return managingEditor;
  }

  @NonNull
  @Override
  public List<? extends Item> getItems() {
    return items;
  }
}

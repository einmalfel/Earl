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

  @NonNull
  public final String title;
  @NonNull
  public final URL link;
  @NonNull
  public final String description;
  @Nullable
  public final String language;
  @Nullable
  public final String copyright;
  @Nullable
  public final String managingEditor;
  @Nullable
  public final String webMaster;
  @Nullable
  public final Date pubDate;
  @Nullable
  public final Date lastBuildDate;
  @NonNull
  public final List<RSSCategory> categories;
  @Nullable
  public final String generator;
  @Nullable
  public final URL docs;
  @Nullable
  public final RSSCloud cloud;
  @Nullable
  public final Integer ttl;
  @Nullable
  public final String rating;
  @Nullable
  public final RSSImage image;
  @Nullable
  public final RSSTextInput textInput;
  @NonNull
  public final List<Integer> skipHours;
  @NonNull
  public final List<String> skipDays;
  @NonNull
  public final List<RSSItem> items;
  @Nullable
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
                skipHours.add(Utils.tryParseInt(parser.nextText()));
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
        Utils.nonNullString(map.remove("title")),
        Utils.nonNullUrl(map.remove("link")),
        Utils.nonNullString(map.remove("description")),
        map.remove("language"),
        map.remove("copyright"),
        map.remove("managingEditor"),
        map.remove("webMaster"),
        map.containsKey("pubDate") ? Utils.parseRFC822Date(map.remove("pubDate")) : null,
        map.containsKey("lastBuildDate") ? Utils.parseRFC822Date(map.remove("lastBuildDate")) : null,
        categories,
        map.remove("generator"),
        map.containsKey("docs") ? Utils.tryParseUrl(map.remove("docs")) : null,
        cloud,
        map.containsKey("ttl") ? Utils.tryParseInt(map.remove("ttl")) : null,
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

  public RSSFeed(@NonNull String title, @NonNull URL link, @NonNull String description,
                 @Nullable String language, @Nullable String copyright,
                 @Nullable String managingEditor, @Nullable String webMaster,
                 @Nullable Date pubDate, @Nullable Date lastBuildDate,
                 @NonNull List<RSSCategory> categories, @Nullable String generator,
                 @Nullable URL docs, @Nullable RSSCloud cloud,
                 @Nullable Integer ttl, @Nullable String rating, @Nullable RSSImage image,
                 @Nullable RSSTextInput textInput,
                 @NonNull List<Integer> skipHours, @NonNull List<String> skipDays,
                 @NonNull List<RSSItem> items, @Nullable ItunesFeed itunes) {
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
    return link.toString();
  }

  @Nullable
  @Override
  public Date getPublicationDate() {
    return pubDate;
  }

  @NonNull
  @Override
  public String getTitle() {
    return title;
  }

  @NonNull
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
    return (image == null) ? null : image.url.toString();
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

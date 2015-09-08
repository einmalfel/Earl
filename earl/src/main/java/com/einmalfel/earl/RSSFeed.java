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

  private static final String TAG = "Earl.RSSFeed";

  private enum ST {
    title, link, description, language, copyright, managingEditor, webMaster, pubDate,
    lastBuildDate, generator, docs, ttl, rating
  }

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
  @Nullable
  public final MediaCommon media;

  @NonNull
  static RSSFeed read(@NonNull XmlPullParser parser, int maxItems)
      throws IOException, XmlPullParserException {
    parser.require(XmlPullParser.START_TAG, XmlPullParser.NO_NAMESPACE, XML_TAG);

    Map<ST, String> map = new HashMap<>(5);
    List<RSSItem> items = new LinkedList<>();
    List<RSSCategory> categories = new LinkedList<>();
    RSSCloud cloud = null;
    RSSImage image = null;
    RSSTextInput textInput = null;
    List<Integer> skipHours = new LinkedList<>();
    List<String> skipDays = new LinkedList<>();
    ItunesFeed.ItunesFeedBuilder itunesBuilder = null;
    MediaCommon.MediaCommonBuilder mediaBuilder = null;

    while (parser.nextTag() == XmlPullParser.START_TAG && (maxItems < 1 || items
        .size() < maxItems)) {
      String namespace = parser.getNamespace();
      if (XmlPullParser.NO_NAMESPACE.equalsIgnoreCase(namespace)) {
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
            while (parser.nextTag() == XmlPullParser.START_TAG && "hour".equals(
                parser.getName())) {
              skipHours.add(Utils.tryParseInt(parser.nextText()));
              Utils.finishTag(parser);
            }
            break;
          case "skipDays":
            while (parser.nextTag() == XmlPullParser.START_TAG && "day".equals(
                parser.getName())) {
              skipDays.add(parser.nextText());
              Utils.finishTag(parser);
            }
            break;
          default:
            try {
              map.put(ST.valueOf(tagName), parser.nextText());
            } catch (IllegalArgumentException ignored) {
              Log.w(TAG, "Unknown RSS feed tag " + tagName);
              Utils.skipTag(parser);
            }
        }
      } else if (Utils.ITUNES_NAMESPACE.equalsIgnoreCase(namespace)) {
        if (itunesBuilder == null) {
          itunesBuilder = new ItunesFeed.ItunesFeedBuilder();
        }
        itunesBuilder.parseTag(parser);
      } else if (Utils.MEDIA_NAMESPACE.equalsIgnoreCase(namespace)) {
        if (mediaBuilder == null) {
          mediaBuilder = new MediaCommon.MediaCommonBuilder();
        }
        if (!mediaBuilder.parseTag(parser)) {
          Log.w(TAG, "Unknown mrss tag on feed level");
          Utils.skipTag(parser);
        }
      } else {
        Log.w(TAG, "Unknown RSS feed extension " + parser.getNamespace());
        Utils.skipTag(parser);
      }
      Utils.finishTag(parser);
    }

    return new RSSFeed(
        Utils.nonNullString(map.remove(ST.title)),
        Utils.nonNullUrl(map.remove(ST.link)),
        Utils.nonNullString(map.remove(ST.description)),
        map.remove(ST.language),
        map.remove(ST.copyright),
        map.remove(ST.managingEditor),
        map.remove(ST.webMaster),
        map.containsKey(ST.pubDate) ? Utils.parseRFC822Date(map.remove(ST.pubDate)) : null,
        map.containsKey(ST.lastBuildDate) ? Utils
            .parseRFC822Date(map.remove(ST.lastBuildDate)) : null,
        categories,
        map.remove(ST.generator),
        map.containsKey(ST.docs) ? Utils.tryParseUrl(map.remove(ST.docs)) : null,
        cloud,
        map.containsKey(ST.ttl) ? Utils.tryParseInt(map.remove(ST.ttl)) : null,
        map.remove(ST.rating),
        image,
        textInput,
        skipHours,
        skipDays,
        items,
        itunesBuilder == null ? null : itunesBuilder.build(),
        mediaBuilder == null ? null : mediaBuilder.build());
  }

  public RSSFeed(@NonNull String title, @NonNull URL link, @NonNull String description,
                 @Nullable String language, @Nullable String copyright,
                 @Nullable String managingEditor, @Nullable String webMaster,
                 @Nullable Date pubDate, @Nullable Date lastBuildDate,
                 @NonNull List<RSSCategory> categories, @Nullable String generator,
                 @Nullable URL docs, @Nullable RSSCloud cloud,
                 @Nullable Integer ttl, @Nullable String rating, @Nullable RSSImage image,
                 @Nullable RSSTextInput textInput, @NonNull List<Integer> skipHours,
                 @NonNull List<String> skipDays, @NonNull List<RSSItem> items,
                 @Nullable ItunesFeed itunes, @Nullable MediaCommon media) {
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
    this.items = Collections.unmodifiableList(items);
    this.itunes = itunes;
    this.media = media;
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
    if (copyright != null) {
      return copyright;
    }
    if (media != null && media.license != null) {
      return media.license.value;
    }
    return null;
  }

  @Nullable
  @Override
  public String getImageLink() {
    if (image != null) {
      return image.url.toString();
    }
    if (itunes != null && itunes.image != null) {
      return itunes.image.toString();
    }
    if (media != null && !media.thumbnails.isEmpty()) {
      return media.thumbnails.get(0).url.toString();
    }
    return null;
  }

  @Nullable
  @Override
  public String getAuthor() {
    if (managingEditor != null) {
      return managingEditor;
    }
    if (itunes != null && itunes.author != null) {
      return itunes.author;
    }
    if (itunes != null && itunes.owner != null) {
      return itunes.owner.name;
    }
    if (media != null && !media.credits.isEmpty()) {
      for (MediaCredit credit : media.credits)
        if ("author".equalsIgnoreCase(credit.role)) {
          return credit.value;
        }
      return media.credits.get(0).value;
    }
    return null;
  }

  @NonNull
  @Override
  public List<? extends Item> getItems() {
    return items;
  }
}

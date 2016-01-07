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
  private static final String TAG = "Earl.RSSItem";

  private enum ST {title, link, description, author, comments, pubDate}

  @Nullable
  public final String title;
  @Nullable
  public final URL link;
  @Nullable
  public final String description;
  @Nullable
  public final String author;
  @NonNull
  public final List<RSSCategory> categories;
  @Nullable
  public final URL comments;
  @NonNull
  public final List<RSSEnclosure> enclosures;
  @Nullable
  public final RSSGuid guid;
  @Nullable
  public final Date pubDate;
  @Nullable
  public final RSSSource source;
  @Nullable
  public final ItunesItem itunes;
  @Nullable
  public final MediaItem media;

  @NonNull
  static RSSItem read(@NonNull XmlPullParser parser) throws IOException, XmlPullParserException {
    parser.require(XmlPullParser.START_TAG, XmlPullParser.NO_NAMESPACE, XML_TAG);
    Map<ST, String> map = new HashMap<>(5);
    List<RSSEnclosure> enclosures = new LinkedList<>();
    List<RSSCategory> categories = new LinkedList<>();
    RSSGuid guid = null;
    RSSSource source = null;
    ItunesItem.ItunesItemBuilder itunesBuilder = null;
    MediaItem.MediaItemBuilder mediaBuilder = null;
    while (parser.nextTag() == XmlPullParser.START_TAG) {
      String namespace = parser.getNamespace();
      if (XmlPullParser.NO_NAMESPACE.equals(namespace)) {
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
            try {
              map.put(ST.valueOf(tagName), parser.nextText());
            } catch (IllegalArgumentException ignored) {
              Log.w(TAG, "Unknown RSS item tag " + tagName);
              Utils.skipTag(parser);
            }
        }
      } else if (Utils.ITUNES_NAMESPACE.equalsIgnoreCase(namespace)) {
        if (itunesBuilder == null) {
          itunesBuilder = new ItunesItem.ItunesItemBuilder();
        }
        itunesBuilder.parseTag(parser);
      } else if (Utils.MEDIA_NAMESPACE.equalsIgnoreCase(namespace)) {
        if (mediaBuilder == null) {
          mediaBuilder = new MediaItem.MediaItemBuilder();
        }
        if (!mediaBuilder.parseTag(parser)) {
          Log.w(TAG, "Unknown mrss tag on item level");
          Utils.skipTag(parser);
        }
      } else {
        Log.w(TAG, "Unknown namespace in RSS item " + parser.getNamespace());
        Utils.skipTag(parser);
      }
      Utils.finishTag(parser);
    }

    return new RSSItem(
        map.remove(ST.title),
        map.containsKey(ST.link) ? Utils.tryParseUrl(map.remove(ST.link)) : null,
        map.remove(ST.description),
        map.remove(ST.author),
        categories,
        map.containsKey(ST.comments) ? Utils.tryParseUrl(map.remove(ST.comments)) : null,
        enclosures,
        guid,
        map.containsKey(ST.pubDate) ? Utils.parseRFC822Date(map.remove(ST.pubDate)) : null,
        source,
        itunesBuilder == null ? null : itunesBuilder.build(),
        mediaBuilder == null ? null : mediaBuilder.build());
  }

  public RSSItem(@Nullable String title, @Nullable URL link, @Nullable String description,
                 @Nullable String author, @NonNull List<RSSCategory> categories,
                 @Nullable URL comments, @NonNull List<RSSEnclosure> enclosures,
                 @Nullable RSSGuid guid, @Nullable Date pubDate, @Nullable RSSSource source,
                 @Nullable ItunesItem itunes, @Nullable MediaItem media) {
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
    this.media = media;
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
    if (title != null) {
      return title;
    }
    if (media != null && media.title != null) {
      return media.title.value;
    }
    if (itunes != null && itunes.subtitle != null) {
      return itunes.subtitle;
    }
    return null;
  }

  @Nullable
  @Override
  public String getDescription() {
    if (description != null) {
      return description;
    }
    if (itunes != null && itunes.subtitle != null) {
      return itunes.subtitle;
    }
    if (itunes != null && itunes.summary != null) {
      return itunes.summary;
    }
    if (media != null && media.description != null) {
      return media.description.value;
    }
    return null;
  }

  @Nullable
  @Override
  public String getImageLink() {
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
    if (author != null) {
      return author;
    }
    if (itunes != null && itunes.author != null) {
      return itunes.author;
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
  public List<? extends Enclosure> getEnclosures() {
    return enclosures;
  }

  @Nullable
  @Override
  public String getId() {
    return guid == null ? null : guid.value;
  }
}

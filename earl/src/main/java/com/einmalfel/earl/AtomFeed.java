package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class AtomFeed extends AtomCommonAttributes implements Feed {
  static final String XML_TAG = "feed";
  private static final String TAG = "E.AFD";

  @NonNull
  public final URI id;
  @NonNull
  public final AtomText title;
  @NonNull
  public final AtomDate updated;
  @NonNull
  public final List<AtomPerson> authors;
  @NonNull
  public final List<AtomLink> links;
  @NonNull
  public final List<AtomCategory> categories;
  @NonNull
  public final List<AtomPerson> contributors;
  @Nullable
  public final AtomGenerator generator;
  @Nullable
  public final URI icon;
  @Nullable
  public final URI logo;
  @Nullable
  public final AtomText rights;
  @Nullable
  public final AtomText subtitle;
  @NonNull
  public final List<AtomEntry> entries;

  @NonNull
  static AtomFeed read(XmlPullParser parser, int maxItm) throws IOException,
      XmlPullParserException {
    parser.require(XmlPullParser.START_TAG, null, XML_TAG);

    List<AtomEntry> entries = new LinkedList<>();
    List<AtomPerson> contributors = new LinkedList<>();
    List<AtomPerson> authors = new LinkedList<>();
    List<AtomLink> links = new LinkedList<>();
    List<AtomCategory> categories = new LinkedList<>();
    AtomText title = null;
    AtomGenerator generator = null;
    AtomText rights = null;
    AtomText subtitle = null;
    String id = null;
    URI icon = null;
    URI logo = null;
    AtomDate updated = null;

    AtomCommonAttributes atomCommonAttributes = new AtomCommonAttributes(parser);
    while (parser.nextTag() == XmlPullParser.START_TAG && (maxItm < 1 || entries.size() < maxItm)) {
      if (Utils.ATOM_NAMESPACE.equalsIgnoreCase(parser.getNamespace())) {
        String tagName = parser.getName();
        switch (tagName) {
          case AtomEntry.XML_TAG:
            entries.add(AtomEntry.read(parser));
            break;
          case "contributor":
            contributors.add(AtomPerson.read(parser));
            break;
          case "author":
            authors.add(AtomPerson.read(parser));
            break;
          case AtomLink.XML_TAG:
            links.add(AtomLink.read(parser));
            break;
          case AtomCategory.XML_TAG:
            categories.add(AtomCategory.read(parser));
            break;
          case AtomGenerator.XML_TAG:
            generator = AtomGenerator.read(parser);
            break;
          case "title":
            title = AtomText.read(parser);
            break;
          case "rights":
            rights = AtomText.read(parser);
            break;
          case "subtitle":
            subtitle = AtomText.read(parser);
            break;
          case "id":
            id = parser.nextText();
            break;
          case "icon":
            icon = Utils.tryParseUri(parser.nextText());
            break;
          case "logo":
            logo = Utils.tryParseUri(parser.nextText());
            break;
          case "updated":
            updated = AtomDate.read(parser);
            break;
          default:
            Log.w(TAG, "Unknown Atom feed tag " + parser.getName());
            Utils.skipTag(parser);
        }
      } else {
        Log.w(TAG, "Unknown Atom feed extension " + parser.getNamespace());
        Utils.skipTag(parser);
      }
    }

    if (title == null) {
      Log.w(TAG, "Missing title tag in atom feed, replacing with empty string");
      title = new AtomText(null, null, "");
    }
    if (updated == null) {
      Log.w(TAG, "Missing title tag in atom feed, replacing with empty string");
      updated = new AtomDate(null, new Date(0));
    }
    return new AtomFeed(
        atomCommonAttributes, Utils.nonNullUri(id), title, updated, authors, contributors,
        generator, icon, logo, rights, subtitle, links, categories, entries);
  }

  public AtomFeed(@Nullable AtomCommonAttributes atomCommonAttributes, @NonNull URI id,
                  @NonNull AtomText title, @NonNull AtomDate updated,
                  @NonNull List<AtomPerson> authors,
                  @NonNull List<AtomPerson> contributors, @Nullable AtomGenerator generator,
                  @Nullable URI icon, @Nullable URI logo, @Nullable AtomText rights,
                  @Nullable AtomText subtitle, @NonNull List<AtomLink> links,
                  @NonNull List<AtomCategory> categories, @NonNull List<AtomEntry> entries) {
    super(atomCommonAttributes);
    this.id = id;
    this.title = title;
    this.updated = updated;
    this.authors = Collections.unmodifiableList(authors);
    this.contributors = Collections.unmodifiableList(contributors);
    this.generator = generator;
    this.icon = icon;
    this.logo = logo;
    this.rights = rights;
    this.subtitle = subtitle;
    this.links = Collections.unmodifiableList(links);
    this.categories = Collections.unmodifiableList(categories);
    this.entries = Collections.unmodifiableList(entries);
  }

  @Nullable
  @Override
  public String getLink() {
    if (links.isEmpty()) {
      return null;
    }
    for (AtomLink link : links)
      if (link.type != null && "alternate".equals(link.type)) {
        return link.href.toString();
      }
    for (AtomLink link : links)
      if (link.type != null && "via".equals(link.type)) {
        return link.href.toString();
      }
    for (AtomLink link : links)
      if (link.type != null && "related".equals(link.type)) {
        return link.href.toString();
      }
    for (AtomLink link : links)
      if (link.type == null) {
        return link.href.toString();
      }
    for (AtomLink link : links)
      if (link.type != null && !"enclosure".equals(link.type) && !"self".equals(link.type)) {
        return link.href.toString();
      }
    return links.get(0).href.toString();
  }

  @NonNull
  @Override
  public Date getPublicationDate() {
    return updated.date;
  }

  @NonNull
  @Override
  public String getTitle() {
    return title.value;
  }

  @Nullable
  @Override
  public String getDescription() {
    return subtitle == null ? null : subtitle.value;
  }

  @Nullable
  @Override
  public String getCopyright() {
    return rights == null ? null : rights.value;
  }

  @Nullable
  @Override
  public String getImageLink() {
    return logo == null ? (icon == null ? null : icon.toString()) : logo.toString();
  }

  @Nullable
  @Override
  public String getAuthor() {
    if (authors.isEmpty()) {
      return contributors.isEmpty() ? null : contributors.get(0).name;
    } else {
      return authors.get(0).name;
    }
  }

  @NonNull
  @Override
  public List<? extends Item> getItems() {
    return entries;
  }
}

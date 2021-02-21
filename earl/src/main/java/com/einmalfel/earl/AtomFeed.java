package com.einmalfel.earl;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public final class AtomFeed extends AtomCommonAttributes implements Feed {
  static final String XML_TAG = "feed";
  private static final String TAG = "Earl.AtomFeed";

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
  public final MediaCommon media;
  @Nullable
  public final AtomGenerator generator;
  @Nullable
  public final URI icon;
  @Nullable
  public final AtomDate published;
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

	final List<AtomEntry> entries = new LinkedList<>();
	final List<AtomPerson> contributors = new LinkedList<>();
	final List<AtomPerson> authors = new LinkedList<>();
	final List<AtomLink> links = new LinkedList<>();
	final List<AtomCategory> categories = new LinkedList<>();
	MediaCommon.MediaCommonBuilder mediaBuilder = null;
	AtomText title = null;
	AtomGenerator generator = null;
	AtomText rights = null;
	AtomText subtitle = null;
	String id = null;
	AtomDate published = null;
	URI icon = null;
	URI logo = null;
	AtomDate updated = null;

	final AtomCommonAttributes atomCommonAttributes = new AtomCommonAttributes(parser);
	while (parser.nextTag() == XmlPullParser.START_TAG && (maxItm < 1 || entries.size() < maxItm)) {
	  final String namespace = parser.getNamespace();
	  if (Utils.ATOM_NAMESPACE.equalsIgnoreCase(namespace)) {
		final String tagName = parser.getName();
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
		  case "published":
			published = AtomDate.read(parser);
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
		if (Utils.MEDIA_NAMESPACE.equalsIgnoreCase(namespace)) {
		  if (mediaBuilder == null) {
			mediaBuilder = new MediaCommon.MediaCommonBuilder();
		  }
		  if (!mediaBuilder.parseTag(parser)) {
			Log.w(TAG, "Unknown mrss tag on feed level");
			Utils.skipTag(parser);
		  }
		} else {
		  Log.w(TAG, "Unknown Atom feed extension " + parser.getNamespace());
		  Utils.skipTag(parser);
		}
	  }
	  Utils.finishTag(parser);
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
	atomCommonAttributes, Utils.nonNullUri(id), title, updated, authors,
	contributors, generator, icon, logo, rights, subtitle, links, published, categories,
	mediaBuilder == null ? null : mediaBuilder.build(), entries);
  }

  public AtomFeed(@Nullable AtomCommonAttributes atomCommonAttributes, @NonNull URI id,
				  @NonNull AtomText title, @NonNull AtomDate updated,
				  @NonNull List<AtomPerson> authors,
				  @NonNull List<AtomPerson> contributors, @Nullable AtomGenerator generator,
				  @Nullable URI icon, @Nullable URI logo, @Nullable AtomText rights,
				  @Nullable AtomText subtitle, @NonNull List<AtomLink> links,
				  @Nullable AtomDate published, @NonNull List<AtomCategory> categories,
				  @Nullable MediaCommon media, @NonNull List<AtomEntry> entries) {
	super(atomCommonAttributes);
	this.id = id;
	this.published = published;
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
	this.media = media;
	this.entries = Collections.unmodifiableList(entries);
  }

  @Nullable
  @Override
  public String getLink() {
	if (links.isEmpty()) {
	  return null;
	}
	for (AtomLink link : links) {
	  if ("alternate".equals(link.rel)) {
		return link.href.toString();
	  }
	}
	for (AtomLink link : links) {
	  if ("via".equals(link.rel)) {
		return link.href.toString();
	  }
	}
	for (AtomLink link : links) {
	  if ("related".equals(link.rel)) {
		return link.href.toString();
	  }
	}
	for (AtomLink link : links) {
	  if (link.rel == null) {
		return link.href.toString();
	  }
	}
	for (AtomLink link : links) {
	  if (link.rel != null && !"enclosure".equals(link.rel) && !"self".equals(link.rel)) {
		return link.href.toString();
	  }
	}
	return links.get(0).href.toString();
  }

  @Nullable
  @Override
  public Date getPublicationDate() {
	return published.date;
  }

  @Nullable
  @Override
  public Date getUpdatedDate() {
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
	if (rights != null) {
	  return rights.value;
	} else {
	  if (media != null && media.license != null) {
		return media.license.value;
	  } else {
		return null;
	  }
	}
  }

  @Nullable
  @Override
  public String getImageLink() {
	if (logo != null) {
	  return logo.toString();
	} else {
	  if (media != null && !media.thumbnails.isEmpty()) {
		return media.thumbnails.get(0).url.toString();
	  } else {
		return null;
	  }
	}
  }

  @Nullable
  @Override
  public String getAuthor() {
	if (authors.isEmpty()) {
	  return contributors.isEmpty() ? null : contributors.get(0).name;
	} else {
	  if (media != null && !media.credits.isEmpty()) {
		for (MediaCredit credit : media.credits) {
		  if ("author".equalsIgnoreCase(credit.role)) {
			return credit.value;
		  }
		}
		return media.credits.get(0).value;
	  } else {
		return authors.get(0).name;
	  }
	}
  }

  @NonNull
  @Override
  public List<? extends Item> getItems() {
	return entries;
  }
}

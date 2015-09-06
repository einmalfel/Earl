package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MediaCommon {
  static class MediaCommonBuilder {
    private Boolean adult;
    private MediaRating rating;
    private MediaTitle title;
    private MediaTitle description;
    private List<String> keywords;
    private List<MediaThumbnail> thumbnails = new LinkedList<>();
    private List<MediaCategory> categories = new LinkedList<>();
    private MediaHash hash;
    private MediaPlayer player;
    private List<MediaCredit> credits = new LinkedList<>();
    private MediaCopyright copyright;
    private List<MediaText> texts = new LinkedList<>();
    private List<MediaRestriction> restrictions = new LinkedList<>();
    private MediaCommunity community;
    private List<String> comments = new LinkedList<>();
    private MediaEmbed embed;
    private List<String> responses = new LinkedList<>();
    private List<URL> backLinks = new LinkedList<>();
    private MediaStatus status;
    private List<MediaPrice> prices = new LinkedList<>();
    private MediaLicense license;
    private List<MediaSubTitle> subTitles = new LinkedList<>();
    private MediaPeerLink peerLink;
    private MediaLocation location;
    private MediaRights rights;
    private List<MediaScene> scenes = new LinkedList<>();

    /**
     * @return true if builder has consumed the tag
     * @throws XmlPullParserException
     * @throws IOException
     */
    boolean parseTag(XmlPullParser parser) throws XmlPullParserException, IOException {
      switch (parser.getName()) {
        case "adult":
          adult = Boolean.parseBoolean(parser.nextText());
          break;
        case MediaRating.XML_TAG:
          rating = MediaRating.read(parser);
          break;
        case "title":
          title = MediaTitle.read(parser);
          break;
        case "description":
          description = MediaTitle.read(parser);
          break;
        case "keywords":
          keywords = Arrays.asList(parser.nextText().split(","));
          break;
        case MediaThumbnail.XML_TAG:
          thumbnails.add(MediaThumbnail.read(parser));
          break;
        case MediaCategory.XML_TAG:
          categories.add(MediaCategory.read(parser));
          break;
        case MediaHash.XML_TAG:
          hash = MediaHash.read(parser);
          break;
        case MediaPlayer.XML_TAG:
          player = MediaPlayer.read(parser);
          break;
        case MediaCredit.XML_TAG:
          credits.add(MediaCredit.read(parser));
          break;
        case MediaCopyright.XML_TAG:
          copyright = MediaCopyright.read(parser);
          break;
        case MediaText.XML_TAG:
          texts.add(MediaText.read(parser));
          break;
        case MediaRestriction.XML_TAG:
          restrictions.add(MediaRestriction.read(parser));
          break;
        case MediaCommunity.XML_TAG:
          community = MediaCommunity.read(parser);
          break;
        case "comments":
          while (parser.nextTag() == XmlPullParser.START_TAG) {
            parser.require(XmlPullParser.START_TAG, null, "comment");
            comments.add(parser.nextText());
          }
          break;
        case MediaEmbed.XML_TAG:
          embed = MediaEmbed.read(parser);
          break;
        case "responses":
          while (parser.nextTag() == XmlPullParser.START_TAG) {
            parser.require(XmlPullParser.START_TAG, null, "response");
            responses.add(parser.nextText());
          }
          break;
        case "backLinks":
          while (parser.nextTag() == XmlPullParser.START_TAG) {
            parser.require(XmlPullParser.START_TAG, null, "backLink");
            backLinks.add(Utils.nonNullUrl(parser.nextText()));
          }
          break;
        case MediaStatus.XML_TAG:
          status = MediaStatus.read(parser);
          break;
        case MediaPrice.XML_TAG:
          prices.add(MediaPrice.read(parser));
          break;
        case MediaLicense.XML_TAG:
          license = MediaLicense.read(parser);
          break;
        case MediaSubTitle.XML_TAG:
          subTitles.add(MediaSubTitle.read(parser));
          break;
        case MediaPeerLink.XML_TAG:
          peerLink = MediaPeerLink.read(parser);
          break;
        case MediaLocation.XML_TAG:
          location = MediaLocation.read(parser);
          break;
        case MediaRights.XML_TAG:
          rights = MediaRights.read(parser);
          break;
        case "scenes":
          while (parser.nextTag() == XmlPullParser.START_TAG) {
            parser.require(XmlPullParser.START_TAG, null, "scene");
            scenes.add(MediaScene.read(parser));
          }
          break;
        default:
          return false;
      }
      return true;
    }

    MediaCommon build() {
      if (keywords == null) {
        keywords = new LinkedList<>();
      }
      return new MediaCommon(
          adult, rating, title, description, keywords, thumbnails, categories, hash, player,
          credits, copyright, texts, restrictions, community, comments, embed, responses, backLinks,
          status, prices, license, subTitles, peerLink, location, rights, scenes);
    }
  }

  @Nullable
  public final Boolean adult;
  @Nullable
  public final MediaRating rating;
  @Nullable
  public final MediaTitle title;
  @Nullable
  public final MediaTitle description;
  @NonNull
  public final List<String> keywords;
  @NonNull
  public final List<MediaThumbnail> thumbnails;
  @NonNull
  public final List<MediaCategory> categories;
  @Nullable
  public final MediaHash hash;
  @Nullable
  public final MediaPlayer player;
  @NonNull
  public final List<MediaCredit> credits;
  @Nullable
  public final MediaCopyright copyright;
  @NonNull
  public final List<MediaText> texts;
  @NonNull
  public final List<MediaRestriction> restrictions;
  @Nullable
  public final MediaCommunity community;
  @NonNull
  public final List<String> comments;
  @Nullable
  public final MediaEmbed embed;
  @NonNull
  public final List<String> responses;
  @NonNull
  public final List<URL> backLinks;
  @Nullable
  public final MediaStatus status;
  @NonNull
  public final List<MediaPrice> prices;
  @Nullable
  public final MediaLicense license;
  @NonNull
  public final List<MediaSubTitle> subTitles;
  @Nullable
  public final MediaPeerLink peerLink;
  @Nullable
  public final MediaLocation location;
  @Nullable
  public final MediaRights rights;
  @NonNull
  public final List<MediaScene> scenes;

  @NonNull
  static MediaCommon read(XmlPullParser parser) throws XmlPullParserException, IOException {
    MediaCommonBuilder builder = new MediaCommonBuilder();
    while (parser.nextTag() == XmlPullParser.START_TAG) {
      builder.parseTag(parser);
    }
    return builder.build();
  }

  public MediaCommon(@Nullable Boolean adult, @Nullable MediaRating rating,
                     @Nullable MediaTitle title, @Nullable MediaTitle description,
                     @NonNull List<String> keywords, @NonNull List<MediaThumbnail> thumbnails,
                     @NonNull List<MediaCategory> categories, @Nullable MediaHash hash,
                     @Nullable MediaPlayer player, @NonNull List<MediaCredit> credits,
                     @Nullable MediaCopyright copyright, @NonNull List<MediaText> texts,
                     @NonNull List<MediaRestriction> restrictions,
                     @Nullable MediaCommunity community, @NonNull List<String> comments,
                     @Nullable MediaEmbed embed, @NonNull List<String> responses,
                     @NonNull List<URL> backLinks, @Nullable MediaStatus status,
                     @NonNull List<MediaPrice> prices, @Nullable MediaLicense license,
                     @NonNull List<MediaSubTitle> subTitles, @Nullable MediaPeerLink peerLink,
                     @Nullable MediaLocation location, @Nullable MediaRights rights,
                     @NonNull List<MediaScene> scenes) {
    this.adult = adult;
    this.rating = rating;
    this.title = title;
    this.description = description;
    this.keywords = Collections.unmodifiableList(keywords);
    this.thumbnails = Collections.unmodifiableList(thumbnails);
    this.categories = Collections.unmodifiableList(categories);
    this.hash = hash;
    this.player = player;
    this.credits = Collections.unmodifiableList(credits);
    this.copyright = copyright;
    this.texts = Collections.unmodifiableList(texts);
    this.restrictions = Collections.unmodifiableList(restrictions);
    this.community = community;
    this.comments = Collections.unmodifiableList(comments);
    this.embed = embed;
    this.responses = Collections.unmodifiableList(responses);
    this.backLinks = Collections.unmodifiableList(backLinks);
    this.status = status;
    this.prices = Collections.unmodifiableList(prices);
    this.license = license;
    this.subTitles = Collections.unmodifiableList(subTitles);
    this.peerLink = peerLink;
    this.location = location;
    this.rights = rights;
    this.scenes = Collections.unmodifiableList(scenes);
  }

  public MediaCommon(@NonNull MediaCommon source) {
    this.adult = source.adult;
    this.rating = source.rating;
    this.title = source.title;
    this.description = source.description;
    this.keywords = source.keywords;
    this.thumbnails = source.thumbnails;
    this.categories = source.categories;
    this.hash = source.hash;
    this.player = source.player;
    this.credits = source.credits;
    this.copyright = source.copyright;
    this.texts = source.texts;
    this.restrictions = source.restrictions;
    this.community = source.community;
    this.comments = source.comments;
    this.embed = source.embed;
    this.responses = source.responses;
    this.backLinks = source.backLinks;
    this.status = source.status;
    this.prices = source.prices;
    this.license = source.license;
    this.subTitles = source.subTitles;
    this.peerLink = source.peerLink;
    this.location = source.location;
    this.rights = source.rights;
    this.scenes = source.scenes;
  }
}

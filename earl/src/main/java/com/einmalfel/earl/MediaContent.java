package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URL;

public class MediaContent extends MediaCommon {
  static final String XML_TAG = "content";

  @Nullable
  public final URL url;
  @Nullable
  public final Integer fileSize;
  @Nullable
  public final String type;
  @Nullable
  public final String medium;
  @Nullable
  public final Boolean isDefault;
  @Nullable
  public final String expression;
  @Nullable
  public final Integer bitrate;
  @Nullable
  public final Integer framerate;
  @Nullable
  public final Integer samplingrate;
  @Nullable
  public final Integer channels;
  @Nullable
  public final Integer duration;
  @Nullable
  public final Integer height;
  @Nullable
  public final Integer width;
  @Nullable
  public final String lang;

  @NonNull
  static MediaContent read(XmlPullParser parser) throws XmlPullParserException, IOException {
    parser.require(XmlPullParser.START_TAG, null, XML_TAG);
    String url = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "url");
    String fileSize = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "fileSize");
    String isDefault = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "isDefault");
    String bitrate = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "bitrate");
    String framerate = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "framerate");
    String samplingrate = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "samplingrate");
    String channels = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "channels");
    String duration = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "duration");
    String height = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "height");
    String width = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "width");
    return new MediaContent(url == null ? null : Utils.tryParseUrl(url),
                            fileSize == null ? null : Utils.tryParseInt(fileSize),
                            parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "type"),
                            parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "medium"),
                            isDefault == null ? null : Boolean.valueOf(isDefault),
                            parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "expression"),
                            bitrate == null ? null : Utils.tryParseInt(bitrate),
                            framerate == null ? null : Utils.tryParseInt(framerate),
                            samplingrate == null ? null : Utils.tryParseInt(samplingrate),
                            channels == null ? null : Utils.tryParseInt(channels),
                            duration == null ? null : Utils.tryParseInt(duration),
                            height == null ? null : Utils.tryParseInt(height),
                            width == null ? null : Utils.tryParseInt(width),
                            parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "lang"),
                            MediaCommon.read(parser));
  }

  public MediaContent(@Nullable URL url, @Nullable Integer fileSize, @Nullable String type,
                      @Nullable String medium, @Nullable Boolean isDefault,
                      @Nullable String expression, @Nullable Integer bitrate,
                      @Nullable Integer framerate, @Nullable Integer samplingrate,
                      @Nullable Integer channels, @Nullable Integer duration,
                      @Nullable Integer height, @Nullable Integer width, @Nullable String lang,
                      @NonNull MediaCommon source) {
    super(source);
    this.url = url;
    this.fileSize = fileSize;
    this.type = type;
    this.medium = medium;
    this.isDefault = isDefault;
    this.expression = expression;
    this.bitrate = bitrate;
    this.framerate = framerate;
    this.samplingrate = samplingrate;
    this.channels = channels;
    this.duration = duration;
    this.height = height;
    this.width = width;
    this.lang = lang;
  }
}

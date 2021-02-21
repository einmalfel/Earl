package com.einmalfel.earl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URL;

public final class MediaSubTitle {
  static final String XML_TAG = "subTitle";

  @Nullable
  public final String type;
  @Nullable
  public final String lang;
  @NonNull
  public final URL href;

  @NonNull
  static MediaSubTitle read(@NonNull XmlPullParser parser) throws XmlPullParserException, IOException {
	final MediaSubTitle result = new MediaSubTitle(
	parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "type"),
	parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "lang"),
	Utils.nonNullUrl(parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "href")));
	parser.nextTag();
	return result;
  }

  public MediaSubTitle(@Nullable String type, @Nullable String lang, @NonNull URL href) {
	this.type = type;
	this.lang = lang;
	this.href = href;
  }
}

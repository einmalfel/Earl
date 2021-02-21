package com.einmalfel.earl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URI;

public final class MediaCategory {
  static final String XML_TAG = "category";

  @Nullable
  public final URI scheme;
  @Nullable
  public final String label;
  @NonNull
  public final String value;

  @NonNull
  static MediaCategory read(@NonNull XmlPullParser parser) throws XmlPullParserException, IOException {
	parser.require(XmlPullParser.START_TAG, null, XML_TAG);
	final String scheme = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "scheme");
	return new MediaCategory(scheme == null ? null : Utils.tryParseUri(scheme),
	parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "label"),
	parser.nextText());
  }

  public MediaCategory(@Nullable URI scheme, @Nullable String label, @NonNull String value) {
	this.scheme = scheme;
	this.label = label;
	this.value = value;
  }
}

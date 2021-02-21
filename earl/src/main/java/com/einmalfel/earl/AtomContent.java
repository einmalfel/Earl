package com.einmalfel.earl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URI;

public final class AtomContent extends AtomText {
  static final String XML_TAG = "content";

  @Nullable
  public final URI src;

  @NonNull
  static AtomContent read(XmlPullParser parser) throws XmlPullParserException, IOException {
	parser.require(XmlPullParser.START_TAG, null, XML_TAG);
	final String srcString = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "src");
	return new AtomContent(
	srcString == null ? null : Utils.tryParseUri(srcString),
	AtomText.read(parser));
  }

  public AtomContent(@Nullable URI src, @NonNull AtomText atomText) {
	super(atomText);
	this.src = src;
  }
}

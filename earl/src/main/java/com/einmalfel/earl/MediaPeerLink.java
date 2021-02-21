package com.einmalfel.earl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URL;

public final class MediaPeerLink {
  static final String XML_TAG = "peerLink";

  @Nullable
  public final String type;
  @NonNull
  public final URL href;

  @NonNull
  static MediaPeerLink read(@NonNull XmlPullParser parser) throws XmlPullParserException, IOException {
	parser.require(XmlPullParser.START_TAG, null, XML_TAG);
	final MediaPeerLink result = new MediaPeerLink(
	parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "type"),
	Utils.nonNullUrl(parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "href")));
	parser.nextTag();
	return result;
  }

  public MediaPeerLink(@Nullable String type, @NonNull URL href) {
	this.type = type;
	this.href = href;
  }
}

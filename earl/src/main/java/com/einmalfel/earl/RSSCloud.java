package com.einmalfel.earl;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public final class RSSCloud {
  static final String XML_TAG = "cloud";

  // RSS spec doesn't declare those attributes required nor it defines them optional
  @Nullable
  public final String domain;
  @Nullable
  public final Integer port;
  @Nullable
  public final String path;
  @Nullable
  public final String registerProcedure;
  @Nullable
  public final String protocol;

  @NonNull
  static RSSCloud read(@NonNull XmlPullParser parser) throws IOException, XmlPullParserException {
	parser.require(XmlPullParser.START_TAG, XmlPullParser.NO_NAMESPACE, XML_TAG);
	final String port = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "port");
	final String domain = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "domain");
	final String path = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "path");
	final String procedure = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "registerProcedure");
	final String protocol = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "protocol");
	parser.next();
	return new RSSCloud(
	domain, port == null ? null : Utils.tryParseInt(port), path, procedure, protocol);
  }

  public RSSCloud(@Nullable String domain, @Nullable Integer port, @Nullable String path,
				  @Nullable String registerProcedure, @Nullable String protocol) {
	this.domain = domain;
	this.port = port;
	this.path = path;
	this.registerProcedure = registerProcedure;
	this.protocol = protocol;
  }
}

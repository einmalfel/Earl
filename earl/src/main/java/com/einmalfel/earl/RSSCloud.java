package com.einmalfel.earl;

import android.support.annotation.NonNull;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class RSSCloud {
  static final String XML_TAG = "cloud";

  public final String domain;
  public final Integer port;
  public final String path;
  public final String registerProcedure;
  public final String protocol;

  @NonNull
  static RSSCloud read(@NonNull XmlPullParser parser) throws IOException, XmlPullParserException {
    parser.require(XmlPullParser.START_TAG, XmlPullParser.NO_NAMESPACE, XML_TAG);
    String port = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "port");
    return new RSSCloud(
        parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "domain"),
        port == null ? null : Integer.parseInt(port),
        parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "path"),
        parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "registerProcedure"),
        parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "protocol"));
  }

  public RSSCloud(String domain, Integer port, String path, String registerProcedure, String protocol) {
    this.domain = domain;
    this.port = port;
    this.path = path;
    this.registerProcedure = registerProcedure;
    this.protocol = protocol;
  }
}

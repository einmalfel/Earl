package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class RSSCloud {
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
    String port = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "port");
    return new RSSCloud(
        parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "domain"),
        port == null ? null : Utils.tryParseInt(port),
        parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "path"),
        parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "registerProcedure"),
        parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "protocol"));
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

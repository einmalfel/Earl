package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URL;

public class RSSEnclosure implements Enclosure {
  static final String XML_TAG = "enclosure";

  public final URL url;
  public final Integer length;
  public final String type;

  @NonNull
  static RSSEnclosure read(@NonNull XmlPullParser parser)
      throws IOException, XmlPullParserException {
    parser.require(XmlPullParser.START_TAG, XmlPullParser.NO_NAMESPACE, XML_TAG);
    String urlString = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "url");
    String lengthString = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "length");
    RSSEnclosure result = new RSSEnclosure(
        urlString == null ? null : new URL(urlString),
        lengthString == null ? null : Integer.valueOf(lengthString),
        parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "type"));
    parser.nextText();
    return result;
  }

  public RSSEnclosure(URL url, Integer length, String type) {
    this.url = url;
    this.length = length;
    this.type = type;
  }

  @Nullable
  @Override
  public String getLink() {
    return url.toString();
  }

  @Nullable
  @Override
  public Integer getLength() {
    return length;
  }

  @Nullable
  @Override
  public String getType() {
    return type;
  }
}

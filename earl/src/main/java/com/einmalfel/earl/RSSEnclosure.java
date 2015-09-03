package com.einmalfel.earl;

import android.support.annotation.NonNull;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URL;

public class RSSEnclosure implements Enclosure {
  static final String XML_TAG = "enclosure";

  @NonNull
  public final URL url;
  @NonNull
  public final Integer length;
  @NonNull
  public final String type;

  @NonNull
  static RSSEnclosure read(@NonNull XmlPullParser parser)
      throws IOException, XmlPullParserException {
    parser.require(XmlPullParser.START_TAG, XmlPullParser.NO_NAMESPACE, XML_TAG);
    RSSEnclosure result = new RSSEnclosure(
        Utils.nonNullUrl(parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "url")),
        Utils.nonNullInt(parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "length")),
        Utils.nonNullString(parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "type")));
    parser.nextText();
    return result;
  }

  public RSSEnclosure(@NonNull URL url, @NonNull Integer length, @NonNull String type) {
    this.url = url;
    this.length = length;
    this.type = type;
  }

  @NonNull
  @Override
  public String getLink() {
    return url.toString();
  }

  @NonNull
  @Override
  public Integer getLength() {
    return length;
  }

  @NonNull
  @Override
  public String getType() {
    return type;
  }
}

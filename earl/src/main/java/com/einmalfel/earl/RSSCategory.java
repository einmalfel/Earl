package com.einmalfel.earl;

import android.support.annotation.NonNull;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class RSSCategory {
  static final String XML_TAG = "category";

  public final String value;
  public final String domain;

  @NonNull
  static RSSCategory read(@NonNull XmlPullParser parser)throws IOException, XmlPullParserException {
    parser.require(XmlPullParser.START_TAG, XmlPullParser.NO_NAMESPACE, XML_TAG);
    return new RSSCategory(Utils.nonNullString(parser.nextText()),
                           parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "domain"));
  }

  public RSSCategory(String value, String domain) {
    this.value = value;
    this.domain = domain;
  }
}

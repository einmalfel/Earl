package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class RSSGuid {
  static final String XML_TAG = "guid";

  @NonNull
  public final String value;
  @Nullable
  public final Boolean isPermalink;

  @NonNull
  static RSSGuid read(@NonNull XmlPullParser parser) throws IOException, XmlPullParserException {
    parser.require(XmlPullParser.START_TAG, XmlPullParser.NO_NAMESPACE, XML_TAG);
    String permalink = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "isPermalink");
    return new RSSGuid(Utils.nonNullString(parser.nextText()),
                       permalink == null ? null : Boolean.valueOf(permalink));
  }

  public RSSGuid(@NonNull String value, @Nullable Boolean isPermalink) {
    this.value = value;
    this.isPermalink = isPermalink;
  }
}

package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URI;

public class AtomLink extends AtomCommonAttributes implements Enclosure {
  private static final String TAG = "E.ALK";
  static final String XML_TAG = "link";

  @NonNull
  public final URI href;
  @Nullable
  public final String rel;
  @Nullable
  public final String type;
  @Nullable
  public final String hreflang;
  @Nullable
  public final String title;
  @Nullable
  public final Integer length;

  @NonNull
  static AtomLink read(XmlPullParser parser)
      throws XmlPullParserException, IOException {
    parser.require(XmlPullParser.START_TAG, Utils.ATOM_NAMESPACE, XML_TAG);
    String length = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "length");
    AtomLink result = new AtomLink(
        new AtomCommonAttributes(parser),
        Utils.nonNullUri(parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "href")),
        parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "rel"),
        parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "type"),
        parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "hreflang"),
        parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "title"),
        length == null ? null : Utils.tryParseInt(length));
    parser.nextTag();
    return result;
  }

  public AtomLink(@Nullable AtomCommonAttributes atomCommonAttributes, @NonNull URI href,
                  @Nullable String rel, @Nullable String type, @Nullable String hreflang,
                  @Nullable String title, @Nullable Integer length) {
    super(atomCommonAttributes);
    this.href = href;
    this.rel = rel;
    this.type = type;
    this.hreflang = hreflang;
    this.title = title;
    this.length = length;
  }

  @NonNull
  @Override
  public String getLink() {
    return href.toString();
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

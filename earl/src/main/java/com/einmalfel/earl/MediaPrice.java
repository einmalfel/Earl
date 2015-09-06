package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class MediaPrice {
  static final String XML_TAG = "price";

  @Nullable
  public final String type;
  @Nullable
  public final String info;
  @Nullable
  public final String price;
  @Nullable
  public final String currency;

  @NonNull
  static MediaPrice read(XmlPullParser parser) throws XmlPullParserException, IOException {
    MediaPrice result = new MediaPrice(
        parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "type"),
        parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "info"),
        parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "price"),
        parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "currency"));
    parser.nextTag();
    return result;
  }

  public MediaPrice(@Nullable String type, @Nullable String info, @Nullable String price,
                    @Nullable String currency) {
    this.type = type;
    this.info = info;
    this.price = price;
    this.currency = currency;
  }
}

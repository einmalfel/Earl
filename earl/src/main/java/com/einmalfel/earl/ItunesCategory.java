package com.einmalfel.earl;

import android.support.annotation.NonNull;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ItunesCategory {
  static final String XML_TAG = "category";
  @NonNull
  public final String text;
  @NonNull
  public final List<ItunesCategory> subCategories;

  @NonNull
  static ItunesCategory read(@NonNull XmlPullParser parser)
      throws IOException, XmlPullParserException {
    parser.require(XmlPullParser.START_TAG, null, XML_TAG);
    String value = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "text");
    List<ItunesCategory> subCategories = new LinkedList<>();
    while (parser.nextTag() != XmlPullParser.END_TAG) {
      subCategories.add(ItunesCategory.read(parser));
    }
    return new ItunesCategory(Utils.nonNullString(value), subCategories);
  }

  public ItunesCategory(@NonNull String text, @NonNull List<ItunesCategory> subCategories) {
    this.text = text;
    this.subCategories = Collections.unmodifiableList(subCategories);
  }
}

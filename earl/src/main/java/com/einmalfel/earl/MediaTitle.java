package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class MediaTitle {
  @Nullable
  public final String type;
  @NonNull
  public final String value;

  @NonNull
  static MediaTitle read(XmlPullParser parser) throws XmlPullParserException, IOException {
    return new MediaTitle(parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "type"),
                          parser.nextText());
  }

  public MediaTitle(@Nullable String type, @NonNull String value) {
    this.type = type;
    this.value = value;
  }
}

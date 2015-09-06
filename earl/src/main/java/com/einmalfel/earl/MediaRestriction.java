package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class MediaRestriction {
  static final String XML_TAG = "restriction";

  @Nullable
  public final String relationship;
  @Nullable
  public final String type;
  @NonNull
  public final String value;

  @NonNull
  static MediaRestriction read(XmlPullParser parser) throws XmlPullParserException, IOException {
    return new MediaRestriction(
        parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "relationship"),
        parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "type"),
        parser.nextText());
  }

  public MediaRestriction(@Nullable String relationship, @Nullable String type,
                          @NonNull String value) {
    this.relationship = relationship;
    this.type = type;
    this.value = value;
  }
}

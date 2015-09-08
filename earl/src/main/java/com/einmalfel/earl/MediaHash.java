package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class MediaHash {
  static final String XML_TAG = "hash";

  @Nullable
  public final String algo;
  @NonNull
  public final String value;

  @NonNull
  static MediaHash read(XmlPullParser parser) throws XmlPullParserException, IOException {
    parser.require(XmlPullParser.START_TAG, null, XML_TAG);
    return new MediaHash(parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "algo"),
                         parser.nextText());
  }

  public MediaHash(@Nullable String algo, @NonNull String value) {
    this.algo = algo;
    this.value = value;
  }
}

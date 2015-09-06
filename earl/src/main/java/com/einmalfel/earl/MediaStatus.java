package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class MediaStatus {
  static final String XML_TAG = "status";

  @Nullable
  public final String state;
  @Nullable
  public final String reason;

  @NonNull
  static MediaStatus read(XmlPullParser parser) throws XmlPullParserException, IOException {
    MediaStatus result = new MediaStatus(
        parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "state"),
        parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "reason"));
    parser.nextTag();
    return result;
  }

  public MediaStatus(@Nullable String state, @Nullable String reason) {
    this.state = state;
    this.reason = reason;
  }
}

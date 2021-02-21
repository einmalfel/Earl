package com.einmalfel.earl;

import androidx.annotation.NonNull;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public final class MediaRights {
  static final String XML_TAG = "rights";

  @NonNull
  public final String status;

  @NonNull
  static MediaRights read(@NonNull XmlPullParser parser) throws XmlPullParserException, IOException {
    parser.require(XmlPullParser.START_TAG, null, XML_TAG);
    MediaRights result = new MediaRights(
        Utils.nonNullString(parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "status")));
    parser.nextTag();
    return result;
  }

  public MediaRights(@NonNull String status) {
    this.status = status;
  }
}

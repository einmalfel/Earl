package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class ItunesOwner {
  static final String XML_TAG = "owner";
  private static final String TAG = "Earl.ItunesOwner";

  @Nullable
  public final String name;
  @Nullable
  public final String eMail;

  @NonNull
  static ItunesOwner read(@NonNull XmlPullParser parser)
      throws IOException, XmlPullParserException {
    parser.require(XmlPullParser.START_TAG, null, XML_TAG);
    String name = null;
    String eMail = null;
    while (parser.nextTag() == XmlPullParser.START_TAG) {
      switch (parser.getName()) {
        case "name":
          name = parser.nextText();
          break;
        case "email":
          eMail = parser.nextText();
          break;
        default:
          Log.w(TAG, "Unexpected owner tag " + parser.getName());
      }
      Utils.finishTag(parser);
    }
    return new ItunesOwner(name, eMail);
  }

  public ItunesOwner(@Nullable String name, @Nullable String eMail) {
    this.name = name;
    this.eMail = eMail;
  }
}

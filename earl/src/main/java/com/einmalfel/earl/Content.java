package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class Content {
  private static final String TAG = "Earl.Content";

  static class ContentBuilder {
    // this is the only tag specified in latest standard draft, see
    // http://web.resource.org/rss/1.0/modules/content/
    private static final String ENCODED_TAG = "encoded";
    private String encodedValue;

    void parseTag(@NonNull XmlPullParser parser) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      if (tagName.equals(ENCODED_TAG)) {
        encodedValue = parser.nextText();
      } else {
        Log.w(TAG, "Unknown Content feed tag '" + tagName + "', skipping..");
        Utils.skipTag(parser);
      }
    }

    @NonNull
    Content build() {
      return new Content(encodedValue);
    }
  }

  @Nullable
  public final String encoded;

  public Content(@Nullable String encoded) {
    this.encoded = encoded;
  }
}

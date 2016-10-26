/*
 * This file is part of Fineswap Android App.
 * Copyright (C) Fineswap AS. All rights reserved.
 *
 * THIS IS PROPRIETARY SOFTWARE AND IS OWNED BY FINESWAP AS.
 * DO NOT DISTRIBUTE! DO NOT MODIFY! DO NOT COPY!
 */

package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RdfFeed {
  private static final String TAG = "Earl.RdfFeed";

  private enum ST {encoded}

  static class RdfFeedBuilder {
    final Map<ST, String> map = new HashMap<>();

    void parseTag(@NonNull XmlPullParser parser) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      switch (tagName) {
        default:
          try {
            map.put(ST.valueOf(tagName), parser.nextText());
          } catch (IllegalArgumentException ignored) {
            Log.w(TAG, "Unknown RDF feed tag '" + tagName + "', skipping..");
            Utils.skipTag(parser);
          }
      }
    }

    @NonNull
    RdfFeed build() {
      return new RdfFeed(
          map.containsKey(ST.encoded) ? map.remove(ST.encoded) : null);
    }
  }

  @Nullable
  public final String contentEncoded;

  public RdfFeed(@Nullable String contentEncoded) {
    this.contentEncoded = contentEncoded;
  }
}

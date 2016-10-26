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

public class RdfItem {
  private static final String TAG = "Earl.RdfItem";

  private enum ST {encoded}

  static class RdfItemBuilder {
    final Map<ST, String> map = new HashMap<>();

    void parseTag(@NonNull XmlPullParser parser) throws IOException, XmlPullParserException {
      String tagName = parser.getName();
      Log.w(TAG, "RDF tag: " + tagName);
      switch (tagName) {
        default:
          try {
            map.put(ST.valueOf(tagName), parser.nextText());
          } catch (IllegalArgumentException ignored) {
            Log.w(TAG, "Unknown RDF item tag '" + tagName + "'");
            Utils.skipTag(parser);
          }
      }
    }

    @NonNull
    RdfItem build() {
      return new RdfItem(
          map.remove(ST.encoded));
    }
  }

  @Nullable
  public final String contentEncoded;

  public RdfItem(@Nullable String contentEncoded) {
    this.contentEncoded = contentEncoded;
  }
}

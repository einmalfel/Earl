package com.einmalfel.earl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.xmlpull.v1.XmlPullParser;

import java.net.URI;

public class AtomCommonAttributes {
  @Nullable
  public final URI base;
  @Nullable
  public final String lang;

  public AtomCommonAttributes(@Nullable URI base, @Nullable String lang) {
	this.base = base;
	this.lang = lang;
  }

  AtomCommonAttributes(@NonNull XmlPullParser parser) {
	final String baseString = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "base");
	base = baseString == null ? null : Utils.tryParseUri(baseString);
	lang = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "lang");
  }

  AtomCommonAttributes(@Nullable AtomCommonAttributes source) {
	if (source == null) {
	  base = null;
	  lang = null;
	} else {
	  base = source.base;
	  lang = source.lang;
	}
  }
}

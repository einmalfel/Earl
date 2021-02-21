package com.einmalfel.earl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class AtomText extends AtomCommonAttributes {
  @Nullable
  public final String type;
  @NonNull
  public final String value;

  @NonNull
  static AtomText read(@NonNull XmlPullParser parser)
      throws XmlPullParserException, IOException {
    return new AtomText(
        new AtomCommonAttributes(parser),
        parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "type"),
        parser.nextText());
  }

  public AtomText(@Nullable AtomCommonAttributes atomCommonAttributes, @Nullable String type,
                  @NonNull String value) {
    super(atomCommonAttributes);
    this.type = type;
    this.value = value;
  }

  AtomText(@NonNull AtomText source) {
    super(source);
    this.type = source.type;
    this.value = source.value;
  }
}

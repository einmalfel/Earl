package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Date;

public class AtomDate extends AtomCommonAttributes {
  private static final String TAG = "Earl.AtomDate";

  @NonNull
  public final Date date;

  @NonNull
  static AtomDate read(XmlPullParser parser) throws XmlPullParserException, IOException {
    AtomCommonAttributes atomCommonAttributes = new AtomCommonAttributes(parser);
    Date date = Utils.parseRFC3339Date(parser.nextText());
    if (date == null) {
      Log.w(TAG, "Replacing date with 0");
      date = new Date(0);
    }
    return new AtomDate(atomCommonAttributes, date);
  }

  public AtomDate(@Nullable AtomCommonAttributes source, @NonNull Date date) {
    super(source);
    this.date = date;
  }
}

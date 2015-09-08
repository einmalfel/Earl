package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MediaCommunity {
  static final String XML_TAG = "community";
  private static final String TAG = "Earl.MediaCommunity";

  public static class StarRating {
    static final String XML_TAG = "starRating";
    @Nullable
    public final String average;
    @Nullable
    public final Integer count;
    @Nullable
    public final Integer min;
    @Nullable
    public final Integer max;

    public StarRating(@Nullable String average, @Nullable Integer count, @Nullable Integer min,
                      @Nullable Integer max) {
      this.average = average;
      this.count = count;
      this.min = min;
      this.max = max;
    }
  }

  public static class Statistics {
    static final String XML_TAG = "statistics";

    @Nullable
    public final Integer views;
    @Nullable
    public final Integer favorites;

    public Statistics(@Nullable Integer views, @Nullable Integer favorites) {
      this.views = views;
      this.favorites = favorites;
    }
  }

  @Nullable
  public final StarRating starRating;
  @Nullable
  public final Statistics statistics;
  @NonNull
  public final List<String> tags;

  @NonNull
  static MediaCommunity read(XmlPullParser parser) throws XmlPullParserException, IOException {
    parser.require(XmlPullParser.START_TAG, null, XML_TAG);
    StarRating starRating = null;
    Statistics statistics = null;
    List<String> tags = null;
    while (parser.nextTag() == XmlPullParser.START_TAG) {
      switch (parser.getName()) {
        case StarRating.XML_TAG:
          String count = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "count");
          String min = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "min");
          String max = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "max");
          starRating = new StarRating(
              parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "average"),
              count == null ? null : Utils.tryParseInt(count),
              min == null ? null : Utils.tryParseInt(min),
              max == null ? null : Utils.tryParseInt(max));
          parser.nextTag();
          break;
        case Statistics.XML_TAG:
          String views = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "views");
          String favorites = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, "favorites");
          statistics = new Statistics(views == null ? null : Utils.tryParseInt(views),
                                      favorites == null ? null : Utils.tryParseInt(favorites));
          parser.nextTag();
          break;
        case "tags":
          tags = Arrays.asList(parser.nextText().split(","));
          break;
        default:
          Log.w(TAG, "Unexpected tag inside media:community: " + parser.getName());
          Utils.skipTag(parser);
      }
      Utils.finishTag(parser);
    }

    return new MediaCommunity(starRating, statistics,
                              tags == null ? new LinkedList<String>() : tags);
  }

  public MediaCommunity(@Nullable StarRating starRating, @Nullable Statistics statistics,
                        @NonNull List<String> tags) {
    this.starRating = starRating;
    this.statistics = statistics;
    this.tags = Collections.unmodifiableList(tags);
  }
}

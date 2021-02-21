package com.einmalfel.earl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;

/**
 * TODO: safe mode: return partially parsed feed, ignore case of tags
 * TODO: atom in rss, like in radio-t feed
 * TODO: extensions: podfm, mrss, creativecommons, feedburner
 */
public final class EarlParser {
  private EarlParser() {
  }

  /**
   * @param inputStream - stream to read feed from
   * @param maxItems    - stop parsing after reading this much feed items
   * @return parsed RSSFeed or AtomFeed, depending on input stream or null if parsing fails
   */
  @Nullable
  public static Feed parse(@NonNull InputStream inputStream, int maxItems) {
    try {
      return parseOrThrow(inputStream, maxItems);
    } catch (IOException | XmlPullParserException | DataFormatException ignored) {
      return null;
    }
  }

  /**
   * @param inputStream - stream to read feed from. Will be closed in this function
   * @param maxItems    - stop parsing after reading this much feed items
   * @return parsed RSSFeed or AtomFeed, depending on input stream
   */
  @NonNull
  public static Feed parseOrThrow(@NonNull InputStream inputStream, int maxItems)
      throws XmlPullParserException, IOException, DataFormatException {
    try {
      final XmlPullParser parser = Xml.newPullParser();
      parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
      parser.setInput(inputStream, null);
      while (parser.next() != XmlPullParser.END_DOCUMENT) {
        if (parser.getEventType() == XmlPullParser.START_TAG) {
          switch (parser.getName()) {
            case RSSFeed.XML_TAG:
              return RSSFeed.read(parser, maxItems);
            case AtomFeed.XML_TAG:
              return AtomFeed.read(parser, maxItems);
            default: // ignore other tags
          }
        }
      }
    } finally {
      inputStream.close();
    }
    throw new DataFormatException("No syndication feeds found in given stream");
  }
}

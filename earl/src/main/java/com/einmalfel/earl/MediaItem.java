package com.einmalfel.earl;

import androidx.annotation.NonNull;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class MediaItem extends MediaCommon {
  private static final String TAG = "Earl.MediaItem";

  static class MediaItemBuilder {
    private final List<MediaGroup> groups = new LinkedList<>();
    private final List<MediaContent> contents = new LinkedList<>();
    private final MediaCommonBuilder builder = new MediaCommonBuilder();

    boolean parseTag(XmlPullParser parser) throws XmlPullParserException, IOException {
      String tagName = parser.getName();
      switch (tagName) {
        case MediaGroup.XML_TAG:
          groups.add(MediaGroup.read(parser));
          break;
        case MediaContent.XML_TAG:
          contents.add(MediaContent.read(parser));
          break;
        default:
          return builder.parseTag(parser);
      }
      return true;
    }

    MediaItem build() {
      return new MediaItem(groups, contents, builder.build());
    }
  }

  @NonNull
  public final List<MediaGroup> groups;
  @NonNull
  public final List<MediaContent> contents;

  public MediaItem(@NonNull List<MediaGroup> groups, @NonNull List<MediaContent> contents,
                   @NonNull MediaCommon common) {
    super(common);
    this.groups = Collections.unmodifiableList(groups);
    this.contents = Collections.unmodifiableList(contents);
  }
}

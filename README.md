[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Earl-green.svg?style=flat)](https://android-arsenal.com/details/1/2490)

### Features
 * Earl parses both Atom and RSS 1.0/2.0
 * Supports RSS extensions (Itunes and Media RSS for now)
 * It's lightweight. There is nothing there but parser. The only dependency it has is android annotations
 * Parser produces easy-to-use immutable POJOs
 * Runs on android versions starting from Android 2.2 (API 8)

### Usage
Earl is available on `jcenter`. Just add a dependency in your `build.gradle` file:

`compile 'com.einmalfel:earl:1.2.0'`

Simple example:
```
InputStream inputStream = new URL(link).openConnection().getInputStream();
Feed feed = EarlParser.parseOrThrow(inputStream, 0);
Log.i(TAG, "Processing feed: " + feed.getTitle());
for (Item item : feed.getItems()) {
  String title = item.getTitle();
  Log.i(TAG, "Item title: " + (title == null ? "N/A" : title));
}
```

More complex example:
```
/** @return a set of keywords assigned to all items of given feed */
Set<String> getFeedKeywords(URL feedLink) {
  InputStream inputStream = new URL(feedLink).openConnection().getInputStream();
  Feed feed = EarlParser.parseOrThrow(inputStream, 0);

  // media and itunes RSS extensions allow to assign keywords to feed items
  if (RSSFeed.class.isInstance(feed)) {
    RSSFeed rssFeed = (RSSFeed) feed;
    for (RSSItem item : rssFeed.items) {
      if (item.itunes != null) {
        result.addAll(item.itunes.keywords);
      }
      if (item.media != null) {
        result.addAll(item.media.keywords);
      }
    }
  }

  return result;
}
```

### License
Earl is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License (LGPL) as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

Earl is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See included copy of GNU LGPLv3.0 for more details. Alternatively, you can find its text at http://www.gnu.org/licenses/lgpl-3.0.txt.

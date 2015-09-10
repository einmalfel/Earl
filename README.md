### Features
 * Earl parses both Atom and RSS 1.0/2.0
 * Supports RSS extensions (Itunes and Media RSS for now)
 * It's lightweight. There is nothing there but parser. The only dependency it has is android annotations
 * Parser produces easy-to-use immutable POJOs
 * Runs on android versions starting from Android 2.2 (API 8)

### Usage
Earl is available on `jcenter`. Just add a dependency in your `build.gradle` file:

`compile 'com.einmalfel:earl:1.0.0'`

Simle example:
```
InputStream inputStream = new URL(link).openConnection().getInputStream();
Feed feed = EarlParser.parseOrThrow(inputStream, 0);
Log.i(TAG, "Processing feed: " + feed.getTitle());
for (Item item : feed.getItems()) {
  String title = item.getTitle();
  Log.i(TAG, "Item title: " + (title == null ? "N/A" : title));
}
```

More coplex example:
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

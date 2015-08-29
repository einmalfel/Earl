package com.einmalfel.earl;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.net.URL;
import java.util.Date;
import java.util.List;

public interface Item {
  @Nullable
  URL getLink();

  @Nullable
  Date getPublicationDate();

  @Nullable
  String getTitle();

  @Nullable
  String getDescription();

  @Nullable
  URL getImageURL();

  @Nullable
  String getAuthor();

  @NonNull
  List<? extends Enclosure> getEnclosures();
}

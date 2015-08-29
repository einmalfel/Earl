package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.net.URL;
import java.util.Date;
import java.util.List;

public interface Feed {
  @Nullable
  URL getLink();

  @Nullable
  Date getPublicationDate();

  @Nullable
  String getTitle();

  @Nullable
  String getDescription();

  @Nullable
  String getCopyright();

  @Nullable
  URL getImageURL();

  @Nullable
  String getAuthor();

  @NonNull
  List<? extends Item> getItems();
}

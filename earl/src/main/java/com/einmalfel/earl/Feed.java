package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;
import java.util.List;

public interface Feed {
  @Nullable
  String getLink();

  @Nullable
  Date getPublicationDate();

  @NonNull
  String getTitle();

  @Nullable
  String getDescription();

  @Nullable
  String getCopyright();

  @Nullable
  String getImageLink();

  @Nullable
  String getAuthor();

  @NonNull
  List<? extends Item> getItems();
}

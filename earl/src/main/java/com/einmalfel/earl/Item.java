package com.einmalfel.earl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Date;
import java.util.List;

public interface Item {
  @Nullable
  String getLink();

  @Nullable
  Date getPublicationDate();

  @Nullable
  String getTitle();

  @Nullable
  String getDescription();

  @Nullable
  String getImageLink();

  @Nullable
  String getAuthor();

  @Nullable
  String getId();

  @NonNull
  List<? extends Enclosure> getEnclosures();
}

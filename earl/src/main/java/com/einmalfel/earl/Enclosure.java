package com.einmalfel.earl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface Enclosure {
  @NonNull
  String getLink();

  @Nullable
  Integer getLength();

  @Nullable
  String getType();
}

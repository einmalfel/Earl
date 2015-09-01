package com.einmalfel.earl;

import android.support.annotation.Nullable;

public interface Enclosure {
  @Nullable
  String getLink();

  @Nullable
  Integer getLength();

  @Nullable
  String getType();
}

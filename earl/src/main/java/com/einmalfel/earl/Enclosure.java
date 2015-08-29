package com.einmalfel.earl;

import android.support.annotation.Nullable;

import java.net.URL;

public interface Enclosure {
  @Nullable
  URL getURL();

  @Nullable
  Integer getLength();

  @Nullable
  String getType();
}

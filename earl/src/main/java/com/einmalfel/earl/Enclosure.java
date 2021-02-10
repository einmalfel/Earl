package com.einmalfel.earl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface Enclosure {
	@NonNull
	String getLink();

	@Nullable
	Integer getLength();

	@Nullable
	String getType();
}

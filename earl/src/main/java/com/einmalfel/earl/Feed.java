package com.einmalfel.earl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Date;
import java.util.List;

public interface Feed {
	@Nullable
	String getLink();

	@Nullable
	Date getPublicationDate();

	@Nullable
	Date getUpdatedDate();

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

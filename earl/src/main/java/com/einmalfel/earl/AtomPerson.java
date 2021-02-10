package com.einmalfel.earl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URI;

public final class AtomPerson extends AtomCommonAttributes {
	private static final String TAG = "Earl.AtomPerson";

	@NonNull
	public final String name;
	@Nullable
	public final URI uri;
	@Nullable
	public final String email;

	@NonNull
	static AtomPerson read(@NonNull XmlPullParser parser)
	throws XmlPullParserException, IOException {
		final AtomCommonAttributes atomCommonAttributes = new AtomCommonAttributes(parser);
		String name = null;
		URI uri = null;
		String email = null;
		while (parser.nextTag() == XmlPullParser.START_TAG) {
			if (Utils.ATOM_NAMESPACE.equalsIgnoreCase(parser.getNamespace())) {
				switch (parser.getName()) {
					case "name":
						name = parser.nextText();
						break;
					case "uri":
						uri = Utils.tryParseUri(parser.nextText());
						break;
					case "email":
						email = Utils.nonNullString(parser.nextText());
						break;
					default:
						Log.w(TAG, "Unexpected tag " + parser.getName() + " in atom person item");
						Utils.skipTag(parser);
				}
			} else {
				Log.w(TAG, "Unknown namespace in Atom person item " + parser.getNamespace());
				Utils.skipTag(parser);
			}
		}
		return new AtomPerson(atomCommonAttributes, Utils.nonNullString(name), uri, email);
	}

	public AtomPerson(@Nullable AtomCommonAttributes atomCommonAttributes, @NonNull String name,
	                  @Nullable URI uri, @Nullable String email) {
		super(atomCommonAttributes);
		this.name = name;
		this.uri = uri;
		this.email = email;
	}
}

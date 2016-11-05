package com.einmalfel.earl.test;

import static android.support.test.InstrumentationRegistry.getContext;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import android.support.annotation.NonNull;

import com.einmalfel.earl.EarlParser;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;
import java.util.zip.DataFormatException;

@RunWith(Parameterized.class)
public class EarlParserTest {
  @NonNull
  private final String sampleXmlAsset;
  @NonNull
  private final String referenceJsonAsset;

  public EarlParserTest(@NonNull String sampleXmlAsset,
                        @NonNull String referenceJsonAsset) {
    this.sampleXmlAsset = sampleXmlAsset;
    this.referenceJsonAsset = referenceJsonAsset;
  }

  @Parameterized.Parameters
  public static Collection<String[]> fixtureData() {
    return Arrays.asList(new String[][] {
        {"samples/radio-t-pruned.xml", "references/radio-t-pruned.json"},
        {"samples/blogtalkradio.xml", "references/blogtalkradio.json"},
        {"samples/CBC news.xml", "references/CBC news.json"},
        {"samples/NPR news.xml", "references/NPR news.json"},
        {"samples/atom podcast.xml", "references/atom podcast.json"},
        {"samples/media-rss.xml", "references/media-rss.json"},
        {"samples/iso8601dates.xml", "references/iso8601dates.json"},
        });
  }

  @Test
  public void doTest()
      throws IOException, XmlPullParserException, DataFormatException {
    InputStream sampleXmlStream = getContext().getAssets().open(sampleXmlAsset);
    InputStream referenceJsonStream = getContext().getAssets().open(referenceJsonAsset);
    Scanner scanner = new Scanner(referenceJsonStream, "UTF-8");
    try {
      assertThat(objectToJson(EarlParser.parseOrThrow(sampleXmlStream, 0)),
                 is(scanner.useDelimiter("\\A").next()));
    } finally {
      scanner.close();
    }
  }

  @NonNull
  private static String objectToJson(@NonNull Object object) throws JsonProcessingException {
    return new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
                             .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
                             .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                             .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
                             .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
                             .writeValueAsString(object);
  }
}

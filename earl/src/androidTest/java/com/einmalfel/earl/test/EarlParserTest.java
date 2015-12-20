package com.einmalfel.earl.test;

import android.test.AndroidTestCase;
import android.util.Log;

import com.einmalfel.earl.EarlParser;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.InputStream;
import java.util.Scanner;

public class EarlParserTest extends AndroidTestCase {
  private static final String TAG = "Earl.EarlParserTest";

  public void testRadioT() throws Exception {
    InputStream sample = getContext().getAssets().open("samples/radio-t-pruned.xml");
    InputStream reference = getContext().getAssets().open("references/radio-t-pruned.json");
    assertEquals(
        objectToJson(EarlParser.parseOrThrow(sample, 0)),
        new Scanner(reference, "UTF-8").useDelimiter("\\A").next());
  }

  public void testBlogTalkRadio() throws Exception {
    InputStream sample = getContext().getAssets().open("samples/blogtalkradio.xml");
    InputStream reference = getContext().getAssets().open("references/blogtalkradio.json");
    assertEquals(
        objectToJson(EarlParser.parseOrThrow(sample, 0)),
        new Scanner(reference, "UTF-8").useDelimiter("\\A").next());
  }

  public void testCBCNews() throws Exception {
    InputStream sample = getContext().getAssets().open("samples/CBC news.xml");
    InputStream reference = getContext().getAssets().open("references/CBC news.json");
    assertEquals(
        objectToJson(EarlParser.parseOrThrow(sample, 0)),
        new Scanner(reference, "UTF-8").useDelimiter("\\A").next());
  }

  public void testNPRNews() throws Exception {
    InputStream sample = getContext().getAssets().open("samples/NPR news.xml");
    InputStream reference = getContext().getAssets().open("references/NPR news.json");
    assertEquals(
        objectToJson(EarlParser.parseOrThrow(sample, 0)),
        new Scanner(reference, "UTF-8").useDelimiter("\\A").next());
  }

  public void testAtomPodcast() throws Exception {
    InputStream sample = getContext().getAssets().open("samples/atom podcast.xml");
    InputStream reference = getContext().getAssets().open("references/atom podcast.json");
    assertEquals(
        objectToJson(EarlParser.parseOrThrow(sample, 0)),
        new Scanner(reference, "UTF-8").useDelimiter("\\A").next());
  }

  public void testMediaRSS() throws Exception {
    InputStream sample = getContext().getAssets().open("samples/media-rss.xml");
    InputStream reference = getContext().getAssets().open("references/media-rss.json");
    assertEquals(
        objectToJson(EarlParser.parseOrThrow(sample, 0)),
        new Scanner(reference, "UTF-8").useDelimiter("\\A").next());
  }

  private String objectToJson(Object object) throws JsonProcessingException {
    return new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
                             .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
                             .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                             .writeValueAsString(object);
  }
}

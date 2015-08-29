package com.einmalfel.earl.test;

import android.test.AndroidTestCase;
import android.util.Log;

import com.einmalfel.earl.EarlParser;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.util.Scanner;

public class EarlParserTest extends AndroidTestCase {
  private static final String TAG = "E.TST";

  public void testRadioT() throws Exception {
    InputStream sample = getContext().getAssets().open("samples/radio-t-pruned.xml");
    InputStream reference = getContext().getAssets().open("references/radio-t-pruned.json");
    assertEquals(
        new GsonBuilder().setPrettyPrinting().create().toJson(EarlParser.parseOrThrow(sample, 0)),
        new Scanner(reference, "UTF-8").useDelimiter("\\A").next());
  }
}

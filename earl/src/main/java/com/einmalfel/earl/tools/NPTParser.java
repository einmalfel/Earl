package com.einmalfel.earl.tools;

import androidx.annotation.NonNull;

import java.text.ParseException;

/**
 * copied from: https://github.com/google/gdata-java-client/blob/master/java/src/com/google/gdata/data/media/mediarss/NormalPlayTime.java
 */

/**
 * Parser class for a NormalPlayTime that supports both time representations.
 */
public class NPTParser {
  private final String text;
  private final int length;
  private int currentIndex;
  /**
   * Current character, 0 when the end is reached.
   */
  private char current;

  private static final char EOF = '\0';

  public NPTParser(@NonNull String text) {
    this.text = text;
    length = text.length();
    currentIndex = -1;
    next();
  }

  public long parse() throws ParseException {
    long ms;
    int first = parseNumber();

    if (current == ':') {
      next();
      long minutes = parseNumber();
      assertCurrentIs(':');
      next();
      long seconds = parseNumber();
      ms = ((((first * 60L) + minutes) * 60L) + seconds) * 1000L;
    } else {
      ms = first * 1000L;
    }
    if (current == '.') {
      next();
      int exp = 100;
      for (int i = 0; i <= 3 && isDigit(); next(), i++, exp /= 10) {
        ms += (long) exp * digitValue();
      }
      // Ignore extra fraction which can't be stored
      parseNumber();
    }
    assertCurrentIs(EOF);
    return ms;
  }

  private int parseNumber() {
    int retval;
    for (retval = 0; isDigit(); next()) {
      retval *= 10;
      retval += digitValue();
    }
    return retval;
  }

  private int digitValue() {
    return current - '0';
  }

  private boolean isDigit() {
    return current >= '0' && current <= '9';
  }

  private void assertCurrentIs(char character) throws ParseException {
    if (character != current) {
      throw new ParseException("Unexpected character", currentIndex);
    }
  }

  private void next() {
    currentIndex++;
    current = currentIndex >= length ? EOF : text.charAt(currentIndex);
  }
}

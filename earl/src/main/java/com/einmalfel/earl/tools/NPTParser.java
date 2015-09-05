package com.einmalfel.earl.tools;

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

  public NPTParser(String text) {
    this.text = text;
    this.length = text.length();
    currentIndex = -1;
    next();
  }

  public long parse() throws ParseException {
    long ms;
    int first = parseNumber();

    if (current == ':') {
      int hours = first;
      next();
      long minutes = parseNumber();
      assertCurrentIs(':');
      next();
      long seconds = parseNumber();
      ms = ((((hours * 60l) + minutes) * 60l) + seconds) * 1000l;
    } else {
      ms = first * 1000l;
    }
    if (current == '.') {
      next();
      int exp = 100;
      for (int i = 0; i <= 3 && isDigit(); next(), i++, exp /= 10) {
        ms += exp * digitValue();
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

  private void assertCurrentIs(char c) throws ParseException {
    if (c != current) {
      throw new ParseException("Unexpected character", currentIndex);
    }
  }

  private void next() {
    currentIndex++;
    if (currentIndex >= length) {
      current = EOF;
    } else {
      current = text.charAt(currentIndex);
    }
  }
}
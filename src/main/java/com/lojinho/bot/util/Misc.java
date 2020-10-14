package com.lojinho.bot.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Misc {
  private static final String[] numberToEmote = { "\u0030\u20E3", "\u0031\u20E3", "\u0032\u20E3", "\u0033\u20E3",
      "\u0034\u20E3", "\u0035\u20E3", "\u0036\u20E3", "\u0037\u20E3", "\u0038\u20E3", "\u0039\u20E3", "\uD83D\uDD1F" };
  private final static HashSet<String> fuzzyTrue = new HashSet<>(
      Arrays.asList("yea", "yep", "yes", "true", "ja", "y", "t", "1", "check"));
  private final static HashSet<String> fuzzyFalse = new HashSet<>(
      Arrays.asList("no", "false", "nope", "nein", "nee", "n", "f", "0"));
  private final static Pattern patternGuildEmote = Pattern.compile("<:.*:(\\d+)>");

  public static boolean isGuildEmote(String emote) {
    return patternGuildEmote.matcher(emote).matches();
  }

  public static String getGuildEmoteId(String emote) {
    Matcher matcher = patternGuildEmote.matcher(emote);
    if (matcher.find()) {
      System.out.println(matcher.group(1));
      return matcher.group(1);
    }
    if (emote.matches("^\\d+$")) {
      return emote;
    }
    return null;
  }

  /**
   * whether a string can fuzzily considered true
   *
   * @param text the string
   * @return true if it can be considered true
   */
  public static boolean isFuzzyTrue(String text) {
    return text != null && fuzzyTrue.contains(text);
  }

  /**
   * whether a string can fuzzily considered true
   *
   * @param text the string to check
   * @return true if it can be considered false
   */
  public static boolean isFuzzyFalse(String text) {
    return text != null && fuzzyFalse.contains(text);
  }

  /**
   * Converts a numer to an emoji
   *
   * @param number number <= 10
   * @return emoji for that number or :x: if not found
   */
  public static String numberToEmote(int number) {
    if (number >= 0 && number < numberToEmote.length) {
      return numberToEmote[number];
    }
    return ":x:";
  }

  public static String emoteToNumber(String emote) {
    for (int i = 0; i < numberToEmote.length; i++) {
      if (numberToEmote[i].equals(emote)) {
        return "" + i;
      }
    }
    return "0";
  }
}

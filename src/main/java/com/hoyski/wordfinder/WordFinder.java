package com.hoyski.wordfinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class WordFinder
{

  private static final int MAX_MAX_WORDS_TO_RETURN = 1000;

  private List<String>     dictionary;

  public WordFinder()
  {
    loadDictionary();
  }

  public FoundWords findWords(String c, int minimumWordLength, String pattern, int indexOfFirst,
      int maxToReturn)
  {
    FoundWords foundWords = new FoundWords();

    foundWords.setFoundWords(new ArrayList<String>());
    foundWords.setTotalMatches(0);
    foundWords.setIndexOfFirst(indexOfFirst);

    if (minimumWordLength < 1)
    {
      minimumWordLength = 1;
    }

    if (c == null || c.length() == 0 || c.length() < minimumWordLength)
    {
      // Nothing can match. Return an empty list
      return foundWords;
    }

    if (maxToReturn < 1 || maxToReturn > MAX_MAX_WORDS_TO_RETURN)
    {
      maxToReturn = MAX_MAX_WORDS_TO_RETURN;
    }

    validatePattern(pattern, c);

    if (pattern != null)
    {
      pattern = pattern.toUpperCase();
      if (minimumWordLength < pattern.length())
      {
        minimumWordLength = pattern.length();
      }
    }

    c = c.toUpperCase();

    for (String dictionaryWord : dictionary)
    {
      if (dictionaryWord.length() >= minimumWordLength && wordContainedInChars(dictionaryWord, c))
      {
        if (matchesPattern(pattern, dictionaryWord))
        {
          if (foundWords.getTotalMatches() >= indexOfFirst
              && foundWords.getTotalMatches() < indexOfFirst + maxToReturn)
          {
            foundWords.getFoundWords().add(dictionaryWord);
          }

          foundWords.setTotalMatches(foundWords.getTotalMatches() + 1);
        }
      }
    }

    // foundWords.getFoundWords().sort(new LengthFirstComparator());

    return foundWords;
  }

  /**
   * Ensure that the pattern is valid. Throw an exception if not
   */
  private void validatePattern(String pattern, String characters)
  {
    if (pattern == null)
    {
      // No pattern specified. All good
      return;
    }

    pattern = pattern.toUpperCase();

    if (pattern.length() == 0 || pattern.length() > characters.length())
    {
      throw new IllegalArgumentException("Pattern is longer than the number of characters");
    }

    for (int i = 0; i < pattern.length(); ++i)
    {
      if (pattern.charAt(i) != '_' && characters.indexOf(pattern.charAt(i)) == -1)
      {
        throw new IllegalArgumentException(
            "Invalid pattern. Must contain only underscores or letters from the input string");
      }
    }
  }

  private boolean matchesPattern(String pattern, String candidateWord)
  {
    if (pattern == null)
    {
      // Not matching on a pattern so everything matches
      return true;
    }

    if (candidateWord.length() != pattern.length())
    {
      return false;
    }

    for (int i = 0; i < pattern.length(); ++i)
    {
      if (pattern.charAt(i) != '_' && pattern.charAt(i) != candidateWord.charAt(i))
      {
        return false;
      }
    }

    // If made it here then all of the non-wildcard characters match
    return true;
  }

  private void loadDictionary()
  {
    String word;

    dictionary = new ArrayList<>();

    try (BufferedReader br = new BufferedReader(
        new InputStreamReader(this.getClass().getResourceAsStream("/words.txt"))))
    {
      while ((word = br.readLine()) != null)
      {
        if (word.toLowerCase().equals(word)) {
          // 'word' is all lowercase. Add it to to the dictionary
          dictionary.add(word.toUpperCase());
        }
      }


    }
    catch (IOException e)
    {
      throw new RuntimeException("Error loading words.txt", e);
    }

    dictionary.sort(new LengthFirstComparator());
  }

  /**
   * Returns {@code true} if all of the letters in {@code word} are contained in
   * {@code chars}. For repeated letters in {@code word} at least as many
   * occurrences must exist in {@code chars}. Otherwise, return {@code false}.
   * 
   * @param word
   * @param chars
   * @return
   */
  public static boolean wordContainedInChars(String word, String chars)
  {
    for (int i = 0; i < word.length(); ++i)
    {
      if (countChars(word, word.charAt(i)) > countChars(chars, word.charAt(i)))
      {
        return false;
      }
    }

    // If made it this far then word is contained in chars
    return true;
  }

  private static int countChars(String str, char charToCount)
  {
    int numChars = 0;

    for (int i = 0; i < str.length(); ++i)
    {
      if (str.charAt(i) == charToCount)
      {
        numChars++;
      }
    }

    return numChars;
  }
}

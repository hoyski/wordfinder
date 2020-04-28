package com.hoyski.wordfinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hoyski.statistics.Combinator;
import com.hoyski.statistics.Permutator;

public class WordFinder
{
  private Set<String> dictionary;

  /**
   * WordFinderContext contains all of the state variables used during the process
   * of finding words
   */
  private class WordFinderContext
  {
    static final int      MAX_CHARS_ALLOWED    = 13;
    static final int      DEFAULT_MIN_WORD_LEN = 3;

    List<Character>       characters;
    String                pattern;
    int                   curWordLen;
    Combinator<Character> combinator;
    Permutator<Character> permutator;
    Set<String>           foundWordsSet;
    List<String>          foundWords;

    long                  numWordsChecked;

    public WordFinderContext(String c, int minimumWordLength, String pattern)
    {
      // Validate the parameters
      if (c == null || c.length() == 0)
      {
        throw new IllegalArgumentException("Must provide at least 1 character");
      }

      if (c.length() > MAX_CHARS_ALLOWED)
      {
        throw new IllegalArgumentException(
            "Cannot send more than " + MAX_CHARS_ALLOWED + " characters");
      }

      populateCharactersList(c, this);

      for (Character curChar : characters)
      {
        if (!(curChar >= 'A' && curChar <= 'Z'))
        {
          throw new IllegalArgumentException("Only letters allowed");
        }
      }

      if (minimumWordLength <= 0)
      {
        minimumWordLength = DEFAULT_MIN_WORD_LEN;
      }
      else
      {
        if (minimumWordLength > c.length())
        {
          throw new IllegalArgumentException(
              "Minimum word length must not exceed the number of characters");
        }
      }

      this.pattern = pattern;

      curWordLen = minimumWordLength;

      validatePattern();

      if (characters.size() < curWordLen)
      {
        throw new IllegalArgumentException("Insufficent number of characters received");
      }

      combinator = new Combinator<Character>(characters, curWordLen);

      permutator = new Permutator<Character>(combinator.nextCombination());

      foundWordsSet = new HashSet<>();

      foundWords = new ArrayList<>();

      numWordsChecked = 0;
    }

    /**
     * Ensure that the pattern is valid. Throw an exception if not
     */
    private void validatePattern()
    {
      if (pattern == null)
      {
        // No pattern specified. All good
        return;
      }

      pattern = pattern.toUpperCase();

      if (pattern.length() == 0 || pattern.length() > characters.size())
      {
        throw new IllegalArgumentException("Pattern is longer than the number of characters");
      }

      for (int i = 0; i < pattern.length(); ++i)
      {
        if (pattern.charAt(i) != '_' && !characters.contains(pattern.charAt(i)))
        {
          throw new IllegalArgumentException(
              "Invalid pattern. Must contain only underscores or letters from the input string");
        }
      }

      // Set the minimum word length to the pattern's length to avoid finding
      // words that are too short
      curWordLen = pattern.length();
    }
  }

  public WordFinder()
  {
    loadDictionary();
  }

  public List<String> findWords(String c)
  {
    return findWords(c, 1, null);
  }

  public List<String> findWords(String c, int minimumLength)
  {
    return findWords(c, minimumLength, null);
  }

  public List<String> findWords(String c, String pattern)
  {
    return findWords(c, 1, pattern);
  }

  public List<String> findWords(String c, int minimumWordLength, String pattern)
  {
    WordFinderContext context = new WordFinderContext(c, minimumWordLength, pattern);

    String            word;

    if (context.characters.size() < context.curWordLen)
    {
      // Not enough characters
      return context.foundWords;
    }

    while ((word = findNextWord(context)) != null)
    {
      context.foundWordsSet.add(word);
    }

    for (String foundWord : context.foundWordsSet)
    {
      if (matchesPattern(context, foundWord))
      {
        context.foundWords.add(foundWord);
      }
    }

    context.foundWords.sort(new Comparator<String>()
    {
      public int compare(String o1, String o2)
      {
        if (o1.length() != o2.length())
        {
          // Sort shorter Strings before longer
          return o1.length() - o2.length();
        }
        else
        {
          // Sort equal length Strings alphabetically
          return o1.compareTo(o2);
        }
      };
    });

    System.out.println("Checked " + context.numWordsChecked + " potential words and found "
        + context.foundWords.size());

    return context.foundWords;
  }

  private String findNextWord(WordFinderContext context)
  {
    String          word = null;
    String          potentialWord;
    boolean         done = false;
    List<Character> potentialWordChars;

    // Handle special case of minimum word length being longer than number of
    // supplied characters
    if (context.curWordLen > context.characters.size())
    {
      return null;
    }

    while (word == null && !done)
    {
      // Check permutations until the permutator is exhausted or a word is found
      while (word == null && (potentialWordChars = context.permutator.nextPermutation()) != null)
      {
        potentialWord = characterListToString(potentialWordChars);

        if (dictionary.contains(potentialWord))
        {
          word = potentialWord;
        }

        context.numWordsChecked++;
      }

      if (word == null)
      {
        // Exhausted the permutator without finding a word. Try the next combination, if
        // any
        List<Character> nextCombination = context.combinator.nextCombination();

        if (nextCombination == null)
        {
          // No more combinations. Build a new combinator for the next higher number of
          // characters, if possible
          context.curWordLen++;

          if (context.curWordLen > context.characters.size())
          {
            // Exhausted the last combinator
            done = true;
          }
          else
          {
            context.combinator = new Combinator<Character>(context.characters, context.curWordLen);
            context.permutator = new Permutator<Character>(context.combinator.nextCombination());
          }
        }
        else
        {
          context.permutator = new Permutator<Character>(nextCombination);
        }
      }
    }

    return word;
  }

  private void populateCharactersList(String c, WordFinderContext context)
  {
    context.characters = new ArrayList<>(c.length());

    c = c.toUpperCase();

    for (int i = 0; i < c.length(); ++i)
    {
      context.characters.add(new Character(c.charAt(i)));
    }
  }

  private boolean matchesPattern(WordFinderContext context, String candidateWord)
  {
    if (context.pattern == null)
    {
      // Not matching on a pattern so everything matches
      return true;
    }

    if (candidateWord.length() != context.pattern.length())
    {
      return false;
    }

    for (int i = 0; i < context.pattern.length(); ++i)
    {
      if (context.pattern.charAt(i) != '_' && context.pattern.charAt(i) != candidateWord.charAt(i))
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

    dictionary = new HashSet<>();

    try (BufferedReader br = new BufferedReader(
        new InputStreamReader(this.getClass().getResourceAsStream("/words.txt"))))
    {
      while ((word = br.readLine()) != null)
      {
        dictionary.add(word.toUpperCase());
      }
    }
    catch (IOException e)
    {
      throw new RuntimeException("Error loading words.txt", e);
    }
  }

  private String characterListToString(List<Character> wordChars)
  {
    StringBuilder wordBuilder = new StringBuilder();

    for (Character wordChar : wordChars)
    {
      wordBuilder.append(wordChar);
    }

    return wordBuilder.toString();
  }
}

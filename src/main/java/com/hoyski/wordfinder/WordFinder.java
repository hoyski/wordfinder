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
  private List<Character> characters;
  private int curWordLen;
  private Set<String> dictionary;
  private Combinator<Character> combinator;
  private Permutator<Character> permutator;
  List<String> foundWords;
  
  public static void main(String[] args) throws InterruptedException
  {
    if (args.length == 0 || args.length > 2)
    {
      printUsageAndExit(null);
    }
    
    int minimumWordLength = 3; // Default minimum word length
    
    if (args.length == 2)
    {
      try
      {
        minimumWordLength = Integer.parseInt(args[1]);
      }
      catch (NumberFormatException e)
      {
        printUsageAndExit("Invalid minimum word length: " + args[1]);
      }
    }
    
    try
    {
      long start = System.currentTimeMillis();
      
      WordFinder wordFinder = new WordFinder(args[0], minimumWordLength);
      
      List<String> foundWords = wordFinder.getFoundWords();
      
      long end = System.currentTimeMillis();
      
      for (String word : foundWords)
      {
        System.out.println(word);
      }
      
      System.out.println();
      System.out.println(String.format("Found %d words in %d ms", foundWords.size(), (end - start)));
    }
    catch (Exception e)
    {
      System.out.println(e.getMessage());
    }
  }
  
  public static void printUsageAndExit(String message)
  {
    if (message != null)
    {
      System.out.println(message);
      System.out.println();
    }
    
    System.out.println("Usage: java com.hoyski.wordfinder.WordFinder <characters> [minimum word length]");
    
    System.exit(-1);
  }
  
  public WordFinder(String c, int minimumWordLength) throws IOException
  {
    // Validate the input parameters
    if (c == null || c.length() == 0)
    {
      throw new IllegalArgumentException("Must provide at least 1 character");
    }
    
    if (minimumWordLength < 1 || minimumWordLength > c.length())
    {
      throw new IllegalArgumentException("Minimum word length must be between 1 and the number of characters");
    }

    loadDictionary();

    curWordLen = minimumWordLength;

    populateCharactersList(c);

    combinator = new Combinator<Character>(characters, curWordLen);
    
    permutator = new Permutator<Character>(combinator.nextCombination());
    
    Set<String> foundWordsSet = new HashSet<>();
    
    String word;
    while ((word = findNextWord()) != null)
    {
      foundWordsSet.add(word);
    }
    
    foundWords = new ArrayList<>();
    
    for (String foundWord : foundWordsSet)
    {
      foundWords.add(foundWord);
    }
    
    foundWords.sort(new Comparator<String>()
    {
      public int compare(String o1, String o2) {
        if (o1.length() != o2.length())
        {
          // Sort shorter Strings before longer
          return o1.length() - o2.length();
        }
        else
        {
          // Sort equal length String alphabetically
          return o1.compareTo(o2);
        }
      };
    });
  }
  
  public List<String> getFoundWords()
  {
    return foundWords;
  }
  
  private String findNextWord()
  {
    String word = null;
    String potentialWord;
    boolean done = false;
    List<Character> potentialWordChars;
    
    while (word == null && !done)
    {
      // Check permutations until the permutator is exhausted or a word is found
      while (word == null && (potentialWordChars = permutator.nextPermutation()) != null)
      {
        potentialWord = characterListToString(potentialWordChars);
        
        if (dictionary.contains(potentialWord))
        {
          word = potentialWord;
        }
      }
      
      if (word == null)
      {
        // Exhausted the permutator without finding a word. Try the next combination, if any
        List<Character> nextCombination = combinator.nextCombination();
        
        if (nextCombination == null)
        {
          // No more combinations. Build a new combinator for the next higher number of characters, if possible
          curWordLen++;
          
          if (curWordLen > characters.size())
          {
            // Exhausted the last combinator
            done = true;
          }
          else
          {
            combinator = new Combinator<Character>(characters, curWordLen);
            permutator = new Permutator<Character>(combinator.nextCombination());
          }
        }
        else
        {
          permutator = new Permutator<Character>(nextCombination);
        }
      }
    }
    
    return word;
  }

  private void populateCharactersList(String c)
  {
    characters = new ArrayList<>();
    
    c = c.toLowerCase();
    
    for (int i = 0; i < c.length(); ++i)
    {
      characters.add(new Character(c.charAt(i)));
    }
  }
  
  private void loadDictionary() throws IOException
  {
    String word;
    
    dictionary = new HashSet<>();
    
    try (BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/words.txt"))))
    {
      while ((word = br.readLine()) != null)
      {
        dictionary.add(word.toLowerCase());
      }
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

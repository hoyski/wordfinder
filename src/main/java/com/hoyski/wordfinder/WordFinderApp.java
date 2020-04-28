package com.hoyski.wordfinder;

import java.util.List;

public class WordFinderApp
{

  public static void main(String[] args) throws InterruptedException
  {
    if (args.length == 0 || args.length > 2)
    {
      printUsageAndExit(null);
    }

    String pattern           = null; // Default to no pattern match
    int    minimumWordLength = 3;    // Default minimum word length

    if (args.length == 2)
    {
      // Second argument is either a minimum length or pattern to match
      try
      {
        minimumWordLength = Integer.parseInt(args[1]);
      }
      catch (NumberFormatException e)
      {
        // Not a valid number so assume it's a pattern
        pattern = args[1];
      }
    }

    try
    {
      long         start      = System.currentTimeMillis();

      WordFinder   wordFinder = new WordFinder();

      List<String> foundWords = wordFinder.findWords(args[0], minimumWordLength, pattern);

      long         end        = System.currentTimeMillis();

      for (String word : foundWords)
      {
        System.out.println(word);
      }

      System.out.println();
      System.out
          .println(String.format("Found %d words in %d ms", foundWords.size(), (end - start)));
    }
    catch (Exception e)
    {
      System.out.println("Caught a " + e.getClass().getName());
      e.printStackTrace(System.out);
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

    System.out.println(
        "Usage: java com.hoyski.wordfinder.WordFinderApp <characters> [minimum word length | pattern to match]");

    System.exit(-1);
  }
}

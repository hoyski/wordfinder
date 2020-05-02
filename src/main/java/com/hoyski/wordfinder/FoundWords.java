package com.hoyski.wordfinder;

import java.util.List;

public class FoundWords
{
  private List<String> foundWords;
  private int          indexOfFirst;
  private int          totalMatches;

  public List<String> getFoundWords()
  {
    return foundWords;
  }

  public void setFoundWords(List<String> foundWords)
  {
    this.foundWords = foundWords;
  }

  public int getIndexOfFirst()
  {
    return indexOfFirst;
  }

  public void setIndexOfFirst(int indexOfFirst)
  {
    this.indexOfFirst = indexOfFirst;
  }

  public int getTotalMatches()
  {
    return totalMatches;
  }

  public void setTotalMatches(int totalMatches)
  {
    this.totalMatches = totalMatches;
  }
}

package com.m3958.vertxio.vertxcombo;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RandomFileFinder {
  /**
   * A {@code FileVisitor} that finds all files that match the specified pattern.
   */
  public static class Finder extends SimpleFileVisitor<Path> {

    private final PathMatcher matcher;

    private int[] randomposition;

    private List<Path> selected = new ArrayList<>();

    private int numMatches = 0;

    Finder(String pattern, int number) {
      matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
      createRandomPosition(number);
    }

    // Compares the glob pattern against
    // the file or directory name.
    void find(Path file) {
      Path name = file.getFileName();
      if (name != null && matcher.matches(name)) {
        if (Arrays.binarySearch(randomposition, numMatches) >= 0) {
          selected.add(file);
        }
        numMatches++;
      }
    }

    private void createRandomPosition(int number) {
      randomposition = new int[number];
      for (int idx = 0; idx < number; idx++) {
        randomposition[idx] = (int) (Math.random() * ((2000 - 0) + 1));
      }
      Arrays.sort(randomposition);
      // Min + (int)(Math.random() * ((Max - Min) + 1))
    }

    // Invoke the pattern matching
    // method on each file.
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
      find(file);
      return FileVisitResult.CONTINUE;
    }

    // Invoke the pattern matching
    // method on each directory.
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
      System.err.println(exc);
      return FileVisitResult.CONTINUE;
    }

    public List<Path> getSelected() {
      return selected;
    }

    public void setSelected(List<Path> selected) {
      this.selected = selected;
    }
  }

  private Path basePath;
  private String pattern;
  private int number;

  public RandomFileFinder(String baseDir, String pattern, int number) {
    this.basePath = Paths.get(baseDir);
    this.pattern = pattern;
    this.number = number;
  }

  public RandomFileFinder(Path baseDir, String pattern, int number) {
    this.basePath = baseDir;
    this.pattern = pattern;
    this.number = number;
  }


  private List<Path> toRelative(List<Path> abs) {
    List<Path> relatives = new ArrayList<>();
    for (Path p : abs) {
      relatives.add(basePath.relativize(p));
    }
    return relatives;
  }


  public List<Path> selectSome() throws IOException {
    Finder finder = new Finder(pattern, number);
    Files.walkFileTree(basePath, finder);
    return toRelative(finder.getSelected());
  }

  public static void main(String[] args) throws IOException {
    RandomFileFinder rff = new RandomFileFinder("c:/staticyui", "*.js", 10);
    List<Path> selected = rff.selectSome();
    for (Path s : selected) {
      System.out.println(s.toString());
    }
  }
}

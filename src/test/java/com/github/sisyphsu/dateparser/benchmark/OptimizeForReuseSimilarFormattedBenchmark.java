package com.github.sisyphsu.dateparser.benchmark;

import com.github.sisyphsu.dateparser.DateParser;
import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 2, time = 2)
@BenchmarkMode(Mode.AverageTime)
@Fork(2)
@Measurement(iterations = 3, time = 3)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class OptimizeForReuseSimilarFormattedBenchmark {
  private static final String[] TEXTS;

  static {
    Random random = new Random(123456789l);
    TEXTS = new String[500000];
    for (int i = 0; i < TEXTS.length; i++) {
      TEXTS[i] = String.format("2020-0%d-1%d 00:%d%d:00 UTC",
        random.nextInt(8) + 1,
        random.nextInt(8) + 1,
        random.nextInt(5),
        random.nextInt(9));
    }
  }

  @Benchmark
  public void regularParser() {
    DateParser parser = DateParser.newBuilder().build();
    for (String text : TEXTS) {
      parser.parseDate(text);
    }
  }

  @Benchmark
  public void optimizedForReuseParser() {
    DateParser parser = DateParser.newBuilder().optimizeForReuseSimilarFormatted(true).build();
    for (String text : TEXTS) {
      parser.parseDate(text);
    }
  }
}

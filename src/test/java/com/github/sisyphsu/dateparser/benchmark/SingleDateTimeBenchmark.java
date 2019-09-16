package com.github.sisyphsu.dateparser.benchmark;

import com.github.sisyphsu.dateparser.DateParserUtils;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * Single pattern's datetime parsing benchmark.
 * Benchmark                       Mode  Cnt     Score    Error  Units
 * SingleDateTimeBenchmark.java    avgt    6   654.553 ± 16.703  ns/op
 * SingleDateTimeBenchmark.parser  avgt    6  1680.690 ± 34.214  ns/op
 *
 * @author sulin
 * @since 2019-09-16 10:26:15
 */
@Warmup(iterations = 2, time = 2)
@BenchmarkMode(Mode.AverageTime)
@Fork(2)
@Measurement(iterations = 3, time = 3)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class SingleDateTimeBenchmark {

    private static final String TEXT = "2019-10-01 10:20:30.123456789 +0800";
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS Z");

    @Benchmark
    public void parser() {
        DateParserUtils.parseDateTime(TEXT);
    }

    @Benchmark
    public void java() {
        LocalDateTime.parse(TEXT, FORMAT);
    }

    @Test
    public void test() {
        LocalDateTime date1 = DateParserUtils.parseDateTime(TEXT);
        LocalDateTime date2 = LocalDateTime.parse(TEXT, FORMAT);
        assert date1.equals(date2);
    }

}

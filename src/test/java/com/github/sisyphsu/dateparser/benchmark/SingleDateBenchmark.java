package com.github.sisyphsu.dateparser.benchmark;

import com.github.sisyphsu.dateparser.DateParserUtils;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Single pattern's datetime parsing benchmark.
 * Benchmark               Mode  Cnt     Score    Error  Units
 * SingleBenchmark.java    avgt    6   921.632 ± 12.299  ns/op
 * SingleBenchmark.parser  avgt    6  1553.909 ± 70.664  ns/op
 *
 * @author sulin
 * @since 2019-09-16 10:26:15
 */
@Warmup(iterations = 2, time = 2)
@BenchmarkMode(Mode.AverageTime)
@Fork(2)
@Measurement(iterations = 3, time = 3)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class SingleDateBenchmark {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzzz");

    private static final String TEXT = "2019-10-01 10:20:30 +0800";

    @Benchmark
    public void parser() {
        DateParserUtils.parseDate(TEXT);
    }

    @Benchmark
    public void java() throws ParseException {
        FORMAT.parse(TEXT);
    }

    @Test
    public void test() throws ParseException {
        Date date1 = DateParserUtils.parseDate(TEXT);
        Date date2 = FORMAT.parse(TEXT);
        assert date1.equals(date2);
    }

}

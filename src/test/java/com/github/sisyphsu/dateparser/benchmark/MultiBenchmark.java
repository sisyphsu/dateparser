package com.github.sisyphsu.dateparser.benchmark;

import com.github.sisyphsu.dateparser.DateParserUtils;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Multiple pattern's benchmark
 * Benchmark              Mode  Cnt      Score      Error  Units
 * MultiBenchmark.format  avgt    6  47385.021 ± 1083.649  ns/op
 * MultiBenchmark.parser  avgt    6  22852.113 ±  310.720  ns/op
 *
 * @author sulin
 * @since 2019-09-16 10:40:46
 */
@Warmup(iterations = 2, time = 2)
@BenchmarkMode(Mode.AverageTime)
@Fork(2)
@Measurement(iterations = 3, time = 3)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class MultiBenchmark {

    private static final String[] TEXTS = {
            "2019-10-01",
            "2019-10-01 10:20:30",
            "2019-10-01 10:20:30.123",
            "2019-10-01 10:20:30.123+0800",
            "2019/10/01",
            "2019/10/01 10:20:30",
            "2019/10/01 10:20:30.123",
            "2019/10/01 10:20:30.123+0800",
            "2019年10月01日",
            "2019年10月01日 10:20:30",
            "2019年10月01日 10:20:30.123",
            "2019年10月01日 10:20:30.123+0800",
            "2019.10.01",
            "2019.10.01 10:20:30",
            "2019.10.01 10:20:30.123",
            "2019.10.01 10:20:30.123+0800",
//            "02 January 2018",
//            "Saturday November 10 2012 10:45:42.720+0100",
    };
    private static final String[] PATTERNS = {
            "yyyy-MM-dd",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss.SSS",
            "yyyy-MM-dd HH:mm:ss.SSSZ",
            "yyyy/MM/dd",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy/MM/dd HH:mm:ss.SSS",
            "yyyy/MM/dd HH:mm:ss.SSSZ",
            "yyyy年MM月dd日",
            "yyyy年MM月dd日 HH:mm:ss",
            "yyyy年MM月dd日 HH:mm:ss.SSS",
            "yyyy年MM月dd日 HH:mm:ss.SSSZ",
            "yyyy.MM.dd",
            "yyyy.MM.dd HH:mm:ss",
            "yyyy.MM.dd HH:mm:ss.SSS",
            "yyyy.MM.dd HH:mm:ss.SSSZ",
//            "dd MMMMM yyyy",
//            "EEEEE MMMMM dd yyyy HH:mm:ss.SSSZ",
    };

    private static final List<SimpleDateFormat> FORMATs = new ArrayList<>();

    static {
        for (String pattern : PATTERNS) {
            FORMATs.add(new SimpleDateFormat(pattern));
        }
        Collections.shuffle(FORMATs);
    }

    @Benchmark
    public void parser() {
        for (String text : TEXTS) {
            DateParserUtils.parseDate(text);
        }
    }

    @Benchmark
    public void format() {
        for (String text : TEXTS) {
            parseDate(text);
        }
    }

    @Test
    public void test() {
        for (String text : TEXTS) {
            Date date1 = DateParserUtils.parseDate(text);
            Date date2 = parseDate(text);
            if (!date1.equals(date2)) {
                System.out.println(text);
                System.out.println(date1 + "\t" + date1.getTime());
                System.out.println(date2 + "\t" + date2.getTime());
            }
            assert date1.equals(date2);
        }
    }

    Date parseDate(String text) {
        ParsePosition position = new ParsePosition(0);
        for (SimpleDateFormat format : FORMATs) {
            position.setIndex(0);
            position.setErrorIndex(-1);
            Date date = format.parse(text, position);
            if (position.getErrorIndex() > 0 || position.getIndex() != text.length()) {
                continue;
            }
            return date;
        }
        throw new IllegalArgumentException("Cannot parse: " + text);
    }

}

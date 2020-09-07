# dateparser
 
[![Travis CI](https://travis-ci.org/sisyphsu/dateparser.svg?branch=master)](https://travis-ci.org/sisyphsu/dateparser)
[![codecov](https://codecov.io/gh/sisyphsu/dateparser/branch/master/graph/badge.svg)](https://codecov.io/gh/sisyphsu/dateparser)

[中文文档](https://sulin.me/2019/38Z4HAT.html)

# Introduce

`dateparser` is a smart and high-performance datetime parser library, it supports hundreds of different patterns. 

For better performance and flexibility, 
`dateparser` doesn't use `SimpleDateFormat` or `DateTimeFormatter`, 
but uses [`retree`](https://github.com/sisyphsu/retree-java) to parse the specified `String` into several matched parts, 
and convert different parts to be different properties like `year`, `month`, `day`, `hour`, `minute`, `second`, `zone` etc.

`dateparser` has lots of predefined regular expressions as rules, like:
 + `(?<week>%s)\W*` to match `Monday` as **week**
 + ` ?(?<year>\d{4})$` to match `2019` as **year**
 + `^(?<year>\d{4})(?<month>\d{2})$` to match `201909` as **year** and **month**
 + ` ?(?<hour>\d{1,2}) o’clock\W*` to match `12 o’clock` as **hour**
 + More rules in [`DateParserBuilder.java`](https://github.com/sisyphsu/dateparser/blob/master/src/main/java/com/github/sisyphsu/dateparser/DateParserBuilder.java)

With so many regular expressions, if use `java.util.regex.Pattern` to match them one by one, 
the performance would be a disaster.
So I choice to use `retree`, `retree` could merge lots of regular expressions as one, 
in my opinion, it is more like a tree, which could execute matching quickly and concurrently. 

You can also customize your own parser, by add new rules.

# Install

Add maven dependency: 

```xml
<dependency>
  <groupId>com.github.sisyphsu</groupId>
  <artifactId>dateparser</artifactId>
  <version>1.0.5</version>
</dependency>
```

# Basic Usage

Parse `String` into `Date`, `Calendar`, `LocalDateTime`, `OffsetDateTime`:

```java
Date date = DateParserUtils.parseDate("Mon Jan 02 15:04:05 -0700 2006");
// Tue Jan 03 06:04:05 CST 2006
Calendar calendar = DateParserUtils.parseCalendar("Fri Jul 03 2015 18:04:07 GMT+0100 (GMT Daylight Time)");
// 2015-07-03T17:04:07Z
LocalDateTime dateTime = DateParserUtils.parseDateTime("2019-09-20 10:20:30.12345678 +0200");
// 2019-09-20T16:20:30.123456780
OffsetDateTime offsetDateTime = DateParserUtils.parseOffsetDateTime("2015-09-30 18:48:56.35272715 +0000 UTC");
// 2015-09-30T18:48:56.352727150Z
```

Please notice the `TimeZone` and `ZoneOffset` like `-0700`, it could affect `time`.

# Create new DateParser

Because `DateParser` isn't thread safe, and the `parse` operation is quite fast(about 1us),
so `DateParserUtils` maintains one parser as default, and wrap it with `synchronized`. 

If you want to use it concurrently, you should create new parser like this:

```java
DateParser parser = DateParser.newBuilder().build();
Date date = parser.parseDate("Mon Jan 02 15:04:05 -0700 2006");
// Tue Jan 03 06:04:05 CST 2006
``` 

The `DateParser`'s instance is a little heavy, you should try to reuse it.

# Prefer `MM/dd` or `dd/MM`

For most cases, `dateparser` could recognize which part is **month** and which part is **day**.

But for `MM/dd/yy` and `dd/MM/yy`, it would be confused, 
because most of countries use `dd/MM/yy`, but little of countries use `MM/dd/yy`, which is mainly the USA.

So `dateparser` will use `dd/MM` as priority, but you could change it by:

```java
DateParserUtils.preferMonthFirst(true);
DateParserUtils.parseCalendar("08.03.71");
// 1971-08-03
DateParserUtils.preferMonthFirst(false);
DateParserUtils.parseCalendar("08.03.71");
// 1971-03-08
``` 

Notice: if either number is larger than `12`, then `preferMonthFirst` wouldn't be effective.

# Customize Parser

You could use `DateParserBuilder` to build your own parser, 
and customize new rules to parse different input.

Like add support for `【2019】`, which isn't supported by default:

```java
DateParser parser = DateParser.newBuilder().addRule("【(?<year>\\d{4})】").build();
Calendar calendar = parser.parseCalendar("【1991】");
assert calendar.get(Calendar.YEAR) == 1991;
``` 

The group name `year` is very important, you cannot use other unknown name.

But, you can register new handler to parse the new rule:

```java
DateParser parser = DateParser.newBuilder()
    .addRule("民国(\\d{3})年", (input, matcher, dt) -> {
        int offset = matcher.start(1);
        int i0 = input.charAt(offset) - '0';
        int i1 = input.charAt(offset + 1) - '0';
        int i2 = input.charAt(offset + 2) - '0';
        dt.setYear(i0 * 100 + i1 * 10 + i2 + 1911);
    })
    .build();
Calendar calendar = parser.parseCalendar("民国101年");
assert calendar.get(Calendar.YEAR) == 2012;
```

The `民国101年` represents `101` years after `1911`. 

# Performance

Compared to single `SimpleDateFormat`, the performance of `dateparser`:

```
Benchmark               Mode  Cnt     Score    Error  Units
SingleBenchmark.java    avgt    6   921.632 ± 12.299  ns/op
SingleBenchmark.parser  avgt    6  1553.909 ± 70.664  ns/op
``` 

Compared to single `DateTimeFormatter`, the performance of `dateparser`:

```
Benchmark                       Mode  Cnt     Score    Error  Units
SingleDateTimeBenchmark.java    avgt    6   654.553 ± 16.703  ns/op
SingleDateTimeBenchmark.parser  avgt    6  1680.690 ± 34.214  ns/op
```

So, for String with known format, the `dateparser` is slower.

But if the number of `format` is not single, 
lets increase to `16`, the performance of `dateparser`:

```
Benchmark              Mode  Cnt      Score      Error  Units
MultiBenchmark.format  avgt    6  47385.021 ± 1083.649  ns/op
MultiBenchmark.parser  avgt    6  22852.113 ±  310.720  ns/op
```

`dateparser` is very stable, with increasing of the number of `format`, it has no performance lose.  

You can checkout the source code of benchmark at [there](https://github.com/sisyphsu/dateparser/tree/master/src/test/java/com/github/sisyphsu/dateparser/benchmark). 

# Showcase

There are some examples of datetime format which `dateparser` supports:

```
May 8, 2009 5:57:51 PM                               
oct 7, 1970                                          
oct 7, '70                                           
oct. 7, 1970                                         
oct. 7, 70                                           
Mon Jan  2 15:04:05 2006                             
Mon Jan  2 15:04:05 MST 2006                         
Mon Jan 02 15:04:05 -0700 2006                       
Monday, 02-Jan-06 15:04:05 MST                       
Mon, 02 Jan 2006 15:04:05 MST                        
Tue, 11 Jul 2017 16:28:13 +0200 (CEST)               
Mon, 02 Jan 2006 15:04:05 -0700                      
Thu, 4 Jan 2018 17:53:36 +0000                       
Mon Aug 10 15:44:11 UTC+0100 2015                    
Fri Jul 03 2015 18:04:07 GMT+0100 (GMT Daylight Time)
September 17, 2012 10:09am                         
September 17, 2012 at 10:09am PST-08               
September 17, 2012, 10:10:09                       
October 7, 1970                                    
October 7th, 1970                                  
12 Feb 2006, 19:17                                 
12 Feb 2006 19:17                                  
7 oct 70                                           
7 oct 1970                                         
03 February 2013                                   
1 July 2013                                        
2013-Feb-03                                        
3/31/2014                                          
03/31/2014                                         
08/21/71                                           
8/1/71                                             
4/8/2014 22:05                                     
04/08/2014 22:05                                   
4/8/14 22:05                                       
04/2/2014 03:00:51                                 
8/8/1965 12:00:00 AM                               
8/8/1965 01:00:01 PM                               
8/8/1965 01:00 PM                                  
8/8/1965 1:00 PM                                   
8/8/1965 12:00 AM                                  
4/02/2014 03:00:51                                 
03/19/2012 10:11:59                                
03/19/2012 10:11:59.3186369                        
2014/3/31                                          
2014/03/31                                         
2014/4/8 22:05                                     
2014/04/08 22:05                                   
2014/04/2 03:00:51                                 
2014/4/02 03:00:51                                 
2012/03/19 10:11:59                                
2012/03/19 10:11:59.3186369                        
2014年04月08日                                      
2006-01-02T15:04:05+0000                           
2009-08-12T22:15:09-07:00                          
2009-08-12T22:15:09                                
2009-08-12T22:15:09Z                               
2014-04-26 17:24:37.3186369                        
2012-08-03 18:31:59.257000000                      
2014-04-26 17:24:37.123                            
2013-04-01 22:43                                   
2013-04-01 22:43:22                                
2014-12-16 06:20:00 UTC                            
2014-12-16 06:20:00 GMT                          
2014-04-26 05:24:37 PM                           
2014-04-26 13:13:43 +0800                        
2014-04-26 13:13:43 +0800 +08                    
2014-04-26 13:13:44 +09:00                       
2012-08-03 18:31:59.257000000 +0000 UTC          
2015-09-30 18:48:56.35272715 +0000 UTC           
2015-02-18 00:12:00 +0000 GMT                    
2015-02-18 00:12:00 +0000 UTC                    
2015-02-08 03:02:00 +0300 MSK m=+0.000000001     
2015-02-08 03:02:00.001 +0300 MSK m=+0.000000001 
2017-07-19 03:21:51+00:00
2014-04-26               
2014-04                  
2014                     
2014-05-11 08:20:13,787  
3.31.2014       
03.31.2014      
08.21.71        
2014.03         
2014.03.30      
20140601        
20140722105203  
1332151919      
1384216367189   
1384216367111222
1384216367111222333 
```

Lots of these examples were copied from https://github.com/araddon/dateparse.

# Support

Let me know if you meet any issues when using this library.

Let me know if you need any features that this library hasn't yet.

Pull Request are welcomed.

# License

MIT

package com.github.sisyphsu.dateparser;

/**
 * This class represents the standard specification of rule's handler.
 * It should parse the specified substring to fill some fields of DateTime.
 *
 * @author sulin
 * @since 2019-09-14 14:25:45
 */
@FunctionalInterface
public interface RuleHandler {

    /**
     * Parse substring[from, to) of the specified string
     *
     * @param chars The original string in char[]
     * @param from  Start offset
     * @param to    End offset
     * @param dt    DateTime to accept parsed properties.
     */
    void handle(CharSequence chars, int from, int to, DateBuilder dt);

}

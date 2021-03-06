package com.redhat.cloud.policies.api.model.condition.expression;

import org.hawkular.alerts.api.model.event.Event;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExprValueTest {

    @Test
    public void testListTargetWithWrongOperators() {
        Event event = new Event();
        Map<String, Object> factMap = new HashMap<>();
        List<String> aList = new ArrayList<>(3);
        aList.add("b");
        aList.add("c");
        aList.add("d");
        factMap.put("a", aList);
        event.setFacts(factMap);

//        String expr = "facts.a = 'b'";
//        assertFalse(ExprParser.evaluate(event, expr));

        String expr = "facts.a > 3";
        assertFalse(ExprParser.evaluate(event, expr));
    }

//    @Test
    // NotImplementedYet
    public void matchingArray() {
        Event event = new Event();
        Map<String, Object> factMap = new HashMap<>();
        List<String> aList = new ArrayList<>(3);
        aList.add("b");
        aList.add("c");
        aList.add("d");
        factMap.put("a", aList);
        event.setFacts(factMap);

        String expr = "facts.a = [b, c, d]";
        assertTrue(ExprParser.evaluate(event, expr));

        expr = "facts.a = [b, c]";
        assertFalse(ExprParser.evaluate(event, expr));

        expr = "facts.a = [b, c, a]";
        assertFalse(ExprParser.evaluate(event, expr));

        // Case-insensitive matching must work inside arrays also
        expr = "facts.a = [b, C, d]";
        assertTrue(ExprParser.evaluate(event, expr));
    }

    @Test
    public void containsInString() {
        Event event = new Event();
        Map<String, Object> factMap = new HashMap<>();
        factMap.put("a", "b c");
        event.setFacts(factMap);

        String expr = "facts.a contains \"b\"";
        assertTrue(ExprParser.evaluate(event, expr));

        expr = "facts.a contains ['b', 'c']";
        assertTrue(ExprParser.evaluate(event, expr));

        expr = "facts.a contains ['b']";
        assertTrue(ExprParser.evaluate(event, expr));

        // Case-insensitive
        expr = "facts.a contains ['B']";
        assertTrue(ExprParser.evaluate(event, expr));

        // Empty array matches always
        expr = "facts.a contains []";
        assertTrue(ExprParser.evaluate(event, expr));

        expr = "facts.a contains ['b', 'e']";
        assertFalse(ExprParser.evaluate(event, expr));
    }

    @Test
    public void containsInArray() {
        Event event = new Event();
        Map<String, Object> factMap = new HashMap<>();
        List<String> aList = new ArrayList<>(3);
        aList.add("b");
        aList.add("c");
        aList.add("d");
        factMap.put("a", aList);
        event.setFacts(factMap);

        String expr = "facts.a contains 'b'";
        assertTrue(ExprParser.evaluate(event, expr));

        expr = "facts.a contains [\"b\", \"c\"]";
        assertTrue(ExprParser.evaluate(event, expr));

        expr = "facts.a contains ['b']";
        assertTrue(ExprParser.evaluate(event, expr));

        // Case-insensitive
        expr = "facts.A contains ['B']";
        assertTrue(ExprParser.evaluate(event, expr));

        // Empty array matches always
        expr = "facts.a contains []";
        assertTrue(ExprParser.evaluate(event, expr));

        expr = "facts.a contains ['b', 'e']";
        assertFalse(ExprParser.evaluate(event, expr));
    }

    @Test
    public void multiKeyTagsMatching() {
        Event event = new Event();
        event.addTag("a", "b");

        String expr = "tags.a = 'b'";
        assertTrue(ExprParser.evaluate(event, expr));

        expr = "tags.a contains 'b'";
        assertTrue(ExprParser.evaluate(event, expr));

        event.addTag("a", "c");

        expr = "tags.a = 'b'";
        assertTrue(ExprParser.evaluate(event, expr));

        expr = "tags.a contains 'b'";
        assertTrue(ExprParser.evaluate(event, expr));

        expr = "tags.a = 'c'";
        assertTrue(ExprParser.evaluate(event, expr));

        expr = "tags.a contains 'c'";
        assertTrue(ExprParser.evaluate(event, expr));

        event.addTag("b", "d");

        expr = "tags.b = 'd' and tags.a = 'c'";
        assertTrue(ExprParser.evaluate(event, expr));

        expr = "tags.b = 'd' and tags.a contains 'c'";
        assertTrue(ExprParser.evaluate(event, expr));
    }
}

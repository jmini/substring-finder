/*******************************************************************************
 * Copyright (c) 2018 Jeremie Bresson and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package fr.jmini.utils.substringfinder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

public class PositionFinderTest {

    @Test
    public void testExampleIndexesOf() throws Exception {
        // tag::indexesOf[]
        String text = "'Hello,world',5,true";

        // end::indexOf[]
        TestUtil.checkInputInFile("testExampleIndexesOf.txt", text);

        // tag::indexOf[]
        PositionFinder finder = PositionFinder.define(",", "'", "'");
        List<Integer> findPositions = finder.indexesOf(text);
        assertEquals(2, findPositions.size(), "size");

        assertEquals("'Hello,world'", text.substring(0, findPositions.get(0)));
        assertEquals("5", text.substring(findPositions.get(0) + 1, findPositions.get(1)));
        assertEquals("true", text.substring(findPositions.get(1) + 1));
        // end::indexesOf[]
    }

    @Test
    public void testExampleIndexOf() throws Exception {
        // tag::indexOf[]
        String text = "lorem(Hello,world),ipsum";

        // end::indexOf[]
        TestUtil.checkInputInFile("testExampleIndexOf.txt", text);

        // tag::indexOf[]
        PositionFinder finder = PositionFinder.define(",", "(", ")");
        Optional<Integer> findPosition = finder.indexOf(text);
        assertEquals(true, findPosition.isPresent(), "isPresent");

        assertEquals("lorem(Hello,world)", text.substring(0, findPosition.get()));
        assertEquals("ipsum", text.substring(findPosition.get() + 1));
        // end::indexOf[]
    }

    @Test
    public void testIndexesOf() {
        PositionFinder finder = PositionFinder.define("|", "{{", "}}");
        Collection<Integer> expected = Arrays.<Integer>asList(Integer.valueOf(5), Integer.valueOf(13));
        assertListEquals(expected, finder.indexesOf("foooo|baaaaar|baz"));
        assertListEquals(expected, finder.indexesOf("foooo|b{{a}}r|baz"));
        assertListEquals(expected, finder.indexesOf("f{{}}|baaaaar|baz"));
        assertListEquals(expected, finder.indexesOf("f{{}}|b{{a}}r|baz"));
        assertListEquals(expected, finder.indexesOf("f{{}}|{{a|x}}|baz"));
        assertListEquals(expected, finder.indexesOf("foooo|{{a|x}}|baz{{zz{{a|b}}zzz|x}}"));
        assertListEquals(Collections.singletonList(Integer.valueOf(3)), finder.indexesOf("foo|b{{ar|ba}}z"));
        assertListEquals(Collections.singletonList(Integer.valueOf(3)), finder.indexesOf("foo|b{{ar|bax|x}}xxx"));
        Collection<Integer> expected2 = Arrays.<Integer>asList(Integer.valueOf(3), Integer.valueOf(20));
        assertListEquals(expected2, finder.indexesOf("foo|b{{ar|bax|e}}xxx|xx"));
        assertListEquals(expected2, finder.indexesOf("foo|b{{ar|bax|e}}xxx|xx{{aa{{r|b}}ax|e}}"));
    }

    @Test
    public void testSingleMatch() {
        singeltonFind("|foobaz");
        singeltonFind("|f{{b}}");
        singeltonFind("foobaz|");
        singeltonFind("{{o}}z|");
        singeltonFind("foo|baz");
        singeltonFind("{{foo|baz");
        singeltonFind("fo{{o|baz");
        singeltonFind("}}foo|baz");
        singeltonFind("fo}}o|baz");
        singeltonFind("foo|baz}}");
        singeltonFind("foo|b}}az");
        singeltonFind("foo|baz{{");
        singeltonFind("foo|b{{az");
    }

    private void singeltonFind(String input) {
        Integer expectedPosition = Integer.valueOf(input.indexOf("|"));
        PositionFinder finder = PositionFinder.define("|", "{{", "}}");
        Optional<Integer> find = finder.indexOf(input);
        assertTrue(find.isPresent());
        assertEquals(expectedPosition, find.get());
        Collection<Integer> positions = finder.indexesOf(input);
        List<Integer> expected = Collections.singletonList(expectedPosition);
        assertListEquals(expected, positions);
    }

    @Test
    public void testNoMatches() {
        noMatch("foo bar", 0);
        noMatch("foo () bar", 0);
        noMatch("foo ( test ) bar", 0);
        noMatch("foo ( , ) bar", 0);
        noMatch(", bar", 2);
        noMatch("( , ) bar", 5);
    }

    private void noMatch(String input, int startAt) {
        PositionFinder finder = PositionFinder.define(",", "(", ")");
        Optional<Integer> find = finder.indexOf(input, startAt);
        assertFalse(find.isPresent());
        List<Integer> positions = finder.indexesOf(input, startAt);
        assertTrue(positions.isEmpty());
    }

    private static void assertListEquals(Collection<Integer> expected, Collection<Integer> actual) {
        assertEquals(expected.size(), actual.size(), "size");
        assertIteratorEquals(expected.iterator(), actual.iterator());
    }

    private static void assertIteratorEquals(Iterator<?> iteratorExpected, Iterator<?> iteratorActual) {
        int i = 0;
        while (iteratorExpected.hasNext()) {
            Object e = iteratorExpected.next();
            if (iteratorActual.hasNext()) {
                Object a = iteratorActual.next();
                assertEquals(e, a, "element (" + i + ")");
            } else {
                fail("Expected element (" + i + ") not found:" + e);
            }
            i++;
        }

        if (iteratorActual.hasNext()) {
            Object a = iteratorActual.next();
            fail("Additional element (" + i + ") found:" + a);
        }
    }
}
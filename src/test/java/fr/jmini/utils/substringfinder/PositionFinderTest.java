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
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

public class PositionFinderTest {

    @Test
    public void testExample() {
        // tag::example[]
        String text = "'Hello,world',5,true";

        PositionFinder finder = PositionFinder.define(",", "'", "'");
        List<Integer> findPositions = finder.indexesOf(text);
        assertEquals(2, findPositions.size(), "size");

        assertEquals("'Hello,world'", text.substring(0, findPositions.get(0)));
        assertEquals("5", text.substring(findPositions.get(0) + 1, findPositions.get(1)));
        assertEquals("true", text.substring(findPositions.get(1) + 1));
        // end::example[]
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
    public void testIndexesOfSingleMatch() {
        singeltonFind("|foobaz");
        singeltonFind("foobaz|");
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
        PositionFinder finder = PositionFinder.define("|", "{{", "}}");
        Collection<Integer> positions = finder.indexesOf(input);
        List<Integer> expected = Collections.singletonList(Integer.valueOf(input.indexOf("|")));
        assertListEquals(expected, positions);
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
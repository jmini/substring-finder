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

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

public class SubstringFinderTest {

    @Test
    public void testExample() throws IOException {
        // tag::example[]
        String text = "5 + (4 + (1 + 2) / 3 - 5) * 10 / (3 + 2)";

        // end::example[]
        TestUtil.checkInputInFile("testExample.txt", text);

        // tag::example[]
        SubstringFinder finder = SubstringFinder.define("(", ")");
        Optional<Range> findRange = finder.nextRange(text);
        if (findRange.isPresent()) {
            Range range = findRange.get();
            String substring = text.substring(range.getRangeStart(), range.getRangeEnd());
            assertEquals("(4 + (1 + 2) / 3 - 5)", substring);
        }
        // end::example[]
    }

    @Test
    public void testNextRangeEmpty() {
        SubstringFinder sf = SubstringFinder.define("[", "]");
        Optional<Range> find = sf.nextRange("abcdef", 0);
        assertEquals(false, find.isPresent(), "range is present");
    }

    @Test
    public void testWithExlude() throws Exception {
        // tag::exclude[]
        String text = ""
                + "package tmp;\n"
                + " \n"
                + "@SomeAnnotation(arg1=\"value ;-)\", arg2=\"other value\")\n"
                + "public class SomeClass {\n"
                + "}\n";

        // end::exclude[]
        TestUtil.checkInputInFile("testWithExlude.txt", text);

        // tag::exclude[]
        SubstringFinder finder = SubstringFinder.define("@SomeAnnotation(", ")", "\"", "\"");
        Optional<Range> findRange = finder.nextRange(text);
        if (findRange.isPresent()) {
            Range range = findRange.get();
            String substring = text.substring(range.getRangeStart(), range.getRangeEnd());
            assertEquals("@SomeAnnotation(arg1=\"value ;-)\", arg2=\"other value\")", substring);
        }
        // end::exclude[]
    }

    private static final String TEST1_VALUE = "[.]x[]xx[[]..]xx[.[::[']:]..]x";

    private static final ExpectedRange TEST1_POS1 = new ExpectedRange(1, 2);

    private static final ExpectedRange TEST1_POS2 = new ExpectedRange(5, 5);

    private static final ExpectedRange TEST1_POS3 = new ExpectedRange(9, 13);

    private static final ExpectedRange TEST1_POS4 = new ExpectedRange(10, 10);

    private static final ExpectedRange TEST1_POS5 = new ExpectedRange(17, 28);

    private static final ExpectedRange TEST1_POS6 = new ExpectedRange(19, 25);

    private static final ExpectedRange TEST1_POS7 = new ExpectedRange(22, 23);

    private static final ExpectedRange TEST1_NONE = new ExpectedRange(-1, -1);

    private void prerequisites1() {
        // Verify that the constant are well defined:
        verifyPos(TEST1_POS1, TEST1_VALUE, ".");
        verifyPos(TEST1_POS2, TEST1_VALUE, "");
        verifyPos(TEST1_POS3, TEST1_VALUE, "[]..");
        verifyPos(TEST1_POS4, TEST1_VALUE, "");
        verifyPos(TEST1_POS5, TEST1_VALUE, ".[::[']:]..");
        verifyPos(TEST1_POS6, TEST1_VALUE, "::[']:");
        verifyPos(TEST1_POS7, TEST1_VALUE, "'");
    }

    @Test
    public void testNextRange1() {
        prerequisites1();

        // Test the function nextRange(..) for each position:
        SubstringFinder sf = SubstringFinder.define("[", "]");
        assertStartEndEquals(TEST1_POS1, sf, sf.nextRange(TEST1_VALUE, 0));
        assertStartEndEquals(TEST1_POS2, sf, sf.nextRange(TEST1_VALUE, 1));
        assertStartEndEquals(TEST1_POS2, sf, sf.nextRange(TEST1_VALUE, 2));
        assertStartEndEquals(TEST1_POS2, sf, sf.nextRange(TEST1_VALUE, 3));
        assertStartEndEquals(TEST1_POS2, sf, sf.nextRange(TEST1_VALUE, 4));
        assertStartEndEquals(TEST1_POS3, sf, sf.nextRange(TEST1_VALUE, 5));
        assertStartEndEquals(TEST1_POS3, sf, sf.nextRange(TEST1_VALUE, 7));
        assertStartEndEquals(TEST1_POS3, sf, sf.nextRange(TEST1_VALUE, 8));
        assertStartEndEquals(TEST1_POS4, sf, sf.nextRange(TEST1_VALUE, 9));
        assertStartEndEquals(TEST1_POS5, sf, sf.nextRange(TEST1_VALUE, 10));
        assertStartEndEquals(TEST1_POS5, sf, sf.nextRange(TEST1_VALUE, 11));
        assertStartEndEquals(TEST1_POS5, sf, sf.nextRange(TEST1_VALUE, 13));
        assertStartEndEquals(TEST1_POS5, sf, sf.nextRange(TEST1_VALUE, 14));
        assertStartEndEquals(TEST1_POS5, sf, sf.nextRange(TEST1_VALUE, 15));
        assertStartEndEquals(TEST1_POS5, sf, sf.nextRange(TEST1_VALUE, 16));
        assertStartEndEquals(TEST1_POS6, sf, sf.nextRange(TEST1_VALUE, 17));
        assertStartEndEquals(TEST1_POS6, sf, sf.nextRange(TEST1_VALUE, 18));
        assertStartEndEquals(TEST1_POS7, sf, sf.nextRange(TEST1_VALUE, 19));
        assertStartEndEquals(TEST1_POS7, sf, sf.nextRange(TEST1_VALUE, 20));
        assertStartEndEquals(TEST1_POS7, sf, sf.nextRange(TEST1_VALUE, 21));
        assertStartEndEquals(TEST1_NONE, sf, sf.nextRange(TEST1_VALUE, 22));
        assertStartEndEquals(TEST1_NONE, sf, sf.nextRange(TEST1_VALUE, 23));
        assertStartEndEquals(TEST1_NONE, sf, sf.nextRange(TEST1_VALUE, 24));
        assertStartEndEquals(TEST1_NONE, sf, sf.nextRange(TEST1_VALUE, 25));
        assertStartEndEquals(TEST1_NONE, sf, sf.nextRange(TEST1_VALUE, 200));
    }

    @Test
    public void testFindAll1() {
        prerequisites1();

        SubstringFinder sf = SubstringFinder.define("[", "]");

        // Test without including nested:
        List<Range> list1 = sf.findAll(TEST1_VALUE, false);
        assertEquals(4, list1.size());
        assertStartEndEquals(TEST1_POS1, sf, list1.get(0));
        assertStartEndEquals(TEST1_POS2, sf, list1.get(1));
        assertStartEndEquals(TEST1_POS3, sf, list1.get(2));
        assertStartEndEquals(TEST1_POS5, sf, list1.get(3));

        // Test with nested:
        List<Range> list2 = sf.findAll(TEST1_VALUE, true);
        assertEquals(7, list2.size());
        assertStartEndEquals(TEST1_POS1, sf, list2.get(0));
        assertStartEndEquals(TEST1_POS2, sf, list2.get(1));
        assertStartEndEquals(TEST1_POS3, sf, list2.get(2));
        assertStartEndEquals(TEST1_POS4, sf, list2.get(3));
        assertStartEndEquals(TEST1_POS5, sf, list2.get(4));
        assertStartEndEquals(TEST1_POS6, sf, list2.get(5));
        assertStartEndEquals(TEST1_POS7, sf, list2.get(6));
    }

    private static final String TEST2_VALUE = "xxx{{ooo}}xxx{{zzz{{www}}zzz{{vv}}z}}xxxxxx{{sss{{uuu{{aa}}uuu}}ss}}xxx";

    private static final ExpectedRange TEST2_POS1 = new ExpectedRange(5, 8);

    private static final ExpectedRange TEST2_POS2 = new ExpectedRange(15, 35);

    private static final ExpectedRange TEST2_POS3 = new ExpectedRange(20, 23);

    private static final ExpectedRange TEST2_POS4 = new ExpectedRange(30, 32);

    private static final ExpectedRange TEST2_POS5 = new ExpectedRange(45, 66);

    private static final ExpectedRange TEST2_POS6 = new ExpectedRange(50, 62);

    private static final ExpectedRange TEST2_POS7 = new ExpectedRange(55, 57);

    private static final ExpectedRange TEST2_NONE = new ExpectedRange(-1, -1);

    @Test
    public void testNextRange2() {
        // Verify that the constant are well defined:
        verifyPos(TEST2_POS1, TEST2_VALUE, "ooo");
        verifyPos(TEST2_POS2, TEST2_VALUE, "zzz{{www}}zzz{{vv}}z");
        verifyPos(TEST2_POS3, TEST2_VALUE, "www");
        verifyPos(TEST2_POS4, TEST2_VALUE, "vv");
        verifyPos(TEST2_POS5, TEST2_VALUE, "sss{{uuu{{aa}}uuu}}ss");
        verifyPos(TEST2_POS6, TEST2_VALUE, "uuu{{aa}}uuu");
        verifyPos(TEST2_POS7, TEST2_VALUE, "aa");

        SubstringFinder sf = SubstringFinder.define("{{", "}}");
        assertStartEndEquals(TEST2_POS1, sf, sf.nextRange(TEST2_VALUE, 0));
        assertStartEndEquals(TEST2_POS1, sf, sf.nextRange(TEST2_VALUE, 3));
        assertStartEndEquals(TEST2_POS2, sf, sf.nextRange(TEST2_VALUE, 4));
        assertStartEndEquals(TEST2_POS2, sf, sf.nextRange(TEST2_VALUE, 5));
        assertStartEndEquals(TEST2_POS2, sf, sf.nextRange(TEST2_VALUE, 10));
        assertStartEndEquals(TEST2_POS3, sf, sf.nextRange(TEST2_VALUE, 15));
        assertStartEndEquals(TEST2_POS3, sf, sf.nextRange(TEST2_VALUE, 16));
        assertStartEndEquals(TEST2_POS3, sf, sf.nextRange(TEST2_VALUE, 18));
        assertStartEndEquals(TEST2_POS4, sf, sf.nextRange(TEST2_VALUE, 19));
        assertStartEndEquals(TEST2_POS4, sf, sf.nextRange(TEST2_VALUE, 20));
        assertStartEndEquals(TEST2_POS4, sf, sf.nextRange(TEST2_VALUE, 28));
        assertStartEndEquals(TEST2_POS5, sf, sf.nextRange(TEST2_VALUE, 29));
        assertStartEndEquals(TEST2_POS5, sf, sf.nextRange(TEST2_VALUE, 30));
        assertStartEndEquals(TEST2_POS5, sf, sf.nextRange(TEST2_VALUE, 35));
        assertStartEndEquals(TEST2_POS5, sf, sf.nextRange(TEST2_VALUE, 40));
        assertStartEndEquals(TEST2_POS5, sf, sf.nextRange(TEST2_VALUE, 43));
        assertStartEndEquals(TEST2_POS6, sf, sf.nextRange(TEST2_VALUE, 44));
        assertStartEndEquals(TEST2_POS6, sf, sf.nextRange(TEST2_VALUE, 45));
        assertStartEndEquals(TEST2_POS6, sf, sf.nextRange(TEST2_VALUE, 48));
        assertStartEndEquals(TEST2_POS7, sf, sf.nextRange(TEST2_VALUE, 49));
        assertStartEndEquals(TEST2_POS7, sf, sf.nextRange(TEST2_VALUE, 50));
        assertStartEndEquals(TEST2_POS7, sf, sf.nextRange(TEST2_VALUE, 53));
        assertStartEndEquals(TEST2_NONE, sf, sf.nextRange(TEST2_VALUE, 54));
        assertStartEndEquals(TEST2_NONE, sf, sf.nextRange(TEST2_VALUE, 55));
        assertStartEndEquals(TEST2_NONE, sf, sf.nextRange(TEST2_VALUE, 58));
        assertStartEndEquals(TEST2_NONE, sf, sf.nextRange(TEST2_VALUE, 100));
    }

    private static final String TEST3_VALUE = "xx[[[ooo)xxx[[[zz[[[www)zzz[[[vv)zz)xxxxxx[[[ss[[[uu[[[aa)uuuu)sss)xxxx";

    private static final ExpectedRange TEST3_POS1 = new ExpectedRange(5, 8);

    private static final ExpectedRange TEST3_POS2 = new ExpectedRange(15, 35);

    private static final ExpectedRange TEST3_POS3 = new ExpectedRange(20, 23);

    private static final ExpectedRange TEST3_POS4 = new ExpectedRange(30, 32);

    private static final ExpectedRange TEST3_POS5 = new ExpectedRange(45, 66);

    private static final ExpectedRange TEST3_POS6 = new ExpectedRange(50, 62);

    private static final ExpectedRange TEST3_POS7 = new ExpectedRange(55, 57);

    private static final ExpectedRange TEST3_NONE = new ExpectedRange(-1, -1);

    @Test
    public void testNextRange3() {
        // Verify that the constant are well defined:
        verifyPos(TEST3_POS1, TEST3_VALUE, "ooo");
        verifyPos(TEST3_POS2, TEST3_VALUE, "zz[[[www)zzz[[[vv)zz");
        verifyPos(TEST3_POS3, TEST3_VALUE, "www");
        verifyPos(TEST3_POS4, TEST3_VALUE, "vv");
        verifyPos(TEST3_POS5, TEST3_VALUE, "ss[[[uu[[[aa)uuuu)sss");
        verifyPos(TEST3_POS6, TEST3_VALUE, "uu[[[aa)uuuu");
        verifyPos(TEST3_POS7, TEST3_VALUE, "aa");

        SubstringFinder sf = SubstringFinder.define("[[[", ")");
        assertStartEndEquals(TEST3_POS1, sf, sf.nextRange(TEST3_VALUE, 0));
        assertStartEndEquals(TEST3_POS1, sf, sf.nextRange(TEST3_VALUE, 2));
        assertStartEndEquals(TEST3_POS2, sf, sf.nextRange(TEST3_VALUE, 3));
        assertStartEndEquals(TEST3_POS2, sf, sf.nextRange(TEST3_VALUE, 4));
        assertStartEndEquals(TEST3_POS2, sf, sf.nextRange(TEST3_VALUE, 10));
        assertStartEndEquals(TEST3_POS3, sf, sf.nextRange(TEST3_VALUE, 15));
        assertStartEndEquals(TEST3_POS3, sf, sf.nextRange(TEST3_VALUE, 16));
        assertStartEndEquals(TEST3_POS3, sf, sf.nextRange(TEST3_VALUE, 17));
        assertStartEndEquals(TEST3_POS4, sf, sf.nextRange(TEST3_VALUE, 18));
        assertStartEndEquals(TEST3_POS4, sf, sf.nextRange(TEST3_VALUE, 20));
        assertStartEndEquals(TEST3_POS4, sf, sf.nextRange(TEST3_VALUE, 27));
        assertStartEndEquals(TEST3_POS5, sf, sf.nextRange(TEST3_VALUE, 28));
        assertStartEndEquals(TEST3_POS5, sf, sf.nextRange(TEST3_VALUE, 30));
        assertStartEndEquals(TEST3_POS5, sf, sf.nextRange(TEST3_VALUE, 35));
        assertStartEndEquals(TEST3_POS5, sf, sf.nextRange(TEST3_VALUE, 40));
        assertStartEndEquals(TEST3_POS5, sf, sf.nextRange(TEST3_VALUE, 42));
        assertStartEndEquals(TEST3_POS6, sf, sf.nextRange(TEST3_VALUE, 43));
        assertStartEndEquals(TEST3_POS6, sf, sf.nextRange(TEST3_VALUE, 45));
        assertStartEndEquals(TEST3_POS6, sf, sf.nextRange(TEST3_VALUE, 47));
        assertStartEndEquals(TEST3_POS7, sf, sf.nextRange(TEST3_VALUE, 48));
        assertStartEndEquals(TEST3_POS7, sf, sf.nextRange(TEST3_VALUE, 50));
        assertStartEndEquals(TEST3_POS7, sf, sf.nextRange(TEST3_VALUE, 52));
        assertStartEndEquals(TEST3_NONE, sf, sf.nextRange(TEST3_VALUE, 53));
        assertStartEndEquals(TEST3_NONE, sf, sf.nextRange(TEST3_VALUE, 55));
        assertStartEndEquals(TEST3_NONE, sf, sf.nextRange(TEST3_VALUE, 58));
        assertStartEndEquals(TEST3_NONE, sf, sf.nextRange(TEST3_VALUE, 100));
    }

    @Test
    public void testNextRangeNotFound() {
        SubstringFinder substringFinder = SubstringFinder.define("<<", ">>");
        assertEquals(Optional.empty(), substringFinder.nextRange("xxxxxx", 0));
        assertEquals(Optional.empty(), substringFinder.nextRange("xxxxxx", 100));
        assertEquals(Optional.empty(), substringFinder.nextRange("xxx>>zzz<<xxx", 0));
        assertEquals(Optional.empty(), substringFinder.nextRange(">>zzz<<", 0));
        assertEquals(Optional.empty(), substringFinder.nextRange("xxx<<zzz", 0));

        SubstringFinder sf = SubstringFinder.define("<<<", ")");

        assertEquals(Optional.empty(), sf.nextRange(")xx<<<zzzxxxx", 0));
        assertEquals(Optional.empty(), sf.nextRange("<<<zzzxxxx", 0));
        assertEquals(Optional.empty(), sf.nextRange(")xxxx", 0));
    }

    @Test
    public void testNextRangeTooManyOpen() {
        SubstringFinder f = SubstringFinder.define("<<", ">>");
        assertStartEndEquals(new ExpectedRange(5, 15), f, f.nextRange("...<<xxx<<zzzzz>>....", 0));
        assertStartEndEquals(new ExpectedRange(10, 15), f, f.nextRange("...<<xxx<<zzzzz>>....", 7));

        assertStartEndEquals(new ExpectedRange(5, 15), f, f.nextRange("...<<xxx<<zzzzz>>...<<aaa....", 0));
        assertStartEndEquals(new ExpectedRange(5, 15), f, f.nextRange("...<<xxx<<zzzzz>><<aaa.......", 0));
    }

    private static void assertStartEndEquals(ExpectedRange expected, SubstringFinder sf, Optional<Range> actual) {
        if (expected.contentStart < 0 && expected.contentEnd < 0) {
            assertEquals(false, actual.isPresent(), "range is present");
        } else {
            assertEquals(true, actual.isPresent(), "range is present");
            Range range = actual.get();
            assertStartEndEquals(expected, sf, range);
        }
    }

    private static void assertStartEndEquals(ExpectedRange expected, SubstringFinder sf, Range actual) {
        int expectedContentStart = expected.contentStart;
        int expectedContentEnd = expected.contentEnd;
        int expectedRangeStart = expectedContentStart - sf.getOpenLength();
        int expectedRangeEnd = expectedContentEnd + sf.getCloseLength();

        assertEquals(expectedContentStart, actual.getContentStart(), "content start");
        assertEquals(expectedContentEnd, actual.getContentEnd(), "content end");
        assertEquals(expectedRangeStart, actual.getRangeStart(), "range start");
        assertEquals(expectedRangeEnd, actual.getRangeEnd(), "range end");
    }

    private static void verifyPos(ExpectedRange range, String value, String expected) {
        assertEquals(expected, value.substring(range.contentStart, range.contentEnd));
    }

    private static class ExpectedRange {

        private final int contentStart;

        private final int contentEnd;

        private ExpectedRange(int start, int end) {
            super();
            this.contentStart = start;
            this.contentEnd = end;
        }
    }
}
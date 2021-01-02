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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SubstringFinder {
    private final String targetOpen;

    private final String targetClose;

    private final String excludeOpen;

    private final String excludeClose;

    private SubstringFinder(String targetOpen, String targetClose, String excludeOpen, String excludeClose) {
        this.targetOpen = targetOpen;
        this.targetClose = targetClose;
        this.excludeOpen = excludeOpen;
        this.excludeClose = excludeClose;
    }

    public static SubstringFinder define(String targetOpen, String targetClose) {
        return new SubstringFinder(targetOpen, targetClose, null, null);
    }

    public static SubstringFinder define(String targetOpen, String targetClose, String excludeOpen, String excludeClose) {
        return new SubstringFinder(targetOpen, targetClose, excludeOpen, excludeClose);
    }

    public List<Range> findAll(String text, boolean includeNested) {
        List<Range> all = new ArrayList<>();
        Optional<Range> find = nextRange(text);
        while (find.isPresent()) {
            Range r = find.get();
            all.add(r);
            int startAt = (includeNested) ? r.getContentStart() : r.getRangeEnd();
            find = nextRange(text, startAt);
        }
        return Collections.unmodifiableList(all);
    }

    public Optional<Range> nextRange(String text) {
        return nextRange(text, 0);
    }

    public Optional<Range> nextRange(String text, int startAt) {
        return nextRange(text, startAt, Integer.MAX_VALUE);
    }

    public Optional<Range> nextRange(String text, int startAt, int endAt) {
        int rangeStartIndex = findIndex(text, targetOpen, startAt);
        if (rangeStartIndex > -1 && rangeStartIndex < endAt) {
            return computeCloseIndexAndCreateRange(text, rangeStartIndex, rangeStartIndex + targetOpen.length());
        }
        return Optional.empty();
    }

    private Optional<Range> computeCloseIndexAndCreateRange(String text, int rangeStartIndex, int startAt) {
        int closeIndex = findIndex(text, targetClose, startAt);
        Optional<Range> findNextRange = nextRange(text, startAt, closeIndex);

        if (findNextRange.isPresent()) {
            Range nextRange = findNextRange.get();
            if (closeIndex < nextRange.getContentStart()) {
                Range r = createRange(rangeStartIndex, closeIndex);
                return Optional.of(r);
            } else {
                Optional<Range> find = computeCloseIndexAndCreateRange(text, rangeStartIndex, nextRange.getRangeEnd());
                Range r;
                if (find.isPresent()) {
                    r = find.get();
                } else {
                    r = createRange(rangeStartIndex, closeIndex);
                }
                return Optional.of(r);
            }
        } else if (closeIndex > 0) {
            Range r = createRange(rangeStartIndex, closeIndex);
            return Optional.of(r);
        }

        return Optional.empty();
    }

    private int findIndex(String text, String search, int startAt) {
        if (excludeOpen != null && excludeClose != null) {
            PositionFinder finder = PositionFinder.define(search, excludeOpen, excludeClose);
            Optional<Integer> indexOf = finder.indexOf(text, startAt);
            if (indexOf.isPresent()) {
                return indexOf.get();
            }
            return -1;
        }
        return text.indexOf(search, startAt);
    }

    private Range createRange(int rangeStart, int contentEnd) {
        return new Range(rangeStart, rangeStart + targetOpen.length(), contentEnd, contentEnd + targetClose.length());
    }

    public int getOpenLength() {
        return targetOpen.length();
    }

    public int getCloseLength() {
        return targetClose.length();
    }
}

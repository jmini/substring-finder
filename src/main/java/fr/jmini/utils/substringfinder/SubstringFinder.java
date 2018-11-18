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

import java.util.Optional;

public class SubstringFinder {
    private final String targetOpen;

    private final String targetClose;

    private SubstringFinder(String targetOpen, String targetClose) {
        this.targetOpen = targetOpen;
        this.targetClose = targetClose;
    }

    public static SubstringFinder define(String targetOpen, String targetClose) {
        return new SubstringFinder(targetOpen, targetClose);
    }

    public Optional<Range> nextRange(String text) {
        return nextRange(text, 0);
    }

    public Optional<Range> nextRange(String text, int startAt) {
        return nextRange(text, startAt, Integer.MAX_VALUE);
    }

    public Optional<Range> nextRange(String text, int startAt, int endAt) {
        int rangeStartIndex = text.indexOf(targetOpen, startAt);
        if (rangeStartIndex > -1 && rangeStartIndex < endAt) {
            return computeCloseIndexAndCreateRange(text, rangeStartIndex, rangeStartIndex + targetOpen.length());
        }
        return Optional.empty();
    }

    private Optional<Range> computeCloseIndexAndCreateRange(String text, int rangeStartIndex, int startAt) {
        int closeIndex = text.indexOf(targetClose, startAt);
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

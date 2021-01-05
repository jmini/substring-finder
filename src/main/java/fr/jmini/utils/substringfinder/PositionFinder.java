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

public class PositionFinder {
    private final String target;

    private final SubstringFinder excludeSubstringFinder;

    public static PositionFinder define(String target, String excludeOpen, String excludeClose) {
        return new PositionFinder(target, excludeOpen, excludeClose);
    }

    private PositionFinder(String target, String excludeOpen, String excludeClose) {
        this.target = target;
        this.excludeSubstringFinder = SubstringFinder.define(excludeOpen, excludeClose);
    }

    public List<Integer> indexesOf(String text) {
        return indexesOf(text, 0);
    }

    public List<Integer> indexesOf(String text, int startAt) {
        List<Integer> results = new ArrayList<Integer>();
        addResult(results, text, startAt);
        return Collections.unmodifiableList(results);
    }

    private void addResult(List<Integer> results, String text, int startAt) {
        Optional<Range> findExclude = excludeSubstringFinder.nextRange(text, startAt);
        if (findExclude.isPresent()) {
            Range exclude = findExclude.get();
            if (exclude.getContentStart() > startAt) {
                addResult(results, text, startAt, exclude.getContentStart());
                addResult(results, text, exclude.getRangeEnd());
            } else {
                addResult(results, text, startAt, text.length());
            }
        } else {
            addResult(results, text, startAt, text.length());
        }
    }

    private void addResult(List<Integer> results, String text, int startAt, int stopBefore) {
        Optional<Integer> find = findIndexOf(text, startAt, stopBefore);
        while (find.isPresent()) {
            Integer index = find.get();
            results.add(index);
            find = findIndexOf(text, index + target.length(), stopBefore);
        }
    }

    public Optional<Integer> indexOf(String text) {
        return indexOf(text, 0);
    }

    public Optional<Integer> indexOf(String text, int startAt) {
        Optional<Range> findExclude = excludeSubstringFinder.nextRange(text, startAt);
        if (findExclude.isPresent()) {
            Range exclude = findExclude.get();
            if (exclude.getContentStart() > startAt) {
                Optional<Integer> find = findIndexOf(text, startAt, exclude.getContentStart());
                if (find.isPresent()) {
                    return find;
                }
                return indexOf(text, exclude.getRangeEnd());
            } else {
                return findIndexOf(text, startAt, text.length());
            }
        }
        return findIndexOf(text, startAt, text.length());
    }

    private Optional<Integer> findIndexOf(String text, int startAt, int stopBefore) {
        int index = text.indexOf(target, startAt);
        if (index > -1 && index < stopBefore) {
            return Optional.of(Integer.valueOf(index));
        }
        return Optional.empty();
    }
}

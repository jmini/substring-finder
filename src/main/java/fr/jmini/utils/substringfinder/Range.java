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

public class Range {
    private final int rangeStart;

    private final int contentStart;

    private final int contentEnd;

    private final int rangeEnd;

    public Range(int rangeStart, int contentStart, int contentEnd, int rangeEnd) {
        super();
        this.rangeStart = rangeStart;
        this.contentStart = contentStart;
        this.contentEnd = contentEnd;
        this.rangeEnd = rangeEnd;
    }

    public int getRangeStart() {
        return rangeStart;
    }

    public int getContentStart() {
        return contentStart;
    }

    public int getContentEnd() {
        return contentEnd;
    }

    public int getRangeEnd() {
        return rangeEnd;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + contentEnd;
        result = prime * result + contentStart;
        result = prime * result + rangeEnd;
        result = prime * result + rangeStart;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Range other = (Range) obj;
        if (contentEnd != other.contentEnd) {
            return false;
        }
        if (contentStart != other.contentStart) {
            return false;
        }
        if (rangeEnd != other.rangeEnd) {
            return false;
        }
        if (rangeStart != other.rangeStart) {
            return false;
        }
        return true;
    }
}
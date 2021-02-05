////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2021 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle.jre6.util;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public final class Collections7 {
    private Collections7() {
    }

    public static <T> Set<T> newHashSet(T... items) {
        final Set<T> result = new HashSet<T>();
        for (T item : items) {
            result.add(item);
        }
        return result;
    }

    public static <T> SortedSet<T> newSortedSet(T... items) {
        final SortedSet<T> result = new TreeSet<T>();
        for (T item : items) {
            result.add(item);
        }
        return result;
    }

    public static <T> Enumeration<T> emptyEnumeration() {
        return new Enumeration<T>() {
            @Override
            public boolean hasMoreElements() {
                return false;
            }

            @Override
            public T nextElement() {
                return null;
            }
        };
    }

    public static <T> Iterator<T> emptyIterator() {
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public T next() {
                throw new NoSuchElementException();
            }

            @Override
            public void remove() {
                throw new NoSuchElementException();
            }
        };
    }
}

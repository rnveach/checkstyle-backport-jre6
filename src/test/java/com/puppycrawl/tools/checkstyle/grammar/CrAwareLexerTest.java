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

package com.puppycrawl.tools.checkstyle.grammar;

import static com.google.common.truth.Truth.assertWithMessage;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.junit.Test;

public class CrAwareLexerTest {

    @Test
    public void testConsumeCarriageReturnZeroCharPositionInLine() {
        final String text = "\r";
        final CharStream charStream = new ANTLRInputStream(text);
        final CrAwareLexerSimulator lexer =
                new CrAwareLexerSimulator(null, null, null, null);
        lexer.consume(charStream);

        assertWithMessage("Carriage return should reset column number.")
                .that(lexer.getCharPositionInLine())
                .isEqualTo(0);
    }

    @Test
    public void testConsumeCarriageReturnNewline() {
        final String text = "\r";
        final CharStream charStream = new ANTLRInputStream(text);
        final CrAwareLexerSimulator lexer =
                new CrAwareLexerSimulator(null, null, null, null);
        lexer.consume(charStream);

        assertWithMessage("Carriage return should increment line number.")
                .that(lexer.getLine())
                .isEqualTo(2);
    }

    @Test
    public void testConsumeWindowsNewlineZeroCharPositionInLine() {
        final String text = "\r\n";
        final CharStream charStream = new ANTLRInputStream(text);
        final CrAwareLexerSimulator lexer =
                new CrAwareLexerSimulator(null, null, null, null);
        lexer.consume(charStream);
        lexer.consume(charStream);

        assertWithMessage("Carriage return should reset column number.")
                .that(lexer.getCharPositionInLine())
                .isEqualTo(0);
    }

    @Test
    public void testConsumeWindowsNewline() {
        final String text = "\r\n";
        final CharStream charStream = new ANTLRInputStream(text);
        final CrAwareLexerSimulator lexer =
                new CrAwareLexerSimulator(null, null, null, null);
        lexer.consume(charStream);
        lexer.consume(charStream);

        assertWithMessage("Carriage return should increment line number.")
                .that(lexer.getLine())
                .isEqualTo(2);
    }
}

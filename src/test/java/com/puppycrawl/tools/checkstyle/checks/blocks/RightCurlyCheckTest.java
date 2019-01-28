////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2019 the original author or authors.
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

package com.puppycrawl.tools.checkstyle.checks.blocks;

import static com.puppycrawl.tools.checkstyle.checks.blocks.RightCurlyCheck.MSG_KEY_LINE_ALONE;
import static com.puppycrawl.tools.checkstyle.checks.blocks.RightCurlyCheck.MSG_KEY_LINE_BREAK_BEFORE;
import static com.puppycrawl.tools.checkstyle.checks.blocks.RightCurlyCheck.MSG_KEY_LINE_SAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;

public class RightCurlyCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/puppycrawl/tools/checkstyle/checks/blocks/rightcurly";
    }

    /* Additional test for jacoco, since valueOf()
     * is generated by javac and jacoco reports that
     * valueOf() is uncovered.
     */
    @Test
    public void testRightCurlyOptionValueOf() {
        final RightCurlyOption option = RightCurlyOption.valueOf("ALONE");
        assertEquals("Invalid valueOf result", RightCurlyOption.ALONE, option);
    }

    @Test
    public void testDefault() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(RightCurlyCheck.class);
        final String[] expected = {
            "25:17: " + getCheckMessage(MSG_KEY_LINE_SAME, "}", 17),
            "28:17: " + getCheckMessage(MSG_KEY_LINE_SAME, "}", 17),
            "40:13: " + getCheckMessage(MSG_KEY_LINE_SAME, "}", 13),
            "44:13: " + getCheckMessage(MSG_KEY_LINE_SAME, "}", 13),
            "93:27: " + getCheckMessage(MSG_KEY_LINE_BREAK_BEFORE, "}", 27),
        };
        verify(checkConfig, getPath("InputRightCurlyLeft.java"), expected);
    }

    @Test
    public void testSame() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(RightCurlyCheck.class);
        checkConfig.addAttribute("option", RightCurlyOption.SAME.toString());
        final String[] expected = {
            "25:17: " + getCheckMessage(MSG_KEY_LINE_SAME, "}", 17),
            "28:17: " + getCheckMessage(MSG_KEY_LINE_SAME, "}", 17),
            "40:13: " + getCheckMessage(MSG_KEY_LINE_SAME, "}", 13),
            "44:13: " + getCheckMessage(MSG_KEY_LINE_SAME, "}", 13),
            "93:27: " + getCheckMessage(MSG_KEY_LINE_BREAK_BEFORE, "}", 27),
        };
        verify(checkConfig, getPath("InputRightCurlyLeft.java"), expected);
    }

    @Test
    public void testSameOmitOneLiners() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(RightCurlyCheck.class);
        checkConfig.addAttribute("option", RightCurlyOption.SAME.toString());
        final String[] expected = CommonUtil.EMPTY_STRING_ARRAY;
        verify(checkConfig, getPath("InputRightCurlyNameForOneLiners.java"), expected);
    }

    @Test
    public void testAlone() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(RightCurlyCheck.class);
        checkConfig.addAttribute("option", RightCurlyOption.ALONE.toString());
        final String[] expected = {
            "93:27: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 27),
            "97:72: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 72),
        };
        verify(checkConfig, getPath("InputRightCurlyLeft.java"), expected);
    }

    @Test
    public void testNewLine() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(RightCurlyCheck.class);
        checkConfig.addAttribute("option", RightCurlyOption.ALONE.toString());
        checkConfig.addAttribute("tokens", "CLASS_DEF, METHOD_DEF, CTOR_DEF");
        final String[] expected = {
            "111:5: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 5),
            "111:6: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 6),
            "122:5: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 5),
            "122:6: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 6),
            "136:5: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 5),
            "136:6: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 6),
        };
        verify(checkConfig, getPath("InputRightCurlyLeft.java"), expected);
    }

    @Test
    public void testShouldStartLine() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(RightCurlyCheck.class);
        checkConfig.addAttribute("option", RightCurlyOption.ALONE.toString());
        final String[] expected = {
            "93:27: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 27),
            "97:72: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 72),
        };
        verify(checkConfig, getPath("InputRightCurlyLeft.java"), expected);
    }

    @Test
    public void testShouldStartLine2() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(RightCurlyCheck.class);
        checkConfig.addAttribute("option", RightCurlyOption.ALONE_OR_SINGLELINE.toString());
        checkConfig.addAttribute("tokens", "CLASS_DEF, METHOD_DEF");
        final String[] expected = {
            "111:6: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 6),
            "122:5: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 5),
            "122:6: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 6),
            "136:5: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 5),
            "136:6: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 6),
        };
        verify(checkConfig, getPath("InputRightCurlyLeft.java"), expected);
    }

    @Test
    public void testMethodCtorNamedClassClosingBrace() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(RightCurlyCheck.class);
        checkConfig.addAttribute("option", RightCurlyOption.ALONE.toString());
        final String[] expected = {
            "93:27: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 27),
            "97:72: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 72),
        };
        verify(checkConfig, getPath("InputRightCurlyLeft.java"), expected);
    }

    @Test
    public void testForceLineBreakBefore() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(RightCurlyCheck.class);
        checkConfig.addAttribute("option", RightCurlyOption.ALONE.toString());
        checkConfig.addAttribute("tokens", "LITERAL_FOR,"
                + "LITERAL_WHILE, LITERAL_DO, STATIC_INIT, INSTANCE_INIT");
        final String[] expected = {
            "35:43: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 43),
            "41:71: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 71),
            "47:25: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 25),
        };
        verify(checkConfig, getPath("InputRightCurlyLineBreakBefore.java"), expected);
    }

    @Test
    public void testForceLineBreakBefore2() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(RightCurlyCheck.class);
        final String[] expected = CommonUtil.EMPTY_STRING_ARRAY;
        verify(checkConfig, getPath("InputRightCurlyLineBreakBefore.java"), expected);
    }

    @Test
    public void testNullPointerException() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(RightCurlyCheck.class);
        checkConfig.addAttribute("option", RightCurlyOption.ALONE.toString());
        checkConfig.addAttribute("tokens", "CLASS_DEF, METHOD_DEF, CTOR_DEF, LITERAL_FOR, "
            + "LITERAL_WHILE, LITERAL_DO, STATIC_INIT, INSTANCE_INIT");
        final String[] expected = CommonUtil.EMPTY_STRING_ARRAY;
        verify(checkConfig, getPath("InputRightCurlyEmptyAbstractMethod.java"), expected);
    }

    @Test
    public void testWithAnnotations() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(RightCurlyCheck.class);
        checkConfig.addAttribute("option", RightCurlyOption.ALONE.toString());
        checkConfig.addAttribute("tokens", "LITERAL_TRY, LITERAL_CATCH, LITERAL_FINALLY, "
            + "LITERAL_IF, LITERAL_ELSE, CLASS_DEF, METHOD_DEF, CTOR_DEF, LITERAL_FOR, "
            + "LITERAL_WHILE, LITERAL_DO, STATIC_INIT, INSTANCE_INIT");
        final String[] expected = {
            "8:77: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 77),
            "11:65: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 65),
            "22:46: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 46),
            "26:31: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 31),
            "29:35: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 35),
            "32:36: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 36),
            "38:73: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 73),
            "40:37: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 37),
            "45:58: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 58),
            "47:88: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 88),
            "50:30: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 30),
            "53:30: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 30),
            "60:38: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 38),
            "67:62: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 62),
            "76:28: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 28),
            "78:21: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 21),
            "80:20: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 20),
            "82:14: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 14),
            "93:26: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 26),
            "103:29: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 29),
            "107:29: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 29),
            "111:88: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 88),
            "111:40: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 40),
            "114:18: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 18),
            "118:23: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 23),
            "121:37: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 37),
            "123:30: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 30),
            "127:77: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 77),
            "136:9: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 9),
            "138:9: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 9),
            "138:33: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 33),
            "150:75: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 75),
            "151:58: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 58),
            "151:74: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 74),
            "152:58: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 58),
            "153:58: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 58),
            "153:74: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 74),
            "159:37: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 37),
            "166:37: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 37),
            "181:9: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 9),
            "188:9: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 9),
            "188:13: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 13),
            "197:9: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 9),
            "197:10: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 10),
            "201:54: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 54),
            "201:55: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 55),
            "204:75: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 75),
            "204:76: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 76),
            "204:77: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 77),
            "208:76: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 76),
            "216:27: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 27),
        };
        verify(checkConfig, getPath("InputRightCurlyAnnotations.java"), expected);
    }

    @Test
    public void testAloneOrSingleLine() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(RightCurlyCheck.class);
        checkConfig.addAttribute("option", RightCurlyOption.ALONE_OR_SINGLELINE.toString());
        checkConfig.addAttribute("tokens", "LITERAL_TRY, LITERAL_CATCH, LITERAL_FINALLY, "
            + "LITERAL_IF, LITERAL_ELSE, CLASS_DEF, METHOD_DEF, CTOR_DEF, LITERAL_FOR, "
            + "LITERAL_WHILE, LITERAL_DO, STATIC_INIT, INSTANCE_INIT");
        final String[] expected = {
            "60:26: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 26),
            "74:42: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 42),
            "77:18: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 18),
            "85:30: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 30),
            "89:77: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 77),
            "97:9: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 9),
            "99:9: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 9),
            "110:75: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 75),
            "111:77: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 77),
            "111:93: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 93),
            "112:77: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 77),
            "113:64: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 64),
            "113:80: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 80),
            "119:37: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 37),
            "126:37: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 37),
            "141:9: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 9),
            "148:9: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 9),
            "148:13: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 13),
            "157:9: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 9),
            "157:10: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 10),
            "161:54: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 54),
            "161:55: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 55),
            "164:75: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 75),
            "164:76: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 76),
            "168:80: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 80),
            "164:77: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 77),
            "176:27: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 27),
            "182:24: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 24),
            "185:24: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 24),
            "188:24: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 24),
        };
        verify(checkConfig, getPath("InputRightCurlyAloneOrSingleline.java"), expected);
    }

    @Test
    public void testCatchWithoutFinally() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(RightCurlyCheck.class);
        final String[] expected = {
            "15:13: " + getCheckMessage(MSG_KEY_LINE_SAME, "}", 13),
        };
        verify(checkConfig, getPath("InputRightCurly.java"), expected);
    }

    @Test
    public void testSingleLineClass() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(RightCurlyCheck.class);
        checkConfig.addAttribute("option", RightCurlyOption.ALONE.toString());
        checkConfig.addAttribute("tokens", "CLASS_DEF");
        final String[] expected = {
            "24:37: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 37),
        };
        verify(checkConfig, getPath("InputRightCurly.java"), expected);
    }

    @Test
    public void testInvalidOption() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(RightCurlyCheck.class);
        checkConfig.addAttribute("option", "invalid_option");

        try {
            final String[] expected = CommonUtil.EMPTY_STRING_ARRAY;

            verify(checkConfig, getPath("InputRightCurly.java"), expected);
            fail("exception expected");
        }
        catch (CheckstyleException ex) {
            final String messageStart =
                "cannot initialize module com.puppycrawl.tools.checkstyle.TreeWalker - "
                    + "Cannot set property 'option' to 'invalid_option' in module";
            assertTrue("Invalid exception message, should start with: " + messageStart,
                ex.getMessage().startsWith(messageStart));
        }
    }

    @Test
    public void testRightCurlySameAndLiteralDo() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(RightCurlyCheck.class);
        checkConfig.addAttribute("option", RightCurlyOption.SAME.toString());
        checkConfig.addAttribute("tokens", "LITERAL_DO");
        final String[] expected = {
            "62:9: " + getCheckMessage(MSG_KEY_LINE_SAME, "}", 9),
            "67:13: " + getCheckMessage(MSG_KEY_LINE_SAME, "}", 13),
            "83:9: " + getCheckMessage(MSG_KEY_LINE_SAME, "}", 9),
        };
        verify(checkConfig, getPath("InputRightCurlyDoWhile.java"), expected);
    }

    @Test
    public void testTryWithResourceSame() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(RightCurlyCheck.class);
        checkConfig.addAttribute("option", RightCurlyOption.SAME.toString());
        final String[] expected = {
            "11:9: " + getCheckMessage(MSG_KEY_LINE_SAME, "}", 9),
            "24:67: " + getCheckMessage(MSG_KEY_LINE_SAME, "}", 67),
            "35:15: " + getCheckMessage(MSG_KEY_LINE_BREAK_BEFORE, "}", 15),
            "37:13: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 13),
        };
        verify(checkConfig, getPath("InputRightCurlyTryResource.java"), expected);
    }

    @Test
    public void testTryWithResourceAlone() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(RightCurlyCheck.class);
        checkConfig.addAttribute("option", RightCurlyOption.ALONE.toString());
        final String[] expected = {
            "19:9: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 9),
            "24:67: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 67),
            "25:35: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 35),
            "27:92: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 92),
            "33:67: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 67),
            "35:15: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 15),
            "37:13: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 13),
        };
        verify(checkConfig, getPath("InputRightCurlyTryResource.java"), expected);
    }

    @Test
    public void testTryWithResourceAloneSingle() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(RightCurlyCheck.class);
        checkConfig.addAttribute("option", RightCurlyOption.ALONE_OR_SINGLELINE.toString());
        final String[] expected = {
            "19:9: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 9),
            "35:15: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 15),
            "37:13: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 13),
        };
        verify(checkConfig, getPath("InputRightCurlyTryResource.java"), expected);
    }

    @Test
    public void testBracePolicyAloneAndSinglelineIfBlocks() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(RightCurlyCheck.class);
        checkConfig.addAttribute("option", RightCurlyOption.ALONE.toString());
        final String[] expected = {
            "5:32: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 32),
            "7:45: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 45),
            "7:47: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 47),
        };
        verify(checkConfig, getPath("InputRightCurlySinglelineIfBlocks.java"), expected);
    }

    @Test
    public void testRightCurlyIsAloneLambda() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(RightCurlyCheck.class);
        checkConfig.addAttribute("option", RightCurlyOption.ALONE.toString());
        final String[] expected = CommonUtil.EMPTY_STRING_ARRAY;
        verify(checkConfig, getPath("InputRightCurlyAloneLambda.java"), expected);
    }

    @Test
    public void testRightCurlyIsAloneOrSinglelineLambda() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(RightCurlyCheck.class);
        checkConfig.addAttribute("option",
            RightCurlyOption.ALONE_OR_SINGLELINE.toString());
        final String[] expected = CommonUtil.EMPTY_STRING_ARRAY;
        verify(checkConfig,
            getPath("InputRightCurlyAloneOrSinglelineLambda.java"), expected);
    }

    @Test
    public void testRightCurlyIsSameLambda() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(RightCurlyCheck.class);
        checkConfig.addAttribute("option", RightCurlyOption.SAME.toString());
        final String[] expected = CommonUtil.EMPTY_STRING_ARRAY;
        verify(checkConfig,
            getPath("InputRightCurlySameLambda.java"), expected);
    }

    @Test
    public void testOptionAlone() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(RightCurlyCheck.class);
        checkConfig.addAttribute("option", RightCurlyOption.ALONE.toString());
        checkConfig.addAttribute("tokens", "CLASS_DEF, METHOD_DEF, LITERAL_IF, LITERAL_ELSE, "
                + "LITERAL_DO, LITERAL_WHILE, LITERAL_FOR, STATIC_INIT, INSTANCE_INIT");
        final String[] expected = {
            "7:15: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 15),
            "8:21: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 21),
            "12:26: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 26),
            "21:37: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 37),
            "38:9: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 9),
            "42:37: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 37),
            "45:30: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 30),
            "47:27: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 27),
            "49:17: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 17),
            "51:53: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 53),
            "53:27: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 27),
            "53:52: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 52),
            "61:42: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 42),
            "63:43: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 43),
            "67:5: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 5),
        };
        verify(checkConfig, getPath("InputRightCurlyAlone.java"),
                expected);
    }

    @Test
    public void testOptionAloneOrSingleLine() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(RightCurlyCheck.class);
        checkConfig.addAttribute("option", RightCurlyOption.ALONE_OR_SINGLELINE.toString());
        checkConfig.addAttribute("tokens", "CLASS_DEF, METHOD_DEF, LITERAL_IF, LITERAL_ELSE, "
                + "LITERAL_DO, LITERAL_WHILE, LITERAL_FOR, STATIC_INIT, INSTANCE_INIT");
        final String[] expected = {
            "12:26: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 26),
            "21:37: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 37),
            "29:9: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 9),
            "38:9: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 9),
            "42:37: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 37),
            "63:43: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 43),
            "67:5: " + getCheckMessage(MSG_KEY_LINE_ALONE, "}", 5),
        };
        verify(checkConfig, getPath(
                "InputRightCurlyAloneOrSingleLine2.java"), expected);
    }

}

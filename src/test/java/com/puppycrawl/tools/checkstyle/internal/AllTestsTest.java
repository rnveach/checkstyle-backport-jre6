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

package com.puppycrawl.tools.checkstyle.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.jre6.util.function.Function;

/**
 * AllTestsTest.
 *
 * @noinspection ClassIndependentOfModule
 */
public class AllTestsTest {

    @Test
    public void testAllInputsHaveTest() {
        final Map<String, List<String>> allTests = new HashMap<String, List<String>>();

        walk(new File("src/test/java"),
            new Function<File, Object>() {
                @Override
                public Object apply(File file) {
                    grabAllTests(allTests, file);
                    return null;
                }
            });

        assertFalse(allTests.keySet().isEmpty(), "found tests");

        walk(new File("src/test/resources/com/puppycrawl"),
                new Function<File, Object>() {
                    @Override
                    public Object apply(File file) {
                        verifyInputFile(allTests, file);
                        return null;
                    }
                });
        walk(new File("src/test/resources-noncompilable/com/puppycrawl"),
                new Function<File, Object>() {
                    @Override
                    public Object apply(File file) {
                        verifyInputFile(allTests, file);
                        return null;
                    }
                });
    }

    private static void walk(File dir, Function<File, Object> func) {
        final File[] list = dir.listFiles();

        if (list != null) {
            for (File f : list) {
                func.apply(f);
                if (f.isDirectory()) {
                    walk(f, func);
                }
            }
        }
    }

    @Test
    public void testAllTestsHaveProductionCode() {
        final Map<String, List<String>> allTests = new HashMap<String, List<String>>();

        walk(new File("src/main/java"),
               new Function<File, Object>() {
                    @Override
                    public Object apply(File file) {
                        grabAllFiles(allTests, file);
                        return null;
                    }
                });

        assertFalse(allTests.keySet().isEmpty(), "found tests");

        walk(new File("src/test/java"),
                new Function<File, Object>() {
                    @Override
                    public Object apply(File file) {
                        verifyHasProductionFile(allTests, file);
                        return null;
                    }
                });
    }

    private static void grabAllTests(Map<String, List<String>> allTests, File file) {
        if (file.isFile() && file.getName().endsWith("Test.java")) {
            String path;

            try {
                path = getSimplePath(file.getCanonicalPath()).replace("CheckTest.java", "")
                        .replace("Test.java", "");
            }
            catch (IOException ex) {
                throw new IllegalStateException(ex);
            }

            // override for 'AbstractCheck' naming
            if (path.endsWith(File.separator + "Abstract")) {
                path += "Check";
            }

            final int slash = path.lastIndexOf(File.separatorChar);
            final String packge = path.substring(0, slash);

            List<String> classes = allTests.get(packge);

            if (classes == null) {
                classes = new ArrayList<String>();

                allTests.put(packge, classes);
            }

            classes.add(path.substring(slash + 1));
        }
    }

    private static void grabAllFiles(Map<String, List<String>> allTests, File file) {
        if (file.isFile()) {
            final String path;

            try {
                path = getSimplePath(file.getCanonicalPath());
            }
            catch (IOException ex) {
                throw new IllegalStateException(ex);
            }

            final int slash = path.lastIndexOf(File.separatorChar);
            final String packge = path.substring(0, slash);

            List<String> classes = allTests.get(packge);

            if (classes == null) {
                classes = new ArrayList<String>();

                allTests.put(packge, classes);
            }

            classes.add(path.substring(slash + 1));
        }
    }

    private static void verifyInputFile(Map<String, List<String>> allTests, File file) {
        if (file.isFile()) {
            final String path;

            try {
                path = getSimplePath(file.getCanonicalPath());
            }
            catch (IOException ex) {
                throw new IllegalStateException(ex);
            }

            // until https://github.com/checkstyle/checkstyle/issues/5105
            if (!path.contains(File.separatorChar + "grammar" + File.separatorChar)
                    && !path.contains(File.separatorChar + "foo" + File.separatorChar)
                    && !path.contains(File.separatorChar + "bar" + File.separatorChar)) {
                String fileName = file.getName();
                final boolean skipFileNaming = shouldSkipInputFileNameCheck(path, fileName);

                if (!skipFileNaming) {
                    assertTrue(fileName.startsWith("Input") || fileName.startsWith("Expected"),
                            "Resource must start with 'Input' or 'Expected': " + path);

                    if (fileName.startsWith("Input")) {
                        fileName = fileName.substring(5);
                    }
                    else {
                        fileName = fileName.substring(8);
                    }

                    final int period = fileName.lastIndexOf('.');

                    if (period > 0) {
                        fileName = fileName.substring(0, period);
                    }
                }

                verifyInputFile(allTests, skipFileNaming, path, fileName);
            }
        }
    }

    private static void verifyInputFile(Map<String, List<String>> allTests, boolean skipFileNaming,
            String path, String fileName) {
        List<String> classes;
        int slash = path.lastIndexOf(File.separatorChar);
        String packge = path.substring(0, slash);
        boolean found = false;

        for (int depth = 0; depth < 4; depth++) {
            // -@cs[MoveVariableInsideIf] assignment value is modified later so it can't be
            // moved
            final String folderPath = packge;
            slash = packge.lastIndexOf(File.separatorChar);
            packge = path.substring(0, slash);
            classes = allTests.get(packge);

            if (classes != null
                    && checkInputMatchCorrectFileStructure(classes, folderPath, skipFileNaming,
                            fileName)) {
                found = true;
                break;
            }
        }

        assertTrue(found, "Resource must be named after a Test like 'InputMyCustomCase.java' "
                + "and be in the sub-package of the test like 'mycustom' "
                + "for test 'MyCustomCheckTest': " + path);
    }

    private static void verifyHasProductionFile(Map<String, List<String>> allTests, File file) {
        if (file.isFile()) {
            final String fileName = file.getName().replace("Test.java", ".java");

            if (isTarget(file, fileName)) {
                final String path;

                try {
                    path = getSimplePath(file.getCanonicalPath());
                }
                catch (IOException ex) {
                    throw new IllegalStateException(ex);
                }

                if (!path.contains(File.separatorChar + "grammar" + File.separatorChar)
                        && !path.contains(File.separatorChar + "internal" + File.separatorChar)) {
                    final int slash = path.lastIndexOf(File.separatorChar);
                    final String packge = path.substring(0, slash);
                    final List<String> classes = allTests.get(packge);

                    assertTrue(classes != null && classes.contains(fileName),
                            "Test must be named after a production class "
                            + "and must be in the same package of the production class: " + path);
                }
            }
        }
    }

    private static boolean isTarget(File file, String fileName) {
        return !fileName.endsWith("TestSupport.java")
                // tests external utility XPathEvaluator
                && !"XpathMapper.java".equals(fileName)
                // JavadocMetadataScraper and related classes are temporarily hosted in test
                && !file.getPath().contains("meta")
                // junit 5 wrapper class
                && !file.getPath().contains(File.separatorChar + "jupiter" + File.separatorChar)
                // InlineConfigParser is hosted in test
                && !file.getPath().contains("bdd")
                // Annotation to suppress invocation of forbidden apis
                && !"SuppressForbiddenApi.java".equals(fileName);
    }

    private static boolean checkInputMatchCorrectFileStructure(List<String> classes,
            String folderPath, boolean skipFileNaming, String fileName) {
        boolean result = false;

        for (String clss : classes) {
            if (folderPath.endsWith(File.separatorChar + clss.toLowerCase(Locale.ENGLISH))
                    && (skipFileNaming || fileName.startsWith(clss))) {
                result = true;
                break;
            }
        }

        return result;
    }

    private static boolean shouldSkipInputFileNameCheck(String path, String fileName) {
        return "package-info.java".equals(fileName)
                || "package.html".equals(fileName)
                // special directory for files that can't be renamed or are secondary inputs
                || path.contains(File.separatorChar + "inputs" + File.separatorChar)
                // all inputs must start with 'messages'
                || path.contains(File.separatorChar + "translation" + File.separatorChar);
    }

    private static String getSimplePath(String path) {
        return path.substring(path.lastIndexOf("com" + File.separator + "puppycrawl"));
    }

}

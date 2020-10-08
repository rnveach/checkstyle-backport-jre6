////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2020 the original author or authors.
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

import static com.google.common.truth.Truth.assertWithMessage;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.Definitions;
import com.puppycrawl.tools.checkstyle.checks.javadoc.AbstractJavadocCheck;
import com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocMethodCheck;
import com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocStyleCheck;
import com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocTypeCheck;
import com.puppycrawl.tools.checkstyle.checks.javadoc.WriteTagCheck;
import com.puppycrawl.tools.checkstyle.internal.utils.CheckUtil;
import com.puppycrawl.tools.checkstyle.jre6.file.Path;
import com.puppycrawl.tools.checkstyle.jre6.file.Paths;
import com.puppycrawl.tools.checkstyle.jre6.lang.String7;

public class XpathRegressionTest extends AbstractModuleTestSupport {

    // Checks that not compatible with SuppressionXpathFilter
    // till https://github.com/checkstyle/checkstyle/issues/5777
    public static final Set<String> INCOMPATIBLE_CHECK_NAMES =
        Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
            "NoCodeInFile (reason is that AST is not generated for a file not containing code)",
            "Regexp (reason is at  #7759)",
            "RegexpSinglelineJava (reason is at  #7759)",
            "TrailingComment"
    )));

    // Javadoc checks are not compatible with SuppressionXpathFilter
    // till https://github.com/checkstyle/checkstyle/issues/5770
    // then all of them should be added to the list of incompatible checks
    // and this field should be removed
    public static final Set<String> INCOMPATIBLE_JAVADOC_CHECK_NAMES =
            Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
                    "AtclauseOrder",
                    "JavadocBlockTagLocation",
                    "JavadocMethod",
                    "JavadocMissingWhitespaceAfterAsterisk",
                    "JavadocParagraph",
                    "JavadocStyle",
                    "JavadocTagContinuationIndentation",
                    "JavadocType",
                    "MissingDeprecated",
                    "NonEmptyAtclauseDescription",
                    "RequireEmptyLineBeforeBlockTagGroup",
                    "SingleLineJavadoc",
                    "SummaryJavadoc",
                    "WriteTag"
    )));

    // Older regex-based checks that are under INCOMPATIBLE_JAVADOC_CHECK_NAMES
    // but not subclasses of AbstractJavadocCheck.
    private static final Set<Class<?>> REGEXP_JAVADOC_CHECKS =
            Collections.unmodifiableSet(new HashSet<Class<?>>(Arrays.asList(
                    JavadocStyleCheck.class,
                    JavadocMethodCheck.class,
                    JavadocTypeCheck.class,
                    WriteTagCheck.class
    )));

    // Checks that allowed to have no XPath IT Regression Testing
    // till https://github.com/checkstyle/checkstyle/issues/6207
    private static final Set<String> MISSING_CHECK_NAMES = new HashSet<String>(Arrays.asList(
            "BooleanExpressionComplexity",
            "CatchParameterName",
            "ClassDataAbstractionCoupling",
            "ClassFanOutComplexity",
            "ClassTypeParameterName",
            "ConstantName",
            "CovariantEquals",
            "DescendantToken",
            "DesignForExtension",
            "EmptyBlock",
            "EmptyStatement",
            "EqualsAvoidNull",
            "EqualsHashCode",
            "ExecutableStatementCount",
            "FinalLocalVariable",
            "FinalParameters",
            "HideUtilityClassConstructor",
            "IllegalInstantiation",
            "IllegalToken",
            "IllegalTokenText",
            "IllegalType",
            "InnerAssignment",
            "InnerTypeLast",
            "InterfaceTypeParameterName",
            "JavaNCSS",
            "IllegalImport",
            "LocalFinalVariableName",
            "LocalVariableName",
            "MagicNumber",
            "MemberName",
            "MethodLength",
            "MethodName",
            "MethodTypeParameterName",
            "ModifiedControlVariable",
            "ModifierOrder",
            "MultipleStringLiterals",
            "MutableException",
            "OperatorWrap",
            "PackageName",
            "ParameterAssignment",
            "ParameterName",
            "ParameterNumber",
            "RedundantImport",
            "RedundantModifier",
            "ReturnCount",
            "SeparatorWrap",
            "SimplifyBooleanExpression",
            "SimplifyBooleanReturn",
            "StaticVariableName",
            "StringLiteralEquality",
            "SuperClone",
            "SuperFinalize",
            "SuppressWarnings",
            "ThrowsCount",
            "TypeName",
            "VisibilityModifier"
    ));

    // Modules that will never have xpath support ever because they not report violations
    private static final Set<String> NO_VIOLATION_MODULES = new HashSet<String>(Collections.singletonList(
            "SuppressWarningsHolder"
    ));

    private static Set<String> simpleCheckNames;
    private static Map<String, String> allowedDirectoryAndChecks;
    private static Set<String> internalModules;

    private Path javaDir;
    private Path inputDir;

    @BeforeClass
    public static void setUpBeforeClass() throws IOException {
        simpleCheckNames = CheckUtil.getSimpleNames(CheckUtil.getCheckstyleChecks());

        allowedDirectoryAndChecks = new HashMap<String, String>();

        for (String id : simpleCheckNames) {
            allowedDirectoryAndChecks.put(id.toLowerCase(Locale.ENGLISH), id);
        }

        internalModules = new HashSet<String>();

        for (String moduleName : Definitions.INTERNAL_MODULES) {
            final String[] packageTokens = moduleName.split("\\.");
            internalModules.add(packageTokens[packageTokens.length - 1]);
        }
    }

    @Before
    public void setUp() throws Exception {
        javaDir = Paths.get("src/it/java/" + getPackageLocation());
        inputDir = Paths.get(getPath(""));
    }

    @Override
    protected String getPackageLocation() {
        return "org/checkstyle/suppressionxpathfilter";
    }

    @Override
    protected String getResourceLocation() {
        return "it";
    }

    @Test
    public void validateIncompatibleJavadocCheckNames() throws IOException {
        // subclasses of AbstractJavadocCheck
        final Set<Class<?>> abstractJavadocCheckNames = new HashSet<Class<?>>();
        for (Class<?> check : CheckUtil.getCheckstyleChecks()) {
            if (AbstractJavadocCheck.class.isAssignableFrom(check)) {
                abstractJavadocCheckNames.add(check);
            }
        }
        // add the extra checks
        abstractJavadocCheckNames.addAll(REGEXP_JAVADOC_CHECKS);
        final Set<String> abstractJavadocCheckSimpleNames =
                CheckUtil.getSimpleNames(abstractJavadocCheckNames);
        abstractJavadocCheckSimpleNames.removeAll(internalModules);
        assertWithMessage("INCOMPATIBLE_JAVADOC_CHECK_NAMES should contains all descendants "
                    + "of AbstractJavadocCheck")
            .that(abstractJavadocCheckSimpleNames)
            .isEqualTo(INCOMPATIBLE_JAVADOC_CHECK_NAMES);
    }

    @Test
    public void validateIntegrationTestClassNames() {
        final Set<String> compatibleChecks = new HashSet<String>();
        final File[] javaPaths = javaDir.getFile().listFiles();

        if (javaPaths != null) {
            final Pattern pattern = Pattern.compile("^XpathRegression(.+)Test\\.java$");

            for (File path : javaPaths) {
                assertTrue(path.isFile(), path + " is not a regular file");
                final String filename = path.getName();
                if (filename.startsWith("Abstract")) {
                    continue;
                }

                final Matcher matcher = pattern.matcher(filename);
                assertTrue(matcher.matches(),
                        "Invalid test file: " + filename + ", expected pattern: " + pattern);

                final String check = matcher.group(1);
                assertTrue(simpleCheckNames.contains(check),
                        "Unknown check '" + check + "' in test file: " + filename);

                assertFalse(MISSING_CHECK_NAMES.contains(check),
                        "Check '" + check + "' is now tested. Please update the todo list in"
                                + " XpathRegressionTest.MISSING_CHECK_NAMES");
                assertFalse(INCOMPATIBLE_CHECK_NAMES.contains(check),
                        "Check '" + check + "' is now compatible with SuppressionXpathFilter."
                                + " Please update the todo list in"
                                + " XpathRegressionTest.INCOMPATIBLE_CHECK_NAMES");
                compatibleChecks.add(check);
            }
        }

        // Ensure that all lists are up to date
        final Set<String> allChecks = new HashSet<String>(simpleCheckNames);
        allChecks.removeAll(INCOMPATIBLE_JAVADOC_CHECK_NAMES);
        allChecks.removeAll(INCOMPATIBLE_CHECK_NAMES);
        allChecks.removeAll(Arrays.asList("Regexp", "RegexpSinglelineJava", "NoCodeInFile"));
        allChecks.removeAll(MISSING_CHECK_NAMES);
        allChecks.removeAll(NO_VIOLATION_MODULES);
        allChecks.removeAll(compatibleChecks);
        allChecks.removeAll(internalModules);

        assertTrue(allChecks.isEmpty(), "XpathRegressionTest is missing for ["
                + String7.join(", ", allChecks)
                + "]. Please add them to src/it/java/org/checkstyle/suppressionxpathfilter");
    }

    @Test
    public void validateInputFiles() {
        final File[] dirs = inputDir.getFile().listFiles();

        if (dirs != null) {
            for (File dir : dirs) {
                // input directory must be named in lower case
                assertTrue(dir.isDirectory(), dir + " is not a directory");
                final String dirName = dir.getName();
                assertTrue(allowedDirectoryAndChecks.containsKey(dirName),
                        "Invalid directory name: " + dirName);

                // input directory must be connected to an existing test
                final String check = allowedDirectoryAndChecks.get(dirName);
                final File javaPath = new File(javaDir.toFile(), "XpathRegression" + check + "Test.java");
                assertTrue(javaPath.exists(),
                        "Input directory '" + dir + "' is not connected to Java test case: "
                                + javaPath);

                // input files should named correctly
                validateInputDirectory(dir);
            }
        }
    }

    private static void validateInputDirectory(File checkDir) {
        final File[] inputPaths = checkDir.listFiles();

        if (inputPaths != null) {
            final Pattern pattern = Pattern.compile("^SuppressionXpathRegression(.+)\\.java$");
            final String check = allowedDirectoryAndChecks.get(checkDir.getName());

            for (File inputPath : inputPaths) {
                final String filename = inputPath.getName();
                if (filename.endsWith("java")) {
                    final Matcher matcher = pattern.matcher(filename);
                    assertTrue(matcher.matches(),
                            "Invalid input file '" + inputPath + "', expected pattern:" + pattern);

                    final String remaining = matcher.group(1);
                    assertTrue(remaining.startsWith(check),
                            "Check name '" + check + "' should be included in input file: "
                            + inputPath);
                }
            }
        }
    }
}

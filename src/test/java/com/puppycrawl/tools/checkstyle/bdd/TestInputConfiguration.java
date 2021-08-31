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

package com.puppycrawl.tools.checkstyle.bdd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.TreeWalker;
import com.puppycrawl.tools.checkstyle.jre6.charset.StandardCharsets;

public final class TestInputConfiguration {

    private static final String ROOT_MODULE_NAME = "root";

    private static final Set<String> CHECKER_CHILDREN = new HashSet<String>(Arrays.asList(
            "com.puppycrawl.tools.checkstyle.filefilters.BeforeExecutionExclusionFileFilter",
            "com.puppycrawl.tools.checkstyle.filters.SeverityMatchFilter",
            "com.puppycrawl.tools.checkstyle.filters.SuppressionFilter",
            "com.puppycrawl.tools.checkstyle.filters.SuppressionSingleFilter",
            "com.puppycrawl.tools.checkstyle.filters.SuppressWarningsFilter",
            "com.puppycrawl.tools.checkstyle.filters.SuppressWithPlainTextCommentFilter",
            "com.puppycrawl.tools.checkstyle.checks.header.HeaderCheck",
            "com.puppycrawl.tools.checkstyle.checks.header.RegexpHeaderCheck",
            "com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocPackageCheck",
            "com.puppycrawl.tools.checkstyle.checks.NewlineAtEndOfFileCheck",
            "com.puppycrawl.tools.checkstyle.checks.UniquePropertiesCheck",
            "com.puppycrawl.tools.checkstyle.checks.OrderedPropertiesCheck",
            "com.puppycrawl.tools.checkstyle.checks.regexp.RegexpMultilineCheck",
            "com.puppycrawl.tools.checkstyle.checks.regexp.RegexpSinglelineCheck",
            "com.puppycrawl.tools.checkstyle.checks.regexp.RegexpOnFilenameCheck",
            "com.puppycrawl.tools.checkstyle.checks.sizes.FileLengthCheck",
            "com.puppycrawl.tools.checkstyle.checks.sizes.LineLengthCheck",
            "com.puppycrawl.tools.checkstyle.checks.whitespace.FileTabCharacterCheck"
    ));

    private final List<ModuleInputConfiguration> childrenModules;

    private final List<TestInputViolation> violations;

    private final List<TestInputViolation> filteredViolations;

    private TestInputConfiguration(List<ModuleInputConfiguration> childrenModules,
                                   List<TestInputViolation> violations,
                                   List<TestInputViolation> filteredViolations) {
        this.childrenModules = childrenModules;
        this.violations = violations;
        this.filteredViolations = filteredViolations;
    }

    public List<ModuleInputConfiguration> getChildrenModules() {
        return Collections.unmodifiableList(childrenModules);
    }

    public List<TestInputViolation> getViolations() {
        return Collections.unmodifiableList(violations);
    }

    public List<TestInputViolation> getFilteredViolations() {
        return Collections.unmodifiableList(filteredViolations);
    }

    public DefaultConfiguration createConfiguration() {
        final DefaultConfiguration root = new DefaultConfiguration(ROOT_MODULE_NAME);
        final DefaultConfiguration treeWalker =
                new DefaultConfiguration(TreeWalker.class.getName());
        root.addProperty("charset", StandardCharsets.UTF_8.name());
        for (ModuleInputConfiguration inputConfig : childrenModules) {
            final DefaultConfiguration moduleConfig = inputConfig.createConfiguration();
            if (CHECKER_CHILDREN.contains(moduleConfig.getName())) {
                root.addChild(moduleConfig);
            }
            else {
                treeWalker.addChild(moduleConfig);
            }
        }
        root.addChild(treeWalker);
        return root;
    }

    public DefaultConfiguration createConfigurationWithoutFilters() {
        final DefaultConfiguration root = new DefaultConfiguration(ROOT_MODULE_NAME);
        final DefaultConfiguration treeWalker =
                new DefaultConfiguration(TreeWalker.class.getName());
        root.addProperty("charset", StandardCharsets.UTF_8.name());
        for (ModuleInputConfiguration inputConfig : childrenModules) {
            final DefaultConfiguration moduleConfig = inputConfig.createConfiguration();
            if (!moduleConfig.getName().endsWith("Filter")) {
                if (CHECKER_CHILDREN.contains(moduleConfig.getName())) {
                    root.addChild(moduleConfig);
                }
                else {
                    treeWalker.addChild(moduleConfig);
                }
            }
        }
        root.addChild(treeWalker);
        return root;
    }

    public static final class Builder {

        private final List<ModuleInputConfiguration> childrenModules = new ArrayList<ModuleInputConfiguration>();

        private final List<TestInputViolation> violations = new ArrayList<TestInputViolation>();

        private final List<TestInputViolation> filteredViolations = new ArrayList<TestInputViolation>();

        public void addChildModule(ModuleInputConfiguration childModule) {
            childrenModules.add(childModule);
        }

        public void addViolation(int violationLine, String violationMessage) {
            violations.add(new TestInputViolation(violationLine, violationMessage));
        }

        public void addFilteredViolation(int violationLine, String violationMessage) {
            filteredViolations.add(new TestInputViolation(violationLine, violationMessage));
        }

        public TestInputConfiguration build() {
            return new TestInputConfiguration(
                    childrenModules,
                    violations,
                    filteredViolations
            );
        }
    }
}

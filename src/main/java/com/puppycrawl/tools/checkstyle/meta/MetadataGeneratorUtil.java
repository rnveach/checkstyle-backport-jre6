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

package com.puppycrawl.tools.checkstyle.meta;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.TreeWalker;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.jre6.charset.StandardCharsets;
import com.puppycrawl.tools.checkstyle.jre6.file.Paths;
import com.puppycrawl.tools.checkstyle.jre6.util.function.Function;

/** Class which handles all the metadata generation and writing calls. */
public final class MetadataGeneratorUtil {

    /** Stop instances being created. **/
    private MetadataGeneratorUtil() {
    }

    /**
     * Generate metadata from the module source files available in the input argument path.
     *
     * @param args arguments
     * @throws CheckstyleException checkstyleException
     */
    public static void generate(String... args) throws CheckstyleException {
        final Checker checker = new Checker();
        checker.setModuleClassLoader(Checker.class.getClassLoader());
        final DefaultConfiguration scraperCheckConfig =
                        new DefaultConfiguration(JavadocMetadataScraper.class.getName());
        final DefaultConfiguration defaultConfiguration = new DefaultConfiguration("configuration");
        final DefaultConfiguration treeWalkerConfig =
                new DefaultConfiguration(TreeWalker.class.getName());
        defaultConfiguration.addAttribute("charset", StandardCharsets.UTF_8.name());
        defaultConfiguration.addChild(treeWalkerConfig);
        treeWalkerConfig.addChild(scraperCheckConfig);
        checker.configure(defaultConfiguration);
        dumpMetadata(checker, args[0]);
    }

    /**
     * Process files using the checker passed and write to corresponding XML files.
     *
     * @param checker checker
     * @param path rootPath
     * @throws CheckstyleException checkstyleException
     */
    private static void dumpMetadata(Checker checker, String path) throws CheckstyleException {
        final List<File> validFiles = new ArrayList<File>();
        if (path.endsWith(".java")) {
            validFiles.add(new File(path));
        }
        else {
            final List<String> moduleFolders = Arrays.asList("checks", "filters", "filefilters");
            for (String folder : moduleFolders) {
                walk(Paths.get(path + "/" + folder).getFile(),
                    new Function<File, Object>() {
                        @Override
                        public Object apply(File file) {
                            if (file.getName().endsWith("SuppressWarningsHolder.java")
                                        || file.getName().endsWith("Check.java")
                                        || file.getName().endsWith("Filter.java")) {
                                validFiles.add(file);
                            }
                            return null;
                        }
                    });
            }
        }

        checker.process(validFiles);
    }

    /**
     * Walks the files in the directory, and sub-directories, and executes the
     * provided function for each file.
     *
     * @param dir The directory to walk.
     * @param func The function to execute for each file.
     */
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
}

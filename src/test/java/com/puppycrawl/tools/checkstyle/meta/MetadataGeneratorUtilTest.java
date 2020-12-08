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

package com.puppycrawl.tools.checkstyle.meta;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.internal.utils.CheckUtil;
import com.puppycrawl.tools.checkstyle.jre6.file.Path;
import com.puppycrawl.tools.checkstyle.jre6.util.function.Function;

public final class MetadataGeneratorUtilTest {
    private final List<String> modulesContainingNoMetadataFile = Arrays.asList(
            "Checker",
            "TreeWalker",
            "JavadocMetadataScraper"
    );

    @Test
    public void generateMetadataFiles() throws Exception {
        MetadataGeneratorUtil.generate(System.getProperty("user.dir")
                + "/src/main/java/com/puppycrawl/tools/checkstyle");
        final Set<String> metaFiles = new TreeSet<String>();

        walk(new File(System.getProperty("user.dir") + "/src/main/resources/com/puppycrawl"
                        + "/tools/checkstyle/meta"),
            new Function<File, Object>() {
                @Override
                public Object apply(File file) {
                    if (file.isFile()) {
                        metaFiles.add(getMetaFileName(new Path(file)));
                    }
                    return null;
                }
            });
        final Set<String> checkstyleModules =
                CheckUtil.getSimpleNames(CheckUtil.getCheckstyleModules());
        checkstyleModules.removeAll(modulesContainingNoMetadataFile);
        assertEquals("Number of generated metadata files dont match with number of checkstyle "
                        + "module", checkstyleModules, metaFiles);
    }

    /**
     * Get meta file name from full file name.
     *
     * @param file file to process
     * @return meta file name
     */
    private static String getMetaFileName(Path file) {
        final String fileName = file.getFileName().toString();
        final int lengthToOmit;
        if (fileName.contains("Check")) {
            lengthToOmit = "Check.xml".length();
        }
        else {
            lengthToOmit = ".xml".length();
        }
        return fileName.substring(0, fileName.length() - lengthToOmit);
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
}

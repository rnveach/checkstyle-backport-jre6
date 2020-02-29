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

package com.puppycrawl.tools.checkstyle.jre6.file;

import java.io.File;

public class Path {
    private String fileName;

    public Path(String fileName) {
        this.fileName = fileName;
    }

    public Path(File file) {
        this.fileName = file.getAbsolutePath();
    }

    public Path getParent() {
        final String parent = this.getFile().getParent();

        if (parent == null) {
            return null;
        }

        return new Path(parent);
    }

    public Object relativize(Path pathAbsolute) {
        return this.getFile().toURI().relativize(pathAbsolute.getFile().toURI()).getPath();
    }

    public File getFile() {
        return new File(this.fileName);
    }

    public Path normalize() {
        return new Path(this.getFile().toURI().normalize().toString());
    }

    public File toFile() {
        return getFile();
    }

    public Object getFileName() {
        return getFile().getName();
    }

    @Override
    public String toString() {
        return getFile().getAbsolutePath();
    }
}

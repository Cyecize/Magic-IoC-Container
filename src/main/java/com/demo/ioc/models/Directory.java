package com.demo.ioc.models;

import com.demo.ioc.enums.DirectoryType;

public class Directory {
    private final String directory;

    private final DirectoryType directoryType;

    public Directory(String directory, DirectoryType directoryType) {
        this.directory = directory;
        this.directoryType = directoryType;
    }

    public String getDirectory() {
        return this.directory;
    }

    public DirectoryType getDirectoryType() {
        return this.directoryType;
    }
}

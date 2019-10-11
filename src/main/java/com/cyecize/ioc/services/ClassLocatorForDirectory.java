package com.cyecize.ioc.services;

import com.cyecize.ioc.constants.Constants;
import com.cyecize.ioc.exceptions.ClassLocationException;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * ClassLocator implementation for directories.
 * <p>
 * User recursion to scan all files in the source root directory and filters
 * those that are classes (end with ".class")
 */
public class ClassLocatorForDirectory implements ClassLocator {
    private static final String INVALID_DIRECTORY_MSG = "Invalid directory '%s'.";

    private final Set<Class<?>> locatedClasses;

    private final ClassLoader classLoader;

    public ClassLocatorForDirectory(ClassLoader classLoader) {
        this.locatedClasses = new HashSet<>();
        this.classLoader = classLoader;
    }

    /**
     * @param directory the given directory.
     * @return a set of located classes.
     */
    @Override
    public Set<Class<?>> locateClasses(String directory) throws ClassLocationException {
        this.locatedClasses.clear();
        File file = new File(directory);

        if (!file.isDirectory()) {
            throw new ClassLocationException(String.format(INVALID_DIRECTORY_MSG, directory));
        }

        try {
            for (File innerFile : file.listFiles()) {
                this.scanDir(innerFile, "");
            }
        } catch (ClassNotFoundException e) {
            throw new ClassLocationException(e.getMessage(), e);
        }

        return this.locatedClasses;
    }

    /**
     * Recursive method for listing all files in a directory.
     * <p>
     * Starts with empty package name - ""
     * If the file is directory, for each sub file calls this method again
     * with the package name having the current file's name and a dot "." appended
     * in order to build a proper package name.
     * <p>
     * If the file is file and its name ends with ".class" it is loaded using the
     * built package name and it is added to a set of located classes.
     *
     * @param file        the current file.
     * @param packageName the current package name.
     */
    private void scanDir(File file, String packageName) throws ClassNotFoundException {
        if (file.isDirectory()) {
            packageName += file.getName() + ".";

            for (File innerFile : file.listFiles()) {
                this.scanDir(innerFile, packageName);
            }
        } else {
            if (!file.getName().endsWith(Constants.JAVA_BINARY_EXTENSION)) {
                return;
            }

            final String className = packageName + file.getName().replace(Constants.JAVA_BINARY_EXTENSION, "");

            this.locatedClasses.add(Class.forName(className, true, this.classLoader));
        }
    }
}

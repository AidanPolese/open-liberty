package com.ibm.ws.security.utility.tasks;

import java.io.File;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Two files are considered to match if their absolute path
 * is the same.
 */
public class FileMatcher extends TypeSafeMatcher<File> {
    private final File file;
    private String error = "NO ERROR";

    /**
     * Creates a FileMatcher to compare the expected file against an
     * actual one.
     * 
     * @param f The expected file to compare against.
     */
    public FileMatcher(File f) {
        if (f == null) {
            throw new IllegalArgumentException("FileMatcher does not support null File in constructor");
        }
        this.file = f;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matchesSafely(File f) {
        boolean matches = false;
        if (f.getAbsolutePath().equals(file.getAbsolutePath())) {
            matches = true;
        } else {
            error = "Path comparison failed, expected path=[" +
                        file.getAbsolutePath() +
                        "] but got [" + f.getAbsolutePath() + "]";
        }
        return matches;
    }

    /**
     * {@inheritDoc}
     */
    public void describeTo(Description description) {
        description.appendText(error);
    }
}

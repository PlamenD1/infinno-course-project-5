package org.example.PostAPI.Utils;

import java.util.regex.Matcher;

public class PathInfo {
    public String pathName;
    public Matcher matcher;

    public PathInfo(String pathName, Matcher matcher) {
        this.pathName = pathName;
        this.matcher = matcher;
    }
}
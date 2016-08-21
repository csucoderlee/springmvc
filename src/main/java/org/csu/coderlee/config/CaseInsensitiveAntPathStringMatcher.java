package org.csu.coderlee.config;

import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lixiang on 16/8/21.
 */
public class CaseInsensitiveAntPathStringMatcher {

    private static final Pattern GLOB_PATTERN = Pattern.compile("\\?|\\*|\\{((?:\\{[^/]+?\\}|[^/{}]|\\\\[{}])+?)\\}");

    private static final String DEFAULT_VARIABLE_PATTERN = "(.*)";

    private final Pattern pattern;

    private final List<String> variableNames = new LinkedList<String>();


    /** Construct a new instance of the <code>AntPatchStringMatcher</code>. */
    CaseInsensitiveAntPathStringMatcher(String pattern) {
        this.pattern = createPattern(pattern);
    }

    private Pattern createPattern(String pattern) {
        StringBuilder patternBuilder = new StringBuilder();
        Matcher m = GLOB_PATTERN.matcher(pattern);
        int end = 0;
        while (m.find()) {
            patternBuilder.append(quote(pattern, end, m.start()));
            String match = m.group();
            if ("?".equals(match)) {
                patternBuilder.append('.');
            }
            else if ("*".equals(match)) {
                patternBuilder.append(".*");
            }
            else if (match.startsWith("{") && match.endsWith("}")) {
                int colonIdx = match.indexOf(':');
                if (colonIdx == -1) {
                    patternBuilder.append(DEFAULT_VARIABLE_PATTERN);
                    variableNames.add(m.group(1));
                }
                else {
                    String variablePattern = match.substring(colonIdx + 1, match.length() - 1);
                    patternBuilder.append('(');
                    patternBuilder.append(variablePattern);
                    patternBuilder.append(')');
                    String variableName = match.substring(1, colonIdx);
                    variableNames.add(variableName);
                }
            }
            end = m.end();
        }
        patternBuilder.append(quote(pattern, end, pattern.length()));
        return Pattern.compile(patternBuilder.toString(), Pattern.CASE_INSENSITIVE);
    }

    private String quote(String s, int start, int end) {
        if (start == end) {
            return "";
        }
        return Pattern.quote(s.substring(start, end));
    }

    /**
     * Main entry point.
     *
     * @return <code>true</code> if the string matches against the pattern, or <code>false</code> otherwise.
     */
    public boolean matchStrings(String str, Map<String, String> uriTemplateVariables) {
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) {
            if (uriTemplateVariables != null) {
                // SPR-8455
                Assert.isTrue(variableNames.size() == matcher.groupCount(),
                        "The number of capturing groups in the pattern segment " + pattern +
                                " does not match the number of URI template variables it defines, which can occur if " +
                                " capturing groups are used in a URI template regex. Use non-capturing groups instead.");
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    String name = this.variableNames.get(i - 1);
                    String value = matcher.group(i);
                    uriTemplateVariables.put(name, value);
                }
            }
            return true;
        }
        else {
            return false;
        }
    }
}

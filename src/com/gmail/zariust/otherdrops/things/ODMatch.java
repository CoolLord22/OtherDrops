package com.gmail.zariust.otherdrops.things;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ODMatch {
    private String msg;

    public ODMatch(String msg) {
        this.msg = msg;
    }

    /** Takes a given pattern, searches for any match & runs provided 
     *  code (via custom ODMatchRunner) to transform each result.
     *  
     * @param msg
     * @param patternString
     * @return
     */
    public String match(String patternString, ODMatchRunner runner) {
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(msg);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, runner.runMatch(matcher.group(1)));
        }
        matcher.appendTail(sb);
        msg = sb.toString();
        return msg;
    }            
}

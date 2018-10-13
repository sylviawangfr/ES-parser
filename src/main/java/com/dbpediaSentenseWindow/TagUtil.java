package com.dbpediaSentenseWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagUtil {

    public String mergeTags(String taggedSW) {
        String merged = taggedSW.replace("</head> <head>", " ")
                .replace("</head><head>", "")
                .replace("</tail> <tail>", " ")
                .replace("</tail><tail>", "");
        merged = merged.replaceFirst("<head>", "<HEAD>")
                .replaceFirst("</head>", "</HEAD>")
                .replace("<head>", "")
                .replace("</head>", "")
                .replaceFirst("<tail>", "<TAIL>")
                .replaceFirst("</tail>", "</TAIL>")
                .replace("<tail>", "")
                .replace("</tail>", "")
                .replace("<HEAD>", "<head>")
                .replace("</HEAD>", "</head>")
                .replace("<TAIL>", "<tail>")
                .replace("</TAIL>", "</tail>");

        return merged;
    }

    public String mergeTagsNearest(String taggedSW) {
        String merged = taggedSW.replace("</head> <head>", " ")
                .replace("</head><head>", "")
                .replace("</tail> <tail>", " ")
                .replace("</tail><tail>", "");

        String tmp = merged;
        String headRegex = "(?=<head>).*?(?<=<\\/head>)";
        String tailRegex = "(?=<tail>).*?(?<=<\\/tail>)";

        List<List<Integer>> headM = getLongestMatch(getTagMatrics(headRegex, merged));
        List<List<Integer>> tailM = getLongestMatch(getTagMatrics(tailRegex, merged));

        List<Integer> headDistance = getFirstColumn(headM);
        List<Integer> tailDistance = getFirstColumn(tailM);

        int shortest = taggedSW.length();
        int headIndex = 0;
        int tailIndex = 0;
        for (int i = 0; i < headDistance.size(); i++) {
            for (int j = 0; j < tailDistance.size(); j++) {
                int d = Math.abs(headDistance.get(i) - tailDistance.get(j));
                if (d < shortest) {
                    shortest = d;
                    headIndex = headDistance.get(i);
                    tailIndex = tailDistance.get(j);
                }
            }
        }

        merged = replaceUnnecessaryTags(merged, headIndex, tailIndex);

        return merged;

    }

    private String replaceUnnecessaryTags(String s, int startHead, int startTail) {
        String subStringFirstPart = s.substring(0, startHead);
        String subStringSecondPart = s.substring(startHead, s.length());
        String keepStart = "<KEEP>";
        String keepEnd = "</KEEP>";
        String deleteStart = "<DLTE>";
        String deleteEnd = "</DLTE>";
        subStringFirstPart = subStringFirstPart.replace("<head>", deleteStart)
                .replace("</head>", deleteEnd);
        subStringSecondPart = subStringSecondPart.replaceFirst("<head>", keepStart)
                .replaceFirst("</head>", keepEnd)
                .replace("<head>", deleteStart)
                .replace("</head>", deleteEnd)
                .replace(keepStart, "<head>")
                .replace(keepEnd, "</head>");

        String replaced = subStringFirstPart + subStringSecondPart;

        subStringFirstPart = replaced.substring(0, startTail);
        subStringSecondPart = replaced.substring(startTail, s.length());
        subStringFirstPart = subStringFirstPart.replace("<tail>", deleteStart)
                .replace("</tail>", deleteEnd);
        subStringSecondPart = subStringSecondPart.replaceFirst("<tail>", keepStart)
                .replaceFirst("</tail>", keepEnd)
                .replace("<tail>", deleteStart)
                .replace("</tail>", deleteEnd)
                .replace(keepStart, "<tail>")
                .replace(keepEnd, "</tail>");

        replaced = subStringFirstPart + subStringSecondPart;

        return replaced.replace(deleteEnd, "").replace(deleteStart, "");
    }

    private List<Integer> getFirstColumn(List<List<Integer>> matric) {
        List<Integer> col = new ArrayList<>();
        for (List<Integer> r : matric) {
            col.add(r.get(0));
        }
        return col;
    }

    private List<List<Integer>> getLongestMatch(List<List<Integer>> matrics) {
        List<List<Integer>> longestMatches = new ArrayList<>();
        int longest = matrics.get(0).get(1);
        for (int i = 1; i < matrics.size(); i++) {
            if (matrics.get(i).get(1) > longest) {
                longest = matrics.get(i).get(1);
            }
        }

        for (int i = 1; i < matrics.size(); i++) {
            if (matrics.get(i).get(1) == longest) {
                longestMatches.add(Arrays.asList(matrics.get(i).get(0), matrics.get(i).get(1)));
            }
        }

        return longestMatches;

    }

    private List<List<Integer>> getTagMatrics(String regex, String taggedSW) {
        String tmp = taggedSW;
        Pattern r = Pattern.compile(regex);
        Matcher m = r.matcher(tmp);
        List<List<Integer>> matrics = new ArrayList<>();
        //[index, length],[index, length]
        while (m.find()) {
            matrics.add(Arrays.asList(m.start(), m.end() - m.start()));
            System.out.print(" start index: " + m.start());
            System.out.print(" End index: " + m.end());
            System.out.println(" Found: " + m.group());
        }
        return matrics;
    }
}

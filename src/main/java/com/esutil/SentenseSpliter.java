package com.esutil;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SentenseSpliter {

    public static List<String> splitToSentences(String source) {

        List<String> sentences = new ArrayList<>();

        BreakIterator bi = BreakIterator.getSentenceInstance(Locale.US);

        bi.setText(source);

        int lastIndex = bi.first();
        while (lastIndex != BreakIterator.DONE) {
            int firstIndex = lastIndex;
            lastIndex = bi.next();

            if (lastIndex != BreakIterator.DONE) {
                String sentence = source.substring(firstIndex, lastIndex);
                if (!sentence.equals(".")) {
                    sentences.add(sentence);
                }
            }
        }
        return sentences;
    }
}

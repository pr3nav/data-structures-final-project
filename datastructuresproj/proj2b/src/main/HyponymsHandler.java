package main;

import browser.NgordnetQueryHandler;
import browser.NgordnetQuery;
import ngrams.NGramMap;
import ngrams.TimeSeries;
import ngordnet.main.WordNetGraph;

import java.util.*;

public class HyponymsHandler extends NgordnetQueryHandler {
    private final WordNetGraph wordNet;
    private final NGramMap ngramMap;

    public HyponymsHandler(String synsets, String hyponyms, String words, String counts) {
        wordNet = new WordNetGraph(synsets, hyponyms);
        ngramMap = new NGramMap(words, counts);
    }

    @Override
    public String handle(NgordnetQuery q) {
        List<String> queryWords = q.words();
        int k = q.k();
        int startYear = q.startYear();
        int endYear = q.endYear();

        // Step 1: Intersect hyponyms of all query words
        Set<String> result = null;
        for (String word : queryWords) {
            Set<String> hyponyms = wordNet.hyponymsOf(word);
            if (result == null) {
                result = new HashSet<>(hyponyms);
            } else {
                result.retainAll(hyponyms);
            }
        }

        if (result == null || result.isEmpty()) {
            return "[]";
        }

        // Step 2: Filter only words in words file and not in original query
        Set<String> validWords = new HashSet<>();
        for (String word : result) {
            if (!ngramMap.countHistory(word).isEmpty() && !queryWords.contains(word)) {
                validWords.add(word);
            }
        }

        if (k == 0) {
            List<String> sorted = new ArrayList<>(validWords);
            Collections.sort(sorted);  // lexicographically
            return sorted.toString();
        }

        // Step 3: Build frequency map over time range
        Map<String, Double> freqMap = new HashMap<>();
        for (String word : validWords) {
            TimeSeries ts = ngramMap.countHistory(word, startYear, endYear);
            double total = ts.values().stream().mapToDouble(Double::doubleValue).sum();
            freqMap.put(word, total);
        }

        // Step 4: Sort by frequency descending, then lexicographically
        List<String> sorted = new ArrayList<>(freqMap.keySet());
        sorted.sort(Comparator
                .comparingDouble((String w) -> freqMap.get(w)).reversed()
                .thenComparing(Comparator.naturalOrder()));

        return sorted.subList(0, Math.min(k, sorted.size())).toString();
    }
}

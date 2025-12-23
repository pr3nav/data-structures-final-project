package ngrams;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NGramMap {

    private final Map<String, TimeSeries> wordCountMap;
    private final TimeSeries totalCounts;

    public NGramMap(String wordsFilename, String countsFilename) {
        wordCountMap = new HashMap<>();
        totalCounts = new TimeSeries();

        try (BufferedReader reader = new BufferedReader(new FileReader(wordsFilename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length < 3) {
                    continue;
                }
                String word = parts[0];
                int year = Integer.parseInt(parts[1]);
                double count = Double.parseDouble(parts[2]);

                wordCountMap.putIfAbsent(word, new TimeSeries());
                wordCountMap.get(word).put(year, count);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading words file", e);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(countsFilename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 2) {
                    continue;
                }
                int year = Integer.parseInt(parts[0]);
                double count = Double.parseDouble(parts[1]);
                totalCounts.put(year, count);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading counts file", e);
        }
    }

    public TimeSeries countHistory(String word, int startYear, int endYear) {
        if (!wordCountMap.containsKey(word)) {
            return new TimeSeries();
        }
        return new TimeSeries(wordCountMap.get(word), startYear, endYear);
    }

    public TimeSeries countHistory(String word) {
        if (!wordCountMap.containsKey(word)) {
            return new TimeSeries();
        }
        return new TimeSeries(wordCountMap.get(word), TimeSeries.MIN_YEAR, TimeSeries.MAX_YEAR);
    }

    public TimeSeries totalCountHistory() {
        return new TimeSeries(totalCounts, TimeSeries.MIN_YEAR, TimeSeries.MAX_YEAR);
    }

    public TimeSeries weightHistory(String word, int startYear, int endYear) {
        if (!wordCountMap.containsKey(word)) {
            return new TimeSeries();
        }
        TimeSeries wordTs = new TimeSeries(wordCountMap.get(word), startYear, endYear);
        TimeSeries totalTs = new TimeSeries(totalCounts, startYear, endYear);
        return wordTs.dividedBy(totalTs);
    }

    public TimeSeries weightHistory(String word) {
        if (!wordCountMap.containsKey(word)) {
            return new TimeSeries();
        }
        TimeSeries wordTs = new TimeSeries(wordCountMap.get(word), TimeSeries.MIN_YEAR, TimeSeries.MAX_YEAR);
        return wordTs.dividedBy(totalCounts);
    }

    public TimeSeries summedWeightHistory(Collection<String> words, int startYear, int endYear) {
        TimeSeries total = new TimeSeries();
        for (String word : words) {
            if (wordCountMap.containsKey(word)) {
                TimeSeries rel = weightHistory(word, startYear, endYear);
                total = total.plus(rel);
            }
        }
        return total;
    }

    public TimeSeries summedWeightHistory(Collection<String> words) {
        TimeSeries total = new TimeSeries();
        for (String word : words) {
            if (wordCountMap.containsKey(word)) {
                TimeSeries rel = weightHistory(word);
                total = total.plus(rel);
            }
        }
        return total;
    }
}
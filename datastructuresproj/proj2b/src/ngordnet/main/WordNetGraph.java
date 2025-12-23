package ngordnet.main;

import java.util.*;
import java.io.*;

public class WordNetGraph {
    private Map<Integer, List<String>> idToWords = new HashMap<>();
    private Map<String, Set<Integer>> wordToIds = new HashMap<>();
    private Map<Integer, Set<Integer>> graph = new HashMap<>();

    public WordNetGraph(String synsetFile, String hyponymFile) {
        parseSynsets(synsetFile);
        parseHyponyms(hyponymFile);
    }

    private void parseSynsets(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                String[] words = parts[1].split(" ");

                idToWords.put(id, Arrays.asList(words));
                for (String word : words) {
                    wordToIds.putIfAbsent(word, new HashSet<>());
                    wordToIds.get(word).add(id);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading synsets file", e);
        }
    }

    private void parseHyponyms(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int src = Integer.parseInt(parts[0]);

                graph.putIfAbsent(src, new HashSet<>());
                for (int i = 1; i < parts.length; i++) {
                    int dest = Integer.parseInt(parts[i]);
                    graph.get(src).add(dest);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading hyponyms file", e);
        }
    }

    public Set<String> hyponymsOf(String word) {
        Set<String> result = new TreeSet<>();
        Set<Integer> ids = wordToIds.getOrDefault(word, new HashSet<>());
        Set<Integer> visited = new HashSet<>();
        Deque<Integer> stack = new ArrayDeque<>(ids);

        while (!stack.isEmpty()) {
            int current = stack.pop();
            if (!visited.add(current)) continue;

            result.addAll(idToWords.getOrDefault(current, new ArrayList<>()));
            for (int neighbor : graph.getOrDefault(current, new HashSet<>())) {
                stack.push(neighbor);
            }
        }

        return result;
    }
}

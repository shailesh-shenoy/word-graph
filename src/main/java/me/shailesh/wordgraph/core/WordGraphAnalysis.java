package me.shailesh.wordgraph.core;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordGraphAnalysis {
    // In this graph, the vertices are the words
    // and the edges are the number of times the words appear together in the text.
    // The graph is represented as an adjacency list.

    private static final String WORD_REGEX = "[a-z0-9_-]+";
    private int n;
    private Map<String, Integer> wordFrequencies;

    private Map<String, List<Edge>> adjacencyList;

    public WordGraphAnalysis() {


    }
    public WordGraphAnalysis(String text) {

        analyzeFrequencies(text);
        this.n = wordFrequencies.size();
        buildAdjacencyList(text);
        System.out.println("n = " + n);
        System.out.println("wordFrequencies = " + wordFrequencies);
    }


    /**
     * Analyze the text and find the frequencies of each word. The text is assumed to be lowercase.
     *
     * @param text the text to create the graph from
     */
    private void analyzeFrequencies(String text) {
        wordFrequencies = new HashMap<>();
        Matcher matcher = Pattern.compile(WORD_REGEX).matcher(text);
        while (matcher.find()) {
            String word = matcher.group();
            wordFrequencies.put(word, wordFrequencies.getOrDefault(word, 0) + 1);
        }
    }

    private void buildAdjacencyList(String text) {
        adjacencyList = new HashMap<>();
    }
}

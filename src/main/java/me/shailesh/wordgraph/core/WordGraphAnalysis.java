package me.shailesh.wordgraph.core;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WordGraphAnalysis {
    // In this graph, the vertices are the words
    // and the edges are the number of times the words appear together in the text.
    // The graph is represented as an adjacency list.

    private static final int MAX_N = 10;
    private static final String WORD_REGEX = "[a-z0-9_-]+";
    private int maxWords;
    private int v;
    private int e;
    private Map<String, Integer> wordFrequencies;
    private Map<String, List<Edge>> adjacencyList;

    public WordGraphAnalysis(String text, int maxWords) {
        e = 0;
        this.maxWords = maxWords;
        analyzeFrequencies(text);
        v = adjacencyList.size();
        buildAdjacencyList(text);
        System.out.println("v = " + v + ", e = " + e);
        System.out.println("wordFrequencies = " + wordFrequencies);
        System.out.println("adjacencyList = " + adjacencyList);
    }

    /**
     * Analyze the text and find the frequencies of each word. The text is assumed to be lowercase.
     *
     * @param text the text to create the graph from
     */
    private void analyzeFrequencies(String text) {
        int wordsToInclude = maxWords;
        if(wordsToInclude == 0) {
            wordsToInclude = MAX_N;
        }
        if(wordsToInclude > MAX_N) {
            wordsToInclude = MAX_N;
        }
        adjacencyList = new HashMap<>();
        var allWordFrequencies = new HashMap<String, Integer>();
        wordFrequencies = new HashMap<>();
        Matcher matcher = Pattern.compile(WORD_REGEX).matcher(text);
        while (matcher.find()) {
            String word = matcher.group();
            allWordFrequencies.put(word, allWordFrequencies.getOrDefault(word, 0) + 1);
        }
        // Filter out words that are not in the top MAX_N words
        List<String> words = new ArrayList<>(allWordFrequencies.keySet());
        words.sort((a, b) -> allWordFrequencies.get(b) - allWordFrequencies.get(a));
        for (int i = 0; i < Math.min(wordsToInclude, words.size()); i++) {
            wordFrequencies.put(words.get(i), allWordFrequencies.get(words.get(i)));
            adjacencyList.put(words.get(i), new ArrayList<>());
        }
    }

    public Path bfs(String start) {
        if(!adjacencyList.containsKey(start)) {
            return null;
        }
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        double pathWeight = 0.0;
        List<String> bfsOrder = new LinkedList<>();

        visited.add(start);
        queue.add(start);
        while(!queue.isEmpty()) {
            var current = queue.poll();
            bfsOrder.add(current);
            for(Edge e : adjacencyList.get(current)) {
                if(!visited.contains(e.to)) {
                    visited.add(e.to);
                    queue.add(e.to);
                    pathWeight+= e.weight;
                }
            }
        }
        return Path.builder().weight(pathWeight).path(bfsOrder).build();
    }

    public Path dfs(String start) {
        if(!adjacencyList.containsKey(start)) {
            return null;
        }
        Stack<Edge> stack = new Stack<>();
        Set<String> visited = new HashSet<>();
        double pathWeight = 0.0;
        List<String> dfsOrder = new LinkedList<>();

        stack.push(new Edge(start, 0.0));
        while(!stack.isEmpty()) {
            var current = stack.pop();
            if(!visited.contains(current.to)) {
                visited.add(current.to);
                dfsOrder.add(current.to);
                pathWeight += current.weight;
            }
            for(var e : adjacencyList.get(current.to)) {
                if(!visited.contains(e.to)) {
                    stack.push(e);
                }
            }
        }
        return Path.builder().weight(pathWeight).path(dfsOrder).build();
    }

    private void buildAdjacencyList(String text) {
        int count = 0;
        // Split the text into sentences
        String[] sentences = text.split("[.!?]");
        Pattern wordPattern = Pattern.compile(WORD_REGEX);
        Matcher matcher;
        // Store pairs of words in the graph in a set
        Set<Set<String>> wordPairs = getWordPairs();
        System.out.println("wordPairs = " + wordPairs);
        // For each pair of words, check if they appear in the same sentence
        // If they do, increment the edge weight
        for (Set<String> wordPair : wordPairs) {
            for (String sentence : sentences) {
                matcher = wordPattern.matcher(sentence);
                // Create a set of words in the sentence
                Set<String> wordsInSentence = new HashSet<>();
                while (matcher.find()) {
                    wordsInSentence.add(matcher.group());
                }
                String[] wordsInPair = wordPair.toArray(new String[0]);
                String word1 = wordsInPair[0];
                String word2 = wordsInPair[1];
                if (wordsInSentence.contains(word1) && wordsInSentence.contains(word2)) {
                    // Add or update edges between the two words
                    updateEdge(word1, word2);
                    updateEdge(word2, word1);
                }
            }
        }
    }

    /**
     * Update the edge in the adjacency list from word1 to word2.
     * If the edge does not exist, create it.
     * If the edge exists, increment the weight.
     * @param word1 the first word
     * @param word2 the second word
     */
    private void updateEdge(String word1, String word2) {
        List<Edge> edges = adjacencyList.get(word1);
        if(edges == null) {
            edges = new ArrayList<>();
        }
        boolean found = false;
        for(Edge edge : edges) {
            if(edge.to.equals(word2)) {
                edge.weight++;
                found = true;
                break;
            }
        }
        if(!found) {
            edges.add(new Edge(word2, 1));
            e++;
        }
    }

    private Set<Set<String>> getWordPairs() {
        Set<Set<String>> wordPairs = new HashSet<>();
        for(String word : adjacencyList.keySet()) {
            for(String otherWord : adjacencyList.keySet()) {
                if(word.equals(otherWord)) {
                    continue;
                }
                var wordPair = new HashSet<String>();
                wordPair.add(word);
                wordPair.add(otherWord);
                wordPairs.add(wordPair);
            }
        }
        return wordPairs;
    }

    public SpanningTree primsMst() {
        SpanningTree mst = new SpanningTree();
        Set<String> visited = new HashSet<>();

        String start = "";
        for(var vertex : adjacencyList.keySet()) {
            mst.addVertex(vertex);
            if(start.isEmpty()) {
                start = vertex;
                mst.addEdge(start, "", 0.0);
                continue;
            }
            mst.addEdge(vertex, "", Double.MAX_VALUE);
        }

        for(int i = 0; i < v - 1; i++) {
            String minVertex = getMinVertex(visited, mst);
            visited.add(minVertex);
            for(var edge : adjacencyList.get(minVertex)) {
                var vertex = edge.to;
                var weight = edge.weight;
                if(visited.contains(vertex)) {
                    continue;
                }
                double weightInMst = mst.getVertexWeight(vertex);
                if(weight >= weightInMst) {
                    continue;
                }
                mst.updateEdge(vertex, minVertex, weight);
            }
        }
        mst.computeWeight();
        return mst;
    }

    public SpanningTree kruskalsMst() {
        SpanningTree mst = new SpanningTree();
        Set<String> visited = new HashSet<>();
        PriorityQueue<DirectedEdge> pq = new PriorityQueue<>(Comparator.comparingDouble(e -> e.weight));
        for(var vertex : adjacencyList.keySet()) {
            mst.addVertex(vertex);
            for(var edge : adjacencyList.get(vertex)) {
                pq.add(new DirectedEdge(vertex, edge.to, edge.weight));
            }
        }
        while(!pq.isEmpty()) {
            var edge = pq.poll();
            if(visited.contains(edge.to) && visited.contains(edge.from)) {
                continue;
            }
            mst.addEdge(edge.from, edge.to, edge.weight);
            visited.add(edge.from);
            visited.add(edge.to);
        }
        mst.computeWeight();
        return mst;
    }

    private String getMinVertex(Set<String> visited, SpanningTree mst) {
        double minWeight = Double.MAX_VALUE;
        String minVertex = "";
        for(var entry : mst.getAdjacencyList().entrySet()) {
            var vertex = entry.getKey();
            if(visited.contains(vertex))
                continue;
            var weight = entry.getValue().getFirst().weight;
            if(weight < minWeight) {
                minWeight = weight;
                minVertex = vertex;
            }
        }
        return minVertex;
    }
}

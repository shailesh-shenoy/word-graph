package me.shailesh.wordgraph.service;

import lombok.AllArgsConstructor;
import me.shailesh.wordgraph.core.WordGraphAnalysis;
import me.shailesh.wordgraph.model.WordGraph;
import me.shailesh.wordgraph.model.WordGraphCreateDto;
import me.shailesh.wordgraph.model.WordGraphDetailDto;
import me.shailesh.wordgraph.model.WordGraphDto;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class WordGraphService {
    private final MongoTemplate mongoTemplate;
    public List<WordGraphDto> getAllWordGraphs() {
        // Query only the required fields
        Query query = new Query();
        query.fields().include("id").include("hash").include("text").include("createdAt").include("maxWords").include("wordCount").include("edgeCount");
        return mongoTemplate.find(query, WordGraph.class).stream()
                .map(wordGraph -> WordGraphDto.builder()
                        .id(wordGraph.getId())
                        .hash(wordGraph.getHash())
                        .text(wordGraph.getText())
                        .maxWords(wordGraph.getMaxWords())
                        .wordCount(wordGraph.getWordCount())
                        .edgeCount(wordGraph.getEdgeCount())
                        .createdAt(wordGraph.getCreatedAt())
                        .build()
                ).toList();
    }

    public WordGraphDetailDto createWordGraph(WordGraphCreateDto wordGraphCreate) {

        String trimmedText = wordGraphCreate.getText().trim().toLowerCase();
        // Get hash of the text
        String hash = getSHA256Hash(trimmedText  + wordGraphCreate.getMaxWords());
        // Check if the word graph already exists
        Query query = new Query();
        query.addCriteria(Criteria.where("hash").is(hash));

        WordGraph wordGraph = mongoTemplate.findOne(query, WordGraph.class);
        if (wordGraph != null) {
            return WordGraphDetailDto.builder()
                    .id(wordGraph.getId())
                    .hash(wordGraph.getHash())
                    .text(wordGraph.getText())
                    .maxWords(wordGraph.getMaxWords())
                    .adjacencyList(wordGraph.getAdjacencyList())
                    .wordFrequencies(wordGraph.getWordFrequencies())
                    .wordCount(wordGraph.getWordCount())
                    .edgeCount(wordGraph.getEdgeCount())
                    .createdAt(wordGraph.getCreatedAt())
                    .build();
        }
        WordGraphAnalysis wga = new WordGraphAnalysis(trimmedText, wordGraphCreate.getMaxWords());
        wordGraph = WordGraph.builder()
                .hash(hash)
                .text(trimmedText)
                .maxWords(wga.getMaxWords())
                .adjacencyList(wga.getAdjacencyList())
                .wordFrequencies(wga.getWordFrequencies())
                .wordCount(wga.getV())
                .edgeCount(wga.getE())
                .createdAt(LocalDateTime.now())
                .build();
        wordGraph = mongoTemplate.save(wordGraph);
        return WordGraphDetailDto.builder()
                .id(wordGraph.getId())
                .hash(wordGraph.getHash())
                .text(wordGraph.getText())
                .maxWords(wordGraph.getMaxWords())
                .adjacencyList(wordGraph.getAdjacencyList())
                .wordFrequencies(wordGraph.getWordFrequencies())
                .wordCount(wordGraph.getWordCount())
                .edgeCount(wordGraph.getEdgeCount())
                .createdAt(wordGraph.getCreatedAt())
                .build();
    }

    public String getSHA256Hash(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error computing hash", e);
        }
    }

    public WordGraphDetailDto getWordGraphById(String id) {
        WordGraph wordGraph = mongoTemplate.findById(id, WordGraph.class);
        if (wordGraph == null) {
            return null;
        }
        return WordGraphDetailDto.builder()
                .id(wordGraph.getId())
                .hash(wordGraph.getHash())
                .text(wordGraph.getText())
                .maxWords(wordGraph.getMaxWords())
                .adjacencyList(wordGraph.getAdjacencyList())
                .wordFrequencies(wordGraph.getWordFrequencies())
                .wordCount(wordGraph.getWordCount())
                .edgeCount(wordGraph.getEdgeCount())
                .createdAt(wordGraph.getCreatedAt())
                .build();
    }

    public WordGraphDetailDto traversal(WordGraphDetailDto wordGraphDetail, String start, String type) {
        var wga = WordGraphAnalysis.builder()
                .v(wordGraphDetail.getWordCount())
                .e(wordGraphDetail.getEdgeCount())
                .wordFrequencies(wordGraphDetail.getWordFrequencies())
                .adjacencyList(wordGraphDetail.getAdjacencyList())
                .build();
        if(type == null) {
            type = "";
        }
        switch(type.toLowerCase()) {
            case "bfs":
                wordGraphDetail.setBfs(wga.bfs(start));
                break;
            case "dfs":
                wordGraphDetail.setDfs(wga.dfs(start));
                break;
            default:
                wordGraphDetail.setBfs(wga.bfs(start));
                wordGraphDetail.setDfs(wga.dfs(start));
                break;
        }
        return wordGraphDetail;
    }

    public WordGraphDetailDto mst(WordGraphDetailDto wordGraphDetail, String type, boolean maximum) {
        var wga = WordGraphAnalysis.builder()
                .v(wordGraphDetail.getWordCount())
                .e(wordGraphDetail.getEdgeCount())
                .wordFrequencies(wordGraphDetail.getWordFrequencies())
                .adjacencyList(wordGraphDetail.getAdjacencyList())
                .build();
        if(type == null) {
            type = "";
        }
        switch(type.toLowerCase()) {
            case "prims":
                if(maximum) {
                    wordGraphDetail.setPrimsMst(wga.primsMstMax());
                    break;
                }
                wordGraphDetail.setPrimsMst(wga.primsMst());
                break;
            case "kruskals":
                if(maximum) {
                    wordGraphDetail.setKruskalsMst(wga.kruskalsMstMax());
                    break;
                }
                wordGraphDetail.setKruskalsMst(wga.kruskalsMst());
                break;
            default:
                if(maximum) {
                    wordGraphDetail.setPrimsMst(wga.primsMstMax());
                    wordGraphDetail.setKruskalsMst(wga.kruskalsMstMax());
                    break;
                }
                wordGraphDetail.setPrimsMst(wga.primsMst());
                wordGraphDetail.setKruskalsMst(wga.kruskalsMst());
                break;
        }
        return wordGraphDetail;
    }

    public WordGraphDetailDto shortestPath(WordGraphDetailDto wordGraphDetail, String start, String type) {
        var wga = WordGraphAnalysis.builder()
                .v(wordGraphDetail.getWordCount())
                .e(wordGraphDetail.getEdgeCount())
                .wordFrequencies(wordGraphDetail.getWordFrequencies())
                .adjacencyList(wordGraphDetail.getAdjacencyList())
                .build();
        if(type == null) {
            type = "";
        }

        switch(type.toLowerCase()) {
            case "floyd-warshall":
                wordGraphDetail.setFloydWarshallShortestPaths(wga.floydWarshallShortestPaths());
                break;
            case "dijkstra":
                if(start == null || start.isBlank()) {
                    wordGraphDetail.setErrorMessage("Start vertex is required for Dijkstra's algorithm");
                    break;
                }
                wordGraphDetail.setDijkstraShortestPath(wga.dijkstraShortestPath(start));
                break;
            default:
                if(start == null || start.isBlank()) {
                    wordGraphDetail.setErrorMessage("Start vertex is required for Dijkstra's algorithm");
                    break;
                }
                wordGraphDetail.setDijkstraShortestPath(wga.dijkstraShortestPath(start));
                wordGraphDetail.setFloydWarshallShortestPaths(wga.floydWarshallShortestPaths());
                break;
        }
        return wordGraphDetail;
    }
}

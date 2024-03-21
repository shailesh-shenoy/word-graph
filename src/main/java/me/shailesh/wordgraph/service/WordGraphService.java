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
        query.fields().include("id").include("hash").include("text").include("createdAt");
        return mongoTemplate.find(query, WordGraph.class).stream()
                .map(wordGraph -> WordGraphDto.builder()
                        .id(wordGraph.getId())
                        .hash(wordGraph.getHash())
                        .text(wordGraph.getText())
                        .createdAt(wordGraph.getCreatedAt())
                        .build()
                ).toList();
    }

    public WordGraphDetailDto createWordGraph(WordGraphCreateDto wordGraphCreate) {

        String trimmedText = wordGraphCreate.getText().trim().toLowerCase();
        // Get hash of the text
        String hash = getSHA256Hash(trimmedText);
        // Check if the word graph already exists
        Query query = new Query();
        query.addCriteria(Criteria.where("hash").is(hash));

        WordGraph wordGraph = mongoTemplate.findOne(query, WordGraph.class);
        if (wordGraph != null) {
            return WordGraphDetailDto.builder()
                    .id(wordGraph.getId())
                    .hash(wordGraph.getHash())
                    .text(wordGraph.getText())
                    .graph(wordGraph.getGraph())
                    .createdAt(wordGraph.getCreatedAt())
                    .build();
        }
        wordGraph = WordGraph.builder()
                .hash(hash)
                .text(trimmedText)
                .graph(WordGraphAnalysis.buildGraph(trimmedText))
                .createdAt(LocalDateTime.now())
                .build();
        wordGraph = mongoTemplate.save(wordGraph);
        return WordGraphDetailDto.builder()
                .id(wordGraph.getId())
                .hash(wordGraph.getHash())
                .text(wordGraph.getText())
                .graph(wordGraph.getGraph())
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
                .graph(wordGraph.getGraph())
                .createdAt(wordGraph.getCreatedAt())
                .build();
    }
}

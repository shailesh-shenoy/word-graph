package me.shailesh.wordgraph.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.shailesh.wordgraph.core.Edge;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "wordgraphs")
public class WordGraph {
    @Id
    private String id;
    @Indexed(unique = true)
    private String hash;
    private String text;
    private int maxWords;
    private Map<String, List<Edge>> adjacencyList;
    private Map<String, Integer> wordFrequencies;
    private int wordCount;
    private int edgeCount;
    private LocalDateTime createdAt;
}

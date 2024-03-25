package me.shailesh.wordgraph.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.shailesh.wordgraph.core.Edge;
import me.shailesh.wordgraph.core.Path;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WordGraphDetailDto {

    private String id;
    private String hash;
    private String text;
    private Map<String, List<Edge>> adjacencyList;
    private Map<String, Integer> wordFrequencies;
    private int edgeCount;
    private LocalDateTime createdAt;

    private Path bfs;
    private Path dfs;
}

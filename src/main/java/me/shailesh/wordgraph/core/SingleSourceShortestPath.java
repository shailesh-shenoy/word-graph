package me.shailesh.wordgraph.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SingleSourceShortestPath {
    private String source;
    private Map<String, Path> shortestPaths;

    public SingleSourceShortestPath(String source) {
        this.source = source;
        shortestPaths = new HashMap<>();
    }
}

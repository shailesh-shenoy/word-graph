package me.shailesh.wordgraph.core;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SpanningTree {
    private double weight;
    private Map<String, List<Edge>> adjacencyList;
}

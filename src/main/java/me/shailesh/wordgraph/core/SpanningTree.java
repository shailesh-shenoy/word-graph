package me.shailesh.wordgraph.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpanningTree {
    private double weight;
    private Map<String, List<Edge>> adjacencyList;

    public void addVertex(String vertex) {
        adjacencyList.put(vertex, new ArrayList<>());
    }

    public void addEdge(String from, String to, double weight) {
        adjacencyList.get(from).add(new Edge(to, weight));
    }

    public double getVertexWeight(String vertex) {
        return adjacencyList.get(vertex).getFirst().weight;
    }

    public void updateEdge(String from, String to, double weight) {
        adjacencyList.get(from).clear();
        adjacencyList.get(from).add(new Edge(to, weight));
    }

    public void computeWeight() {
        weight = 0.0;
        for(var edges : adjacencyList.values()) {
            weight += edges.getFirst().weight;
        }
    }
}

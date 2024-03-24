package me.shailesh.wordgraph.core;

import lombok.Data;

@Data
public class Edge {
    protected String to;
    protected double weight;
    public Edge(String to, double weight) {
        this.to = to;
        this.weight = weight;
    }
}
package me.shailesh.wordgraph.core;

class Edge {
    protected String to;
    protected double weight;
    public Edge(String to, double weight) {
        this.to = to;
        this.weight = weight;
    }
}
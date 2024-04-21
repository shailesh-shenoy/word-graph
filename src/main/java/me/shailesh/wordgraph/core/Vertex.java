package me.shailesh.wordgraph.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vertex implements Comparable<Vertex> {
    protected String name;
    protected double distance;
    protected Vertex predecessor;

    public Vertex(String name) {
        this.name = name;
        this.distance = Double.MAX_VALUE;
        this.predecessor = null;
    }

    @Override
    public int compareTo(Vertex o) {
        return Double.compare(this.distance, o.distance);
    }
}

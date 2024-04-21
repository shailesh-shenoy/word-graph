package me.shailesh.wordgraph.core;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DirectedEdge {
    protected String from;
    protected String to;
    protected double weight;

}

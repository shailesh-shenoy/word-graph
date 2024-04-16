package me.shailesh.wordgraph.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WordGraphCreateDto {
    private String text;
    private int maxWords;
}

package me.shailesh.wordgraph.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WordGraphDto {
    private String id;
    private String hash;
    private String text;
    private int wordCount;
    private int edgeCount;
    private LocalDateTime createdAt;
}

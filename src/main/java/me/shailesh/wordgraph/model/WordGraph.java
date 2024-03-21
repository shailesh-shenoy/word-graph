package me.shailesh.wordgraph.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "wordgraphs")
public class WordGraph {
    @Id
    private String id;
    @Indexed(unique = true)
    private String hash;
    private String text;
    private String graph;
    private LocalDateTime createdAt;
}

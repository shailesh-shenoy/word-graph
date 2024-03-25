package me.shailesh.wordgraph.controller;

import lombok.AllArgsConstructor;
import me.shailesh.wordgraph.model.WordGraph;
import me.shailesh.wordgraph.model.WordGraphCreateDto;
import me.shailesh.wordgraph.model.WordGraphDetailDto;
import me.shailesh.wordgraph.model.WordGraphDto;
import me.shailesh.wordgraph.service.WordGraphService;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/wordgraphs")
@AllArgsConstructor
public class WordGraphController {

    private final WordGraphService wordGraphService;
    @GetMapping
    public ResponseEntity<List<WordGraphDto>> fetchAllWordGraphs() {
        return ResponseEntity.ok(wordGraphService.getAllWordGraphs());
    }

    @PostMapping
    public ResponseEntity<WordGraphDetailDto> createWordGraph(@RequestBody WordGraphCreateDto wordGraphCreate) {
        var wordGraphDetail = wordGraphService.createWordGraph(wordGraphCreate);
        return ResponseEntity.ok(wordGraphDetail);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WordGraphDetailDto> fetchWordGraph(@PathVariable String id) {
        var wordGraphDetail = wordGraphService.getWordGraphById(id);
        if(wordGraphDetail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(wordGraphDetail);
    }

    @GetMapping("/{id}/bfs")
    public ResponseEntity<WordGraphDetailDto> fetchWordGraphBFS(@PathVariable String id, @RequestParam String start) {
        var wordGraphDetail = wordGraphService.getWordGraphById(id);
        if(wordGraphDetail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(wordGraphDetail);
    }
}

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

    @GetMapping("/{id}/traversal")
    public ResponseEntity<WordGraphDetailDto> fetchWordGraphTraversal(@PathVariable String id, @RequestParam String start, @RequestParam(required = false) String type) {
        var wordGraphDetail = wordGraphService.getWordGraphById(id);
        if(wordGraphDetail == null) {
            return ResponseEntity.notFound().build();
        }
        wordGraphDetail = wordGraphService.traversal(wordGraphDetail, start, type);
        return ResponseEntity.ok(wordGraphDetail);
    }

    @GetMapping("/{id}/mst")
    public ResponseEntity<WordGraphDetailDto> fetchWordGraphMst(@PathVariable String id, @RequestParam(required = false) String type, @RequestParam(required = false) boolean maximum) {
        var wordGraphDetail = wordGraphService.getWordGraphById(id);
        if(wordGraphDetail == null) {
            return ResponseEntity.notFound().build();
        }
        wordGraphDetail = wordGraphService.mst(wordGraphDetail, type, maximum);
        return ResponseEntity.ok(wordGraphDetail);
    }

    @GetMapping("/{id}/shortest-path")
    public ResponseEntity<WordGraphDetailDto> fetchWordGraphShortestPath(@PathVariable String id, @RequestParam(required = false) String start, @RequestParam(required = false) String type, @RequestParam(required = false) boolean inverse) {
        var wordGraphDetail = wordGraphService.getWordGraphById(id);
        if(wordGraphDetail == null) {
            return ResponseEntity.notFound().build();
        }
        wordGraphDetail = wordGraphService.shortestPath(wordGraphDetail, start, type, inverse);
        if(wordGraphDetail.getErrorMessage() != null) {
            return ResponseEntity.badRequest().body(wordGraphDetail);
        }
        return ResponseEntity.ok(wordGraphDetail);
    }
}

package com.david.facebookapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:5173") // Allow React frontend
public class PostController {

    private final PostRepository postRepository;

    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // Create a new post
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        Post saved = postRepository.save(post);
        return ResponseEntity.created(URI.create("/api/posts/" + saved.getId())).body(saved);
    }

    // Get all posts
    @GetMapping
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // Get a single post
    @GetMapping("/{id}")
    public Post getPost(@PathVariable Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
    }

    // Update a post (full update)
    @PutMapping("/{id}")
    public Post updatePost(@PathVariable Long id, @RequestBody Post updated) {
        return postRepository.findById(id).map(existing -> {
            existing.setAuthor(updated.getAuthor());
            existing.setContent(updated.getContent());
            existing.setImageUrl(updated.getImageUrl());
            return postRepository.save(existing);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
    }

    // Delete a post
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        return postRepository.findById(id).map(p -> {
            postRepository.delete(p);
            return ResponseEntity.noContent().<Void>build();
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
    }
}
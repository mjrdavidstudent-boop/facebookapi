package com.david.facebookapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
// REMOVED: @CrossOrigin(origins = "http://localhost:5173")
// The global CorsConfig now handles cross-origin requests.
public class PostController {

    private final PostRepository postRepository;

    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // Create a new post (Returns 201 Created)
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        Post saved = postRepository.save(post);
        // Returns the location of the new resource in the header
        return ResponseEntity.created(URI.create("/api/posts/" + saved.getId())).body(saved);
    }

    // Get all posts (Returns 200 OK)
    @GetMapping
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // Get a single post (Returns 200 OK or 404 Not Found)
    @GetMapping("/{id}")
    public Post getPost(@PathVariable Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
    }

    // Update a post (full update - Returns 200 OK or 404 Not Found)
    @PutMapping("/{id}")
    public Post updatePost(@PathVariable Long id, @RequestBody Post updated) {
        return postRepository.findById(id).map(existing -> {
            existing.setAuthor(updated.getAuthor());
            existing.setContent(updated.getContent());
            existing.setImageUrl(updated.getImageUrl());
            return postRepository.save(existing);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
    }

    // Delete a post (Returns 204 No Content or 404 Not Found)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        if (postRepository.existsById(id)) {
            postRepository.deleteById(id);
            // 204 is the appropriate status for a successful deletion with no body
            return ResponseEntity.noContent().build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
    }
}
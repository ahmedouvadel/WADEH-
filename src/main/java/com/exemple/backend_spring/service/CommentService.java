package com.exemple.backend_spring.service;

import com.exemple.backend_spring.Security.Repository.UserRepository;
import com.exemple.backend_spring.dto.CommentDTO;
import com.exemple.backend_spring.model.Comment;
import com.exemple.backend_spring.repository.CommentRepository;
import com.exemple.backend_spring.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;

    public List<CommentDTO> getAllComments() {
        return commentRepository.findAll()
                .stream()
                .map(comment -> mapToDTO(comment))
                .collect(Collectors.toList());
    }

    public CommentDTO getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        return mapToDTO(comment);
    }

    public List<CommentDTO> getCommentsByContentId(Long contentId) {
        return commentRepository.findByContentId(contentId)
                .stream()
                .map(comment -> mapToDTO(comment))
                .collect(Collectors.toList());
    }

    public CommentDTO createComment(CommentDTO commentDTO) {
        Comment comment = new Comment();
        comment.setText(commentDTO.getText());
        comment.setUser(userRepository.findById(commentDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found")));
        comment.setContent(contentRepository.findById(commentDTO.getContentId())
                .orElseThrow(() -> new RuntimeException("Content not found")));
        Comment savedComment = commentRepository.save(comment);
        return mapToDTO(savedComment);
    }

    public void validateOwnership(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("You do not have permission to modify or delete this comment.");
        }
    }

    public CommentDTO updateComment(Long id, CommentDTO commentDTO) {
        validateOwnership(id, commentDTO.getUserId());

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        comment.setText(commentDTO.getText());
        Comment updatedComment = commentRepository.save(comment);
        return mapToDTO(updatedComment);
    }

    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        commentRepository.delete(comment);
    }


    private CommentDTO mapToDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setUserId(comment.getUser().getId());
        dto.setContentId(comment.getContent().getId());
        dto.setUserProfile(comment.getUser().getUserprofile());
        return dto;
    }
}

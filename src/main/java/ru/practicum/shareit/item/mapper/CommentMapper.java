package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreationCommentDto;
import ru.practicum.shareit.item.model.Comment;

@Service
public class CommentMapper {
    public static Comment toComment(CreationCommentDto creationCommentDto) {
        Comment comment = new Comment();
        comment.setText(creationCommentDto.getText());
        return comment;
    }

    public static CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getUser().getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }
}

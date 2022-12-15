package com.nikitalipatov.handmadeshop.core.services;

import com.nikitalipatov.handmadeshop.core.models.Comments;
import com.nikitalipatov.handmadeshop.core.repositories.CommentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Transactional
@Service
public class CommentsService {
    private final CommentsRepository commentsRepository;
    private final ProductService productService;
    private final UserService userService;


    @Autowired
    public CommentsService(CommentsRepository commentsRepository, ProductService productService, UserService userService) {
        this.commentsRepository = commentsRepository;
        this.productService = productService;
        this.userService = userService;
    }

    public List<Comments> findAllComment(UUID productId){
        return commentsRepository.findAllByProductId(productId);
    }

    public Comments saveComment(UUID productUUID, Comments comment, HttpServletRequest request){
        var user = userService.findUser(request);
        if (commentsRepository.existsByProductIdAndUserId(productUUID, user.get().getId())) {
            delComment(productUUID, request);
        }
        Comments newComment = new Comments();
        newComment.setProductId(productUUID);
        newComment.setUserId(user.get().getId());
        newComment.setDate(LocalDateTime.now());
        newComment.setRating(comment.getRating());
        newComment.setText(comment.getText());
        newComment.setUserName(user.get().getUsername());
        return commentsRepository.save(newComment);
    }

    public Optional<Comments> modifyComment(UUID productId ,Comments comment, HttpServletRequest request){
        var user = userService.findUser(request);
        Optional<Comments> result = commentsRepository.findByProductIdAndUserId(productId, user.get().getId());
        return result
                .map(entity -> {
                    entity.setText(comment.getText());
                    entity.setRating(comment.getRating());
                    entity.setDate(LocalDateTime.now());
                    return commentsRepository.save(entity);
                });
    }

    public Optional<Boolean> delComment(UUID productId, HttpServletRequest request){
        var user = userService.findUser(request);
        Optional<Comments> deletedComment = commentsRepository.findByProductIdAndUserId(productId, user.get().getId());
        return deletedComment
                .map(entity -> {
                    commentsRepository.deleteByProductIdAndUserId(productId, user.get().getId());
                    return true;
                });
    }

}

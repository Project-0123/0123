package com.spring.wordseed.service.impl;

import com.spring.wordseed.dto.in.CreateLikeInDTO;
import com.spring.wordseed.dto.out.CreateLikeOutDTO;
import com.spring.wordseed.entity.PostLiked;
import com.spring.wordseed.repo.PostLikedRepo;
import com.spring.wordseed.repo.PostRepo;
import com.spring.wordseed.repo.UserRepo;
import com.spring.wordseed.service.PostLikedService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PostLikedServiceImpl implements PostLikedService {
    private final PostLikedRepo postLikedRepo;
    private final UserRepo userRepo;
    private final PostRepo postRepo;

    public PostLikedServiceImpl(PostLikedRepo postLikedRepo, UserRepo userRepo, PostRepo postRepo) {
        this.postLikedRepo = postLikedRepo;
        this.userRepo = userRepo;
        this.postRepo = postRepo;
    }

    @Override
    public CreateLikeOutDTO createLike(CreateLikeInDTO createLikeInDTO) throws Exception {
        PostLiked postLiked = PostLiked.builder()
                .user(userRepo.findById(7L).orElseThrow(Exception::new))
                .post(postRepo.findById(createLikeInDTO.getPostId()).orElseThrow(Exception::new))
                .build();

        postLikedRepo.save(postLiked);
        postLikedRepo.flush();

        return CreateLikeOutDTO.builder()
                .postLikedId(postLiked.getPostLikedId())
                .userId(postLiked.getUser().getUserId())
                .postId(postLiked.getPost().getPostId())
                .createdAt(postLiked.getCreatedAt())
                .updatedAt(postLiked.getUpdatedAt())
                .build();
    }
}

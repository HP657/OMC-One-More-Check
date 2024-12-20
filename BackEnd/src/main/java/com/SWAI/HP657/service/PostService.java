package com.SWAI.HP657.service;

import com.SWAI.HP657.dto.In.PostUploadDto;
import com.SWAI.HP657.dto.Response;
import com.SWAI.HP657.entity.Posts;
import com.SWAI.HP657.entity.Users;
import com.SWAI.HP657.repository.PostRepository;
import com.SWAI.HP657.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

// 게시물 관련 서비스 레이아웃
@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private FlaskService flaskService;

    // 게시물 등록 서비스
    @Transactional
    public Response<String> postAdd(PostUploadDto postUploadDto, HttpServletRequest request) throws IOException {
        Long userId = (Long) request.getSession().getAttribute("userId");

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Posts post = new Posts();
        post.setUser(user);
        post.setPostText(postUploadDto.getContent());
        post.setShare(false);
        post.setUsername(postUploadDto.getUsername());
        post.setReviewRequested(false);

        MultipartFile postImg = postUploadDto.getPostImg();
        if (postImg != null && !postImg.isEmpty()) {
            String postImgUrl = imageService.uploadImage(postImg, "posts");
            post.setPostImgUrl(postImgUrl);

            post = postRepository.save(post);
            Long postId = post.getPostId();

            boolean isConfident = flaskService.verifyImageWithFlask(postImgUrl);
            System.out.println(isConfident);
            if (isConfident) {
                post.setShare(true);
                postRepository.save(post);
                return new Response<>("게시물 업로드 성공적 (이미지 검증 성공)", HttpStatus.OK);
            } else {
                return new Response<>("게시물 업로드 성공적 (이미지 검증 실패)", HttpStatus.OK);
            }
        } else {
            postRepository.save(post);
            return new Response<>("게시물 업로드 성공적 (이미지 없음)", HttpStatus.OK);
        }
    }

    // 게시물 삭제 서비스
    public Response<String> postDelete(Long postId) {
        Optional<Posts> post = postRepository.findByPostId(postId);
        if (post.isPresent()) {
            String imageUrl = post.get().getPostImgUrl();

            boolean isImageDeleted = imageService.deleteImage(imageUrl);
            if (!isImageDeleted) {
                return new Response<>("게시물 삭제 시 이미지 삭제 실패", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            postRepository.delete(post.get());
            return new Response<>("게시물 삭제됨", HttpStatus.OK);
        } else {
            return new Response<>("게시물을 찾을 수 없음", HttpStatus.NOT_FOUND);
        }
    }

    // 각 상황별 게시물 요청 서비스 (내 게시물, 모든 게시물, 공유된 게시물, 검열된 게시물, 재검토 요청온 게시물, )
    public Response<List<Posts>> myPost(HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        List<Posts> posts = postRepository.findByUser_UserIdOrderByPostIdDesc(userId);
        return new Response<>(posts, HttpStatus.OK);
    }
    public Response<List<Posts>> viewAPost() {
        return new Response<>(postRepository.findAllByOrderByPostIdDesc(), HttpStatus.OK);
    }
    public Response<List<Posts>> viewTPost() {
        return new Response<>(postRepository.findByShareTrueOrderByPostIdDesc(), HttpStatus.OK);
    }
    public Response<List<Posts>> viewFPost() {
        return new Response<>(postRepository.findByShareFalseOrderByPostIdDesc(), HttpStatus.OK);
    }
    public Response<List<Posts>> viewRPost() {
        return new Response<>(postRepository.findByReviewRequestedTrueOrderByPostIdDesc(), HttpStatus.OK);
    }

    // Id로 게시물 찾기 서비스
    public Response<Posts> idPost(Long postId) {
        Optional<Posts> post = postRepository.findByPostId(postId);
        if (post.isPresent()) {
            return new Response<>(post.get(), HttpStatus.OK);
        }
        return new Response<>(null, HttpStatus.NOT_FOUND);
    }

    // 재검토 요청 서비스
    public Response<String> requestReview(Long postId) {
        Optional<Posts> post = postRepository.findByPostId(postId);
        if (post.isPresent()) {
            Posts fixPost = post.get();
            fixPost.setReviewRequested(true);
            postRepository.save(fixPost);
            return new Response<>("재검토 요청 완료", HttpStatus.OK);
        }
        return new Response<>(null, HttpStatus.NOT_FOUND);
    }

    // 재검토 요청 응답 서비스
    public Response<String> requestReviewOk(Long postId) {
        Optional<Posts> post = postRepository.findByPostId(postId);
        if (post.isPresent()) {
            Posts fixPost = post.get();
            fixPost.setReviewRequested(false);
            fixPost.setShare(true);
            postRepository.save(fixPost);
            return new Response<>("재검토 요청 승인", HttpStatus.OK);
        }
        return new Response<>(null, HttpStatus.NOT_FOUND);

    }
}

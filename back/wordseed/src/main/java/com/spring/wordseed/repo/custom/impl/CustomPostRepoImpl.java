package com.spring.wordseed.repo.custom.impl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.spring.wordseed.dto.out.ReadPostOutDTO;
import com.spring.wordseed.entity.*;
import com.spring.wordseed.enu.PostSort;
import com.spring.wordseed.enu.PostType;
import com.spring.wordseed.repo.custom.CustomPostRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomPostRepoImpl implements CustomPostRepo {
    @PersistenceContext
    EntityManager em;
    private final QPost qPost = QPost.post;
    private final QUser qUser = QUser.user;
    private final QBookMark qBookMark = QBookMark.bookMark;
    private final QPostLiked qPostLiked = QPostLiked.postLiked;
    private final QWord qWord = QWord.word1;

    @Override
    public List<ReadPostOutDTO> FindPostAllBy(String postTypes, String mark, Long userId, PostSort sort, String query, Long page, Long size) {
        // postType
        List<String> postType = new ArrayList<>(Arrays.asList(postTypes.split(",")));
        String postTypesInSQL = "";

        for (String type : postType)
            postTypesInSQL += ("\"" + type + "\",");

        postTypesInSQL = postTypesInSQL.substring(0, postTypesInSQL.length() - 1);
        String postSQL = "AND P.POST_TYPE IN (" + postTypesInSQL + ") ";

        // word


        // sort
        String sortSQL = "ORDER BY ";

        if (sort == PostSort.DATE_ASC)
            sortSQL += "CREATED_AT ASC ";

        else if (sort == PostSort.DATE_DSC)
            sortSQL += "CREATED_AT DESC ";

        else if (sort == PostSort.LIKE_ASC)
            sortSQL += "LIKECNT ASC ";

        else if (sort == PostSort.LIKE_DSC)
            sortSQL += "LIKECNT DESC ";

        // paging
        String pagingSQL = String.format("LIMIT %d OFFSET %s", size, (page - 1) * size);

        // native query
        String sql = "SELECT P.POST_ID PID, P.USER_ID, U.USER_NAME, P.POST_TYPE, P.CONTENT, P.URL, P.LIKED_CNT LIKECNT, P.BOOK_MARK_CNT, P.COMMENT_CNT, " +
                "EXISTS(SELECT * FROM BOOK_MARKS BM JOIN POSTS P ON BM.POST_ID = P.POST_ID WHERE BM.USER_ID = :bind1 AND P.POST_ID = PID), " +
                "EXISTS(SELECT * FROM POST_LIKEDS PL JOIN POSTS P ON PL.POST_ID = P.POST_ID WHERE PL.USER_ID = :bind2 AND P.POST_ID = PID), " +
                "EXISTS(SELECT * FROM FOLLOWS F WHERE F.SRC_ID = :bind3 AND F.DST_ID = P.USER_ID), " +
                "DATE_FORMAT(P.CREATED_AT, '%Y-%m-%d %H:%i:%s') AS CREATED_AT," +
                 "DATE_FORMAT(P.UPDATED_AT, '%Y-%m-%d %H:%i:%s') AS UPDATED_AT " +
                "FROM POSTS P " +
                "JOIN USERS U ON P.USER_ID = U.USER_ID " +
                "JOIN WORDS W ON P.WORD_ID = W.WORD_ID " +
                "WHERE P.USER_ID = :bind4 " +
                postSQL +
                sortSQL +
                pagingSQL;

        // create query
        Query nativeQuery = em.createNativeQuery(sql);

        // %should change
        nativeQuery.setParameter("bind1", "7"); // post user Id
        nativeQuery.setParameter("bind2", "7"); // post user Id
        nativeQuery.setParameter("bind3", "3"); // user Id
        nativeQuery.setParameter("bind4", "3"); // user Id

        List<Object[]> resultList = nativeQuery.getResultList();
        List<ReadPostOutDTO> readPostOutDTOs = new ArrayList<>();

        // initialize data
        for (Object[] row : resultList){
            Long rPostId = (Long) row[0];
            Long rUserId = (Long) row[1];
            String rUserName = (String) row[2];
            PostType rPostType = PostType.valueOf((String) row[3]);
            String rContent = (String) row[4];
            String rUrl = (String) row[5];
            Long rLikedCnt = (Long) row[6];
            Long rBookMarkCnt = (Long) row[7];
            Long rCommentCnt = (Long) row[8];
            Boolean rBookMarked = (Long) row[9] == 1L;
            Boolean rLiked = (Long) row[10] == 1L;
            Boolean rSubs = (Long) row[11] == 1L;

            // createAt
            String createAtString = (String) row[12];
            DateTimeFormatter createFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime rCreatedAt = LocalDateTime.parse(createAtString, createFormatter);

            // updateAt
            String updateAtString = (String) row[13];
            DateTimeFormatter updateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime rUpdatedAt = LocalDateTime.parse(updateAtString, updateFormatter);

            // build
            ReadPostOutDTO readPostOutDTO = ReadPostOutDTO.builder()
                    .postId(rPostId)
                    .userId(rUserId)
                    .userName(rUserName)
                    .postType(rPostType)
                    .content(rContent)
                    .url(rUrl)
                    .likedCnt(rLikedCnt)
                    .bookMarkCnt(rBookMarkCnt)
                    .commentCnt(rCommentCnt)
                    .bookMarked(rBookMarked)
                    .liked(rLiked)
                    .subscribed(rSubs)
                    .createdAt(rCreatedAt)
                    .updatedAt(rUpdatedAt)
                    .build();

            readPostOutDTOs.add(readPostOutDTO);
        }

        // if bookMark is true
        if (mark.equals("true"))
            readPostOutDTOs.removeIf(element -> !element.getBookMarked());

        return readPostOutDTOs;
    }
}

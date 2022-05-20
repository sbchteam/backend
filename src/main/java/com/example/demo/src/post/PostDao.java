package com.example.demo.src.post;

import com.example.demo.src.post.model.*;
import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.List;

@Repository
public class PostDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    String getPostsquery="" +
            "select p.id, title, category, price, num,transaction_status,p.created_at, \n" +
            "       max( case\n" +
            "       when pi.user_id =? then pi.status\n" +
            "       when pi.user_id!=? then 0\n" +
            "       when pi.user_id is null then 0\n" +
            "       end ) as interest_status,\n" +
            "       ifnull(interestNum,0) as interest_num,\n" +
            "        case\n" +
            "           when TIMESTAMPDIFF(hour, p.created_at, current_timestamp()) < 24\n" +
            "               then case\n" +
            "                        when TIMESTAMPDIFF(hour, p.created_at, current_timestamp()) < 1\n" +
            "                            then concat(timestampdiff(minute, p.created_at, current_timestamp()), ' 분전')\n" +
            "                        when TIMESTAMPDIFF(hour, p.created_at, current_timestamp()) >= 1\n" +
            "                            then concat(timestampdiff(hour, p.created_at, current_timestamp()), ' 시간전')\n" +
            "               end\n" +
            "           when TIMESTAMPDIFF(hour, p.created_at, current_timestamp()) < 48\n" +
            "               then '어제'\n" +
            "           when TIMESTAMPDIFF(hour, p.created_at, current_timestamp()) < 72\n" +
            "               then '그저께'\n" +
            "           else concat(-datediff(p.created_at, current_timestamp()), ' 일전')\n" +
            "        end    as timediff,\n" +
            "       img\n" +
            "from post p\n" +
            "left join post_interest pi\n" +
            "on p.id = pi.post_id\n" +
            "join category c\n" +
            "on c.id = p.category_id\n" +
            "left join post_image pimg\n" +
            "on p.id = pimg.post_id\n" +
            "left join (\n" +
            "    select post_id, count(*) as interestNum\n" +
            "    from post_interest\n" +
            "    group by post_id\n" +
            ") incnt on p.id=incnt.post_id\n";


    public List<PostList> getPosts(int userId) {
        String getPostsQuery ="" +
                "select *\n" +
                "from(\n" +
                getPostsquery+
                "where p.status=1 && TIMESTAMPDIFF(minute , now(),p.date)>0 && p.user_id not in (\n" +
                "    select user_id\n" +
                "    from user_block\n" +
                "    where blocker_id=?\n" +
                ")\n"+
                "group by p.id) plist\n" +
                "order by plist.created_at desc;";

        int getPostsParams = userId;
        return this.jdbcTemplate.query(getPostsQuery,
                (rs, rowNum) -> new PostList(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("category"),
                        rs.getInt("price"),
                        rs.getString("transaction_status"),
                        rs.getInt("interest_status"),
                        rs.getInt("interest_num"),
                        rs.getString("timediff"),
                        rs.getString("img")
                ),
                getPostsParams, getPostsParams,getPostsParams
        );
    }
    public List<PostList> getPostsInterest(int userId) {
        String getPostsInterestQuery =
                "select *\n" +
                "from(\n" +
                getPostsquery+
                "where p.status=1 && TIMESTAMPDIFF(minute , now(),p.date)>0 && p.user_id not in (\n" +
                "    select user_id\n" +
                "    from user_block\n" +
                "    where blocker_id=?\n" +
                ")\n"+
                "group by p.id) plist\n " +
                "order by plist.interest_num desc";
        int getPostsInterestParams = userId;
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy년 MM월 dd일");
        return this.jdbcTemplate.query(getPostsInterestQuery,
                (rs, rowNum) -> new PostList(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("category"),
                        rs.getInt("price"),
                        rs.getString("transaction_status"),
                        rs.getInt("interest_status"),
                        rs.getInt("interest_num"),
                        rs.getString("timediff"),
                        rs.getString("img")
                ),
                getPostsInterestParams, getPostsInterestParams,getPostsInterestParams
        );
    }

    public List<PostList> getPostsOngoing(int userId) {
        String getPostsOngoingQuery =
                "select *\n" +
                "from(\n" +
                getPostsquery+
                "where p.transaction_status\n" +
                "not in ('complete') && p.status=1 && TIMESTAMPDIFF(minute , now(),p.date)>0 && p.user_id not in (\n" +
                "    select user_id\n" +
                "    from user_block\n" +
                "    where blocker_id=?\n" +
                ")\n" +
                "group by p.id) plist\n" +
                "order by plist.created_at desc;";
        int getPostsOngoingParams = userId;
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy년 MM월 dd일");
        return this.jdbcTemplate.query(getPostsOngoingQuery,
                (rs, rowNum) -> new PostList(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("category"),
                        rs.getInt("price"),
                        rs.getString("transaction_status"),
                        rs.getInt("interest_status"),
                        rs.getInt("interest_num"),
                        rs.getString("timediff"),
                        rs.getString("img")
                ),
                getPostsOngoingParams, getPostsOngoingParams,getPostsOngoingParams
        );
    }
    //공구 검색 api
    public List<PostList> getPostSearch(int userId, String word) {
        String getPostSearchQuery =
                "select *\n" +
                "from(\n" +
                getPostsquery+
                "where p.product_name like ? && p.status=1 && TIMESTAMPDIFF(minute , now(),p.date)>0 && p.user_id not in (\n" +
                "    select user_id\n" +
                "    from user_block\n" +
                "    where blocker_id=?\n" +
                ")\n"+
                "group by p.id) plist\n" +
                "order by plist.created_at desc";

        int getPostSearchParams = userId;
        String keyword='%'+word+'%';
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy년 MM월 dd일");
        return this.jdbcTemplate.query(getPostSearchQuery,
                (rs, rowNum) -> new PostList(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("category"),
                        rs.getInt("price"),
                        rs.getString("transaction_status"),
                        rs.getInt("interest_status"),
                        rs.getInt("interest_num"),
                        rs.getString("timediff"),
                        rs.getString("img")
                ),
                getPostSearchParams, getPostSearchParams, keyword,getPostSearchParams
        );
    }
    //키워드 등록 api
    public void PostKeyword(int userId,String word){
        String PostKeywordQuery = "insert into user_keyword (user_id,keyword) VALUES (?,?)";
        Object[] PostKeywordParams = new Object[]{userId,word};
        this.jdbcTemplate.update(PostKeywordQuery, PostKeywordParams);
    }


    //date처리, createdAt처리리
   public PostDetail getPost(int postId, int userId) {
        String getWorkQuery =
                "select p.id,p.user_id,u.profile_img,u.nick,town, category,title,price,date,num,content,transaction_status,ifnull(pi.status,0) as interestStatus,\n" +
                "       case\n" +
                "           when TIMESTAMPDIFF(hour, p.created_at, current_timestamp()) < 24\n" +
                "               then case\n" +
                "                        when TIMESTAMPDIFF(hour, p.created_at, current_timestamp()) < 1\n" +
                "                            then concat(timestampdiff(minute, p.created_at, current_timestamp()), ' 분전')\n" +
                "                        when TIMESTAMPDIFF(hour, p.created_at, current_timestamp()) >= 1\n" +
                "                            then concat(timestampdiff(hour, p.created_at, current_timestamp()), ' 시간전')\n" +
                "               end\n" +
                "           when TIMESTAMPDIFF(hour, p.created_at, current_timestamp()) < 48\n" +
                "               then '어제'\n" +
                "           when TIMESTAMPDIFF(hour, p.created_at, current_timestamp()) < 72\n" +
                "               then '그저께'\n" +
                "           else concat(-datediff(p.created_at, current_timestamp()), ' 일전')\n" +
                "        end    as created_at\n" +
                "from post p\n" +
                "join user u\n" +
                "on p.user_id = u.id\n" +
                "join user_location ul\n" +
                "on p.location_id = ul.id\n" +
                "join category c\n" +
                "on p.category_id = c.id\n" +
                "left join post_interest pi\n" +
                "on p.id = pi.post_id && pi.user_id=?\n" +
                "where p.id=?\n";
        int getPostParams = postId;
        int getPostParams2 = userId;
       SimpleDateFormat dateFormat= new SimpleDateFormat("MM월 dd일");
        return this.jdbcTemplate.queryForObject(getWorkQuery,
                (rs, rowNum) -> new PostDetail(
                        rs.getInt("p.id"),
                        rs.getInt("p.user_id"),
                        rs.getString("profile_img"),
                        rs.getString("nick"),
                        rs.getString("town"),
                        rs.getString("category"),
                        rs.getString("title"),
                        rs.getInt("price"),
                        rs.getTimestamp("date"),
                        rs.getInt("num"),
                        rs.getString("content"),
                        rs.getString("transaction_status"),
                        rs.getInt("interestStatus"),
                        rs.getString("created_at")
                ),
                getPostParams2, getPostParams);
    }

    public List<String> getPostImg(int postId){
        String getPostImgQuery =
                "select img\n" +
                "from post_image\n" +
                "where post_id=?";
        int getPostImgParams = postId;
        return this.jdbcTemplate.query(getPostImgQuery,
                (rs, rowNum) -> new String(
                        rs.getString("img")
                ),
                getPostImgParams);
    }

    /*작품 추천 api*/
    public List<PostRecommend> getPostsRecommend(int PostId){
        String getPostCategory="" +
                "select category_id\n" +
                "from post\n" +
                "where post.id=?";
        int getPostId=PostId;
        int categoryId= this.jdbcTemplate.queryForObject(getPostCategory,
                int.class,
                getPostId);

        String getPostLocation="" +
                "select town\n" +
                "from user_location\n" +
                "join post p on user_location.id = p.location_id\n" +
                "where p.id=?;";
        String town= this.jdbcTemplate.queryForObject(getPostLocation,
                String.class,
                getPostId);

        String getPostQuery =
                "select p.id, title, price,img\n" +
                "from post p\n" +
                "join user_location ul on p.location_id = ul.id\n" +
                "left join post_image pi on p.id = pi.post_id\n" +
                "where town like ? && p.category_id=? && p.id!=? && p.transaction_status!='complete' && p.status=1 && TIMESTAMPDIFF(minute , now(),p.date)>0\n" +
                "group by p.id\n" +
                "limit 3;";
        String keyword='%'+town+'%';
        return this.jdbcTemplate.query(getPostQuery,
                (rs, rowNum) -> new PostRecommend(
                        rs.getInt("id"),
                        rs.getString("img"),
                        rs.getString("title"),
                        rs.getInt("price")
                ),keyword,categoryId,getPostId);
    }

    public PostInterest PushPostInterest(int postId, int userId){

        String checkInterestQuery = "" +
                "select EXISTS(\n" +
                "select *\n" +
                "from post_interest pi\n" +
                "where pi.post_id=? && pi.user_id=?) as exist";
        Object[] checkInterestParams = new Object[]{postId,userId};

        int result= this.jdbcTemplate.queryForObject(checkInterestQuery,
                int.class,
                checkInterestParams);


        //존재하면
        if(result==1){
            String checkStatusQuery = "" +
                    "select status\n" +
                    "from post_interest pi\n" +
                    "where pi.post_id=? && pi.user_id=?";
            Object[] checkStatusParams = new Object[]{postId,userId};

            int status= this.jdbcTemplate.queryForObject(checkStatusQuery,
                    int.class,
                    checkStatusParams);

            //관심이 눌린 상태면
            if(status==1){
                String clearInterestQuery = "update post_interest set status=0 where post_id=? && user_id=?";
                Object[] clearInterestParams = new Object[]{postId,userId};
                this.jdbcTemplate.update(clearInterestQuery, clearInterestParams);
            }else{
                String createInterestQuery = "update post_interest set status=1 where post_id=? && user_id=?";
                Object[] createInterestParams = new Object[]{postId,userId};
                this.jdbcTemplate.update(createInterestQuery, createInterestParams);
            }
        }
        else{
            String createInterestQuery = "insert into post_interest(post_id,user_id) VALUES (?,?)";
            Object[] createInterestParams = new Object[]{postId,userId};
            this.jdbcTemplate.update(createInterestQuery, createInterestParams);

        }
        String getInterestQuery="" +
                "select post_id,user_id,status\n" +
                "from post_interest\n" +
                "where post_id=? && user_id=?";
        Object[] getInterestParams = new Object[]{postId,userId};
        return this.jdbcTemplate.queryForObject(getInterestQuery,
                (rs, rowNum) -> new PostInterest(
                        rs.getInt("post_id"),
                        rs.getInt("user_id"),
                        rs.getInt("status")
                ),
                getInterestParams);
    }
    /*거래 상황 변경*/
    public PostDetail changeTranslate(int postId,int userId, String translateStatus){
        String changeTranslateQuery="update post set transaction_status=? where id=? && post.user_id=?";
        Object[] changeTranslateParams = new Object[]{translateStatus,postId,userId};
        this.jdbcTemplate.update(changeTranslateQuery, changeTranslateParams);
        return getPost(postId,userId);
    }
    /*공구 게시글 신고*/
    public PostInterest PostReport(int postId,int userId){

        String setPostReportQuery = "insert into post_report (post_id,user_id) VALUES (?,?)";
        Object[] setPostReportParams = new Object[]{postId,userId};
        this.jdbcTemplate.update(setPostReportQuery, setPostReportParams);

        String lastInsertIdQuery = "select last_insert_id()";
        int getPostReportParams=this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);

        /*만약 이 postId의 신고가 총합 2번이라면 status=0으로 설정*/
        String checkReportCnt="" +
                "select count(*) as reportCnt\n" +
                "from post_report pr\n" +
                "where post_id=?\n" +
                "group by pr.post_id";
        int reportCnt= this.jdbcTemplate.queryForObject(checkReportCnt,
                int.class,
                postId);

        if(reportCnt==2){

            String changeStatusQuery="update post set status=? where id=?";
            Object[] changeStatusParams = new Object[]{0,postId};
            this.jdbcTemplate.update(changeStatusQuery, changeStatusParams);
        }

        String getPostReportQuery="select * from post_report where id=?";
        return this.jdbcTemplate.queryForObject(getPostReportQuery,
                (rs, rowNum) -> new PostInterest(
                        rs.getInt("post_id"),
                        rs.getInt("user_id"),
                        rs.getInt("status")
                ),
                getPostReportParams);
    }

    /*post객체 반환*/
    public Post getPostObject(int postId){
        String getPostObjectQuery =
                "select *\n" +
                "from post\n" +
                "where id=?";
        int getPostObjectParams = postId;
        return this.jdbcTemplate.queryForObject(getPostObjectQuery,
                (rs, rowNum) -> new Post(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getInt("category_id"),
                        rs.getString("product_name"),
                        rs.getInt("price"),
                        rs.getInt("location_id"),
                        rs.getTimestamp("date"),
                        rs.getInt("num"),
                        rs.getString("content"),
                        rs.getString("transaction_status"),
                        rs.getInt("status"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                ),
                getPostObjectParams);
    }

    //post_image에 imgUrl넣음
    public List<String> putImage(int postId,String imgurl){
        String putImageQuery = "insert into post_image (post_id,img) VALUES (?,?)";
        Object[] putImageParams = new Object[]{postId,imgurl};
        this.jdbcTemplate.update(putImageQuery, putImageParams);

        return getPostImg(postId);
    }

    //이미지 한개만 받을때
    public void putPostImage(int postId,String imgurl){
        String putPostImageQuery = "insert into post_image (post_id,img) VALUES (?,?)";
        Object[] putPostImageParams = new Object[]{postId,imgurl};
        this.jdbcTemplate.update(putPostImageQuery, putPostImageParams);
    }

    /*게시물 작성*/
    public PostDetail createPost (Post post, int userId) {
        String getPostingQuery = "insert into post (user_id, title, category_id, product_name, " +
                "price, location_id, date, num, content)" +
                "VALUES (?,?,?,?,?,?,?,?,?)";
        Object[] createPostParams = {userId, post.getTitle(), post.getCategoryId(),
                post.getProductName(), post.getPrice(), post.getLocationId(),
                post.getDate(), post.getNum(), post.getContent()};
        this.jdbcTemplate.update(getPostingQuery, createPostParams);

        String lastInsertIDQuery = "select last_insert_id()";
        int getPostParams = this.jdbcTemplate.queryForObject(lastInsertIDQuery, int.class);
        int getPostParams2 = userId;
        return getPost(getPostParams, getPostParams2);
    }

    /*게시물 수정*/
    public void updatePost(Post post, int postId) {
        String updateQuery = "update post set title=?, category_id=?, product_name=?," +
                "price=?, location_id=?, date=?, num=?, content=? where id=? && user_id=?";
        Object[] updateParams = {post.getTitle(), post.getCategoryId(), post.getProductName(),
                post.getPrice(), post.getLocationId(), post.getDate(), post.getNum(),
                post.getContent(),postId,post.getUserId()};
        this.jdbcTemplate.update(updateQuery, updateParams);
    }

    /*게시물 삭제*/
    public void deletePost(int postId, int userId) {
        String deleteQuery = "update post set status=0 where id=? && user_id=?";
        Object[] deleteParams = {postId, userId};
        this.jdbcTemplate.update(deleteQuery, deleteParams);
    }

    /*카테고리 조회*/
    public List<Category> getCategory() {
        String getCategoryQuery = "select id, category from category";
        return this.jdbcTemplate.query(getCategoryQuery,
                (rs, rowNum) -> new Category(
                        rs.getInt("Id"),
                        rs.getString("category")
                ));
    }

    /*등록 위치 가져오기*/
    public List<Location> getLocation(int userId) {
        String getLocationQuery = "select id, town from user_location where user_id=?";
        int getLocationParams = userId;
        return this.jdbcTemplate.query(getLocationQuery,
                (rs, rowNum) -> new Location(
                        rs.getInt("id"),
                        rs.getString("town")
                ), getLocationParams);
    }

    /*참여하기 버튼 누르기, 해제하기*/
    public String PostJoin(int postId, int userId){

        String text="";
        String checkJoinQuery = "" +
                "select EXISTS(\n" +
                "select *\n" +
                "from post_join pj\n" +
                "where pj.post_id=? && pj.user_id=?) as exist;";
        Object[] checkJoinParams = new Object[]{postId,userId};

        int result= this.jdbcTemplate.queryForObject(checkJoinQuery,
                int.class,
                checkJoinParams);

        //존재하면
        if(result==1){
            String checkStatusQuery = "" +
                    "select status\n" +
                    "from post_join pj\n" +
                    "where pj.post_id=? && pj.user_id=?";
            Object[] checkStatusParams = new Object[]{postId,userId};

            int status= this.jdbcTemplate.queryForObject(checkStatusQuery,
                    int.class,
                    checkStatusParams);
            //관심이 눌린 상태면
            if(status==1){
                String clearJoinQuery = "update post_join set status=0 where post_id=? && user_id=?";
                Object[] clearJoinParams = new Object[]{postId,userId};
                this.jdbcTemplate.update(clearJoinQuery, clearJoinParams);
                text="참여취소";
            }else{
                String createJoinQuery = "update post_join set status=1 where post_id=? && user_id=?";
                Object[] createJoinParams = new Object[]{postId,userId};
                this.jdbcTemplate.update(createJoinQuery, createJoinParams);
                text="참여신청";
            }
        }
        else{
            String createJoinQuery = "insert into post_join(post_id,user_id) VALUES (?,?)";
            Object[] createJoinParams = new Object[]{postId,userId};
            this.jdbcTemplate.update(createJoinQuery, createJoinParams);
            text="참여신청";
        }

        return text;
    }

    /*공구 참여 수락하기*/
    public String PostJoinApply(int postId,int userId) {
        String result="";

        //4명 이하일때 만 수락 가능
        String checkCntQuery = "" +
                "select count(*) as joincnt\n" +
                "from post_join pj\n" +
                "where pj.post_id=? && pj.joinStatus=1";

        int joinCnt= this.jdbcTemplate.queryForObject(checkCntQuery, int.class, postId);

        String checkNumQuery = "" +
                "select num\n" +
                "from post\n" +
                "where id=?";

        int num= this.jdbcTemplate.queryForObject(checkNumQuery, int.class, postId);
        if(joinCnt<=num-1){

            String postJoinApplyQuery = "update post_join set joinStatus=1  where post_id=? && user_id=?";
            Object[] postJoinApplyParams = {postId,userId};
            this.jdbcTemplate.update(postJoinApplyQuery, postJoinApplyParams);
            result="공구참여수락완료";
            if(joinCnt==num-1){
                String changeTranslateQuery="update post set transaction_status='deal' where id=?";
                Object[] changeTranslateParams = new Object[]{postId};
                this.jdbcTemplate.update(changeTranslateQuery, changeTranslateParams);
            }

        }else{
            result="이미참";
        }

        return "공구참여수락완료";
    }

    /*공구 참여 거절 & 취소하기*/
    public String PostJoinRefuse(int postId,int userId) {

        String postJoinApplyQuery = "update post_join set joinStatus=0, status=0  where post_id=? && user_id=?";
        Object[] postJoinApplyParams = {postId,userId};
        this.jdbcTemplate.update(postJoinApplyQuery, postJoinApplyParams);
        return "공구참여 거절 & 취소 완료";
    }

    /*공구 참여신청자 리스트 보기*/
    public List<JoinList> PostJoinList(int postId,int userId) {
        String postJoinListQuery = "" +
                "select u.id, nick, profile_img\n" +
                "from post p\n" +
                "join post_join pj on p.id = pj.post_id\n" +
                "join user u on pj.user_id = u.id\n" +
                "where p.id=? && p.user_id=? && pj.status=1";
        Object[] postJoinListParams = {postId,userId};
        return this.jdbcTemplate.query(postJoinListQuery,
                (rs, rowNum) -> new JoinList(
                        rs.getInt("u.id"),
                        rs.getString("nick"),
                        rs.getString("profile_img")
                ), postJoinListParams);
    }

    /*공구 참여자 리스트 보기*/
    public List<JoinList> PostJoinOnlyList(int postId,int userId) {
        String postJoinOnlyListQuery = "" +
                "select u.id, nick, profile_img\n" +
                "from post p\n" +
                "join post_join pj on p.id = pj.post_id\n" +
                "join user u on pj.user_id = u.id\n" +
                "where p.id=? && p.user_id=? && pj.status=1 && pj.joinStatus=1";
        Object[] postJoinOnlyListParams = {postId,userId};
        return this.jdbcTemplate.query(postJoinOnlyListQuery,
                (rs, rowNum) -> new JoinList(
                        rs.getInt("u.id"),
                        rs.getString("nick"),
                        rs.getString("profile_img")
                ), postJoinOnlyListParams);
    }
}

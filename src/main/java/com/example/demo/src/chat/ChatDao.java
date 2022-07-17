package com.example.demo.src.chat;

import com.example.demo.src.chat.model.ChatMessage;
import com.example.demo.src.chat.model.ChatRoom;
import com.example.demo.src.chat.model.ChatRoomDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ChatDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int createRoom(int postId,int userId){

        String creatRoomQuery = "insert into chat_room(post_id) VALUES (?)";
        this.jdbcTemplate.update(creatRoomQuery, postId); //post_id넣은 room생성

        String lastInsertIdQuery = "select last_insert_id()";
        int roomId=this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class); //방금 생성한 roomId구하기

        String checkUserQuery = "" +
                "select user_id\n" +
                "from post\n" +
                "where id=?";
        int postWriterId= this.jdbcTemplate.queryForObject(checkUserQuery ,int.class, postId); // 주최자 id구하기

        int[] users={postWriterId, userId};
        for(int i=0;i<2;i++){

            String creatRoomUserQuery = "insert into chat_room_users (room_id, user_id) VALUES (?,?)";
            Object[] createRoomUserParams = new Object[]{roomId,users[i]};
            this.jdbcTemplate.update(creatRoomUserQuery , createRoomUserParams );				//참여자(주최자, 채팅방 생성자) chat_room_users에 추가

        }


        return roomId;
    }

    public List<ChatRoom> getRooms(int userId){

        String getUserRooms = "" +
                "select cm.room_id, sender_id, nick, profile_img,message, cm.created_at\n" +
                "from chat_message cm\n" +
                "join user u on cm.sender_id = u.id\n" +
                "join(\n" +
                "    select max(cm.created_at) as max_created\n" +
                "    from chat_message cm\n" +
                "    join chat_room_users cru\n" +
                "    on cru.user_id=? && cru.room_id=cm.room_id\n" +
                "    group by cm.room_id\n" +
                ") lastest_m\n" +
                "on lastest_m.max_created=cm.created_at";


        return this.jdbcTemplate.query(getUserRooms ,
                (rs,rowNum)-> new ChatRoom(
                        rs.getInt("cm.room_id"),
                        rs.getInt("sender_id"),
                        rs.getString("nick"),
                        rs.getString("profile_img"),
                        rs.getString("message"),
                        rs.getTimestamp("cm.created_at")
                ),
                userId
        );
    }
    public List<ChatRoom> getPostRooms(int userId,int postId){

        String getUserRooms = "" +
                "select cm.room_id, sender_id, nick, profile_img,message, cm.created_at\n" +
                "from chat_message cm\n" +
                "join user u on cm.sender_id = u.id\n" +
                "join(\n" +
                "    select max(cm.created_at) as max_created\n" +
                "    from chat_message cm\n" +
                "    join chat_room_users cru\n" +
                "    on cru.user_id=? && cru.room_id=cm.room_id\n" +
                "    join chat_room cr on cm.room_id = cr.id && cr.post_id=?\n" +
                "    group by cm.room_id\n" +
                ") lastest_m\n" +
                "on lastest_m.max_created=cm.created_at";


        return this.jdbcTemplate.query(getUserRooms ,
                (rs,rowNum)-> new ChatRoom(
                        rs.getInt("cm.room_id"),
                        rs.getInt("sender_id"),
                        rs.getString("nick"),
                        rs.getString("profile_img"),
                        rs.getString("message"),
                        rs.getTimestamp("cm.created_at")
                ),
                userId,postId
        );
    }


    public List<ChatRoomDetail> getRoom(int roomId){

        String getUserRoom = "" +
                "select u.id,nick,profile_img,message,cm.created_at\n" +
                "from chat_message cm\n" +
                "join user u on cm.sender_id = u.id\n" +
                "where room_id=?\n" +
                "order by cm.created_at";
        return this.jdbcTemplate.query(getUserRoom ,
                (rs,rowNum)-> new ChatRoomDetail (
                        rs.getInt("u.id"),
                        rs.getString("nick"),
                        rs.getString("profile_img"),
                        rs.getString("message"),
                        rs.getTimestamp("cm.created_at")
                ),
                roomId
        );
    }

    public void createMessage(ChatMessage message){
        String createMessageQuery = "insert into chat_message(room_id,sender_id,message) VALUES (?,?,?)";
        Object[] createMessageParams = new Object[]{message.getRoomId(),message.getSender(),message.getMessage()};
        this.jdbcTemplate.update(createMessageQuery, createMessageParams);
    }

}

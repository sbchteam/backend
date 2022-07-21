package com.example.demo.src.chat;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.chat.model.ChatMessage;
import com.example.demo.src.chat.model.ChatRoom;
import com.example.demo.src.chat.model.ChatRoomDetail;
import com.example.demo.src.user.UserProvider;
import com.example.demo.src.user.UserService;
import com.example.demo.src.user.model.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.demo.utils.JwtService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private final ChatService chatService;
    @Autowired
    private final JwtService jwtService;


    // 모든 채팅방 목록 반환
    @GetMapping("/rooms")
    @ResponseBody
    public BaseResponse<List<ChatRoom>> getRooms() {
        try{
            int userId=jwtService.getUserIdx();
            List<ChatRoom> chatRooms=chatService.getRooms(userId);
            return new BaseResponse<>(chatRooms);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
//    // 특정 공구 채팅방 목록 반환 -주최자 필요
//    @GetMapping("/rooms/{postId}")
//    @ResponseBody
//    public BaseResponse<List<ChatRoom>> getPostRooms(@PathVariable int postId) {
//        try{
//            int userId=jwtService.getUserIdx();
//            List<ChatRoom> chatRooms=chatService.getPostRooms(userId,postId);
//            return new BaseResponse<>(chatRooms);
//        } catch(BaseException exception){
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }

    // 채팅방 생성
    @PostMapping("/room")
    @ResponseBody
    public BaseResponse<Integer> createRoom(@RequestParam int postId) {
        try{
            int userId=jwtService.getUserIdx();
            int roomId=chatService.createRoom(postId,userId);
            return new BaseResponse<>(roomId);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 채팅방 초대
    @PostMapping("/room/invite")
    @ResponseBody
    public BaseResponse<String> enterRoom(@RequestBody ChatMessage message) {
        chatService.enterRoom(message.getRoomId(),message.getSender());
        String result="초대완료";
        return new BaseResponse<>(result);
    }


    // 특정 채팅방 조회
    @GetMapping("/room/{roomId}")
    @ResponseBody
    public BaseResponse<List<ChatRoomDetail>> roomInfo(@PathVariable int roomId) {
        try{
            int userId=jwtService.getUserIdx();
            List<ChatRoomDetail> chatRoomDetail=chatService.getRoom(roomId);
            return new BaseResponse<>(chatRoomDetail);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    // 채팅 리스트 화면
    @GetMapping("/room")
    public String rooms(Model model) {
        return "chat/room";
    }
    // 채팅방 입장 화면
    @GetMapping("/room/enter/{roomId}")
    public String roomDetail(Model model, @PathVariable String roomId) {
        model.addAttribute("roomId", roomId);
        return "chat/roomdetail";
    }
}
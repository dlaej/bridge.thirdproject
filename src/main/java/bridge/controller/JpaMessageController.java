package bridge.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import bridge.dto.UserDto;
import bridge.entity.ChattingEntity;
import bridge.entity.MessageEntity;
import bridge.service.JpaService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class JpaMessageController {

    private final SimpMessageSendingOperations simpMessageSendingOperations;
    
    @Autowired
    private JpaService jpaService;
    
    @ApiOperation(value="채팅 목록 조회")
    @GetMapping("/chatroom")
    public ResponseEntity<Map<String,Object>> chatroom(Authentication authentication){
    	UserDto userDto = (UserDto) authentication.getPrincipal();
    	List<ChattingEntity> chattingEntity = jpaService.getChattingRoom(userDto.getUserId());
    	Map<String,Object> map = new HashMap<>();
    	map.put("sender", userDto.getUserId());
    	map.put("chatting", chattingEntity);
    	return ResponseEntity.status(HttpStatus.OK).body(map);
    }
    
    @ApiOperation(value="채팅방 열기")
    @PostMapping("/api/chatroom")
    public void openChat(@RequestBody ChattingEntity chattingEntity){
    	System.out.println(">>>>>>>>>>>>>>>>>>>> 오픈챗 실행");
        jpaService.openChat(chattingEntity);

        System.out.println(">>>>>>>>>>>>>>>> 오픈챗 종료");
    }

    @ApiOperation(value="채팅 작성")
    @GetMapping("/chat/{roomIdx}")
	public ResponseEntity<Map<String, Object>> connect(@PathVariable("roomIdx") int roomIdx){
    	Map<String,Object> map = new HashMap<>();
    	List<MessageEntity> MessageEntity = jpaService.getMessage(roomIdx);
    	map.put("messagelist", MessageEntity);
    	ChattingEntity chattingEntity = jpaService.getchatting(roomIdx);
    	
    	map.put("chatting",chattingEntity);
    	return ResponseEntity.status(HttpStatus.OK).body(map);
    }
    
    @ApiOperation(value="채팅 메세지 조회")
    @MessageMapping("/hello")
    public void message(MessageEntity message) {
    	simpMessageSendingOperations.convertAndSend("/sub/channel/" + message.getRoomIdx(), message);
    	jpaService.insertData(message);
    }
    
}
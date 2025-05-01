package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Message;
import ch.uzh.ifi.hase.soprafs24.entity.chat.ChatChannel;
// dto
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatChannelGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatChannelPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.MessageGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.MessagePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserTypingStatusPushDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserTypingStatusGetDTO;
// service
import ch.uzh.ifi.hase.soprafs24.service.ChatService;
// springboot
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// utils
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chat")
public class ChatController {
    
    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // POST /chat/channels -> Create a new chat channel.
    @PostMapping("/channels")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatChannelGetDTO createChatChannel(@RequestBody ChatChannelPostDTO chatChannelPostDTO) {
        ChatChannel createdChannel = chatService.createChatChannel(chatChannelPostDTO);
        return DTOMapper.INSTANCE.convertEntityToChatChannelGetDTO(createdChannel);
    }

    // GET /chat/channels/user/{userId} -> Get all channels for a given user.
    @GetMapping("/channels/user/{userId}")
    public ResponseEntity<List<ChatChannelGetDTO>> getChannelsForUser(@PathVariable Long userId) {
        List<ChatChannel> channels = chatService.getChannelsForUser(userId);
        List<ChatChannelGetDTO> channelDTOs = channels.stream()
                .map(DTOMapper.INSTANCE::convertEntityToChatChannelGetDTO)
                .collect(Collectors.toList());

        if (channelDTOs.isEmpty()) {
            // Returns 204
            return ResponseEntity.noContent().build();
        }
        // Returns 200
        return ResponseEntity.ok(channelDTOs);
    }

    // POST /chat/{channelId}/message -> Send a message in a channel.
    @PostMapping("/{channelId}/message")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageGetDTO sendMessage(@PathVariable Long channelId,
                                     @RequestBody MessagePostDTO messagePostDTO) {
        Message createdMessage = chatService.sendMessage(channelId, messagePostDTO);
        return DTOMapper.INSTANCE.convertEntityToMessageGetDTO(createdMessage);
    }

    // GET /chat/channels/{channelId} -> Get chat history for a channel.
    @GetMapping("/channels/{channelId}")
    public ResponseEntity<List<MessageGetDTO>> getChatHistory(@PathVariable Long channelId) {
        List<Message> messages = chatService.getChatHistory(channelId);
        List<MessageGetDTO> messageDTOs = messages.stream()
                .map(DTOMapper.INSTANCE::convertEntityToMessageGetDTO)
                .collect(Collectors.toList());

        if (messageDTOs.isEmpty()) {
            // 204
            return ResponseEntity.noContent().build();
        }
        // 200
        return ResponseEntity.ok(messageDTOs);
    }

    // PUT /chat/typing -> Update the typing indicator.
    @PutMapping("/typing")
    public ResponseEntity<UserTypingStatusGetDTO> updateTypingStatus(@RequestBody UserTypingStatusPushDTO pushDTO) {
        UserTypingStatusGetDTO result = chatService.updateTypingStatus(pushDTO);
        return ResponseEntity.ok(result);
    }

    // GET /chat/typing/{userId} -> Retrieve the current typing indicator along with the user's status.
    @GetMapping("/typing/{userId}")
    public ResponseEntity<UserTypingStatusGetDTO> getTypingStatus(@PathVariable Long userId) {
        UserTypingStatusGetDTO status = chatService.getTypingStatus(userId);
        return ResponseEntity.ok(status);
    }

    // PUT /chat/channels/{channelId} -> update a channel (group channels)
    @PutMapping("/channels/{channelId}")
    public ChatChannelGetDTO updateChatChannel(
            @PathVariable Long channelId,
            @RequestBody ChatChannelPostDTO updateDTO) {
        ChatChannel updated = chatService.updateChatChannel(channelId, updateDTO);
        return DTOMapper.INSTANCE.convertEntityToChatChannelGetDTO(updated);
    }

}

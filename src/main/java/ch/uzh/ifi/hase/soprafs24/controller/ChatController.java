package ch.uzh.ifi.hase.soprafs24.controller;

// entity
import ch.uzh.ifi.hase.soprafs24.entity.ChatChannel;
import ch.uzh.ifi.hase.soprafs24.entity.Message;
// dto
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatChannelGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatChannelPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.MessageGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.MessagePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
// service
import ch.uzh.ifi.hase.soprafs24.service.ChatService;
// springboot
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.OK)
    public List<ChatChannelGetDTO> getChannelsForUser(@PathVariable Long userId) {
        List<ChatChannel> channels = chatService.getChannelsForUser(userId);
        return channels.stream()
                .map(DTOMapper.INSTANCE::convertEntityToChatChannelGetDTO)
                .collect(Collectors.toList());
    }

    // POST /chat/{channelId}/message -> Send a message in a channel.
    @PostMapping("/{channelId}/message")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageGetDTO sendMessage(@PathVariable Long channelId,
                                     @RequestBody MessagePostDTO messagePostDTO) {
        Message createdMessage = chatService.sendMessage(channelId, messagePostDTO);
        return DTOMapper.INSTANCE.convertEntityToMessageGetDTO(createdMessage);
    }

    // GET /api/chat/channels/{channelId} -> Get chat history for a channel.
    @GetMapping("/channels/{channelId}")
    @ResponseStatus(HttpStatus.OK)
    public List<MessageGetDTO> getChatHistory(@PathVariable Long channelId) {
        List<Message> messages = chatService.getChatHistory(channelId);
        return messages.stream()
                .map(DTOMapper.INSTANCE::convertEntityToMessageGetDTO)
                .collect(Collectors.toList());
    }

}

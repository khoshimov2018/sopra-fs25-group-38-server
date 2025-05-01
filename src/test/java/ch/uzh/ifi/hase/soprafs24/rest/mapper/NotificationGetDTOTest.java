package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.uzh.ifi.hase.soprafs24.rest.dto.NotificationGetDTO;

public class NotificationGetDTOTest {
        @Test
    void testSettersAndGetters() {
        NotificationGetDTO dto = new NotificationGetDTO();

        Long id = 1L;
        Long userId = 42L;
        String message = "You have a new message";
        String creationDate = "2024-04-30T12:00:00";
        boolean read = true;
        String type = "MATCH_REQUEST";
        Long relatedEntityId = 100L;

        dto.setId(id);
        dto.setUserId(userId);
        dto.setMessage(message);
        dto.setCreationDate(creationDate);
        dto.setRead(read);
        dto.setType(type);
        dto.setRelatedEntityId(relatedEntityId);

        assertEquals(id, dto.getId());
        assertEquals(userId, dto.getUserId());
        assertEquals(message, dto.getMessage());
        assertEquals(creationDate, dto.getCreationDate());
        assertTrue(dto.isRead());
        assertEquals(type, dto.getType());
        assertEquals(relatedEntityId, dto.getRelatedEntityId());
    }
}

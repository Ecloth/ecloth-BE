package com.ecloth.beta.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Document(collection = "chats")
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Chat {

    @Id
    private String id;
    private Long sender_id;
    private Long receiver_id;
    private String content;
    private LocalDateTime create_date;

}

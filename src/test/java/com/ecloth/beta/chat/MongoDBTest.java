package com.ecloth.beta.chat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MongoDBTest {

    @Autowired
    ChatRepository chatRepository;

//    @Test
//    void saveTest(){
//
//        chatRepository.save(Chat.builder()
//                .id("chat1")
//                .sender("test-sender")
//                .receiver("test-receiver")
//                .text("text text text text text text text text text")
//                .build());
//
//    }
//
//    @Test
//    void getTest(){
//
//        var optionalChat = chatRepository.findChatById("chat1");
//        if (optionalChat.isPresent()){
//            var chat = optionalChat.get();
//            System.out.println(chat.getId());
//            System.out.println(chat.getSender());
//            System.out.println(chat.getReceiver());
//            System.out.println(chat.getText());
//        }
//
//    }

}

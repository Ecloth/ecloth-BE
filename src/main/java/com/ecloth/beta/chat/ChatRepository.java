package com.ecloth.beta.chat;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ChatRepository extends MongoRepository<Chat, String> {

    Optional<Chat> findChatById(String id);

}

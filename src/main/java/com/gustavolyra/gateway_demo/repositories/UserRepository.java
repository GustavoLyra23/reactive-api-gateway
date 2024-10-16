package com.gustavolyra.gateway_demo.repositories;

import com.gustavolyra.gateway_demo.models.entities.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository extends ReactiveMongoRepository<User, UUID> {

    Mono<User> findByEmail(String email);




}

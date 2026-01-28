package com.shramikconnect.modules.user.repository;

import com.shramikconnect.entity.Client;
import com.shramikconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Integer> {
    Optional<Client> findByUser(User user);
}

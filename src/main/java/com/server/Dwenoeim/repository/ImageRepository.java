package com.server.Dwenoeim.repository;

import com.server.Dwenoeim.domain.Image;
import com.server.Dwenoeim.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByUser(User user);
}

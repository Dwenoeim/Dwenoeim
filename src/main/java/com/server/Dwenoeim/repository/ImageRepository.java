package com.server.Dwenoeim.repository;

import com.server.Dwenoeim.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}

package com.file_share.repository;

import com.file_share.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface FileEntityRepository extends JpaRepository<FileEntity, UUID> {
    Optional<FileEntity> findByDownloadToken(String downloadToken);
}


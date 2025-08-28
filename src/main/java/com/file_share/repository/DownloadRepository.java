package com.file_share.repository;

import com.file_share.entity.Download;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DownloadRepository extends JpaRepository<Download, UUID> {

}

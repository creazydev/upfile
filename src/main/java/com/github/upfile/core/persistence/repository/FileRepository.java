package com.github.upfile.core.persistence.repository;

import com.github.upfile.core.persistence.model.File;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface FileRepository extends ReactiveCrudRepository<File, String> {
}

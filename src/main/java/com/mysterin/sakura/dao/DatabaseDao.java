package com.mysterin.sakura.dao;

import com.mysterin.sakura.model.DatabaseModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DatabaseDao extends JpaRepository<DatabaseModel, Long> {

}

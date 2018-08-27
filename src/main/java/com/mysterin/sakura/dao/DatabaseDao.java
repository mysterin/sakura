package com.mysterin.sakura.dao;

import com.mysterin.sakura.model.DatabaseModel;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author linxb
 */
public interface DatabaseDao extends JpaRepository<DatabaseModel, Long> {

}

package restful.dao;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

import restful.model.common.ElectricHealthRecord;


public interface EHRRepository extends JpaRepository<ElectricHealthRecord, Long> {
  Collection<ElectricHealthRecord> findById(Long id);
}

package restful.repositories;


import org.springframework.data.jpa.repository.JpaRepository;

import restful.common.ElectricHealthRecord;

import java.util.Collection;

public interface EHRRepository extends JpaRepository<ElectricHealthRecord, Long> {
    Collection<ElectricHealthRecord> findById(Long id);
}

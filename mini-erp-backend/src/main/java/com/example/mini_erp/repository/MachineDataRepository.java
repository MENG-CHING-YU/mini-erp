package com.example.mini_erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.mini_erp.entity.MachineData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MachineDataRepository extends JpaRepository<MachineData, Long> {
    
    // 查詢特定機台的最新數據
    Optional<MachineData> findFirstByMachineIdOrderByRecordedAtDesc(Integer machineId);
    
    // 查詢特定機台在時間範圍內的數據
    List<MachineData> findByMachineIdAndRecordedAtBetweenOrderByRecordedAtDesc(
        Integer machineId, 
        LocalDateTime start, 
        LocalDateTime end
    );
    
    // 查詢所有機台的最新數據
    @Query("SELECT md FROM MachineData md WHERE md.recordedAt = " +
           "(SELECT MAX(md2.recordedAt) FROM MachineData md2 WHERE md2.machineId = md.machineId)")
    List<MachineData> findLatestDataForAllMachines();
    
    // 刪除舊數據（保留最近N天的數據）
    void deleteByRecordedAtBefore(LocalDateTime cutoffDate);
}
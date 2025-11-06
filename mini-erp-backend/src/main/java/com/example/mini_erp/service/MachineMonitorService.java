package com.example.mini_erp.service;

import com.example.mini_erp.dto.MachineRealtimeDTO;
import com.example.mini_erp.entity.Machine;
import com.example.mini_erp.entity.MachineData;
import com.example.mini_erp.repository.MachineDataRepository;
import com.example.mini_erp.repository.MachineRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MachineMonitorService {

    @Autowired
    private MachineDataRepository machineDataRepository;

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private RedisTemplate<String, MachineRealtimeDTO> redisTemplate;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${machine.monitor.temperature.warning:70.0}")
    private Double temperatureWarning;

    @Value("${machine.monitor.temperature.critical:90.0}")
    private Double temperatureCritical;

    private static final String REDIS_KEY_PREFIX = "machine:realtime:";
    
    // 暫存待存入資料庫的數據
    private final Map<Integer, MachineData> pendingDataMap = new ConcurrentHashMap<>();

    /**
     * 處理從 MQTT 接收到的訊息
     */
    public void processMqttMessage(String topic, String payload) {
        try {
            log.info("📥 接收到 MQTT 訊息 - Topic: {}, Payload: {}", topic, payload);
            
            // 解析 JSON 數據
            MachineData data = objectMapper.readValue(payload, MachineData.class);
            data.setRecordedAt(LocalDateTime.now());
            
            // 1. 存入 Redis 快取
            cacheToRedis(data);
            
            // 2. 暫存到記憶體，等待批次存入資料庫
            pendingDataMap.put(data.getMachineId(), data);
            
            // 3. 通過 WebSocket 推送到前端
            pushToWebSocket(data);
            
        } catch (Exception e) {
            log.error("❌ 處理 MQTT 訊息失敗: {}", e.getMessage(), e);
        }
    }

    /**
     * 將數據存入 Redis 快取
     */
    private void cacheToRedis(MachineData data) {
        try {
            MachineRealtimeDTO dto = convertToDTO(data);
            String key = REDIS_KEY_PREFIX + data.getMachineId();
            redisTemplate.opsForValue().set(key, dto, 5, TimeUnit.MINUTES);
            log.debug("✅ 快取已更新 - 機台 ID: {}", data.getMachineId());
        } catch (Exception e) {
            log.error("❌ Redis 快取失敗: {}", e.getMessage(), e);
        }
    }

    /**
     * 通過 WebSocket 推送到前端
     */
    private void pushToWebSocket(MachineData data) {
        try {
            MachineRealtimeDTO dto = convertToDTO(data);
            messagingTemplate.convertAndSend("/topic/machine-data", dto);
            log.debug("📡 WebSocket 推送成功 - 機台 ID: {}", data.getMachineId());
        } catch (Exception e) {
            log.error("❌ WebSocket 推送失敗: {}", e.getMessage(), e);
        }
    }

    /**
     * 定期將暫存的數據批次存入資料庫（每分鐘執行一次）
     */
    @Scheduled(fixedDelayString = "${machine.monitor.save-interval:60000}")
    @Transactional
    public void savePendingDataToDatabase() {
        if (pendingDataMap.isEmpty()) {
            return;
        }

        try {
            List<MachineData> dataList = new ArrayList<>(pendingDataMap.values());
            machineDataRepository.saveAll(dataList);
            
            log.info("💾 批次存入資料庫成功 - {} 筆數據", dataList.size());
            pendingDataMap.clear();
            
        } catch (Exception e) {
            log.error("❌ 批次存入資料庫失敗: {}", e.getMessage(), e);
        }
    }

    /**
     * 從 Redis 獲取所有機台的最新狀態
     */
    public List<MachineRealtimeDTO> getAllMachinesRealtimeData() {
        List<MachineRealtimeDTO> result = new ArrayList<>();
        
        try {
            // 獲取所有機台
            List<Machine> machines = machineRepository.findAll();
            
            for (Machine machine : machines) {
                String key = REDIS_KEY_PREFIX + machine.getId();
                MachineRealtimeDTO dto = redisTemplate.opsForValue().get(key);
                
                if (dto == null) {
                    // Redis 中沒有數據，從資料庫查詢最新數據
                    Optional<MachineData> latestData = 
                        machineDataRepository.findFirstByMachineIdOrderByRecordedAtDesc(machine.getId());
                    
                    if (latestData.isPresent()) {
                        dto = convertToDTO(latestData.get());
                    } else {
                        // 如果資料庫也沒有，建立預設狀態
                        dto = createDefaultDTO(machine);
                    }
                }
                
                result.add(dto);
            }
            
        } catch (Exception e) {
            log.error("❌ 查詢即時數據失敗: {}", e.getMessage(), e);
        }
        
        return result;
    }

    /**
     * 獲取特定機台的即時數據
     */
    public MachineRealtimeDTO getMachineRealtimeData(Integer machineId) {
        String key = REDIS_KEY_PREFIX + machineId;
        MachineRealtimeDTO dto = redisTemplate.opsForValue().get(key);
        
        if (dto == null) {
            Optional<MachineData> latestData = 
                machineDataRepository.findFirstByMachineIdOrderByRecordedAtDesc(machineId);
            
            if (latestData.isPresent()) {
                dto = convertToDTO(latestData.get());
            } else {
                Optional<Machine> machine = machineRepository.findById(machineId);
                dto = machine.map(this::createDefaultDTO).orElse(null);
            }
        }
        
        return dto;
    }

    /**
     * 獲取機台的歷史數據
     */
    public List<MachineData> getMachineHistoryData(Integer machineId, LocalDateTime start, LocalDateTime end) {
        return machineDataRepository.findByMachineIdAndRecordedAtBetweenOrderByRecordedAtDesc(
            machineId, start, end
        );
    }

    /**
     * 轉換為 DTO 並設定警報等級
     */
    private MachineRealtimeDTO convertToDTO(MachineData data) {
        MachineRealtimeDTO dto = new MachineRealtimeDTO();
        dto.setMachineId(data.getMachineId());
        dto.setTemperature(data.getTemperature());
        dto.setStatus(data.getStatus());
        dto.setSpeed(data.getSpeed());
        dto.setPowerConsumption(data.getPowerConsumption());
        dto.setVibration(data.getVibration());
        dto.setErrorCode(data.getErrorCode());
        dto.setMessage(data.getMessage());
        dto.setRecordedAt(data.getRecordedAt());
        
        // 設定機台名稱
        machineRepository.findById(data.getMachineId())
            .ifPresent(machine -> dto.setMachineName(machine.getName()));
        
        // 判斷警報等級
        dto.setAlertLevel(determineAlertLevel(data));
        
        return dto;
    }

    /**
     * 建立預設 DTO
     */
    private MachineRealtimeDTO createDefaultDTO(Machine machine) {
        MachineRealtimeDTO dto = new MachineRealtimeDTO();
        dto.setMachineId(machine.getId());
        dto.setMachineName(machine.getName());
        dto.setStatus("unknown");
        dto.setAlertLevel("normal");
        dto.setMessage("無即時數據");
        return dto;
    }

    /**
     * 判斷警報等級
     */
    private String determineAlertLevel(MachineData data) {
        if (data.getTemperature() != null) {
            if (data.getTemperature() >= temperatureCritical) {
                return "critical";
            } else if (data.getTemperature() >= temperatureWarning) {
                return "warning";
            }
        }
        
        if ("error".equalsIgnoreCase(data.getStatus())) {
            return "critical";
        } else if ("warning".equalsIgnoreCase(data.getStatus())) {
            return "warning";
        }
        
        return "normal";
    }

    /**
     * 清理舊數據（保留最近30天）
     */
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2點執行
    @Transactional
    public void cleanOldData() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        try {
            machineDataRepository.deleteByRecordedAtBefore(cutoffDate);
            log.info("🧹 已清理 {} 之前的舊數據", cutoffDate);
        } catch (Exception e) {
            log.error("❌ 清理舊數據失敗: {}", e.getMessage(), e);
        }
    }
}
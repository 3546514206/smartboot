package org.smartboot.smart.flow.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.smartboot.smart.flow.admin.model.EngineHistory;

import java.util.List;

/**
 * @author qinluo
 * @date 2023-01-30
 * @since 1.0.0
 */
@Mapper
public interface EngineHistoryMapper {

    /**
     * 查询引擎历史配置
     *
     * @param id   引擎id
     * @return     引擎配置列表
     */
    List<EngineHistory> queryHistory(@Param("engineId") Long id);

    /**
     * 查询详情
     *
     * @param id id
     * @return   详情
     */
    EngineHistory detail(@Param("id") Long id);

    /**
     * 插入一条配置
     *
     * @param config 配置
     * @return       1/0
     */
    int insert(@Param("config") EngineHistory config);

}

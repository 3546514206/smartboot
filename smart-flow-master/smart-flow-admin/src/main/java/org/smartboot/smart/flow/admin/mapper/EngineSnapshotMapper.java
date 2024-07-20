package org.smartboot.smart.flow.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.smartboot.smart.flow.admin.model.EngineSnapshot;

/**
 * @author qinluo
 * @date 2023-01-30
 * @since 1.0.0
 */
@Mapper
public interface EngineSnapshotMapper {

    /**
     * 查询详情
     *
     * @param name 引擎名称
     * @param md5  引擎内容md5
     * @return     详情
     */
    EngineSnapshot detail(@Param("engine") String name, @Param("md5") String md5);

    /**
     * 插入一条快照
     *
     * @param snapshot 快照记录
     * @return         1/0
     */
    int insert(@Param("snapshot") EngineSnapshot snapshot);

}

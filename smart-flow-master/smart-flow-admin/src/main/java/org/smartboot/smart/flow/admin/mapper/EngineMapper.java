package org.smartboot.smart.flow.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.smartboot.smart.flow.admin.model.EngineConfig;

import java.util.List;

/**
 * @author qinluo
 * @date 2023-01-30
 * @since 1.0.0
 */
@Mapper
public interface EngineMapper {

    /**
     * 查询引擎配置
     *
     * @param name 引擎名称
     * @param start start
     * @param size size
     * @return     引擎配置列表
     */
    List<EngineConfig> queryConfig(@Param("name") String name,
                                   @Param("start") Integer start,
                                   @Param("size") Integer size);

    /**
     * count
     *
     * @param name 引擎名称
     * @return     cnt
     */
    long count(@Param("name") String name);

    /**
     * 插入一条配置
     *
     * @param config 配置
     * @return       1/0
     */
    int insert(@Param("config") EngineConfig config);

    /**
     * 更新一条配置
     *
     * @param config 配置
     * @return       1/0
     */
    int update(@Param("config") EngineConfig config);

    /**
     * 通过名称查询engine
     *
     * @param name 名称
     * @return     engine
     */
    EngineConfig getByName(@Param("name") String name);

    /**
     * 修改引擎状态为下线
     *
     * @param id 引擎id
     * @return   1/0
     */
    int offline(@Param("id") Long id);

    /**
     * 修改引擎状态为上线
     *
     * @param id 引擎id
     * @return   1/0
     */
    int online(@Param("id") Long id);

    /**
     * 查询详情
     *
     * @param id id
     * @return   详情
     */
    EngineConfig detail(@Param("id") Long id);
}

package org.smartboot.smart.flow.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.smartboot.smart.flow.admin.controller.ReportQuery;
import org.smartboot.smart.flow.admin.model.EngineMetrics;

import java.util.Date;
import java.util.List;

/**
 * @author qinluo
 * @date 2023-01-30
 * @since 1.0.0
 */
@Mapper
public interface EngineMetricMapper {

    /**
     * 查询详情
     *
     * @param id 记录id
     * @return   详情
     */
    EngineMetrics detail(@Param("id") Long id);

    /**
     * 插入一条记录
     *
     * @param metrics 记录
     * @return        1/0
     */
    int insert(@Param("trace") EngineMetrics metrics);

    /**
     * List top 20 metrics
     *
     * @param query query condition
     * @return      list.
     */
    List<EngineMetrics> list(@Param("query") ReportQuery query);

    /**
     * count
     *
     * @param query query condition
     * @return      cnt.
     */
    long count(@Param("query") ReportQuery query);

    /**
     * List flat engines
     *
     * @param query query condition
     * @return      list.
     */
    List<EngineMetrics> selectEngines(@Param("query") ReportQuery query);

    /**
     * List max ids.
     *
     * @param query query condition
     * @return      list.
     */
    List<EngineMetrics> queryLastestRecordIds(@Param("query") ReportQuery query);

    /**
     * List by id list.
     *
     * @param idList query condition
     * @return       list.
     */
    List<EngineMetrics> listByIdList(@Param("ids") List<Long> idList);

    /**
     * 删除最旧的数据
     *
     * @param date 时间
     * @return     旧数据条数
     */
    long deleteOldest(@Param("date") Date date);
}

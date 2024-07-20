package org.smartboot.smart.flow.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.smartboot.smart.flow.admin.controller.ReportQuery;
import org.smartboot.smart.flow.admin.model.EngineTrace;

import java.util.Date;
import java.util.List;

/**
 * @author qinluo
 * @date 2023-01-30
 * @since 1.0.0
 */
@Mapper
public interface EngineTraceMapper {

    /**
     * 查询详情
     *
     * @param id 记录id
     * @return   详情
     */
    EngineTrace detail(@Param("id") Long id);

    /**
     * 插入一条记录
     *
     * @param trace 记录
     * @return      1/0
     */
    int insert(@Param("trace") EngineTrace trace);

    /**
     * List top 20 traces.
     *
     * @param query query condition
     * @return      list.
     */
    List<EngineTrace> list(@Param("query") ReportQuery query);

    /**
     * count
     *
     * @param query query condition
     * @return      cnt.
     */
    long count(@Param("query") ReportQuery query);

    /**
     * 删除最旧的数据
     *
     * @param date 时间
     * @return     旧数据条数
     */
    long deleteOldest(@Param("date") Date date);
}

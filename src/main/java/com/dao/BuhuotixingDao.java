package com.dao;

import com.entity.BuhuotixingEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.entity.vo.BuhuotixingVO;
import com.entity.view.BuhuotixingView;


/**
 * 补货提醒
 * 
 * @author 
 * @email 
 * @date 2025-01-02 15:00:00
 */
public interface BuhuotixingDao extends BaseMapper<BuhuotixingEntity> {
	
	List<BuhuotixingVO> selectListVO(@Param("ew") Wrapper<BuhuotixingEntity> wrapper);
	
	BuhuotixingVO selectVO(@Param("ew") Wrapper<BuhuotixingEntity> wrapper);
	
	List<BuhuotixingView> selectListView(@Param("ew") Wrapper<BuhuotixingEntity> wrapper);

	List<BuhuotixingView> selectListView(Pagination page,@Param("ew") Wrapper<BuhuotixingEntity> wrapper);

	
	BuhuotixingView selectView(@Param("ew") Wrapper<BuhuotixingEntity> wrapper);
	

    List<Map<String, Object>> selectValue(@Param("params") Map<String, Object> params,@Param("ew") Wrapper<BuhuotixingEntity> wrapper);

    List<Map<String, Object>> selectTimeStatValue(@Param("params") Map<String, Object> params,@Param("ew") Wrapper<BuhuotixingEntity> wrapper);

    List<Map<String, Object>> selectGroup(@Param("params") Map<String, Object> params,@Param("ew") Wrapper<BuhuotixingEntity> wrapper);



}

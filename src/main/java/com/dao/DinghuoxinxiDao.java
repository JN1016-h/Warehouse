package com.dao;

import com.entity.DinghuoxinxiEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.entity.vo.DinghuoxinxiVO;
import com.entity.view.DinghuoxinxiView;


/**
 * 订货信息
 * 
 * @author 
 * @email 
 * @date 2025-01-02 14:58:26
 */
public interface DinghuoxinxiDao extends BaseMapper<DinghuoxinxiEntity> {
	
	List<DinghuoxinxiVO> selectListVO(@Param("ew") Wrapper<DinghuoxinxiEntity> wrapper);
	
	DinghuoxinxiVO selectVO(@Param("ew") Wrapper<DinghuoxinxiEntity> wrapper);
	
	List<DinghuoxinxiView> selectListView(@Param("ew") Wrapper<DinghuoxinxiEntity> wrapper);

	List<DinghuoxinxiView> selectListView(Pagination page,@Param("ew") Wrapper<DinghuoxinxiEntity> wrapper);

	
	DinghuoxinxiView selectView(@Param("ew") Wrapper<DinghuoxinxiEntity> wrapper);
	

    List<Map<String, Object>> selectValue(@Param("params") Map<String, Object> params,@Param("ew") Wrapper<DinghuoxinxiEntity> wrapper);

    List<Map<String, Object>> selectTimeStatValue(@Param("params") Map<String, Object> params,@Param("ew") Wrapper<DinghuoxinxiEntity> wrapper);

    List<Map<String, Object>> selectGroup(@Param("params") Map<String, Object> params,@Param("ew") Wrapper<DinghuoxinxiEntity> wrapper);



}

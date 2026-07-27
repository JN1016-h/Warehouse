package com.dao;

import com.entity.ChukuxinxiEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import org.apache.ibatis.annotations.Param;
import com.entity.vo.ChukuxinxiVO;
import com.entity.view.ChukuxinxiView;


/**
 * 出库信息
 * 
 * @author 
 * @email 
 * @date 2025-01-02 14:58:26
 */
public interface ChukuxinxiDao extends BaseMapper<ChukuxinxiEntity> {
	
	List<ChukuxinxiVO> selectListVO(@Param("ew") Wrapper<ChukuxinxiEntity> wrapper);
	
	ChukuxinxiVO selectVO(@Param("ew") Wrapper<ChukuxinxiEntity> wrapper);
	
	List<ChukuxinxiView> selectListView(@Param("ew") Wrapper<ChukuxinxiEntity> wrapper);

	List<ChukuxinxiView> selectListView(IPage<?> page,@Param("ew") Wrapper<ChukuxinxiEntity> wrapper);

	
	ChukuxinxiView selectView(@Param("ew") Wrapper<ChukuxinxiEntity> wrapper);
	

}

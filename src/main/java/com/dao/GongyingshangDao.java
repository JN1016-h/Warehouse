package com.dao;

import com.entity.GongyingshangEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import org.apache.ibatis.annotations.Param;
import com.entity.vo.GongyingshangVO;
import com.entity.view.GongyingshangView;


/**
 * 供应商
 * 
 * @author 
 * @email 
 * @date 2025-01-02 14:58:26
 */
public interface GongyingshangDao extends BaseMapper<GongyingshangEntity> {
	
	List<GongyingshangVO> selectListVO(@Param("ew") Wrapper<GongyingshangEntity> wrapper);
	
	GongyingshangVO selectVO(@Param("ew") Wrapper<GongyingshangEntity> wrapper);
	
	List<GongyingshangView> selectListView(@Param("ew") Wrapper<GongyingshangEntity> wrapper);

	List<GongyingshangView> selectListView(IPage<?> page,@Param("ew") Wrapper<GongyingshangEntity> wrapper);

	
	GongyingshangView selectView(@Param("ew") Wrapper<GongyingshangEntity> wrapper);
	

}

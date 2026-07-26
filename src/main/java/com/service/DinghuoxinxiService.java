package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.DinghuoxinxiEntity;
import java.util.List;
import java.util.Map;
import com.entity.vo.DinghuoxinxiVO;
import org.apache.ibatis.annotations.Param;
import com.entity.view.DinghuoxinxiView;


/**
 * 订货信息
 *
 * @author 
 * @email 
 * @date 2025-01-02 14:58:26
 */
public interface DinghuoxinxiService extends IService<DinghuoxinxiEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<DinghuoxinxiVO> selectListVO(Wrapper<DinghuoxinxiEntity> wrapper);
   	
   	DinghuoxinxiVO selectVO(@Param("ew") Wrapper<DinghuoxinxiEntity> wrapper);
   	
   	List<DinghuoxinxiView> selectListView(Wrapper<DinghuoxinxiEntity> wrapper);
   	
   	DinghuoxinxiView selectView(@Param("ew") Wrapper<DinghuoxinxiEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<DinghuoxinxiEntity> wrapper);

   	

    List<Map<String, Object>> selectValue(Map<String, Object> params,Wrapper<DinghuoxinxiEntity> wrapper);

    List<Map<String, Object>> selectTimeStatValue(Map<String, Object> params,Wrapper<DinghuoxinxiEntity> wrapper);

    List<Map<String, Object>> selectGroup(Map<String, Object> params,Wrapper<DinghuoxinxiEntity> wrapper);



}


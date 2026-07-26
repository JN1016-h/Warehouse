package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.BuhuotixingEntity;
import java.util.List;
import java.util.Map;
import com.entity.vo.BuhuotixingVO;
import org.apache.ibatis.annotations.Param;
import com.entity.view.BuhuotixingView;


/**
 * 补货提醒
 *
 * @author 
 * @email 
 * @date 2025-01-02 15:00:00
 */
public interface BuhuotixingService extends IService<BuhuotixingEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<BuhuotixingVO> selectListVO(Wrapper<BuhuotixingEntity> wrapper);
   	
   	BuhuotixingVO selectVO(@Param("ew") Wrapper<BuhuotixingEntity> wrapper);
   	
   	List<BuhuotixingView> selectListView(Wrapper<BuhuotixingEntity> wrapper);
   	
   	BuhuotixingView selectView(@Param("ew") Wrapper<BuhuotixingEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<BuhuotixingEntity> wrapper);

   	

    List<Map<String, Object>> selectValue(Map<String, Object> params,Wrapper<BuhuotixingEntity> wrapper);

    List<Map<String, Object>> selectTimeStatValue(Map<String, Object> params,Wrapper<BuhuotixingEntity> wrapper);

    List<Map<String, Object>> selectGroup(Map<String, Object> params,Wrapper<BuhuotixingEntity> wrapper);

    /**
     * 检查库存并创建补货提醒
     * @param fuzhuangbianhao 服装编号
     * @param yonghuzhanghao 用户账号
     */
    void checkAndCreateAlert(String fuzhuangbianhao, String yonghuzhanghao);
    
    /**
     * 完成补货提醒
     * @param id 补货提醒ID
     */
    void completeAlert(Long id);
    
    /**
     * 取消补货提醒
     * @param id 补货提醒ID
     */
    void cancelAlert(Long id);

}

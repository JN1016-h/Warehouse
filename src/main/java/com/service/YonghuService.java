package com.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.utils.PageUtils;
import com.entity.YonghuEntity;
import java.util.List;
import java.util.Map;
import com.entity.vo.YonghuVO;
import org.apache.ibatis.annotations.Param;
import com.entity.view.YonghuView;


/**
 * 用户
 *
 * @author 
 * @email 
 * @date 2025-01-02 14:58:26
 */
public interface YonghuService extends IService<YonghuEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<YonghuVO> selectListVO(Wrapper<YonghuEntity> wrapper);
   	
   	YonghuVO selectVO(@Param("ew") Wrapper<YonghuEntity> wrapper);
   	
   	List<YonghuView> selectListView(Wrapper<YonghuEntity> wrapper);
   	
   	YonghuView selectView(@Param("ew") Wrapper<YonghuEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<YonghuEntity> wrapper);

    // ---------------------------------------------------------------------
    // Compatibility layer for unit tests (MyBatis-Plus 2.x -> 3.x)
    // Old names: selectById/insert
    // New names provided by IService: getById/save
    // ---------------------------------------------------------------------
    default YonghuEntity selectById(Long id) {
        return getById(id);
    }

    // Some tests call selectById(any()) and Mockito infers Object.
    default YonghuEntity selectById(Object id) {
        if (id == null) {
            return null;
        }
        if (id instanceof Long) {
            return getById((Long) id);
        }
        if (id instanceof Integer) {
            return getById(((Integer) id).longValue());
        }
        return null;
    }

    default boolean insert(YonghuEntity entity) {
        return save(entity);
    }

    // Some tests stub insert(any()) where any() is inferred as Object.
    default boolean insert(Object entity) {
        if (entity == null) {
            return false;
        }
        if (entity instanceof YonghuEntity) {
            return save((YonghuEntity) entity);
        }
        return false;
    }

}


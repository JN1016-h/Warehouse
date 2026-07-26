package com.entity.view;

import com.entity.BuhuotixingEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

import java.io.Serializable;
import com.utils.EncryptUtil;
 

/**
 * 补货提醒
 * 后端返回视图实体辅助类   
 * （通常后端关联的表或者自定义的字段需要返回使用）
 * @author 
 * @email 
 * @date 2025-01-02 15:00:00
 */
@TableName("buhuotixing")
public class BuhuotixingView  extends BuhuotixingEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	public BuhuotixingView(){
	}
 
 	public BuhuotixingView(BuhuotixingEntity buhuotixingEntity){
 	try {
			BeanUtils.copyProperties(this, buhuotixingEntity);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		
	}


}

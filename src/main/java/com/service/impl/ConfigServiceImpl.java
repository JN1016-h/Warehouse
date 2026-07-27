
package com.service.impl;


import java.util.Map;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dao.ConfigDao;
import com.entity.ConfigEntity;
import com.service.ConfigService;
import com.utils.PageUtils;
import com.utils.Query;


/**
 * 系统用户
 */
@Service("configService")
public class ConfigServiceImpl extends ServiceImpl<ConfigDao, ConfigEntity> implements ConfigService {
	@Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<ConfigEntity> wrapper) {
		Page<ConfigEntity> page = new Query<ConfigEntity>(params).getPage();
		// Compatibility for unit tests: allow stubbing selectPage(..)
		if (wrapper instanceof QueryWrapper) {
			@SuppressWarnings("unchecked")
			Page<ConfigEntity> result = this.selectPage(page, (QueryWrapper<ConfigEntity>) wrapper);
			return new PageUtils(result);
		}
		Page<ConfigEntity> result = this.page(page, wrapper);
		return new PageUtils(result);
	}

	// ---------------------------------------------------------------------
	// Compatibility layer for existing unit tests (*ServiceImplTest)
	// ---------------------------------------------------------------------
	public Page<ConfigEntity> selectPage(Page<ConfigEntity> page, QueryWrapper<ConfigEntity> wrapper) {
		return this.page(page, wrapper);
	}
}

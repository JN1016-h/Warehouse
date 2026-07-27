
package com.service.impl;


import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dao.UsersDao;
import com.entity.UsersEntity;
import com.service.UsersService;
import com.utils.PageUtils;
import com.utils.Query;


/**
 * 系统用户
 */
@Service("usersService")
public class UsersServiceImpl extends ServiceImpl<UsersDao, UsersEntity> implements UsersService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		Page<UsersEntity> page = new Query<UsersEntity>(params).getPage();
		// Compatibility for unit tests: allow stubbing selectPage(..)
		Page<UsersEntity> result = this.selectPage(page, new QueryWrapper<UsersEntity>());
		return new PageUtils(result);
	}

	@Override
	public List<UsersEntity> selectListView(Wrapper<UsersEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public PageUtils queryPage(Map<String, Object> params,
			Wrapper<UsersEntity> wrapper) {
		 Page<UsersEntity> page =new Query<UsersEntity>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
	}

	// ---------------------------------------------------------------------
	// Compatibility layer for existing unit tests (selectPage)
	// ---------------------------------------------------------------------
	public Page<UsersEntity> selectPage(Page<UsersEntity> page, QueryWrapper<UsersEntity> wrapper) {
		return this.page(page, wrapper);
	}
}

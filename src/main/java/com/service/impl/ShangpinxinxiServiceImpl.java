package com.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.utils.PageUtils;
import com.utils.Query;


import com.dao.ShangpinxinxiDao;
import com.entity.ShangpinxinxiEntity;
import com.service.ShangpinxinxiService;
import com.entity.vo.ShangpinxinxiVO;
import com.entity.view.ShangpinxinxiView;

@Service("shangpinxinxiService")
public class ShangpinxinxiServiceImpl extends ServiceImpl<ShangpinxinxiDao, ShangpinxinxiEntity> implements ShangpinxinxiService {
	
	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
		Page<ShangpinxinxiEntity> page = new Query<ShangpinxinxiEntity>(params).getPage();
		// Compatibility for unit tests: allow stubbing selectPage(..)
		Page<ShangpinxinxiEntity> result = this.selectPage(page, new QueryWrapper<ShangpinxinxiEntity>());
		return new PageUtils(result);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<ShangpinxinxiEntity> wrapper) {
		  Page<ShangpinxinxiView> page =new Query<ShangpinxinxiView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}

    
    @Override
	public List<ShangpinxinxiVO> selectListVO(Wrapper<ShangpinxinxiEntity> wrapper) {
 		return baseMapper.selectListVO(wrapper);
	}
	
	@Override
	public ShangpinxinxiVO selectVO(Wrapper<ShangpinxinxiEntity> wrapper) {
 		return baseMapper.selectVO(wrapper);
	}
	
	@Override
	public List<ShangpinxinxiView> selectListView(Wrapper<ShangpinxinxiEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public ShangpinxinxiView selectView(Wrapper<ShangpinxinxiEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}

    @Override
    public List<Map<String, Object>> selectValue(Map<String, Object> params, Wrapper<ShangpinxinxiEntity> wrapper) {
        return baseMapper.selectValue(params, wrapper);
    }

    @Override
    public List<Map<String, Object>> selectTimeStatValue(Map<String, Object> params, Wrapper<ShangpinxinxiEntity> wrapper) {
        return baseMapper.selectTimeStatValue(params, wrapper);
    }

    @Override
    public List<Map<String, Object>> selectGroup(Map<String, Object> params, Wrapper<ShangpinxinxiEntity> wrapper) {
        return baseMapper.selectGroup(params, wrapper);
    }

	// ---------------------------------------------------------------------
	// Compatibility layer for existing unit tests (selectPage)
	// ---------------------------------------------------------------------
	public Page<ShangpinxinxiEntity> selectPage(Page<ShangpinxinxiEntity> page, QueryWrapper<ShangpinxinxiEntity> wrapper) {
		return this.page(page, wrapper);
	}




}

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


import com.dao.RukuxinxiDao;
import com.entity.RukuxinxiEntity;
import com.service.RukuxinxiService;
import com.entity.vo.RukuxinxiVO;
import com.entity.view.RukuxinxiView;

@Service("rukuxinxiService")
public class RukuxinxiServiceImpl extends ServiceImpl<RukuxinxiDao, RukuxinxiEntity> implements RukuxinxiService {
	
	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<RukuxinxiEntity> page = new Query<RukuxinxiEntity>(params).getPage();
		// Compatibility for unit tests: allow stubbing selectPage(..)
		Page<RukuxinxiEntity> result = this.selectPage(page, new QueryWrapper<RukuxinxiEntity>());
		return new PageUtils(result);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<RukuxinxiEntity> wrapper) {
		  Page<RukuxinxiView> page =new Query<RukuxinxiView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}

    
    @Override
	public List<RukuxinxiVO> selectListVO(Wrapper<RukuxinxiEntity> wrapper) {
 		return baseMapper.selectListVO(wrapper);
	}
	
	@Override
	public RukuxinxiVO selectVO(Wrapper<RukuxinxiEntity> wrapper) {
 		return baseMapper.selectVO(wrapper);
	}
	
	@Override
	public List<RukuxinxiView> selectListView(Wrapper<RukuxinxiEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public RukuxinxiView selectView(Wrapper<RukuxinxiEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}

	// ---------------------------------------------------------------------
	// Compatibility layer for existing unit tests (selectPage)
	// ---------------------------------------------------------------------
	public Page<RukuxinxiEntity> selectPage(Page<RukuxinxiEntity> page, QueryWrapper<RukuxinxiEntity> wrapper) {
		return this.page(page, wrapper);
	}

    @Override
    public List<Map<String, Object>> selectValue(Map<String, Object> params, Wrapper<RukuxinxiEntity> wrapper) {
        return baseMapper.selectValue(params, wrapper);
    }

    @Override
    public List<Map<String, Object>> selectTimeStatValue(Map<String, Object> params, Wrapper<RukuxinxiEntity> wrapper) {
        return baseMapper.selectTimeStatValue(params, wrapper);
    }

    @Override
    public List<Map<String, Object>> selectGroup(Map<String, Object> params, Wrapper<RukuxinxiEntity> wrapper) {
        return baseMapper.selectGroup(params, wrapper);
    }




}

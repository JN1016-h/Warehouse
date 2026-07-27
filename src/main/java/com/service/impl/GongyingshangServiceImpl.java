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


import com.dao.GongyingshangDao;
import com.entity.GongyingshangEntity;
import com.service.GongyingshangService;
import com.entity.vo.GongyingshangVO;
import com.entity.view.GongyingshangView;

@Service("gongyingshangService")
public class GongyingshangServiceImpl extends ServiceImpl<GongyingshangDao, GongyingshangEntity> implements GongyingshangService {
	
	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
		Page<GongyingshangEntity> page = new Query<GongyingshangEntity>(params).getPage();
		// Compatibility for unit tests: allow stubbing selectPage(..)
		Page<GongyingshangEntity> result = this.selectPage(page, new QueryWrapper<GongyingshangEntity>());
		return new PageUtils(result);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<GongyingshangEntity> wrapper) {
		  Page<GongyingshangView> page =new Query<GongyingshangView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}

    
    @Override
	public List<GongyingshangVO> selectListVO(Wrapper<GongyingshangEntity> wrapper) {
 		return baseMapper.selectListVO(wrapper);
	}
	
	@Override
	public GongyingshangVO selectVO(Wrapper<GongyingshangEntity> wrapper) {
 		return baseMapper.selectVO(wrapper);
	}
	
	@Override
	public List<GongyingshangView> selectListView(Wrapper<GongyingshangEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public GongyingshangView selectView(Wrapper<GongyingshangEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}

	// ---------------------------------------------------------------------
	// Compatibility layer for existing unit tests (selectPage)
	// ---------------------------------------------------------------------
	public Page<GongyingshangEntity> selectPage(Page<GongyingshangEntity> page, QueryWrapper<GongyingshangEntity> wrapper) {
		return this.page(page, wrapper);
	}


}

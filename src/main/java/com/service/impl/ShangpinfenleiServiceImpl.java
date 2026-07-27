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


import com.dao.ShangpinfenleiDao;
import com.entity.ShangpinfenleiEntity;
import com.service.ShangpinfenleiService;
import com.entity.vo.ShangpinfenleiVO;
import com.entity.view.ShangpinfenleiView;

@Service("shangpinfenleiService")
public class ShangpinfenleiServiceImpl extends ServiceImpl<ShangpinfenleiDao, ShangpinfenleiEntity> implements ShangpinfenleiService {
	
	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<ShangpinfenleiEntity> page = new Query<ShangpinfenleiEntity>(params).getPage();
		// Compatibility for unit tests: allow stubbing selectPage(..)
		Page<ShangpinfenleiEntity> result = this.selectPage(page, new QueryWrapper<ShangpinfenleiEntity>());
		return new PageUtils(result);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<ShangpinfenleiEntity> wrapper) {
		  Page<ShangpinfenleiView> page =new Query<ShangpinfenleiView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}

    
    @Override
	public List<ShangpinfenleiVO> selectListVO(Wrapper<ShangpinfenleiEntity> wrapper) {
 		return baseMapper.selectListVO(wrapper);
	}
	
	@Override
	public ShangpinfenleiVO selectVO(Wrapper<ShangpinfenleiEntity> wrapper) {
 		return baseMapper.selectVO(wrapper);
	}
	
	@Override
	public List<ShangpinfenleiView> selectListView(Wrapper<ShangpinfenleiEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public ShangpinfenleiView selectView(Wrapper<ShangpinfenleiEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}

	// ---------------------------------------------------------------------
	// Compatibility layer for existing unit tests (selectPage)
	// ---------------------------------------------------------------------
	public Page<ShangpinfenleiEntity> selectPage(Page<ShangpinfenleiEntity> page,
			QueryWrapper<ShangpinfenleiEntity> wrapper) {
		return this.page(page, wrapper);
	}


}

package com.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.List;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.utils.PageUtils;
import com.utils.Query;


import com.dao.DinghuoxinxiDao;
import com.entity.DinghuoxinxiEntity;
import com.service.DinghuoxinxiService;
import com.entity.vo.DinghuoxinxiVO;
import com.entity.view.DinghuoxinxiView;

@Service("dinghuoxinxiService")
public class DinghuoxinxiServiceImpl extends ServiceImpl<DinghuoxinxiDao, DinghuoxinxiEntity> implements DinghuoxinxiService {
	
	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<DinghuoxinxiEntity> page = this.selectPage(
                new Query<DinghuoxinxiEntity>(params).getPage(),
                new EntityWrapper<DinghuoxinxiEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<DinghuoxinxiEntity> wrapper) {
		  Page<DinghuoxinxiView> page =new Query<DinghuoxinxiView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}

    
    @Override
	public List<DinghuoxinxiVO> selectListVO(Wrapper<DinghuoxinxiEntity> wrapper) {
 		return baseMapper.selectListVO(wrapper);
	}
	
	@Override
	public DinghuoxinxiVO selectVO(Wrapper<DinghuoxinxiEntity> wrapper) {
 		return baseMapper.selectVO(wrapper);
	}
	
	@Override
	public List<DinghuoxinxiView> selectListView(Wrapper<DinghuoxinxiEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public DinghuoxinxiView selectView(Wrapper<DinghuoxinxiEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}

    @Override
    public List<Map<String, Object>> selectValue(Map<String, Object> params, Wrapper<DinghuoxinxiEntity> wrapper) {
        return baseMapper.selectValue(params, wrapper);
    }

    @Override
    public List<Map<String, Object>> selectTimeStatValue(Map<String, Object> params, Wrapper<DinghuoxinxiEntity> wrapper) {
        return baseMapper.selectTimeStatValue(params, wrapper);
    }

    @Override
    public List<Map<String, Object>> selectGroup(Map<String, Object> params, Wrapper<DinghuoxinxiEntity> wrapper) {
        return baseMapper.selectGroup(params, wrapper);
    }




}

package com.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;
import java.util.List;
import java.util.Date;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.utils.PageUtils;
import com.utils.Query;


import com.dao.BuhuotixingDao;
import com.entity.BuhuotixingEntity;
import com.service.BuhuotixingService;
import com.service.ShangpinxinxiService;
import com.entity.ShangpinxinxiEntity;
import com.entity.vo.BuhuotixingVO;
import com.entity.view.BuhuotixingView;

@Service("buhuotixingService")
public class BuhuotixingServiceImpl extends ServiceImpl<BuhuotixingDao, BuhuotixingEntity> implements BuhuotixingService {
	
	@Autowired
	private ShangpinxinxiService shangpinxinxiService;
	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<BuhuotixingEntity> page = this.selectPage(
                new Query<BuhuotixingEntity>(params).getPage(),
                new EntityWrapper<BuhuotixingEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<BuhuotixingEntity> wrapper) {
		  Page<BuhuotixingView> page =new Query<BuhuotixingView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}

    
    @Override
	public List<BuhuotixingVO> selectListVO(Wrapper<BuhuotixingEntity> wrapper) {
 		return baseMapper.selectListVO(wrapper);
	}
	
	@Override
	public BuhuotixingVO selectVO(Wrapper<BuhuotixingEntity> wrapper) {
 		return baseMapper.selectVO(wrapper);
	}
	
	@Override
	public List<BuhuotixingView> selectListView(Wrapper<BuhuotixingEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public BuhuotixingView selectView(Wrapper<BuhuotixingEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}

    @Override
    public List<Map<String, Object>> selectValue(Map<String, Object> params, Wrapper<BuhuotixingEntity> wrapper) {
        return baseMapper.selectValue(params, wrapper);
    }

    @Override
    public List<Map<String, Object>> selectTimeStatValue(Map<String, Object> params, Wrapper<BuhuotixingEntity> wrapper) {
        return baseMapper.selectTimeStatValue(params, wrapper);
    }

    @Override
    public List<Map<String, Object>> selectGroup(Map<String, Object> params, Wrapper<BuhuotixingEntity> wrapper) {
        return baseMapper.selectGroup(params, wrapper);
    }

    @Override
    public void checkAndCreateAlert(String fuzhuangbianhao, String yonghuzhanghao) {
        // 1. 查询商品信息
        EntityWrapper<ShangpinxinxiEntity> wrapper = new EntityWrapper<>();
        wrapper.eq("fuzhuangbianhao", fuzhuangbianhao);
        ShangpinxinxiEntity product = shangpinxinxiService.selectOne(wrapper);
        
        if (product == null) {
            return;
        }
        
        // 2. 检查库存是否低于阈值
        Integer currentStock = product.getFuzhuangkucun();
        Integer threshold = product.getKucunyuzhi() != null ? product.getKucunyuzhi() : 10;
        
        if (currentStock >= threshold) {
            return;
        }
        
        // 3. 检查是否已存在该用户的待处理补货提醒
        EntityWrapper<BuhuotixingEntity> alertWrapper = new EntityWrapper<>();
        alertWrapper.eq("fuzhuangbianhao", fuzhuangbianhao);
        alertWrapper.eq("chuangjianren", yonghuzhanghao);
        alertWrapper.eq("tixingzhuangtai", "待处理");
        int count = this.selectCount(alertWrapper);
        
        if (count > 0) {
            return; // 已存在该用户的待处理提醒，不重复创建
        }
        
        // 4. 创建补货提醒
        BuhuotixingEntity alert = new BuhuotixingEntity();
        alert.setFuzhuangbianhao(product.getFuzhuangbianhao());
        alert.setFuzhuangmingcheng(product.getFuzhuangmingcheng());
        alert.setShangpinfenlei(product.getShangpinfenlei());
        alert.setGongyingshangmingcheng(product.getGongyingshangmingcheng());
        alert.setDangqiankucun(currentStock);
        alert.setKucunyuzhi(threshold);
        alert.setBuhuoshuliang(threshold - currentStock + 20); // 建议补货到阈值+20
        alert.setTixingzhuangtai("待处理");
        alert.setChuangjianren(yonghuzhanghao); // 设置为用户账号
        
        this.insert(alert);
    }
    
    @Override
    public void completeAlert(Long id) {
        BuhuotixingEntity alert = this.selectById(id);
        if (alert != null) {
            alert.setTixingzhuangtai("已完成");
            alert.setWanchengshijian(new Date());
            this.updateById(alert);
        }
    }
    
    @Override
    public void cancelAlert(Long id) {
        BuhuotixingEntity alert = this.selectById(id);
        if (alert != null) {
            alert.setTixingzhuangtai("已取消");
            this.updateById(alert);
        }
    }

}

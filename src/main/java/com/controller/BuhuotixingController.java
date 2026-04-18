package com.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.utils.ValidatorUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.annotation.IgnoreAuth;

import com.entity.BuhuotixingEntity;
import com.entity.view.BuhuotixingView;

import com.service.BuhuotixingService;
import com.service.TokenService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MPUtil;
import com.utils.MapUtils;
import com.utils.CommonUtil;

/**
 * 补货提醒
 * 后端接口
 * @author 
 * @email 
 * @date 2025-01-02 15:00:00
 */
@RestController
@RequestMapping("/buhuotixing")
public class BuhuotixingController {
    @Autowired
    private BuhuotixingService buhuotixingService;

    /**
     * 后台列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, BuhuotixingEntity buhuotixing,
                  HttpServletRequest request){
        String tableName = request.getSession().getAttribute("tableName").toString();
        if(tableName.equals("yonghu")) {
            // 用户只能看到自己的补货提醒
            String username = (String)request.getSession().getAttribute("username");
            buhuotixing.setChuangjianren(username);
        }
        
        EntityWrapper<BuhuotixingEntity> ew = new EntityWrapper<BuhuotixingEntity>();

        PageUtils page = buhuotixingService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, buhuotixing), params), params));

        return R.ok().put("data", page);
    }
    
    /**
     * 前台列表
     */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, BuhuotixingEntity buhuotixing, 
                  HttpServletRequest request){
        EntityWrapper<BuhuotixingEntity> ew = new EntityWrapper<BuhuotixingEntity>();

        PageUtils page = buhuotixingService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, buhuotixing), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/lists")
    public R list(BuhuotixingEntity buhuotixing){
        EntityWrapper<BuhuotixingEntity> ew = new EntityWrapper<BuhuotixingEntity>();
        ew = (EntityWrapper<BuhuotixingEntity>) MPUtil.allLike(ew, buhuotixing);
        return R.ok().put("data", buhuotixingService.selectListView(ew));
    }

    /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(BuhuotixingEntity buhuotixing){
        EntityWrapper<BuhuotixingEntity> ew = new EntityWrapper<BuhuotixingEntity>();
        ew = (EntityWrapper<BuhuotixingEntity>) MPUtil.allLike(ew, buhuotixing);
        BuhuotixingView buhuotixingView = buhuotixingService.selectView(ew);
        return R.ok("查询补货提醒成功").put("data", buhuotixingView);
    }
	
    /**
     * 后台详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        BuhuotixingEntity buhuotixing = buhuotixingService.selectById(id);
        return R.ok().put("data", buhuotixing);
    }

    /**
     * 前台详情
     */
    @IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        BuhuotixingEntity buhuotixing = buhuotixingService.selectById(id);
        return R.ok().put("data", buhuotixing);
    }

    /**
     * 后台保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody BuhuotixingEntity buhuotixing, HttpServletRequest request){
    	//ValidatorUtils.validateEntity(buhuotixing);
        if(buhuotixing.getTixingzhuangtai() == null || buhuotixing.getTixingzhuangtai().isEmpty()) {
            buhuotixing.setTixingzhuangtai("待处理");
        }
        buhuotixingService.insert(buhuotixing);
        return R.ok();
    }
    
    /**
     * 前台保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody BuhuotixingEntity buhuotixing, HttpServletRequest request){
    	//ValidatorUtils.validateEntity(buhuotixing);
        if(buhuotixing.getTixingzhuangtai() == null || buhuotixing.getTixingzhuangtai().isEmpty()) {
            buhuotixing.setTixingzhuangtai("待处理");
        }
        buhuotixingService.insert(buhuotixing);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody BuhuotixingEntity buhuotixing, HttpServletRequest request){
        //ValidatorUtils.validateEntity(buhuotixing);
        buhuotixingService.updateById(buhuotixing);//全部更新
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        buhuotixingService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
    /**
     * 提醒接口
     */
	@RequestMapping("/remind/{columnName}/{type}")
	public R remindCount(@PathVariable("columnName") String columnName, HttpServletRequest request, 
						 @PathVariable("type") String type,@RequestParam Map<String, Object> map) {
		map.put("column", columnName);
		map.put("type", type);
		
		if(type.equals("2")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar c = Calendar.getInstance();
			Date remindStartDate = null;
			Date remindEndDate = null;
			if(map.get("remindstart")!=null) {
				Integer remindStart = Integer.parseInt(map.get("remindstart").toString());
				c.setTime(new Date()); 
				c.add(Calendar.DAY_OF_MONTH,remindStart);
				remindStartDate = c.getTime();
				map.put("remindstart", sdf.format(remindStartDate));
			}
			if(map.get("remindend")!=null) {
				Integer remindEnd = Integer.parseInt(map.get("remindend").toString());
				c.setTime(new Date());
				c.add(Calendar.DAY_OF_MONTH,remindEnd);
				remindEndDate = c.getTime();
				map.put("remindend", sdf.format(remindEndDate));
			}
		}
		
		Wrapper<BuhuotixingEntity> wrapper = new EntityWrapper<BuhuotixingEntity>();
		if(map.get("remindstart")!=null) {
			wrapper.ge(columnName, map.get("remindstart"));
		}
		if(map.get("remindend")!=null) {
			wrapper.le(columnName, map.get("remindend"));
		}

		int count = buhuotixingService.selectCount(wrapper);
		return R.ok().put("count", count);
	}
	
    /**
     * 完成补货提醒
     */
    @RequestMapping("/complete/{id}")
    public R complete(@PathVariable("id") Long id) {
        buhuotixingService.completeAlert(id);
        return R.ok("补货提醒已完成");
    }
    
    /**
     * 取消补货提醒
     */
    @RequestMapping("/cancel/{id}")
    public R cancel(@PathVariable("id") Long id) {
        buhuotixingService.cancelAlert(id);
        return R.ok("补货提醒已取消");
    }
    
    /**
     * 统计各状态的补货提醒数量
     */
    @RequestMapping("/count")
    public R count(@RequestParam Map<String, Object> params, BuhuotixingEntity buhuotixing) {
        EntityWrapper<BuhuotixingEntity> ew = new EntityWrapper<BuhuotixingEntity>();
        int count = buhuotixingService.selectCount(
            MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, buhuotixing), params), params));
        return R.ok().put("data", count);
    }
    
    /**
     * 按值统计
     */
    @RequestMapping("/value/{xColumnName}/{yColumnName}")
    public R value(@PathVariable("yColumnName") String yColumnName, @PathVariable("xColumnName") String xColumnName,HttpServletRequest request) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("xColumn", xColumnName);
        params.put("yColumn", yColumnName);
        EntityWrapper<BuhuotixingEntity> ew = new EntityWrapper<BuhuotixingEntity>();
        List<Map<String, Object>> result = buhuotixingService.selectValue(params, ew);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for(Map<String, Object> m : result) {
            for(String k : m.keySet()) {
                if(m.get(k) instanceof Date) {
                    m.put(k, sdf.format((Date)m.get(k)));
                }
            }
        }
        return R.ok().put("data", result);
    }

    /**
     * 按时间统计
     */
    @RequestMapping("/value/{xColumnName}/{yColumnName}/{timeStatType}")
    public R valueDay(@PathVariable("yColumnName") String yColumnName, @PathVariable("xColumnName") String xColumnName, @PathVariable("timeStatType") String timeStatType,HttpServletRequest request) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("xColumn", xColumnName);
        params.put("yColumn", yColumnName);
        params.put("timeStatType", timeStatType);
        EntityWrapper<BuhuotixingEntity> ew = new EntityWrapper<BuhuotixingEntity>();
        List<Map<String, Object>> result = buhuotixingService.selectTimeStatValue(params, ew);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for(Map<String, Object> m : result) {
            for(String k : m.keySet()) {
                if(m.get(k) instanceof Date) {
                    m.put(k, sdf.format((Date)m.get(k)));
                }
            }
        }
        return R.ok().put("data", result);
    }
    
    /**
     * 分组统计
     */
    @RequestMapping("/group/{columnName}")
    public R group(@PathVariable("columnName") String columnName,HttpServletRequest request) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("column", columnName);
        EntityWrapper<BuhuotixingEntity> ew = new EntityWrapper<BuhuotixingEntity>();
        List<Map<String, Object>> result = buhuotixingService.selectGroup(params, ew);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for(Map<String, Object> m : result) {
            for(String k : m.keySet()) {
                if(m.get(k) instanceof Date) {
                    m.put(k, sdf.format((Date)m.get(k)));
                }
            }
        }
        return R.ok().put("data", result);
    }

}

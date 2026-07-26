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
import java.util.Collections;

import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import com.utils.ValidatorUtils;
import com.utils.DeSensUtil;
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

import com.entity.ChukuxinxiEntity;
import com.entity.view.ChukuxinxiView;

import com.service.ChukuxinxiService;
import com.service.ShangpinxinxiService;
import com.service.BuhuotixingService;
import com.service.TokenService;
import com.entity.ShangpinxinxiEntity;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MPUtil;
import com.utils.MapUtils;
import com.utils.CommonUtil;
import java.io.IOException;

/**
 * 出库信息
 * 后端接口
 * @author 
 * @email 
 * @date 2025-01-02 14:58:26
 */
@RestController
@RequestMapping("/chukuxinxi")
public class ChukuxinxiController {
    @Autowired
    private ChukuxinxiService chukuxinxiService;
    
    @Autowired
    private ShangpinxinxiService shangpinxinxiService;
    
    @Autowired
    private BuhuotixingService buhuotixingService;






    



    /**
     * 后台列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,ChukuxinxiEntity chukuxinxi,
		HttpServletRequest request){
		String tableName = request.getSession().getAttribute("tableName").toString();
		if(tableName.equals("yonghu")) {
			// 用户只能看到自己的出库记录
			String username = (String)request.getSession().getAttribute("username");
			chukuxinxi.setZhanghao(username);
		}
        EntityWrapper<ChukuxinxiEntity> ew = new EntityWrapper<ChukuxinxiEntity>();



		PageUtils page = chukuxinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, chukuxinxi), params), params));
				Map<String, String> deSens = new HashMap<>();
				DeSensUtil.desensitize(page,deSens);
        return R.ok().put("data", page);
    }
    
    /**
     * 前台列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,ChukuxinxiEntity chukuxinxi, 
		HttpServletRequest request){
        EntityWrapper<ChukuxinxiEntity> ew = new EntityWrapper<ChukuxinxiEntity>();

		PageUtils page = chukuxinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, chukuxinxi), params), params));
		
				Map<String, String> deSens = new HashMap<>();
				DeSensUtil.desensitize(page,deSens);
        return R.ok().put("data", page);
    }



	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( ChukuxinxiEntity chukuxinxi){
       	EntityWrapper<ChukuxinxiEntity> ew = new EntityWrapper<ChukuxinxiEntity>();
      	ew.allEq(MPUtil.allEQMapPre( chukuxinxi, "chukuxinxi")); 
        return R.ok().put("data", chukuxinxiService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(ChukuxinxiEntity chukuxinxi){
        EntityWrapper< ChukuxinxiEntity> ew = new EntityWrapper< ChukuxinxiEntity>();
 		ew.allEq(MPUtil.allEQMapPre( chukuxinxi, "chukuxinxi")); 
		ChukuxinxiView chukuxinxiView =  chukuxinxiService.selectView(ew);
		return R.ok("查询出库信息成功").put("data", chukuxinxiView);
    }
	
    /**
     * 后台详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        ChukuxinxiEntity chukuxinxi = chukuxinxiService.selectById(id);
				Map<String, String> deSens = new HashMap<>();
				DeSensUtil.desensitize(chukuxinxi,deSens);
        return R.ok().put("data", chukuxinxi);
    }

    /**
     * 前台详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        ChukuxinxiEntity chukuxinxi = chukuxinxiService.selectById(id);
				Map<String, String> deSens = new HashMap<>();
				DeSensUtil.desensitize(chukuxinxi,deSens);
        return R.ok().put("data", chukuxinxi);
    }
    



    /**
     * 后台保存
     */
    @RequestMapping("/save")
    @Transactional
    public R save(@RequestBody ChukuxinxiEntity chukuxinxi, HttpServletRequest request){
    	//ValidatorUtils.validateEntity(chukuxinxi);
    	
    	// 自动从session获取用户账号和姓名
    	String tableName = request.getSession().getAttribute("tableName").toString();
    	if(tableName.equals("yonghu")) {
    		String username = (String)request.getSession().getAttribute("username");
    		String xingming = (String)request.getSession().getAttribute("xingming");
    		chukuxinxi.setZhanghao(username);
    		chukuxinxi.setXingming(xingming);
    	}
    	
        chukuxinxiService.insert(chukuxinxi);
        
        // 更新商品库存（出库减少库存）
        if(chukuxinxi.getFuzhuangbianhao() != null && chukuxinxi.getFuzhuangkucun() != null) {
            EntityWrapper<ShangpinxinxiEntity> wrapper = new EntityWrapper<>();
            wrapper.eq("fuzhuangbianhao", chukuxinxi.getFuzhuangbianhao());
            ShangpinxinxiEntity product = shangpinxinxiService.selectOne(wrapper);
            if(product != null) {
                Integer currentStock = product.getFuzhuangkucun() != null ? product.getFuzhuangkucun() : 0;
                product.setFuzhuangkucun(currentStock - chukuxinxi.getFuzhuangkucun());
                shangpinxinxiService.updateById(product);
                
                // 检查是否需要生成补货提醒（传递用户账号）
                String username = chukuxinxi.getZhanghao();
                if(username != null && !username.isEmpty()) {
                    buhuotixingService.checkAndCreateAlert(chukuxinxi.getFuzhuangbianhao(), username);
                }
            }
        }
        
        return R.ok();
    }
    
    /**
     * 前台保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody ChukuxinxiEntity chukuxinxi, HttpServletRequest request){
    	//ValidatorUtils.validateEntity(chukuxinxi);
        chukuxinxiService.insert(chukuxinxi);
        return R.ok().put("data",chukuxinxi.getId());
    }





    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody ChukuxinxiEntity chukuxinxi, HttpServletRequest request){
        //ValidatorUtils.validateEntity(chukuxinxi);
        //全部更新
        chukuxinxiService.updateById(chukuxinxi);

        return R.ok();
    }



    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        chukuxinxiService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
	












}

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

import com.entity.RukuxinxiEntity;
import com.entity.view.RukuxinxiView;

import com.service.RukuxinxiService;
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
 * 入库信息
 * 后端接口
 * @author 
 * @email 
 * @date 2025-01-02 14:58:26
 */
@RestController
@RequestMapping("/rukuxinxi")
public class RukuxinxiController {
    @Autowired
    private RukuxinxiService rukuxinxiService;
    
    @Autowired
    private ShangpinxinxiService shangpinxinxiService;
    
    @Autowired
    private BuhuotixingService buhuotixingService;






    



    /**
     * 后台列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,RukuxinxiEntity rukuxinxi,
		HttpServletRequest request){
		String tableName = request.getSession().getAttribute("tableName").toString();
		if(tableName.equals("yonghu")) {
			// 用户只能看到自己验收的入库记录
			String username = (String)request.getSession().getAttribute("username");
			rukuxinxi.setZhanghao(username);
		}
        EntityWrapper<RukuxinxiEntity> ew = new EntityWrapper<RukuxinxiEntity>();



		PageUtils page = rukuxinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, rukuxinxi), params), params));
				Map<String, String> deSens = new HashMap<>();
				DeSensUtil.desensitize(page,deSens);
        return R.ok().put("data", page);
    }
    
    /**
     * 前台列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,RukuxinxiEntity rukuxinxi, 
		HttpServletRequest request){
        EntityWrapper<RukuxinxiEntity> ew = new EntityWrapper<RukuxinxiEntity>();

		PageUtils page = rukuxinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, rukuxinxi), params), params));
		
				Map<String, String> deSens = new HashMap<>();
				DeSensUtil.desensitize(page,deSens);
        return R.ok().put("data", page);
    }



	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( RukuxinxiEntity rukuxinxi){
       	EntityWrapper<RukuxinxiEntity> ew = new EntityWrapper<RukuxinxiEntity>();
      	ew.allEq(MPUtil.allEQMapPre( rukuxinxi, "rukuxinxi")); 
        return R.ok().put("data", rukuxinxiService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(RukuxinxiEntity rukuxinxi){
        EntityWrapper< RukuxinxiEntity> ew = new EntityWrapper< RukuxinxiEntity>();
 		ew.allEq(MPUtil.allEQMapPre( rukuxinxi, "rukuxinxi")); 
		RukuxinxiView rukuxinxiView =  rukuxinxiService.selectView(ew);
		return R.ok("查询入库信息成功").put("data", rukuxinxiView);
    }
	
    /**
     * 后台详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        RukuxinxiEntity rukuxinxi = rukuxinxiService.selectById(id);
				Map<String, String> deSens = new HashMap<>();
				DeSensUtil.desensitize(rukuxinxi,deSens);
        return R.ok().put("data", rukuxinxi);
    }

    /**
     * 前台详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        RukuxinxiEntity rukuxinxi = rukuxinxiService.selectById(id);
				Map<String, String> deSens = new HashMap<>();
				DeSensUtil.desensitize(rukuxinxi,deSens);
        return R.ok().put("data", rukuxinxi);
    }
    



    /**
     * 后台保存
     */
    @RequestMapping("/save")
    @Transactional
    public R save(@RequestBody RukuxinxiEntity rukuxinxi, HttpServletRequest request){
    	//ValidatorUtils.validateEntity(rukuxinxi);
    	
    	// 自动从session获取用户账号和姓名
    	String tableName = request.getSession().getAttribute("tableName").toString();
    	if(tableName.equals("yonghu")) {
    		String username = (String)request.getSession().getAttribute("username");
    		String xingming = (String)request.getSession().getAttribute("xingming");
    		rukuxinxi.setZhanghao(username);
    		rukuxinxi.setXingming(xingming);
    	}
    	
        rukuxinxiService.insert(rukuxinxi);
        
        // 更新商品库存
        if(rukuxinxi.getFuzhuangbianhao() != null && rukuxinxi.getFuzhuangkucun() != null) {
            EntityWrapper<ShangpinxinxiEntity> wrapper = new EntityWrapper<>();
            wrapper.eq("fuzhuangbianhao", rukuxinxi.getFuzhuangbianhao());
            ShangpinxinxiEntity product = shangpinxinxiService.selectOne(wrapper);
            if(product != null) {
                Integer currentStock = product.getFuzhuangkucun() != null ? product.getFuzhuangkucun() : 0;
                product.setFuzhuangkucun(currentStock + rukuxinxi.getFuzhuangkucun());
                shangpinxinxiService.updateById(product);
                
                // 检查是否需要生成补货提醒（传递用户账号）
                String username = rukuxinxi.getZhanghao();
                if(username != null && !username.isEmpty()) {
                    buhuotixingService.checkAndCreateAlert(rukuxinxi.getFuzhuangbianhao(), username);
                }
            }
        }
        
        return R.ok();
    }
    
    /**
     * 前台保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody RukuxinxiEntity rukuxinxi, HttpServletRequest request){
    	//ValidatorUtils.validateEntity(rukuxinxi);
        rukuxinxiService.insert(rukuxinxi);
        return R.ok().put("data",rukuxinxi.getId());
    }





    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody RukuxinxiEntity rukuxinxi, HttpServletRequest request){
        //ValidatorUtils.validateEntity(rukuxinxi);
        //全部更新
        rukuxinxiService.updateById(rukuxinxi);

        return R.ok();
    }



    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        rukuxinxiService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
	








        /**
     * （按值统计）
     */
    @RequestMapping("/value/{xColumnName}/{yColumnName}")
    public R value(@PathVariable("yColumnName") String yColumnName, @PathVariable("xColumnName") String xColumnName,HttpServletRequest request) throws IOException {
        java.nio.file.Path path = java.nio.file.Paths.get("value_rukuxinxi_" + xColumnName + "_" + yColumnName + "_timeType.json");
        if(java.nio.file.Files.exists(path)) {
            String content = new String(java.nio.file.Files.readAllBytes(path), java.nio.charset.StandardCharsets.UTF_8);
            return R.ok().put("data", (new org.json.JSONArray(content)).toList());
        }else{
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("xColumn", xColumnName);
        params.put("yColumn", yColumnName);
        EntityWrapper<RukuxinxiEntity> ew = new EntityWrapper<RukuxinxiEntity>();
        String tableName = request.getSession().getAttribute("tableName").toString();
                                                        if(tableName.equals("yonghu")) {
            ew.eq("zhanghao", (String)request.getSession().getAttribute("username"));
        }
                            List<Map<String, Object>> result = rukuxinxiService.selectValue(params, ew);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for(Map<String, Object> m : result) {
            for(String k : m.keySet()) {
                if(m.get(k) instanceof Date) {
                    m.put(k, sdf.format((Date)m.get(k)));
                }
            }
        }
        Collections.sort(result, (map1, map2) -> {
            // 假设 total 总是存在并且是数值类型
            Number total1 = (Number) map1.get("total");
            Number total2 = (Number) map2.get("total");
            return Double.compare(total2.doubleValue(), total1.doubleValue());
        });
        return R.ok().put("data", result);
        }
    }
    
    /**
     * （按值统计(多)）
     */
    @RequestMapping("/valueMul/{xColumnName}")
    public R valueMul(@PathVariable("xColumnName") String xColumnName,@RequestParam String yColumnNameMul,HttpServletRequest request)  throws IOException {
        java.nio.file.Path path = java.nio.file.Paths.get("value_rukuxinxi_" + xColumnName + "_" + yColumnNameMul + "_timeType.json");
        if(java.nio.file.Files.exists(path)) {
            String content = new String(java.nio.file.Files.readAllBytes(path), java.nio.charset.StandardCharsets.UTF_8);
            return R.ok().put("data", (new org.json.JSONArray(content)).toList());
        }else{
        String[] yColumnNames = yColumnNameMul.split(",");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("xColumn", xColumnName);
        List<List<Map<String, Object>>> result2 = new ArrayList<List<Map<String,Object>>>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        EntityWrapper<RukuxinxiEntity> ew = new EntityWrapper<RukuxinxiEntity>();
String tableName = request.getSession().getAttribute("tableName").toString();
                                                        if(tableName.equals("yonghu")) {
            ew.eq("zhanghao", (String)request.getSession().getAttribute("username"));
        }
                        for(int i=0;i<yColumnNames.length;i++) {
            params.put("yColumn", yColumnNames[i]);
            List<Map<String, Object>> result = rukuxinxiService.selectValue(params, ew);
            for(Map<String, Object> m : result) {
                for(String k : m.keySet()) {
                    if(m.get(k) instanceof Date) {
                        m.put(k, sdf.format((Date)m.get(k)));
                    }
                }
            }
            result2.add(result);
        }
        return R.ok().put("data", result2);
    }
}
    
    /**
     * （按值统计）时间统计类型
     */
    @RequestMapping("/value/{xColumnName}/{yColumnName}/{timeStatType}")
    public R valueDay(@PathVariable("yColumnName") String yColumnName, @PathVariable("xColumnName") String xColumnName, @PathVariable("timeStatType") String timeStatType,HttpServletRequest request) throws IOException {
        java.nio.file.Path path = java.nio.file.Paths.get("value_rukuxinxi_" + xColumnName + "_" + yColumnName + "_"+timeStatType+".json");
        if(java.nio.file.Files.exists(path)) {
            String content = new String(java.nio.file.Files.readAllBytes(path), java.nio.charset.StandardCharsets.UTF_8);
            return R.ok().put("data", (new org.json.JSONArray(content)).toList());
        }else{
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("xColumn", xColumnName);
            params.put("yColumn", yColumnName);
            params.put("timeStatType", timeStatType);
            EntityWrapper<RukuxinxiEntity> ew = new EntityWrapper<RukuxinxiEntity>();
    String tableName = request.getSession().getAttribute("tableName").toString();
                                                                                                                                                            if(tableName.equals("yonghu")) {
                ew.eq("zhanghao", (String)request.getSession().getAttribute("username"));
            }
                                                                    List<Map<String, Object>> result = rukuxinxiService.selectTimeStatValue(params, ew);
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
    
        /**
     * （按值统计）时间统计类型(多)
     */
    @RequestMapping("/valueMul/{xColumnName}/{timeStatType}")
    public R valueMulDay(@PathVariable("xColumnName") String xColumnName, @PathVariable("timeStatType") String timeStatType,@RequestParam String yColumnNameMul,HttpServletRequest request) throws IOException
    {
        java.nio.file.Path path = java.nio.file.Paths.get("value_rukuxinxi_" + xColumnName + "_" + yColumnNameMul + "_" + timeStatType + ".json");
        if (java.nio.file.Files.exists(path)) {
            String content = new String(java.nio.file.Files.readAllBytes(path), java.nio.charset.StandardCharsets.UTF_8);
            return R.ok().put("data", (new org.json.JSONArray(content)).toList());
        }else{
            String[] yColumnNames = yColumnNameMul.split(",");
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("xColumn", xColumnName);
            params.put("timeStatType", timeStatType);
            List<List<Map<String, Object>>> result2 = new ArrayList<List<Map<String,Object>>>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            EntityWrapper<RukuxinxiEntity> ew = new EntityWrapper<RukuxinxiEntity>();
    String tableName = request.getSession().getAttribute("tableName").toString();
                                                                                                                                                            if(tableName.equals("yonghu")) {
                ew.eq("zhanghao", (String)request.getSession().getAttribute("username"));
            }
                                                            for(int i=0;i<yColumnNames.length;i++) {
                params.put("yColumn", yColumnNames[i]);
                List<Map<String, Object>> result = rukuxinxiService.selectTimeStatValue(params, ew);
                for(Map<String, Object> m : result) {
                    for(String k : m.keySet()) {
                        if(m.get(k) instanceof Date) {
                            m.put(k, sdf.format((Date)m.get(k)));
                        }
                    }
                }
                result2.add(result);
            }
            return R.ok().put("data", result2);
        }
    }
    
        /**
     * 分组统计
     */
    @RequestMapping("/group/{columnName}")
    public R group(@PathVariable("columnName") String columnName,HttpServletRequest request) throws IOException {
        java.nio.file.Path path = java.nio.file.Paths.get("group_rukuxinxi_" + columnName + "_timeType.json");
        if(java.nio.file.Files.exists(path)){
            String content = new String(java.nio.file.Files.readAllBytes(path), java.nio.charset.StandardCharsets.UTF_8);
            return R.ok().put("data", (new org.json.JSONArray(content)).toList());
        }else{
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("column", columnName);
        EntityWrapper<RukuxinxiEntity> ew = new EntityWrapper<RukuxinxiEntity>();
String tableName = request.getSession().getAttribute("tableName").toString();
                                                        if(tableName.equals("yonghu")) {
            ew.eq("zhanghao", (String)request.getSession().getAttribute("username"));
        }
                            List<Map<String, Object>> result = rukuxinxiService.selectGroup(params, ew);
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
    
    




    /**
     * 总数量
     */
    @RequestMapping("/count")
    public R count(@RequestParam Map<String, Object> params,RukuxinxiEntity rukuxinxi, HttpServletRequest request){
        String tableName = request.getSession().getAttribute("tableName").toString();
        if(tableName.equals("yonghu")) {
            rukuxinxi.setZhanghao((String)request.getSession().getAttribute("username"));
        }
        EntityWrapper<RukuxinxiEntity> ew = new EntityWrapper<RukuxinxiEntity>();
        int count = rukuxinxiService.selectCount(MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, rukuxinxi), params), params));
        return R.ok().put("data", count);
    }




}

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
import com.entity.TokenEntity;
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

import com.entity.YonghuEntity;
import com.entity.view.YonghuView;

import com.service.YonghuService;
import com.service.TokenService;
import com.service.UserService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.MPUtil;
import com.utils.MapUtils;
import com.utils.CommonUtil;
import com.utils.EncryptUtil;
import com.dto.LoginRequest;
import com.dto.UserDTO;
import com.enums.UserRole;
import com.utils.LoginRequestUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 用户
 * 后端接口
 * @author 
 * @email 
 * @date 2025-01-02 14:58:26
 */
@RestController
@RequestMapping("/yonghu")
public class YonghuController {

	private static final Logger log = LoggerFactory.getLogger(YonghuController.class);

    @Autowired
    private YonghuService yonghuService;

    @Autowired
    private UserService userService;






    
	@Autowired
	private TokenService tokenService;
	
	/**
	 * 登录
	 */
	@IgnoreAuth
	@RequestMapping(value = "/login")
	public R login(
			@RequestParam(value = "username", required = false) String username,
			@RequestParam(value = "password", required = false) String password,
			@RequestParam(value = "captcha", required = false) String captcha,
			@RequestBody(required = false) LoginRequest body,
			HttpServletRequest request) {
		String[] c = LoginRequestUtil.merge(username, password, captcha, body);
		username = c[0];
		password = c[1];
		// captcha c[2] 预留
		if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
			log.warn("Yonghu login: blank username or password");
			return R.error("账号或密码不正确");
		}
		YonghuEntity u = yonghuService.selectOne(new EntityWrapper<YonghuEntity>().eq("yonghuzhanghao", username));
		if (u == null) {
			log.warn("Yonghu login: account not found, username='{}'", username);
			return R.error("账号或密码不正确");
		}

		String stored = u.getMima();
		String inputMd5 = EncryptUtil.md5(password);
		boolean ok = inputMd5 != null && inputMd5.equals(stored);
		// Backward compatible: some old rows may store plaintext password.
		if (!ok && stored != null && stored.equals(password)) {
			ok = true;
			u.setMima(inputMd5);
			yonghuService.updateById(u);
		}
		if (!ok) {
			log.warn("Yonghu login: password mismatch, username='{}'", username);
			return R.error("账号或密码不正确");
		}
		
		String token = tokenService.generateToken(u.getId(), username,"yonghu",  "用户" );
		return R.ok().put("token", token);
	}


	
	/**
     * 注册
     */
	@IgnoreAuth
    @RequestMapping("/register")
    public R register(@RequestBody YonghuEntity yonghu){
    	//ValidatorUtils.validateEntity(yonghu);
		if (yonghu == null || StringUtils.isBlank(yonghu.getYonghuzhanghao()) || StringUtils.isBlank(yonghu.getMima())) {
			log.warn("Yonghu register: missing yonghuzhanghao or mima");
			return R.error("账号和密码不能为空");
		}
		yonghu.setYonghuzhanghao(yonghu.getYonghuzhanghao().trim());
		if (yonghu.getYonghuxingming() != null) {
			yonghu.setYonghuxingming(yonghu.getYonghuxingming().trim());
		}
    	YonghuEntity u = yonghuService.selectOne(new EntityWrapper<YonghuEntity>().eq("yonghuzhanghao", yonghu.getYonghuzhanghao()));
		if(u!=null) {
			return R.error("注册用户已存在");
		}
		Long uId = new Date().getTime();
		yonghu.setId(uId);
		yonghu.setMima(EncryptUtil.md5(yonghu.getMima()));
		try {
			yonghuService.insert(yonghu);
		} catch (Exception e) {
			log.error("Yonghu register insert failed, username='{}'", yonghu.getYonghuzhanghao(), e);
			return R.error("注册失败，请稍后重试或联系管理员");
		}
        return R.ok();
    }

	
	/**
	 * 退出
	 */
	@RequestMapping("/logout")
	public R logout(HttpServletRequest request) {
		request.getSession().invalidate();
		return R.ok("退出成功");
	}
	
	/**
     * 获取用户的session用户信息
     */
    @RequestMapping("/session")
    public R getCurrUser(HttpServletRequest request){
    	Long id = (Long)request.getSession().getAttribute("userId");
        YonghuEntity u = yonghuService.selectById(id);
        return R.ok().put("data", u);
    }
    
    /**
     * 密码重置
     */
    @IgnoreAuth
	@RequestMapping(value = "/resetPass")
    public R resetPass(String username, HttpServletRequest request){
    	YonghuEntity u = yonghuService.selectOne(new EntityWrapper<YonghuEntity>().eq("yonghuzhanghao", username));
    	if(u==null) {
    		return R.error("账号不存在");
    	}
        u.setMima(EncryptUtil.md5("123456"));
        yonghuService.updateById(u);
        return R.ok("密码已重置为：123456");
    }



    /**
     * 后台列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,YonghuEntity yonghu,
		HttpServletRequest request){
        EntityWrapper<YonghuEntity> ew = new EntityWrapper<YonghuEntity>();



		PageUtils page = yonghuService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, yonghu), params), params));
				Map<String, String> deSens = new HashMap<>();
				DeSensUtil.desensitize(page,deSens);
        return R.ok().put("data", page);
    }
    
    /**
     * 前台列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,YonghuEntity yonghu, 
		HttpServletRequest request){
        EntityWrapper<YonghuEntity> ew = new EntityWrapper<YonghuEntity>();

		PageUtils page = yonghuService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, yonghu), params), params));
		
				Map<String, String> deSens = new HashMap<>();
				DeSensUtil.desensitize(page,deSens);
        return R.ok().put("data", page);
    }



	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( YonghuEntity yonghu){
       	EntityWrapper<YonghuEntity> ew = new EntityWrapper<YonghuEntity>();
      	ew.allEq(MPUtil.allEQMapPre( yonghu, "yonghu")); 
        return R.ok().put("data", yonghuService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(YonghuEntity yonghu){
        EntityWrapper< YonghuEntity> ew = new EntityWrapper< YonghuEntity>();
 		ew.allEq(MPUtil.allEQMapPre( yonghu, "yonghu")); 
		YonghuView yonghuView =  yonghuService.selectView(ew);
		return R.ok("查询用户成功").put("data", yonghuView);
    }
	
    /**
     * 后台详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        YonghuEntity yonghu = yonghuService.selectById(id);
				Map<String, String> deSens = new HashMap<>();
				DeSensUtil.desensitize(yonghu,deSens);
        return R.ok().put("data", yonghu);
    }

    /**
     * 前台详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        YonghuEntity yonghu = yonghuService.selectById(id);
				Map<String, String> deSens = new HashMap<>();
				DeSensUtil.desensitize(yonghu,deSens);
        return R.ok().put("data", yonghu);
    }
    



    /**
     * 后台保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody YonghuEntity yonghu, HttpServletRequest request){
        if(yonghuService.selectCount(new EntityWrapper<YonghuEntity>().eq("yonghuzhanghao", yonghu.getYonghuzhanghao()))>0) {
            return R.error("账号已存在");
        }
    	yonghu.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(yonghu);
    	YonghuEntity u = yonghuService.selectOne(new EntityWrapper<YonghuEntity>().eq("yonghuzhanghao", yonghu.getYonghuzhanghao()));
		if(u!=null) {
			return R.error("用户已存在");
		}
		yonghu.setId(new Date().getTime());
		if (StringUtils.isNotBlank(yonghu.getMima())) {
			yonghu.setMima(EncryptUtil.md5(yonghu.getMima()));
		}
        yonghuService.insert(yonghu);
        return R.ok();
    }
    
    /**
     * 前台保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody YonghuEntity yonghu, HttpServletRequest request){
        if(yonghuService.selectCount(new EntityWrapper<YonghuEntity>().eq("yonghuzhanghao", yonghu.getYonghuzhanghao()))>0) {
            return R.error("账号已存在");
        }
    	yonghu.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(yonghu);
    	YonghuEntity u = yonghuService.selectOne(new EntityWrapper<YonghuEntity>().eq("yonghuzhanghao", yonghu.getYonghuzhanghao()));
		if(u!=null) {
			return R.error("用户已存在");
		}
		yonghu.setId(new Date().getTime());
		if (StringUtils.isNotBlank(yonghu.getMima())) {
			yonghu.setMima(EncryptUtil.md5(yonghu.getMima()));
		}
        yonghuService.insert(yonghu);
        return R.ok().put("data",yonghu.getId());
    }





    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody YonghuEntity yonghu, HttpServletRequest request){
        //ValidatorUtils.validateEntity(yonghu);
        if(yonghuService.selectCount(new EntityWrapper<YonghuEntity>().ne("id", yonghu.getId()).eq("yonghuzhanghao", yonghu.getYonghuzhanghao()))>0) {
            return R.error("账号已存在");
        }
        //全部更新
        yonghuService.updateById(yonghu);
    if(null!=yonghu.getYonghuzhanghao())
    {
        // 修改token
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setUsername(yonghu.getYonghuzhanghao());
        tokenService.update(tokenEntity, new EntityWrapper<TokenEntity>().eq("userid", yonghu.getId()));
    }


        return R.ok();
    }



    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        yonghuService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
    /**
     * 获取用户详细信息（包含角色）
     * 
     * @param id 用户ID
     * @return 用户详细信息DTO
     */
    @RequestMapping("/getUserInfo/{id}")
    public R getUserInfo(@PathVariable("id") Long id) {
        if (id == null) {
            return R.error("用户ID不能为空");
        }
        
        UserDTO userDTO = userService.getUserInfo(id);
        if (userDTO == null) {
            return R.error("用户不存在");
        }
        
        return R.ok().put("data", userDTO);
    }
    
    /**
     * 更新用户角色
     * 
     * @param params 包含userId和role的参数
     * @return 更新结果
     */
    @RequestMapping("/updateUserRole")
    public R updateUserRole(@RequestBody Map<String, Object> params) {
        Long userId = Long.parseLong(params.get("userId").toString());
        String roleStr = params.get("role").toString();
        
        if (userId == null || roleStr == null || roleStr.isEmpty()) {
            return R.error("用户ID和角色不能为空");
        }
        
        // 验证角色是否有效
        if (!userService.isValidRole(roleStr)) {
            return R.error("无效的用户角色");
        }
        
        UserRole role = UserRole.valueOf(roleStr);
        boolean success = userService.updateUserRole(userId, role);
        
        if (success) {
            return R.ok("角色更新成功");
        } else {
            return R.error("角色更新失败");
        }
    }
    
	












}

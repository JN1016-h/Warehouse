package com.entity.model;

import com.entity.ChukuxinxiEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
 

/**
 * 出库信息
 * 接收传参的实体类  
 *（实际开发中配合移动端接口开发手动去掉些没用的字段， 后端一般用entity就够用了） 
 * 取自ModelAndView 的model名称
 * @author 
 * @email 
 * @date 2025-01-02 14:58:26
 */
public class ChukuxinxiModel  implements Serializable {
	private static final long serialVersionUID = 1L;

	 			
	/**
	 * 服装名称
	 */
	
	private String fuzhuangmingcheng;
		
	/**
	 * 供应商名称
	 */
	
	private String gongyingshangmingcheng;
		
	/**
	 * 商品分类
	 */
	
	private String shangpinfenlei;
		
	/**
	 * 联系电话
	 */
	
	private String lianxidianhua;
		
	/**
	 * 服装图片
	 */
	
	private String fuzhuangtupian;
		
	/**
	 * 颜色
	 */
	
	private String yanse;
		
	/**
	 * 尺寸
	 */
	
	private String chicun;
		
	/**
	 * 出库数量
	 */
	
	private Integer fuzhuangkucun;
		
	/**
	 * 出库账号
	 */
	
	private String zhanghao;
		
	/**
	 * 出库人
	 */
	
	private String xingming;
		
	/**
	 * 交货时间
	 */
		
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat 
	private Date jiaohuoshijian;
		
	/**
	 * 客户名称
	 */
	
	private String kehumingcheng;
				
	
	/**
	 * 设置：服装名称
	 */
	 
	public void setFuzhuangmingcheng(String fuzhuangmingcheng) {
		this.fuzhuangmingcheng = fuzhuangmingcheng;
	}
	
	/**
	 * 获取：服装名称
	 */
	public String getFuzhuangmingcheng() {
		return fuzhuangmingcheng;
	}
				
	
	/**
	 * 设置：供应商名称
	 */
	 
	public void setGongyingshangmingcheng(String gongyingshangmingcheng) {
		this.gongyingshangmingcheng = gongyingshangmingcheng;
	}
	
	/**
	 * 获取：供应商名称
	 */
	public String getGongyingshangmingcheng() {
		return gongyingshangmingcheng;
	}
				
	
	/**
	 * 设置：商品分类
	 */
	 
	public void setShangpinfenlei(String shangpinfenlei) {
		this.shangpinfenlei = shangpinfenlei;
	}
	
	/**
	 * 获取：商品分类
	 */
	public String getShangpinfenlei() {
		return shangpinfenlei;
	}
				
	
	/**
	 * 设置：联系电话
	 */
	 
	public void setLianxidianhua(String lianxidianhua) {
		this.lianxidianhua = lianxidianhua;
	}
	
	/**
	 * 获取：联系电话
	 */
	public String getLianxidianhua() {
		return lianxidianhua;
	}
				
	
	/**
	 * 设置：服装图片
	 */
	 
	public void setFuzhuangtupian(String fuzhuangtupian) {
		this.fuzhuangtupian = fuzhuangtupian;
	}
	
	/**
	 * 获取：服装图片
	 */
	public String getFuzhuangtupian() {
		return fuzhuangtupian;
	}
				
	
	/**
	 * 设置：颜色
	 */
	 
	public void setYanse(String yanse) {
		this.yanse = yanse;
	}
	
	/**
	 * 获取：颜色
	 */
	public String getYanse() {
		return yanse;
	}
				
	
	/**
	 * 设置：尺寸
	 */
	 
	public void setChicun(String chicun) {
		this.chicun = chicun;
	}
	
	/**
	 * 获取：尺寸
	 */
	public String getChicun() {
		return chicun;
	}
				
	
	/**
	 * 设置：出库数量
	 */
	 
	public void setFuzhuangkucun(Integer fuzhuangkucun) {
		this.fuzhuangkucun = fuzhuangkucun;
	}
	
	/**
	 * 获取：出库数量
	 */
	public Integer getFuzhuangkucun() {
		return fuzhuangkucun;
	}
				
	
	/**
	 * 设置：出库账号
	 */
	 
	public void setZhanghao(String zhanghao) {
		this.zhanghao = zhanghao;
	}
	
	/**
	 * 获取：出库账号
	 */
	public String getZhanghao() {
		return zhanghao;
	}
				
	
	/**
	 * 设置：出库人
	 */
	 
	public void setXingming(String xingming) {
		this.xingming = xingming;
	}
	
	/**
	 * 获取：出库人
	 */
	public String getXingming() {
		return xingming;
	}
				
	
	/**
	 * 设置：交货时间
	 */
	 
	public void setJiaohuoshijian(Date jiaohuoshijian) {
		this.jiaohuoshijian = jiaohuoshijian;
	}
	
	/**
	 * 获取：交货时间
	 */
	public Date getJiaohuoshijian() {
		return jiaohuoshijian;
	}
				
	
	/**
	 * 设置：客户名称
	 */
	 
	public void setKehumingcheng(String kehumingcheng) {
		this.kehumingcheng = kehumingcheng;
	}
	
	/**
	 * 获取：客户名称
	 */
	public String getKehumingcheng() {
		return kehumingcheng;
	}
			
}

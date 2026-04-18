package com.entity.model;

import com.entity.DinghuoxinxiEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
 

/**
 * 订货信息
 * 接收传参的实体类  
 *（实际开发中配合移动端接口开发手动去掉些没用的字段， 后端一般用entity就够用了） 
 * 取自ModelAndView 的model名称
 * @author 
 * @email 
 * @date 2025-01-02 14:58:26
 */
public class DinghuoxinxiModel  implements Serializable {
	private static final long serialVersionUID = 1L;

	 			
	/**
	 * 服装名称
	 */
	
	private String fuzhuangmingcheng;
		
	/**
	 * 商品分类
	 */
	
	private String shangpinfenlei;
		
	/**
	 * 供应商编号
	 */
	
	private String gongyingshangbianhao;
		
	/**
	 * 供应商名称
	 */
	
	private String gongyingshangmingcheng;
		
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
	 * 订货数量
	 */
	
	private Integer fuzhuangkucun;
		
	/**
	 * 销售单价
	 */
	
	private Double xiaoshoudanjia;
		
	/**
	 * 总金额
	 */
	
	private Double zongjine;
		
	/**
	 * 出库账号
	 */
	
	private String zhanghao;
		
	/**
	 * 出库人
	 */
	
	private String xingming;
		
	/**
	 * 订货时间
	 */
		
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat 
	private Date dinghuoshijian;
		
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
	 * 设置：供应商编号
	 */
	 
	public void setGongyingshangbianhao(String gongyingshangbianhao) {
		this.gongyingshangbianhao = gongyingshangbianhao;
	}
	
	/**
	 * 获取：供应商编号
	 */
	public String getGongyingshangbianhao() {
		return gongyingshangbianhao;
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
	 * 设置：订货数量
	 */
	 
	public void setFuzhuangkucun(Integer fuzhuangkucun) {
		this.fuzhuangkucun = fuzhuangkucun;
	}
	
	/**
	 * 获取：订货数量
	 */
	public Integer getFuzhuangkucun() {
		return fuzhuangkucun;
	}
				
	
	/**
	 * 设置：销售单价
	 */
	 
	public void setXiaoshoudanjia(Double xiaoshoudanjia) {
		this.xiaoshoudanjia = xiaoshoudanjia;
	}
	
	/**
	 * 获取：销售单价
	 */
	public Double getXiaoshoudanjia() {
		return xiaoshoudanjia;
	}
				
	
	/**
	 * 设置：总金额
	 */
	 
	public void setZongjine(Double zongjine) {
		this.zongjine = zongjine;
	}
	
	/**
	 * 获取：总金额
	 */
	public Double getZongjine() {
		return zongjine;
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
	 * 设置：订货时间
	 */
	 
	public void setDinghuoshijian(Date dinghuoshijian) {
		this.dinghuoshijian = dinghuoshijian;
	}
	
	/**
	 * 获取：订货时间
	 */
	public Date getDinghuoshijian() {
		return dinghuoshijian;
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

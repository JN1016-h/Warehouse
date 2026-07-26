package com.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.lang.reflect.InvocationTargetException;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.beanutils.BeanUtils;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.enums.FieldFill;
import com.baomidou.mybatisplus.enums.IdType;


/**
 * 补货提醒
 * 数据库通用操作实体类（普通增删改查）
 * @author 
 * @email 
 * @date 2025-01-02 15:00:00
 */
@TableName("buhuotixing")
public class BuhuotixingEntity<T> implements Serializable {
	private static final long serialVersionUID = 1L;


	public BuhuotixingEntity() {
		
	}
	
	public BuhuotixingEntity(T t) {
		try {
			BeanUtils.copyProperties(this, t);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 主键id
	 */
    @TableId(type = IdType.AUTO)
	private Long id;
	/**
	 * 服装编号
	 */
					
	private String fuzhuangbianhao;
	
	/**
	 * 服装名称
	 */
					
	private String fuzhuangmingcheng;
	
	/**
	 * 商品分类
	 */
					
	private String shangpinfenlei;
	
	/**
	 * 供应商名称
	 */
					
	private String gongyingshangmingcheng;
	
	/**
	 * 当前库存
	 */
					
	private Integer dangqiankucun;
	
	/**
	 * 库存阈值
	 */
					
	private Integer kucunyuzhi;
	
	/**
	 * 建议补货数量
	 */
					
	private Integer buhuoshuliang;
	
	/**
	 * 提醒状态
	 */
					
	private String tixingzhuangtai;
	
	/**
	 * 创建人
	 */
					
	private String chuangjianren;
	
	/**
	 * 备注
	 */
					
	private String beizhu;
	
	/**
	 * 完成时间
	 */
				
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat 		
	private Date wanchengshijian;
	
	
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
	private Date addtime;

	public Date getAddtime() {
		return addtime;
	}
	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 设置：服装编号
	 */
	public void setFuzhuangbianhao(String fuzhuangbianhao) {
		this.fuzhuangbianhao = fuzhuangbianhao;
	}
	/**
	 * 获取：服装编号
	 */
	public String getFuzhuangbianhao() {
		return fuzhuangbianhao;
	}
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
	 * 设置：当前库存
	 */
	public void setDangqiankucun(Integer dangqiankucun) {
		this.dangqiankucun = dangqiankucun;
	}
	/**
	 * 获取：当前库存
	 */
	public Integer getDangqiankucun() {
		return dangqiankucun;
	}
	/**
	 * 设置：库存阈值
	 */
	public void setKucunyuzhi(Integer kucunyuzhi) {
		this.kucunyuzhi = kucunyuzhi;
	}
	/**
	 * 获取：库存阈值
	 */
	public Integer getKucunyuzhi() {
		return kucunyuzhi;
	}
	/**
	 * 设置：建议补货数量
	 */
	public void setBuhuoshuliang(Integer buhuoshuliang) {
		this.buhuoshuliang = buhuoshuliang;
	}
	/**
	 * 获取：建议补货数量
	 */
	public Integer getBuhuoshuliang() {
		return buhuoshuliang;
	}
	/**
	 * 设置：提醒状态
	 */
	public void setTixingzhuangtai(String tixingzhuangtai) {
		this.tixingzhuangtai = tixingzhuangtai;
	}
	/**
	 * 获取：提醒状态
	 */
	public String getTixingzhuangtai() {
		return tixingzhuangtai;
	}
	/**
	 * 设置：创建人
	 */
	public void setChuangjianren(String chuangjianren) {
		this.chuangjianren = chuangjianren;
	}
	/**
	 * 获取：创建人
	 */
	public String getChuangjianren() {
		return chuangjianren;
	}
	/**
	 * 设置：备注
	 */
	public void setBeizhu(String beizhu) {
		this.beizhu = beizhu;
	}
	/**
	 * 获取：备注
	 */
	public String getBeizhu() {
		return beizhu;
	}
	/**
	 * 设置：完成时间
	 */
	public void setWanchengshijian(Date wanchengshijian) {
		this.wanchengshijian = wanchengshijian;
	}
	/**
	 * 获取：完成时间
	 */
	public Date getWanchengshijian() {
		return wanchengshijian;
	}

}

package com.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import cn.hutool.core.bean.BeanUtil;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * Mybatis-Plus工具类
 */
public class MPUtil {
	public static final char UNDERLINE = '_';

	
	//mybatis plus allEQ 表达式转换
		public static Map allEQMapPre(Object bean,String pre) {
		   Map<String, Object> map =BeanUtil.beanToMap(bean);
		  return camelToUnderlineMap(map,pre);
	   }

		//mybatis plus allEQ 表达式转换
		public static Map allEQMap(Object bean) {
		   Map<String, Object> map =BeanUtil.beanToMap(bean);
		   return camelToUnderlineMap(map,"");
	   }

		public static <T> QueryWrapper<T> allLikePre(QueryWrapper<T> wrapper,Object bean,String pre) {
			   Map<String, Object> map =BeanUtil.beanToMap(bean);
			   Map result = camelToUnderlineMap(map,pre);
			 
			return genLike(wrapper,result);
		}
	
		public static <T> QueryWrapper<T> allLike(QueryWrapper<T> wrapper,Object bean) {
			  Map result = BeanUtil.beanToMap(bean, true, true);			 
			return genLike(wrapper,result);
		}
	
	
		public static <T> QueryWrapper<T> genLike(QueryWrapper<T> wrapper,Map param) {
			Iterator<Map.Entry<String, Object>> it = param.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Object> entry = it.next();
				String key = entry.getKey();
				Object value = entry.getValue();
				if (value == null) {
					continue;
				}
				wrapper.like(key, value.toString());
			}
			return wrapper;
		}
		
		public static <T> QueryWrapper<T> likeOrEq(QueryWrapper<T> wrapper,Object bean) {
			  Map result = BeanUtil.beanToMap(bean, true, true);			 
			return genLikeOrEq(wrapper,result);
		}
		
		public static <T> QueryWrapper<T> genLikeOrEq(QueryWrapper<T> wrapper,Map param) {
			Iterator<Map.Entry<String, Object>> it = param.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Object> entry = it.next();
				String key = entry.getKey();
				Object val = entry.getValue();
				if (val == null) {
					continue;
				}
				if(val.toString().contains("%")) {
					wrapper.like(key, val.toString().replace("%", ""));
				} else {
					wrapper.eq(key, val);
				}
			}
			return wrapper;
		}
		
		public static <T> QueryWrapper<T> allEq(QueryWrapper<T> wrapper,Object bean) {
			  Map result = BeanUtil.beanToMap(bean, true, true);			 
			return genEq(wrapper,result);
		}
	
	
		public static <T> QueryWrapper<T> genEq(QueryWrapper<T> wrapper,Map param) {
			Iterator<Map.Entry<String, Object>> it = param.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Object> entry = it.next();
				String key = entry.getKey();
				wrapper.eq(key, entry.getValue());
			}
			return wrapper;
		}
	
	
		public static <T> QueryWrapper<T> between(QueryWrapper<T> wrapper,Map<String, Object> params) {
			for(String key : params.keySet()) {
				String columnName = "";
				Object pval = params.get(key);
				if (pval == null) {
					continue;
				}
				if(key.endsWith("_start")) {
					columnName = key.substring(0, key.indexOf("_start"));
					if(StringUtils.isNotBlank(pval.toString())) {
						wrapper.ge(columnName, pval);
					}
				}
				if(key.endsWith("_end")) {
					columnName = key.substring(0, key.indexOf("_end"));
					if(StringUtils.isNotBlank(pval.toString())) {
						wrapper.le(columnName, pval);
					}
				}
			}
			return wrapper;
		}
	
		public static <T> QueryWrapper<T> sort2(QueryWrapper<T> wrapper,Map<String, Object> params) {
			String order = "";
			if(params.get("order") != null && StringUtils.isNotBlank(params.get("order").toString())) {
				order = params.get("order").toString();
			}
			if(params.get("sort") != null && StringUtils.isNotBlank(params.get("sort").toString())) {
				String sort = params.get("sort").toString();
				if(order.equalsIgnoreCase("desc")) {
					wrapper.orderByDesc(sort);
				} else {
					wrapper.orderByAsc(sort);
				}
			}
			return wrapper;
		}

        public static <T> QueryWrapper<T> sort(QueryWrapper<T> wrapper,Map<String, Object> params) {
            List<String> orderList = new ArrayList<String>();
            List<String> sortList = new ArrayList<String>();
            if(params.get("order") != null && StringUtils.isNotBlank(params.get("order").toString())) {
                orderList = Arrays.asList(params.get("order").toString().split(","));
            }
            if(params.get("sort") != null && StringUtils.isNotBlank(params.get("sort").toString())) {
                sortList = Arrays.asList(params.get("sort").toString().split(","));
            }
            if(orderList!=null && sortList!=null && orderList.size()==sortList.size()) {
                for(int i=0; i<orderList.size(); i++) {
                    if(orderList.get(i).equalsIgnoreCase("desc")) {
                        wrapper.orderByDesc(sortList.get(i));
                    } else {
                        wrapper.orderByAsc(sortList.get(i));
                    }
                }
            }
            return wrapper;
        }
	
	
	/**
	 * 驼峰格式字符串转换为下划线格式字符串
	 * 
	 * @param param
	 * @return
	 */
	public static String camelToUnderline(String param) {
		if (param == null || "".equals(param.trim())) {
			return "";
		}
		int len = param.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char c = param.charAt(i);
			if (Character.isUpperCase(c)) {
				sb.append(UNDERLINE);
				sb.append(Character.toLowerCase(c));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static void main(String[] ages) {
		System.out.println(camelToUnderline("ABCddfANM"));
	}
	
	public static Map camelToUnderlineMap(Map param, String pre) {

		Map<String, Object> newMap = new HashMap<String, Object>();
		Iterator<Map.Entry<String, Object>> it = param.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> entry = it.next();
			String key = entry.getKey();
			String newKey = camelToUnderline(key);
			if (pre.endsWith(".")) {
				newMap.put(pre + newKey, entry.getValue());
			} else if (StringUtils.isEmpty(pre)) {
				newMap.put(newKey, entry.getValue());
			} else {

				newMap.put(pre + "." + newKey, entry.getValue());
			}
		}
		return newMap;
	}
}

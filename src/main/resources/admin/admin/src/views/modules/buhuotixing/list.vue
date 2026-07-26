<template>
	<div class="main-content" :style='{"padding":"20px 30px","fontSize":"15px"}'>
		<!-- 列表页 -->
		<el-form class="center-form-pv" :style='{"margin":"0 10px 20px"}' :inline="true" :model="searchForm">
				<el-row :style='{"padding":"10px","background":"#fff","display":"flex"}' >
					<div :style='{"alignItems":"center","margin":"0 10px 0 0","display":"flex"}'>
						<label :style='{"margin":"0 10px 0 0","whiteSpace":"nowrap","color":"#666","display":"inline-block","lineHeight":"40px","fontSize":"inherit","fontWeight":"500","height":"40px"}' class="item-label">服装编号</label>
						<el-input v-model="searchForm.fuzhuangbianhao" placeholder="服装编号" @keydown.enter.native="search()" clearable></el-input>
					</div>
					<div :style='{"alignItems":"center","margin":"0 10px 0 0","display":"flex"}'>
						<label :style='{"margin":"0 10px 0 0","whiteSpace":"nowrap","color":"#666","display":"inline-block","lineHeight":"40px","fontSize":"inherit","fontWeight":"500","height":"40px"}' class="item-label">服装名称</label>
						<el-input v-model="searchForm.fuzhuangmingcheng" placeholder="服装名称" @keydown.enter.native="search()" clearable></el-input>
					</div>
					<div :style='{"alignItems":"center","margin":"0 10px 0 0","display":"flex"}' class="select" label="提醒状态" prop="tixingzhuangtai">
						<label :style='{"margin":"0 10px 0 0","whiteSpace":"nowrap","color":"#666","display":"inline-block","lineHeight":"40px","fontSize":"inherit","fontWeight":"500","height":"40px"}' class="item-label">提醒状态</label>
						<el-select clearable v-model="searchForm.tixingzhuangtai" placeholder="请选择提醒状态" >
							<el-option label="待处理" value="待处理"></el-option>
							<el-option label="已完成" value="已完成"></el-option>
							<el-option label="已取消" value="已取消"></el-option>
						</el-select>
					</div>
					<el-button class="search" type="success" @click="search()">
						<span class="icon iconfont icon-fangdajing07" :style='{"margin":"0 2px","fontSize":"16px","color":"#fff","height":"40px"}'></span>
						查询
					</el-button>
				</el-row>

				<el-row class="actions" :style='{"padding":"10px","margin":"0","flexWrap":"wrap","background":"#fff","display":"flex"}'>
					<el-alert
						title="提示：此页面显示实时库存不足的商品，数据来自商品信息表"
						type="info"
						:closable="false"
						show-icon>
					</el-alert>
				</el-row>
			</el-form>
			<div :style='{"width":"100%","padding":"0 10px","fontSize":"14px","color":"#000"}'>
				<el-table class="tables"
					:stripe='true'
					:style='{"borderColor":"#eee","borderRadius":"5px 5px 0 0","borderWidth":"1px 0 0 1px","background":"#fff","width":"100%","fontSize":"inherit","borderStyle":"solid"}' 
					:border='true'
					:data="dataList"
					v-loading="dataListLoading">
					<el-table-column :resizable='true' :sortable='true' label="序号" type="index" width="50" />
					<el-table-column :resizable='true' :sortable='true'  
						prop="fuzhuangbianhao"
						label="服装编号">
						<template slot-scope="scope">
							{{scope.row.fuzhuangbianhao}}
						</template>
					</el-table-column>
					<el-table-column :resizable='true' :sortable='true'  
						prop="fuzhuangmingcheng"
						label="服装名称">
						<template slot-scope="scope">
							{{scope.row.fuzhuangmingcheng}}
						</template>
					</el-table-column>
					<el-table-column :resizable='true' :sortable='true'  
						prop="shangpinfenlei"
						label="商品分类">
						<template slot-scope="scope">
							{{scope.row.shangpinfenlei}}
						</template>
					</el-table-column>
					<el-table-column :resizable='true' :sortable='true'  
						prop="gongyingshangmingcheng"
						label="供应商名称">
						<template slot-scope="scope">
							{{scope.row.gongyingshangmingcheng}}
						</template>
					</el-table-column>
					<el-table-column :resizable='true' :sortable='true'  
						prop="dangqiankucun"
						label="当前库存">
						<template slot-scope="scope">
							<el-tag :type="scope.row.dangqiankucun < scope.row.kucunyuzhi ? 'danger' : 'success'">
								{{scope.row.dangqiankucun}}
							</el-tag>
						</template>
					</el-table-column>
					<el-table-column :resizable='true' :sortable='true'  
						prop="kucunyuzhi"
						label="库存阈值">
						<template slot-scope="scope">
							{{scope.row.kucunyuzhi}}
						</template>
					</el-table-column>
					<el-table-column :resizable='true' :sortable='true'  
						prop="buhuoshuliang"
						label="建议补货数量">
						<template slot-scope="scope">
							<el-tag type="warning">{{scope.row.buhuoshuliang}}</el-tag>
						</template>
					</el-table-column>
					<el-table-column :resizable='true' :sortable='true'  
						prop="tixingzhuangtai"
						label="提醒状态">
						<template slot-scope="scope">
							<el-tag v-if="scope.row.tixingzhuangtai=='待处理'" type="warning">{{scope.row.tixingzhuangtai}}</el-tag>
							<el-tag v-else-if="scope.row.tixingzhuangtai=='已完成'" type="success">{{scope.row.tixingzhuangtai}}</el-tag>
							<el-tag v-else type="info">{{scope.row.tixingzhuangtai}}</el-tag>
						</template>
					</el-table-column>
					<el-table-column :resizable='true' :sortable='true'  
						prop="chuangjianren"
						label="创建人">
						<template slot-scope="scope">
							{{scope.row.chuangjianren}}
						</template>
					</el-table-column>
					<el-table-column :resizable='true' :sortable='true'  
						prop="addtime"
						label="创建时间"
						width="160">
						<template slot-scope="scope">
							{{scope.row.addtime}}
						</template>
					</el-table-column>
					<el-table-column :resizable='true' align="center" label="操作" width="200">
						<template slot-scope="scope">
							<el-button type="primary" icon="el-icon-edit" size="mini" @click="goToProduct(scope.row.id)">查看商品</el-button>
						</template>
					</el-table-column>
				</el-table>
				<el-pagination
					@size-change="sizeChangeHandle"
					@current-change="currentChangeHandle"
					:current-page="pageIndex"
					:page-sizes="[10, 20, 50, 100]"
					:page-size="pageSize"
					:total="totalPage"
					layout="total, sizes, prev, pager, next, jumper">
				</el-pagination>
			</div>
		</el-form>
	</div>
</template>

<script>
export default {
	data() {
		return {
			searchForm: {
				fuzhuangbianhao: '',
				fuzhuangmingcheng: '',
				tixingzhuangtai: ''
			},
			dataList: [],
			pageIndex: 1,
			pageSize: 10,
			totalPage: 0,
			dataListLoading: false
		};
	},
	mounted() {
		this.getDataList();
	},
	activated() {
		this.getDataList();
	},
	methods: {
		// 获取数据列表 - 从商品表实时读取库存不足的商品
		getDataList() {
			this.dataListLoading = true;
			let params = {
				page: 1,
				limit: 1000, // 获取所有商品
				sort: 'fuzhuangkucun',
				order: 'asc'
			};
			
			// 添加搜索条件
			if (this.searchForm.fuzhuangbianhao) {
				params['fuzhuangbianhao'] = '%' + this.searchForm.fuzhuangbianhao + '%';
			}
			if (this.searchForm.fuzhuangmingcheng) {
				params['fuzhuangmingcheng'] = '%' + this.searchForm.fuzhuangmingcheng + '%';
			}
			
			this.$http({
				url: "shangpinxinxi/list",
				method: "get",
				params: params
			}).then(({ data }) => {
				if (data && data.code === 0) {
					// 筛选出库存低于阈值的商品
					let lowStockItems = data.data.list
						.filter(item => {
							const threshold = item.kucunyuzhi || 10;
							return item.fuzhuangkucun < threshold;
						})
						.map(item => {
							const threshold = item.kucunyuzhi || 10;
							return {
								id: item.id,
								fuzhuangbianhao: item.fuzhuangbianhao,
								fuzhuangmingcheng: item.fuzhuangmingcheng,
								shangpinfenlei: item.shangpinfenlei,
								gongyingshangmingcheng: item.gongyingshangmingcheng,
								dangqiankucun: item.fuzhuangkucun,
								kucunyuzhi: threshold,
								buhuoshuliang: Math.max(threshold * 2 - item.fuzhuangkucun, 0),
								tixingzhuangtai: '待处理',
								chuangjianren: '系统',
								addtime: new Date().toISOString().slice(0, 19).replace('T', ' ')
							};
						});
					
					// 根据提醒状态筛选（如果有选择）
					if (this.searchForm.tixingzhuangtai) {
						lowStockItems = lowStockItems.filter(item => 
							item.tixingzhuangtai === this.searchForm.tixingzhuangtai
						);
					}
					
					// 手动分页
					this.totalPage = lowStockItems.length;
					const start = (this.pageIndex - 1) * this.pageSize;
					const end = start + this.pageSize;
					this.dataList = lowStockItems.slice(start, end);
				} else {
					this.dataList = [];
					this.totalPage = 0;
				}
				this.dataListLoading = false;
			}).catch(error => {
				console.error('获取库存提醒失败:', error);
				this.dataList = [];
				this.totalPage = 0;
				this.dataListLoading = false;
			});
		},
		// 每页数
		sizeChangeHandle(val) {
			this.pageSize = val;
			this.pageIndex = 1;
			this.getDataList();
		},
		// 当前页
		currentChangeHandle(val) {
			this.pageIndex = val;
			this.getDataList();
		},
		// 搜索
		search() {
			this.pageIndex = 1;
			this.getDataList();
		},
		// 跳转到商品详情页面
		goToProduct(id) {
			// 直接跳转到商品信息列表页面
			this.$router.push({ path: '/shangpinxinxi' });
		}
	}
};
</script>

<style lang="scss" scoped>
</style>

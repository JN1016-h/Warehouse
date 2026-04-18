<template>
	<div class="main-content" :style='{"padding":"20px 30px","fontSize":"15px"}'>
		<!-- 应付款项报表页面 -->
		<el-form class="center-form-pv" :style='{"margin":"0 10px 20px"}' :inline="true" :model="queryForm">
			<el-row :style='{"padding":"10px","background":"#fff","display":"flex"}'>
				<div :style='{"alignItems":"center","margin":"0 10px 0 0","display":"flex"}'>
					<label :style='{"margin":"0 10px 0 0","whiteSpace":"nowrap","color":"#666","display":"inline-block","lineHeight":"40px","fontSize":"inherit","fontWeight":"500","height":"40px"}' class="item-label">日期范围</label>
					<el-date-picker
						v-model="queryForm.dateRange"
						type="daterange"
						range-separator="至"
						start-placeholder="开始日期"
						end-placeholder="结束日期"
						value-format="yyyy-MM-dd"
						clearable>
					</el-date-picker>
				</div>
				<div :style='{"alignItems":"center","margin":"0 10px 0 0","display":"flex"}' class="select">
					<label :style='{"margin":"0 10px 0 0","whiteSpace":"nowrap","color":"#666","display":"inline-block","lineHeight":"40px","fontSize":"inherit","fontWeight":"500","height":"40px"}' class="item-label">付款状态</label>
					<el-select clearable v-model="queryForm.paymentStatus" placeholder="请选择付款状态">
						<el-option label="全部" value=""></el-option>
						<el-option label="已付款" value="PAID"></el-option>
						<el-option label="未付款" value="UNPAID"></el-option>
					</el-select>
				</div>
				<div :style='{"alignItems":"center","margin":"0 10px 0 0","display":"flex"}'>
					<label :style='{"margin":"0 10px 0 0","whiteSpace":"nowrap","color":"#666","display":"inline-block","lineHeight":"40px","fontSize":"inherit","fontWeight":"500","height":"40px"}' class="item-label">供应商名称</label>
					<el-input v-model="queryForm.supplierName" placeholder="供应商名称" @keydown.enter.native="handleQuery()" clearable></el-input>
				</div>
				<el-button class="search" type="success" @click="handleQuery()">
					<span class="icon iconfont icon-fangdajing07" :style='{"margin":"0 2px","fontSize":"16px","color":"#fff","height":"40px"}'></span>
					查询
				</el-button>
				<el-button @click="handleReset()">重置</el-button>
			</el-row>
		</el-form>

		<!-- 金额统计卡片 -->
		<el-card class="summary-card" :style='{"margin":"0 10px 20px","background":"#fff"}'>
			<div :style='{"display":"flex","justifyContent":"space-around","padding":"10px"}'>
				<div class="summary-item" :style='{"textAlign":"center"}'>
					<div :style='{"color":"#999","fontSize":"14px","marginBottom":"10px"}'>总金额</div>
					<div class="amount" :style='{"color":"#333","fontSize":"24px","fontWeight":"bold"}'>{{ formatAmount(summary.totalAmount) }}</div>
				</div>
				<div class="summary-item" :style='{"textAlign":"center"}'>
					<div :style='{"color":"#999","fontSize":"14px","marginBottom":"10px"}'>已付款</div>
					<div class="amount paid" :style='{"color":"#67C23A","fontSize":"24px","fontWeight":"bold"}'>{{ formatAmount(summary.paidAmount) }}</div>
				</div>
				<div class="summary-item" :style='{"textAlign":"center"}'>
					<div :style='{"color":"#999","fontSize":"14px","marginBottom":"10px"}'>未付款</div>
					<div class="amount unpaid" :style='{"color":"#F56C6C","fontSize":"24px","fontWeight":"bold"}'>{{ formatAmount(summary.unpaidAmount) }}</div>
				</div>
			</div>
		</el-card>

		<!-- 导出按钮 -->
		<div class="export-buttons" :style='{"margin":"0 10px 10px","display":"flex"}'>
			<el-button type="success" @click="exportExcel()">
				<span class="icon iconfont icon-excel"></span>
				导出Excel
			</el-button>
			<el-button type="warning" @click="exportPDF()">
				<span class="icon iconfont icon-pdf"></span>
				导出PDF
			</el-button>
		</div>

		<!-- 数据表格 -->
		<div :style='{"width":"100%","padding":"0 10px","fontSize":"14px","color":"#000"}'>
			<el-table class="tables"
				:stripe='true'
				:style='{"borderColor":"#eee","borderRadius":"5px 5px 0 0","borderWidth":"1px 0 0 1px","background":"#fff","width":"100%","fontSize":"inherit","borderStyle":"solid"}' 
				:border='true'
				:data="dataList"
				v-loading="dataListLoading">
				<el-table-column :resizable='true' :sortable='true' label="序号" type="index" width="50" />
				<el-table-column :resizable='true' :sortable='true' prop="orderNo" label="订单号">
					<template slot-scope="scope">
						{{scope.row.orderNo}}
					</template>
				</el-table-column>
				<el-table-column :resizable='true' :sortable='true' prop="supplierName" label="供应商名称">
					<template slot-scope="scope">
						{{scope.row.supplierName}}
					</template>
				</el-table-column>
				<el-table-column :resizable='true' :sortable='true' prop="amount" label="应付金额">
					<template slot-scope="scope">
						{{formatAmount(scope.row.amount)}}
					</template>
				</el-table-column>
				<el-table-column :resizable='true' :sortable='true' prop="paymentStatus" label="付款状态" width="120">
					<template slot-scope="scope">
						<el-tag :type="scope.row.paymentStatus === 'PAID' ? 'success' : 'danger'" size="small">
							{{ scope.row.paymentStatus === 'PAID' ? '已付款' : '未付款' }}
						</el-tag>
					</template>
				</el-table-column>
				<el-table-column :resizable='true' :sortable='true' prop="orderDate" label="订单日期">
					<template slot-scope="scope">
						{{scope.row.orderDate}}
					</template>
				</el-table-column>
				<el-table-column :resizable='true' prop="paymentTime" label="付款时间">
					<template slot-scope="scope">
						{{scope.row.paymentTime || '-'}}
					</template>
				</el-table-column>
				<el-table-column label="操作" width="150">
					<template slot-scope="scope">
						<el-button 
							v-if="scope.row.paymentStatus === 'UNPAID'"
							class="edit"
							type="success" 
							size="small"
							@click="markAsPaid(scope.row)">
							标记为已付款
						</el-button>
						<span v-else style="color: #999;">-</span>
					</template>
				</el-table-column>
			</el-table>
		</div>

		<!-- 分页组件 -->
		<el-pagination
			@size-change="handleSizeChange"
			@current-change="handleCurrentChange"
			:current-page="pagination.page"
			background
			:page-sizes="[10, 20, 50, 100]"
			:page-size="pagination.limit"
			:layout="layouts.join()"
			:total="pagination.total"
			prev-text="< "
			next-text="> "
			:hide-on-single-page="false"
			:style='{"border":"1px solid #F1F1F1","padding":"0","margin":"20px auto 0","whiteSpace":"nowrap","color":"#000000","textAlign":"center","display":"flex","justifyContent":"space-between","borderRadius":"5px","background":"#fff","width":"500px","fontSize":"12","fontWeight":"400","height":"50px"}'
		></el-pagination>
	</div>
</template>

<script>
export default {
	name: 'PayablesReport',
	data() {
		return {
			queryForm: {
				dateRange: [],
				paymentStatus: '',
				supplierName: ''
			},
			dataList: [],
			summary: {
				totalAmount: 0,
				paidAmount: 0,
				unpaidAmount: 0
			},
			pagination: {
				page: 1,
				limit: 10,
				total: 0
			},
			dataListLoading: false,
			layouts: ["prev", "pager", "next", "sizes"]
		};
	},
	created() {
		this.loadData();
	},
	methods: {
		// 加载数据
		loadData() {
			this.getDataList();
			this.getSummary();
		},
		
		// 查询数据列表
		getDataList() {
			this.dataListLoading = true;
			let params = {
				page: this.pagination.page,
				limit: this.pagination.limit
			};
			
			// 添加查询条件
			if (this.queryForm.dateRange && this.queryForm.dateRange.length === 2) {
				params.startDate = this.queryForm.dateRange[0];
				params.endDate = this.queryForm.dateRange[1];
			}
			if (this.queryForm.paymentStatus) {
				params.paymentStatus = this.queryForm.paymentStatus;
			}
			if (this.queryForm.supplierName) {
				params.supplierName = this.queryForm.supplierName;
			}
			
			this.$http({
				url: "finance/payables",
				method: "get",
				params: params
			}).then(({ data }) => {
				if (data && data.code === 0) {
					this.dataList = data.data.list || [];
					this.pagination.total = data.data.total || 0;
				} else {
					this.$message.error(data.msg || '查询失败');
					this.dataList = [];
					this.pagination.total = 0;
				}
				this.dataListLoading = false;
			}).catch(error => {
				console.error('查询应付款项失败:', error);
				this.$message.error('查询失败，请稍后重试');
				this.dataListLoading = false;
			});
		},
		
		// 获取金额统计
		getSummary() {
			let params = {};
			
			// 添加查询条件
			if (this.queryForm.dateRange && this.queryForm.dateRange.length === 2) {
				params.startDate = this.queryForm.dateRange[0];
				params.endDate = this.queryForm.dateRange[1];
			}
			if (this.queryForm.paymentStatus) {
				params.paymentStatus = this.queryForm.paymentStatus;
			}
			if (this.queryForm.supplierName) {
				params.supplierName = this.queryForm.supplierName;
			}
			
			this.$http({
				url: "finance/payables/summary",
				method: "get",
				params: params
			}).then(({ data }) => {
				if (data && data.code === 0) {
					this.summary = data.data || {
						totalAmount: 0,
						paidAmount: 0,
						unpaidAmount: 0
					};
				} else {
					this.$message.error(data.msg || '获取统计数据失败');
				}
			}).catch(error => {
				console.error('获取统计数据失败:', error);
			});
		},
		
		// 查询按钮
		handleQuery() {
			this.pagination.page = 1;
			this.loadData();
		},
		
		// 重置按钮
		handleReset() {
			this.queryForm = {
				dateRange: [],
				paymentStatus: '',
				supplierName: ''
			};
			this.pagination.page = 1;
			this.loadData();
		},
		
		// 每页数量变化
		handleSizeChange(val) {
			this.pagination.limit = val;
			this.pagination.page = 1;
			this.getDataList();
		},
		
		// 当前页变化
		handleCurrentChange(val) {
			this.pagination.page = val;
			this.getDataList();
		},
		
		// 标记为已付款
		markAsPaid(row) {
			this.$confirm('确定将此订单标记为已付款吗?', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(() => {
				this.$http({
					url: "finance/updatePaymentStatus",
					method: "post",
					data: {
						orderId: row.id,
						orderType: 'INBOUND',
						paymentStatus: 'PAID'
					}
				}).then(({ data }) => {
					if (data && data.code === 0) {
						this.$message({
							message: '操作成功',
							type: 'success',
							duration: 1500
						});
						this.loadData();
					} else {
						this.$message.error(data.msg || '操作失败');
					}
				}).catch(error => {
					console.error('更新付款状态失败:', error);
					this.$message.error('操作失败，请稍后重试');
				});
			}).catch(() => {
				// 用户取消操作
			});
		},
		
		// 导出Excel
		exportExcel() {
			let params = {};
			
			// 添加查询条件
			if (this.queryForm.dateRange && this.queryForm.dateRange.length === 2) {
				params.startDate = this.queryForm.dateRange[0];
				params.endDate = this.queryForm.dateRange[1];
			}
			if (this.queryForm.paymentStatus) {
				params.paymentStatus = this.queryForm.paymentStatus;
			}
			if (this.queryForm.supplierName) {
				params.supplierName = this.queryForm.supplierName;
			}
			
			// 添加token到参数
			const token = this.$storage.get('Token');
			if (token) {
				params.token = token;
			}
			
			// 构建URL参数
			const queryString = Object.keys(params)
				.map(key => `${key}=${encodeURIComponent(params[key])}`)
				.join('&');
			
			const url = `${this.$base.url}finance/payables/export/excel?${queryString}`;
			window.open(url, '_blank');
		},
		
		// 导出PDF
		exportPDF() {
			let params = {};
			
			// 添加查询条件
			if (this.queryForm.dateRange && this.queryForm.dateRange.length === 2) {
				params.startDate = this.queryForm.dateRange[0];
				params.endDate = this.queryForm.dateRange[1];
			}
			if (this.queryForm.paymentStatus) {
				params.paymentStatus = this.queryForm.paymentStatus;
			}
			if (this.queryForm.supplierName) {
				params.supplierName = this.queryForm.supplierName;
			}
			
			// 添加token到参数
			const token = this.$storage.get('Token');
			if (token) {
				params.token = token;
			}
			
			// 构建URL参数
			const queryString = Object.keys(params)
				.map(key => `${key}=${encodeURIComponent(params[key])}`)
				.join('&');
			
			const url = `${this.$base.url}finance/payables/export/pdf?${queryString}`;
			window.open(url, '_blank');
		},
		
		// 格式化金额
		formatAmount(amount) {
			if (amount === null || amount === undefined) {
				return '¥0.00';
			}
			return '¥' + Number(amount).toLocaleString('zh-CN', {
				minimumFractionDigits: 2,
				maximumFractionDigits: 2
			});
		}
	}
};
</script>

<style lang="scss" scoped>
.center-form-pv {
	.el-date-editor.el-input {
		width: auto;
	}
}

.el-input {
	width: auto;
}

// form
.center-form-pv .el-input {
	width: 100%;
}
.center-form-pv .el-input ::v-deep .el-input__inner {
	border: 1px solid #BFBFBF;
	border-radius: 0px;
	padding: 0 12px;
	color: #666;
	width: 100%;
	font-size: 16px;
	height: 34px;
}
.center-form-pv .el-select {
	width: 100%;
}
.center-form-pv .el-select ::v-deep .el-input__inner {
	border: 1px solid #ddd;
	border-radius: 0px;
	padding: 0 10px;
	color: #666;
	width: 100%;
	font-size: 16px;
	height: 34px;
}
.center-form-pv .el-date-editor {
	width: 100%;
}

.center-form-pv .el-date-editor ::v-deep .el-input__inner {
	border: 1px solid #ddd;
	border-radius: 0px;
	padding: 0 10px 0 30px;
	color: #666;
	width: 100%;
	font-size: 16px;
	height: 34px;
}

.center-form-pv .search {
	border: 0;
	cursor: pointer;
	align-self: center;
	padding: 0 10px;
	color: #fff;
	background: linear-gradient(150deg, #59A8FF 0%, #8CEFFC 100%);
	width: auto;
	font-size: 16px;
	height: 34px;
}

.center-form-pv .search:hover {
	opacity: 0.8;
}

// table
.el-table ::v-deep .el-table__header-wrapper thead {
	color: #999;
	font-weight: 500;
	width: 100%;
}

.el-table ::v-deep .el-table__header-wrapper thead tr {
	background: #F3F3F3;
}

.el-table ::v-deep .el-table__header-wrapper thead tr th {
	padding: 4px 0;
	background: none;
	border-color: #eee;
	border-width: 0 0px 1px 0;
	border-style: solid;
}

.el-table ::v-deep .el-table__header-wrapper thead tr th .cell {
	padding: 0 0 0 5px;
	word-wrap: normal;
	color: #000000;
	white-space: normal;
	font-weight: 400;
	display: flex;
	vertical-align: middle;
	font-size: 14px;
	line-height: 24px;
	text-overflow: ellipsis;
	word-break: break-all;
	width: 100%;
	align-items: center;
	position: relative;
	min-width: 110px;
}

.el-table ::v-deep .el-table__body-wrapper tbody {
	width: 100%;
}

.el-table ::v-deep .el-table__body-wrapper tbody tr {
	background: #fff;
}

.el-table ::v-deep .el-table__body-wrapper tbody tr td {
	padding: 4px 0;
	color: #333;
	background: #fff;
	font-size: inherit;
	border-color: #E1E1E1;
	border-width: 0 0px 1px 0;
	border-style: solid;
	text-align: left;
}

.el-table ::v-deep .el-table__body-wrapper tbody tr.el-table__row--striped td {
	background: #f8f8f8;
}

.el-table ::v-deep .el-table__body-wrapper tbody tr:hover td {
	padding: 4px 0;
	color: #333;
	background: #f0f0f0;
	border-color: #eee;
	border-width: 0 0px 1px 0;
	border-style: solid;
	text-align: left;
}

.el-table ::v-deep .el-table__body-wrapper tbody tr td .cell {
	padding: 0 0 0 5px;
	overflow: hidden;
	word-break: break-all;
	white-space: normal;
	font-size: inherit;
	line-height: 24px;
	text-overflow: ellipsis;
}

.el-table ::v-deep .el-table__body-wrapper tbody tr td .edit {
	border: 1px solid #5BAAFF;
	cursor: pointer;
	border-radius: 5;
	padding: 0 20px;
	margin: 0 5px 5px 0;
	color: #5BAAFF;
	background: #EAFDFF;
	letter-spacing: 1;
	width: auto;
	font-size: 14px;
	height: 32px;
}

.el-table ::v-deep .el-table__body-wrapper tbody tr td .edit:hover {
	opacity: 0.8;
}

// pagination
.main-content .el-pagination ::v-deep .el-pagination__total {
	margin: 0 10px 0 0;
	color: #666;
	font-weight: 400;
	vertical-align: top;
	font-size: inherit;
	line-height: 48px;
	height: 48px;
}

.main-content .el-pagination ::v-deep .btn-prev {
	border: none;
	border-radius: 2px;
	padding: 0;
	margin: 0 5px;
	color: #666;
	background: none;
	display: inline-block;
	vertical-align: top;
	font-size: 16px;
	line-height: auto;
	min-width: 35px;
	height: 48px;
}

.main-content .el-pagination ::v-deep .btn-next {
	border: none;
	border-radius: 2px;
	padding: 0;
	margin: 0 5px;
	color: #666;
	background: none;
	display: inline-block;
	vertical-align: top;
	font-size: 16px;
	line-height: auto;
	min-width: 35px;
	height: 48px;
}

.main-content .el-pagination ::v-deep .btn-prev:disabled {
	border: none;
	cursor: not-allowed;
	border-radius: 2px;
	padding: 0;
	margin: 0 5px;
	color: #C0C4CC;
	background: none;
	display: inline-block;
	vertical-align: top;
	font-size: 16px;
	line-height: auto;
	height: 48px;
}

.main-content .el-pagination ::v-deep .btn-next:disabled {
	border: none;
	cursor: not-allowed;
	border-radius: 2px;
	padding: 0;
	margin: 0 5px;
	color: #C0C4CC;
	background: none;
	display: inline-block;
	vertical-align: top;
	font-size: 16px;
	line-height: auto;
	height: 48px;
}

.main-content .el-pagination ::v-deep .el-pager {
	padding: 0;
	margin: 0;
	display: flex;
	vertical-align: top;
}

.main-content .el-pagination ::v-deep .el-pager .number {
	cursor: pointer;
	padding: 0 20px;
	color: #000000;
	background: none;
	font-weight: 400;
	display: flex;
	vertical-align: top;
	font-size: 16px;
	line-height: 100%;
	align-items: center;
	text-align: center;
	height: 100%;
}

.main-content .el-pagination ::v-deep .el-pager .number:hover {
	cursor: pointer;
	padding: 0 20px;
	color: #FFFFFF;
	font-weight: 400;
	display: flex;
	vertical-align: top;
	font-size: 16px;
	line-height: 100%;
	border-radius: 5px;
	background: #5BAAFF;
	align-items: center;
	text-align: center;
	height: 100%;
}

.main-content .el-pagination ::v-deep .el-pager .number.active {
	cursor: pointer;
	padding: 0 20px;
	color: #FFFFFF;
	font-weight: 400;
	display: flex;
	vertical-align: top;
	font-size: 16px;
	line-height: 100%;
	border-radius: 5px;
	background: #5BAAFF;
	align-items: center;
	text-align: center;
	height: 100%;
}

.main-content .el-pagination ::v-deep .el-pagination__sizes {
	display: none;
	vertical-align: top;
	font-size: 16px;
	line-height: 48px;
	height: 48px;
}

.main-content .el-pagination ::v-deep .el-pagination__jump {
	margin: 0 0 0 24px;
	color: #606266;
	display: inline-block;
	vertical-align: top;
	font-size: 16px;
	line-height: 48px;
	height: 48px;
}

// Summary card
.summary-card {
	border-radius: 5px;
}

.summary-item {
	flex: 1;
}

// Export buttons
.export-buttons {
	gap: 10px;
}
</style>

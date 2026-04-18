<template>
	<div class="addEdit-block">
		<el-form class="detail-form-content" ref="ruleForm" :model="ruleForm" :rules="rules" label-width="120px">
			<el-row>
				<el-col :span="12">
					<el-form-item label="服装编号" prop="fuzhuangbianhao">
						<el-input v-model="ruleForm.fuzhuangbianhao" placeholder="服装编号" clearable :readonly="ro.fuzhuangbianhao"></el-input>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="服装名称" prop="fuzhuangmingcheng">
						<el-input v-model="ruleForm.fuzhuangmingcheng" placeholder="服装名称" clearable :readonly="ro.fuzhuangmingcheng"></el-input>
					</el-form-item>
				</el-col>
			</el-row>
			<el-row>
				<el-col :span="12">
					<el-form-item label="商品分类" prop="shangpinfenlei">
						<el-input v-model="ruleForm.shangpinfenlei" placeholder="商品分类" clearable :readonly="ro.shangpinfenlei"></el-input>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="供应商名称" prop="gongyingshangmingcheng">
						<el-input v-model="ruleForm.gongyingshangmingcheng" placeholder="供应商名称" clearable :readonly="ro.gongyingshangmingcheng"></el-input>
					</el-form-item>
				</el-col>
			</el-row>
			<el-row>
				<el-col :span="12">
					<el-form-item label="当前库存" prop="dangqiankucun">
						<el-input-number v-model="ruleForm.dangqiankucun" :min="0" :readonly="ro.dangqiankucun" style="width: 100%"></el-input-number>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="库存阈值" prop="kucunyuzhi">
						<el-input-number v-model="ruleForm.kucunyuzhi" :min="0" :readonly="ro.kucunyuzhi" style="width: 100%"></el-input-number>
					</el-form-item>
				</el-col>
			</el-row>
			<el-row>
				<el-col :span="12">
					<el-form-item label="建议补货数量" prop="buhuoshuliang">
						<el-input-number v-model="ruleForm.buhuoshuliang" :min="1" :readonly="ro.buhuoshuliang" style="width: 100%"></el-input-number>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="提醒状态" prop="tixingzhuangtai">
						<el-select v-model="ruleForm.tixingzhuangtai" placeholder="请选择提醒状态" :disabled="ro.tixingzhuangtai" style="width: 100%">
							<el-option label="待处理" value="待处理"></el-option>
							<el-option label="已完成" value="已完成"></el-option>
							<el-option label="已取消" value="已取消"></el-option>
						</el-select>
					</el-form-item>
				</el-col>
			</el-row>
			<el-row>
				<el-col :span="12">
					<el-form-item label="创建人" prop="chuangjianren">
						<el-input v-model="ruleForm.chuangjianren" placeholder="创建人" clearable :readonly="ro.chuangjianren"></el-input>
					</el-form-item>
				</el-col>
				<el-col :span="12" v-if="ruleForm.wanchengshijian">
					<el-form-item label="完成时间" prop="wanchengshijian">
						<el-date-picker
							v-model="ruleForm.wanchengshijian"
							type="datetime"
							value-format="yyyy-MM-dd HH:mm:ss"
							placeholder="选择完成时间"
							:readonly="ro.wanchengshijian"
							style="width: 100%">
						</el-date-picker>
					</el-form-item>
				</el-col>
			</el-row>
			<el-row>
				<el-col :span="24">
					<el-form-item label="备注" prop="beizhu">
						<el-input v-model="ruleForm.beizhu" type="textarea" placeholder="备注" :rows="3" :readonly="ro.beizhu"></el-input>
					</el-form-item>
				</el-col>
			</el-row>
		</el-form>
		<div class="formModel-btns" v-if="type!='info'">
			<el-button class="btn-success" type="success" @click="onSubmit">提交</el-button>
			<el-button class="btn-close" @click="back()">取消</el-button>
		</div>
		<div class="formModel-btns" v-else>
			<el-button class="btn-close" @click="back()">返回</el-button>
		</div>
	</div>
</template>

<script>
export default {
	data() {
		return {
			id: 0,
			type: '',
			ro: {
				fuzhuangbianhao: false,
				fuzhuangmingcheng: false,
				shangpinfenlei: false,
				gongyingshangmingcheng: false,
				dangqiankucun: false,
				kucunyuzhi: false,
				buhuoshuliang: false,
				tixingzhuangtai: false,
				chuangjianren: false,
				beizhu: false,
				wanchengshijian: false
			},
			ruleForm: {
				fuzhuangbianhao: '',
				fuzhuangmingcheng: '',
				shangpinfenlei: '',
				gongyingshangmingcheng: '',
				dangqiankucun: 0,
				kucunyuzhi: 10,
				buhuoshuliang: 0,
				tixingzhuangtai: '待处理',
				chuangjianren: '',
				beizhu: '',
				wanchengshijian: ''
			},
			rules: {
				fuzhuangbianhao: [
					{ required: true, message: '服装编号不能为空', trigger: 'blur' }
				],
				fuzhuangmingcheng: [
					{ required: true, message: '服装名称不能为空', trigger: 'blur' }
				],
				dangqiankucun: [
					{ required: true, message: '当前库存不能为空', trigger: 'blur' }
				],
				kucunyuzhi: [
					{ required: true, message: '库存阈值不能为空', trigger: 'blur' }
				],
				buhuoshuliang: [
					{ required: true, message: '建议补货数量不能为空', trigger: 'blur' },
					{ type: 'number', min: 1, message: '补货数量必须大于0', trigger: 'blur' }
				]
			}
		};
	},
	methods: {
		init(id, type) {
			this.id = id || 0;
			this.type = type || '';
			this.ruleForm = {
				fuzhuangbianhao: '',
				fuzhuangmingcheng: '',
				shangpinfenlei: '',
				gongyingshangmingcheng: '',
				dangqiankucun: 0,
				kucunyuzhi: 10,
				buhuoshuliang: 0,
				tixingzhuangtai: '待处理',
				chuangjianren: this.$storage.get('sessionTable') == 'yonghu' ? this.$storage.get('username') : '管理员',
				beizhu: '',
				wanchengshijian: ''
			};
			
			if (this.type == 'info') {
				this.ro.fuzhuangbianhao = true;
				this.ro.fuzhuangmingcheng = true;
				this.ro.shangpinfenlei = true;
				this.ro.gongyingshangmingcheng = true;
				this.ro.dangqiankucun = true;
				this.ro.kucunyuzhi = true;
				this.ro.buhuoshuliang = true;
				this.ro.tixingzhuangtai = true;
				this.ro.chuangjianren = true;
				this.ro.beizhu = true;
				this.ro.wanchengshijian = true;
			}
			
			if (this.id) {
				this.getInfo();
			}
		},
		// 获取详情
		getInfo() {
			this.$http({
				url: `buhuotixing/info/${this.id}`,
				method: 'get'
			}).then(({ data }) => {
				if (data && data.code === 0) {
					this.ruleForm = data.data;
				}
			});
		},
		// 提交
		onSubmit() {
			this.$refs['ruleForm'].validate(valid => {
				if (valid) {
					this.$http({
						url: `buhuotixing/${!this.id ? 'save' : 'update'}`,
						method: 'post',
						data: this.ruleForm
					}).then(({ data }) => {
						if (data && data.code === 0) {
							this.$message({
								message: '操作成功',
								type: 'success',
								duration: 1500,
								onClose: () => {
									this.back();
								}
							});
						} else {
							this.$message.error(data.msg);
						}
					});
				}
			});
		},
		// 返回
		back() {
			this.$parent.showFlag = true;
			this.$parent.getDataList();
		}
	}
};
</script>

<style lang="scss" scoped>
.addEdit-block {
	padding: 20px;
	background: #fff;
	
	.detail-form-content {
		padding: 20px;
	}
	
	.formModel-btns {
		text-align: center;
		margin-top: 30px;
		
		.el-button {
			min-width: 100px;
			margin: 0 10px;
		}
	}
}
</style>

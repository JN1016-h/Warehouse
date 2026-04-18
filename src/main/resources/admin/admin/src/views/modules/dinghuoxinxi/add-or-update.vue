<template>
	<div class="addEdit-block">
		<el-form
			class="add-update-preview"
			ref="ruleForm"
			:model="ruleForm"
			:rules="rules"
			label-width="180px"
		>
			<template >
				<el-form-item class="input" v-if="type!='info'"  label="服装编号" prop="fuzhuangbianhao" >
					<el-input v-model="ruleForm.fuzhuangbianhao" placeholder="服装编号" clearable  :readonly="ro.fuzhuangbianhao"></el-input>
				</el-form-item>
				<el-form-item v-else class="input" label="服装编号" prop="fuzhuangbianhao" >
					<el-input v-model="ruleForm.fuzhuangbianhao" placeholder="服装编号" readonly></el-input>
				</el-form-item>
				<el-form-item class="input" v-if="type!='info'"  label="服装名称" prop="fuzhuangmingcheng" >
					<el-input v-model="ruleForm.fuzhuangmingcheng" placeholder="服装名称" clearable  :readonly="ro.fuzhuangmingcheng"></el-input>
				</el-form-item>
				<el-form-item v-else class="input" label="服装名称" prop="fuzhuangmingcheng" >
					<el-input v-model="ruleForm.fuzhuangmingcheng" placeholder="服装名称" readonly></el-input>
				</el-form-item>
				<el-form-item class="select" v-if="type!='info'"  label="商品分类" prop="shangpinfenlei" >
					<el-select :disabled="ro.shangpinfenlei" v-model="ruleForm.shangpinfenlei" placeholder="请选择商品分类" >
						<el-option
							v-for="(item,index) in shangpinfenleiOptions"
							v-bind:key="index"
							:label="item"
							:value="item">
						</el-option>
					</el-select>
				</el-form-item>
				<el-form-item v-else class="input" label="商品分类" prop="shangpinfenlei" >
					<el-input v-model="ruleForm.shangpinfenlei"
						placeholder="商品分类" readonly></el-input>
				</el-form-item>
				<el-form-item class="input" v-if="type!='info'"  label="供应商编号" prop="gongyingshangbianhao" >
					<el-input v-model="ruleForm.gongyingshangbianhao" placeholder="供应商编号" clearable  :readonly="ro.gongyingshangbianhao"></el-input>
				</el-form-item>
				<el-form-item v-else class="input" label="供应商编号" prop="gongyingshangbianhao" >
					<el-input v-model="ruleForm.gongyingshangbianhao" placeholder="供应商编号" readonly></el-input>
				</el-form-item>
				<el-form-item class="input" v-if="type!='info'"  label="供应商名称" prop="gongyingshangmingcheng" >
					<el-input v-model="ruleForm.gongyingshangmingcheng" placeholder="供应商名称" clearable  :readonly="ro.gongyingshangmingcheng"></el-input>
				</el-form-item>
				<el-form-item v-else class="input" label="供应商名称" prop="gongyingshangmingcheng" >
					<el-input v-model="ruleForm.gongyingshangmingcheng" placeholder="供应商名称" readonly></el-input>
				</el-form-item>
				<el-form-item class="input" v-if="type!='info'"  label="联系电话" prop="lianxidianhua" >
					<el-input v-model="ruleForm.lianxidianhua" placeholder="联系电话" clearable  :readonly="ro.lianxidianhua"></el-input>
				</el-form-item>
				<el-form-item v-else class="input" label="联系电话" prop="lianxidianhua" >
					<el-input v-model="ruleForm.lianxidianhua" placeholder="联系电话" readonly></el-input>
				</el-form-item>
				<el-form-item class="upload" v-if="type!='info' && !ro.fuzhuangtupian" label="服装图片" prop="fuzhuangtupian" >
					<file-upload
						tip="点击上传服装图片"
						action="file/upload"
						:limit="3"
						:multiple="true"
						:fileUrls="ruleForm.fuzhuangtupian?ruleForm.fuzhuangtupian:''"
						@change="fuzhuangtupianUploadChange"
					></file-upload>
				</el-form-item>
				<el-form-item class="upload" v-else-if="ruleForm.fuzhuangtupian" label="服装图片" prop="fuzhuangtupian" >
					<img v-if="ruleForm.fuzhuangtupian.substring(0,4)=='http'" class="upload-img" style="margin-right:20px;" v-bind:key="index" :src="ruleForm.fuzhuangtupian.split(',')[0]" width="100" height="100">
					<img v-else class="upload-img" style="margin-right:20px;" v-bind:key="index" v-for="(item,index) in ruleForm.fuzhuangtupian.split(',')" :src="$base.url+item" width="100" height="100">
				</el-form-item>
				<el-form-item class="input" v-if="type!='info'"  label="颜色" prop="yanse" >
					<el-input v-model="ruleForm.yanse" placeholder="颜色" clearable  :readonly="ro.yanse"></el-input>
				</el-form-item>
				<el-form-item v-else class="input" label="颜色" prop="yanse" >
					<el-input v-model="ruleForm.yanse" placeholder="颜色" readonly></el-input>
				</el-form-item>
				<el-form-item class="input" v-if="type!='info'"  label="尺寸" prop="chicun" >
					<el-input v-model="ruleForm.chicun" placeholder="尺寸" clearable  :readonly="ro.chicun"></el-input>
				</el-form-item>
				<el-form-item v-else class="input" label="尺寸" prop="chicun" >
					<el-input v-model="ruleForm.chicun" placeholder="尺寸" readonly></el-input>
				</el-form-item>
				<el-form-item class="input" v-if="type!='info'"  label="订货数量" prop="fuzhuangkucun" >
					<el-input v-model.number="ruleForm.fuzhuangkucun" placeholder="订货数量" clearable  :readonly="ro.fuzhuangkucun"></el-input>
				</el-form-item>
				<el-form-item v-else class="input" label="订货数量" prop="fuzhuangkucun" >
					<el-input v-model="ruleForm.fuzhuangkucun" placeholder="订货数量" readonly></el-input>
				</el-form-item>
				<el-form-item class="input" v-if="type!='info'"  label="销售单价" prop="xiaoshoudanjia" >
					<el-input-number v-model="ruleForm.xiaoshoudanjia" placeholder="销售单价" :disabled="ro.xiaoshoudanjia"></el-input-number>
				</el-form-item>
				<el-form-item v-else class="input" label="销售单价" prop="xiaoshoudanjia" >
					<el-input v-model="ruleForm.xiaoshoudanjia" placeholder="销售单价" readonly></el-input>
				</el-form-item>
				<el-form-item class="input" v-if="type!='info'"  label="总金额" prop="zongjine" >
					<el-input v-model="zongjine" placeholder="总金额" readonly></el-input>
				</el-form-item>
				<el-form-item class="input" v-else-if="ruleForm.zongjine" label="总金额" prop="zongjine" >
					<el-input v-model="ruleForm.zongjine" placeholder="总金额" readonly></el-input>
				</el-form-item>
				<el-form-item class="input" v-if="type!='info'"  label="出库账号" prop="zhanghao" >
					<el-input v-model="ruleForm.zhanghao" placeholder="出库账号" clearable  :readonly="ro.zhanghao"></el-input>
				</el-form-item>
				<el-form-item v-else class="input" label="出库账号" prop="zhanghao" >
					<el-input v-model="ruleForm.zhanghao" placeholder="出库账号" readonly></el-input>
				</el-form-item>
				<el-form-item class="input" v-if="type!='info'"  label="出库人" prop="xingming" >
					<el-input v-model="ruleForm.xingming" placeholder="出库人" clearable  :readonly="ro.xingming"></el-input>
				</el-form-item>
				<el-form-item v-else class="input" label="出库人" prop="xingming" >
					<el-input v-model="ruleForm.xingming" placeholder="出库人" readonly></el-input>
				</el-form-item>
				<el-form-item class="date" v-if="type!='info'" label="订货时间" prop="dinghuoshijian" >
					<el-date-picker
						format="yyyy 年 MM 月 dd 日"
						value-format="yyyy-MM-dd"
						v-model="ruleForm.dinghuoshijian" 
						type="date"
						:readonly="ro.dinghuoshijian"
						placeholder="订货时间"
					></el-date-picker> 
				</el-form-item>
				<el-form-item class="input" v-else-if="ruleForm.dinghuoshijian" label="订货时间" prop="dinghuoshijian" >
					<el-input v-model="ruleForm.dinghuoshijian" placeholder="订货时间" readonly></el-input>
				</el-form-item>
				<el-form-item class="input" v-if="type!='info'"  label="客户名称" prop="kehumingcheng" >
					<el-input v-model="ruleForm.kehumingcheng" placeholder="客户名称" clearable  :readonly="ro.kehumingcheng"></el-input>
				</el-form-item>
				<el-form-item v-else class="input" label="客户名称" prop="kehumingcheng" >
					<el-input v-model="ruleForm.kehumingcheng" placeholder="客户名称" readonly></el-input>
				</el-form-item>
			</template>
			<el-form-item class="btn">
				<el-button class="btn3"  v-if="type!='info'" type="success" @click="onSubmit">
					<span class="icon iconfont icon-xihuan"></span>
					提交
				</el-button>
				<el-button class="btn4" v-if="type!='info'" type="success" @click="back()">
					<span class="icon iconfont icon-xihuan"></span>
					取消
				</el-button>
				<el-button class="btn5" v-if="type=='info'" type="success" @click="back()">
					<span class="icon iconfont icon-xihuan"></span>
					返回
				</el-button>
			</el-form-item>
		</el-form>
    

	</div>
</template>
<script>
	import { 
		isNumber,
		isIntNumer,
	} from "@/utils/validate";
	export default {
		data() {
			var validateNumber = (rule, value, callback) => {
				if(!value){
					callback();
				} else if (!isNumber(value)) {
					callback(new Error("请输入数字"));
				} else {
					callback();
				}
			};
			var validateIntNumber = (rule, value, callback) => {
				if(!value){
					callback();
				} else if (!isIntNumer(value)) {
					callback(new Error("请输入整数"));
				} else {
					callback();
				}
			};
			return {
				id: '',
				type: '',
			
			
				ro:{
					fuzhuangbianhao : false,
					fuzhuangmingcheng : false,
					shangpinfenlei : false,
					gongyingshangbianhao : false,
					gongyingshangmingcheng : false,
					lianxidianhua : false,
					fuzhuangtupian : false,
					yanse : false,
					chicun : false,
					fuzhuangkucun : false,
					xiaoshoudanjia : false,
					zongjine : false,
					zhanghao : false,
					xingming : false,
					dinghuoshijian : false,
					kehumingcheng : false,
				},
			
				ruleForm: {
					fuzhuangbianhao: '',
					fuzhuangmingcheng: '',
					shangpinfenlei: '',
					gongyingshangbianhao: '',
					gongyingshangmingcheng: '',
					lianxidianhua: '',
					fuzhuangtupian: '',
					yanse: '',
					chicun: '',
					fuzhuangkucun: '',
					xiaoshoudanjia: '',
					zongjine: '',
					zhanghao: '',
					xingming: '',
					dinghuoshijian: '',
					kehumingcheng: '',
				},
		
				shangpinfenleiOptions: [],

				rules: {
					fuzhuangbianhao: [
					],
					fuzhuangmingcheng: [
					],
					shangpinfenlei: [
					],
					gongyingshangbianhao: [
					],
					gongyingshangmingcheng: [
					],
					lianxidianhua: [
					],
					fuzhuangtupian: [
					],
					yanse: [
					],
					chicun: [
					],
					fuzhuangkucun: [
						{ validator: validateIntNumber, trigger: 'blur' },
					],
					xiaoshoudanjia: [
						{ validator: validateNumber, trigger: 'blur' },
					],
					zongjine: [
						{ validator: validateNumber, trigger: 'blur' },
					],
					zhanghao: [
					],
					xingming: [
					],
					dinghuoshijian: [
					],
					kehumingcheng: [
					],
				},
			};
		},
		props: ["parent"],
		computed: {


			zongjine:{
				get: function () {
					return 1*this.ruleForm.fuzhuangkucun*this.ruleForm.xiaoshoudanjia
				}
			},

		},
		components: {
		},
		created() {
			this.ruleForm.dinghuoshijian = this.getCurDate()
		},
		methods: {
			// 下载
			download(file){
				window.open(`${file}`)
			},
			// 初始化
			init(id,type) {
				if (id) {
					this.id = id;
					this.type = type;
				}
				if(this.type=='info'||this.type=='else'){
					this.info(id);
				}else if(this.type=='logistics'){
					this.logistics=false;
					this.info(id);
				}else if(this.type=='cross'){
					var obj = this.$storage.getObj('crossObj');
					for (var o in obj){
						if(o=='fuzhuangbianhao'){
							this.ruleForm.fuzhuangbianhao = obj[o];
							this.ro.fuzhuangbianhao = true;
							continue;
						}
						if(o=='fuzhuangmingcheng'){
							this.ruleForm.fuzhuangmingcheng = obj[o];
							this.ro.fuzhuangmingcheng = true;
							continue;
						}
						if(o=='shangpinfenlei'){
							this.ruleForm.shangpinfenlei = obj[o];
							this.ro.shangpinfenlei = true;
							continue;
						}
						if(o=='gongyingshangbianhao'){
							this.ruleForm.gongyingshangbianhao = obj[o];
							this.ro.gongyingshangbianhao = true;
							continue;
						}
						if(o=='gongyingshangmingcheng'){
							this.ruleForm.gongyingshangmingcheng = obj[o];
							this.ro.gongyingshangmingcheng = true;
							continue;
						}
						if(o=='lianxidianhua'){
							this.ruleForm.lianxidianhua = obj[o];
							this.ro.lianxidianhua = true;
							continue;
						}
						if(o=='fuzhuangtupian'){
							this.ruleForm.fuzhuangtupian = obj[o];
							this.ro.fuzhuangtupian = true;
							continue;
						}
						if(o=='yanse'){
							this.ruleForm.yanse = obj[o];
							this.ro.yanse = true;
							continue;
						}
						if(o=='chicun'){
							this.ruleForm.chicun = obj[o];
							this.ro.chicun = true;
							continue;
						}
						if(o=='fuzhuangkucun'){
							this.ruleForm.fuzhuangkucun = obj[o];
							this.ro.fuzhuangkucun = true;
							continue;
						}
						if(o=='xiaoshoudanjia'){
							this.ruleForm.xiaoshoudanjia = obj[o];
							this.ro.xiaoshoudanjia = true;
							continue;
						}
						if(o=='zongjine'){
							this.ruleForm.zongjine = obj[o];
							this.ro.zongjine = true;
							continue;
						}
						if(o=='zhanghao'){
							this.ruleForm.zhanghao = obj[o];
							this.ro.zhanghao = true;
							continue;
						}
						if(o=='xingming'){
							this.ruleForm.xingming = obj[o];
							this.ro.xingming = true;
							continue;
						}
						if(o=='dinghuoshijian'){
							this.ruleForm.dinghuoshijian = obj[o];
							this.ro.dinghuoshijian = true;
							continue;
						}
						if(o=='kehumingcheng'){
							this.ruleForm.kehumingcheng = obj[o];
							this.ro.kehumingcheng = true;
							continue;
						}
					}
					this.ruleForm.fuzhuangkucun = 0
					this.ro.fuzhuangkucun = false;
				}
				// 获取用户信息
				this.$http({
					url: `${this.$storage.get('sessionTable')}/session`,
					method: "get"
				}).then(({ data }) => {
					if (data && data.code === 0) {
						var json = data.data;
						if(((json.zhanghao!=''&&json.zhanghao) || json.zhanghao==0) && this.$storage.get("role")!="管理员"){
							this.ruleForm.zhanghao = json.zhanghao
							this.ro.zhanghao = true;
						}
						if(((json.xingming!=''&&json.xingming) || json.xingming==0) && this.$storage.get("role")!="管理员"){
							this.ruleForm.xingming = json.xingming
							this.ro.xingming = true;
						}
					} else {
						this.$message.error(data.msg);
					}
				});
				this.$http({
					url: `option/shangpinfenlei/shangpinfenlei`,
					method: "get"
				}).then(({ data }) => {
					if (data && data.code === 0) {
						this.shangpinfenleiOptions = data.data;
					} else {
						this.$message.error(data.msg);
					}
				});
			
			},
			// 多级联动参数

			info(id) {
				this.$http({
					url: `dinghuoxinxi/info/${id}`,
					method: "get"
				}).then(({ data }) => {
					if (data && data.code === 0) {
						this.ruleForm = data.data;
						//解决前台上传图片后台不显示的问题
						let reg=new RegExp('../../../upload','g')//g代表全部
					} else {
						this.$message.error(data.msg);
					}
				});
			},

			// 提交
			async onSubmit() {
					this.ruleForm.zongjine = this.zongjine
					if(this.ruleForm.fuzhuangtupian!=null) {
						this.ruleForm.fuzhuangtupian = this.ruleForm.fuzhuangtupian.replace(new RegExp(this.$base.url,"g"),"");
					}
					var objcross = this.$storage.getObj('crossObj');
					var table = this.$storage.getObj('crossTable');
					if(objcross!=null) {
						if(!this.ruleForm.fuzhuangkucun){
							this.$message.error("订货数量不能为空");
							return
						}
						objcross.fuzhuangkucun = objcross.fuzhuangkucun - this.ruleForm.fuzhuangkucun
						if(objcross.fuzhuangkucun<0){
							this.$message.error("订货数量不足");
							return
						}
											}
					await this.$refs["ruleForm"].validate(async valid => {
						if (valid) {
							if(this.type=='cross'){
								var statusColumnName = this.$storage.get('statusColumnName');
								var statusColumnValue = this.$storage.get('statusColumnValue');
								if(statusColumnName!='') {
									var obj = this.$storage.getObj('crossObj');
									if(statusColumnName && !statusColumnName.startsWith("[")) {
										for (var o in obj){
											if(o==statusColumnName){
												obj[o] = statusColumnValue;
											}
										}
										var table = this.$storage.get('crossTable');
										await this.$http({
											url: `${table}/update`,
											method: "post",
											data: obj
										}).then(({ data }) => {});
										await this.$http({
											url: `${table}/update`,
											method: "post",
											data: objcross
										}).then(({ data }) => {});
									}
								}
							}
							
							await this.$http({
								url: `dinghuoxinxi/${!this.ruleForm.id ? "save" : "update"}`,
								method: "post",
								data: this.ruleForm
							}).then(async ({ data }) => {
								if (data && data.code === 0) {
									this.$message({
										message: "操作成功",
										type: "success",
										duration: 1500,
										onClose: () => {
											this.parent.showFlag = true;
											this.parent.addOrUpdateFlag = false;
											this.parent.dinghuoxinxiCrossAddOrUpdateFlag = false;
											this.parent.search();
											this.parent.contentStyleChange();
										}
									});
									var table = this.$storage.get('crossTable');
									await this.$http({
										url: `${table}/update`,
										method: "post",
										data: objcross
									}).then(({ data }) => {});
								} else {
									this.$message.error(data.msg);
								}
							});
						}
					});
			},
			// 获取uuid
			getUUID () {
				return new Date().getTime();
			},
			// 返回
			back() {
				this.parent.showFlag = true;
				this.parent.addOrUpdateFlag = false;
				this.parent.dinghuoxinxiCrossAddOrUpdateFlag = false;
				this.parent.contentStyleChange();
			},
			fuzhuangtupianUploadChange(fileUrls) {
				this.ruleForm.fuzhuangtupian = fileUrls;
			},
		}
	};
</script>
<style lang="scss" scoped>
	.addEdit-block {
		padding: 30px;
	}
	.add-update-preview {
		padding: 40px 80px 80px 0;
		margin: 0 0 0 10px;
		background: #FFFFFF;
		display: flex;
		border-color: #eee;
		border-width: 0px 0 0;
		border-style: solid;
		flex-wrap: wrap;
	}
	.amap-wrapper {
		width: 100%;
		height: 500px;
	}
	
	.search-box {
		position: absolute;
	}
	
	.el-date-editor.el-input {
		width: auto;
	}
	.add-update-preview ::v-deep .el-form-item {
		border: 0px solid #eee;
		padding: 0;
		margin: 0 0 26px 0;
		display: inline-block;
		width: 100%;
	}
	.add-update-preview .el-form-item ::v-deep .el-form-item__label {
		padding: 0 10px 0 0;
		color: #9E9E9E;
		font-weight: 400;
		width: 180px;
		font-size: 14px;
		line-height: 40px;
		text-align: right;
	}
	
	.add-update-preview .el-form-item ::v-deep .el-form-item__content {
		margin-left: 180px;
	}
	.add-update-preview .el-form-item span.text {
		border: 1px solid #E8E8E8;
		padding: 0 10px;
		color: #333;
		background: none;
		font-weight: 500;
		display: inline-block;
		font-size: 16px;
		min-height: 200px;
		line-height: 40px;
		min-width: 100%;
	}
	
	.add-update-preview .el-input {
		width: 100%;
	}
	.add-update-preview .el-input ::v-deep .el-input__inner {
		border: 1px solid #ccc;
		border-radius: 0px;
		padding: 0 12px;
		color: #666;
		width: 100%;
		font-size: 16px;
		min-width: 50%;
		height: 40px;
	}
	.add-update-preview .el-input ::v-deep .el-input__inner[readonly="readonly"] {
		border: 1px solid #ccc;
		border-radius: 0px;
		padding: 0 12px;
		color: #666;
		width: 100%;
		font-size: 16px;
		min-width: 50%;
		height: 40px;
	}
	.add-update-preview .el-input-number {
		text-align: left;
		width: 100%;
	}
	.add-update-preview .el-input-number ::v-deep .el-input__inner {
		text-align: left;
		border: 1px solid #ccc;
		border-radius: 0px;
		padding: 0 12px;
		color: #666;
		width: 100%;
		font-size: 16px;
		min-width: 50%;
		height: 40px;
	}
	.add-update-preview .el-input-number ::v-deep .is-disabled .el-input__inner {
		text-align: left;
		border: 1px solid #ccc;
		border-radius: 0px;
		padding: 0 12px;
		color: #666;
		width: 100%;
		font-size: 16px;
		min-width: 50%;
		height: 40px;
	}
	.add-update-preview .el-input-number ::v-deep .el-input-number__decrease {
		display: none;
	}
	.add-update-preview .el-input-number ::v-deep .el-input-number__increase {
		display: none;
	}
	.add-update-preview .el-select {
		width: 100%;
	}
	.add-update-preview .el-select ::v-deep .el-input__inner {
		border: 1px solid #ccc;
		border-radius: 0px;
		padding: 0 12px;
		color: #666;
		width: 100%;
		font-size: 16px;
		min-width: 50%;
		height: 40px;
	}
	.add-update-preview .el-select ::v-deep .is-disabled .el-input__inner {
		border: 1px solid #ccc;
		border-radius: 0px;
		padding: 0 12px;
		color: #666;
		width: 100%;
		font-size: 16px;
		min-width: 50%;
		height: 40px;
	}
	.add-update-preview .el-date-editor {
		width: 100%;
	}
	.add-update-preview .el-date-editor ::v-deep .el-input__inner {
		border: 1px solid #ccc;
		border-radius: 0px;
		padding: 0 30px;
		color: #666;
		width: 100%;
		font-size: 16px;
		min-width: 50%;
		height: 40px;
	}
	.add-update-preview .el-date-editor ::v-deep .el-input__inner[readonly="readonly"] {
		border: 1px solid #ccc;
		border-radius: 0px;
		padding: 0 30px;
		color: #666;
		width: 100%;
		font-size: 16px;
		min-width: 50%;
		height: 40px;
	}
	.add-update-preview .viewBtn {
		border: 0px solid #ccc;
		cursor: pointer;
		border-radius: 0px;
		padding: 0 15px;
		margin: 0 20px 0 0;
		color: #666;
		background: #fff;
		width: auto;
		font-size: 15px;
		line-height: 34px;
		height: 34px;
		.iconfont {
			margin: 0 2px;
			color: #666;
			font-size: 16px;
			height: 34px;
		}
	}
	.add-update-preview .viewBtn:hover {
		opacity: 0.8;
	}
	.add-update-preview .downBtn {
		border: 0px solid #ccc;
		cursor: pointer;
		border-radius: 0px;
		padding: 0 15px;
		margin: 0 20px 0 0;
		color: #666;
		background: #fff;
		width: auto;
		font-size: 15px;
		line-height: 34px;
		height: 34px;
		.iconfont {
			margin: 0 2px;
			color: #666;
			font-size: 16px;
			height: 34px;
		}
	}
	.add-update-preview .downBtn:hover {
		opacity: 0.8;
	}
	.add-update-preview .unBtn {
		border: 0;
		cursor: not-allowed;
		border-radius: 4px;
		padding: 0 0px;
		margin: 0 20px 0 0;
		outline: none;
		color: #999;
		background: none;
		width: auto;
		font-size: 16px;
		line-height: 40px;
		height: 40px;
		.iconfont {
			margin: 0 2px;
			color: #fff;
			display: none;
			font-size: 14px;
			height: 34px;
		}
	}
	.add-update-preview .unBtn:hover {
		opacity: 0.8;
	}
	.add-update-preview ::v-deep .el-upload--picture-card {
		background: transparent;
		border: 0;
		border-radius: 0;
		width: auto;
		height: auto;
		line-height: initial;
		vertical-align: middle;
	}
	
	.add-update-preview ::v-deep .upload .upload-img {
		border: 1px solid #ccc;
		cursor: pointer;
		border-radius: 0px;
		color: #666;
		background: #fff;
		width: 90px;
		font-size: 24px;
		line-height: 60px;
		text-align: center;
		height: 60px;
	}
	
	.add-update-preview ::v-deep .el-upload-list .el-upload-list__item {
		border: 1px solid #ccc;
		cursor: pointer;
		border-radius: 0px;
		color: #666;
		background: #fff;
		width: 90px;
		font-size: 24px;
		line-height: 60px;
		text-align: center;
		height: 60px;
	}
	
	.add-update-preview ::v-deep .el-upload .el-icon-plus {
		border: 1px solid #ccc;
		cursor: pointer;
		border-radius: 0px;
		color: #666;
		background: #fff;
		width: 90px;
		font-size: 24px;
		line-height: 60px;
		text-align: center;
		height: 60px;
	}
	.add-update-preview ::v-deep .el-upload__tip {
		color: #666;
		font-size: 15px;
	}
	
	.add-update-preview .el-textarea ::v-deep .el-textarea__inner {
		border: 1px solid #ccc;
		border-radius: 0px;
		padding: 0 12px;
		color: #666;
		width: 100%;
		font-size: 16px;
		min-height: 200px;
		line-height: 24px;
		min-width: 100%;
		height: auto;
	}
	.add-update-preview .el-textarea ::v-deep .el-textarea__inner[readonly="readonly"] {
				border: 1px solid #ccc;
				border-radius: 0px;
				padding: 0 12px;
				color: #666;
				width: 100%;
				font-size: 16px;
				min-height: 200px;
				line-height: 24px;
				min-width: 100%;
				height: auto;
			}
	.add-update-preview .el-form-item.btn {
		padding: 0;
		margin: 20px 0 0;
		.btn1 {
			border: 0px solid #ccc;
			cursor: pointer;
			border-radius: 4px;
			padding: 0 10px;
			margin: 0 10px 0 0;
			color: #fff;
			background: #5BAAFF;
			width: auto;
			font-size: 16px;
			min-width: 110px;
			height: 40px;
			.iconfont {
				margin: 0 2px;
				color: #fff;
				display: none;
				font-size: 14px;
				height: 40px;
			}
		}
		.btn1:hover {
			opacity: 0.8;
		}
		.btn2 {
			border: 0px solid #ccc;
			cursor: pointer;
			border-radius: 4px;
			padding: 0 10px;
			margin: 0 10px 0 0;
			color: #fff;
			background: #60DFE4;
			width: auto;
			font-size: 16px;
			min-width: 110px;
			height: 40px;
			.iconfont {
				margin: 0 2px;
				color: #fff;
				display: none;
				font-size: 14px;
				height: 34px;
			}
		}
		.btn2:hover {
			opacity: 0.8;
		}
		.btn3 {
			border: 0px solid #ccc;
			cursor: pointer;
			border-radius: 4px;
			padding: 0 10px;
			margin: 0 10px 0 0;
			color: #fff;
			background: #60E495;
			width: auto;
			font-size: 16px;
			min-width: 110px;
			height: 40px;
			.iconfont {
				margin: 0 2px;
				color: #fff;
				display: none;
				font-size: 14px;
				height: 40px;
			}
		}
		.btn3:hover {
			opacity: 0.8;
		}
		.btn4 {
			border: 0px solid #ccc;
			cursor: pointer;
			border-radius: 4px;
			padding: 0 10px;
			margin: 0 10px 0 0;
			color: #fff;
			background: #C3E460;
			width: auto;
			font-size: 16px;
			min-width: 110px;
			height: 40px;
			.iconfont {
				margin: 0 2px;
				color: #fff;
				display: none;
				font-size: 14px;
				height: 40px;
			}
		}
		.btn4:hover {
			opacity: 0.8;
		}
		.btn5 {
			border: 0px solid #ccc;
			cursor: pointer;
			border-radius: 4px;
			padding: 0 10px;
			margin: 0 10px 0 0;
			color: #fff;
			background: #E4B860;
			width: auto;
			font-size: 16px;
			min-width: 110px;
			height: 40px;
			.iconfont {
				margin: 0 2px;
				color: #fff;
				display: none;
				font-size: 14px;
				height: 40px;
			}
		}
		.btn5:hover {
			opacity: 0.8;
		}
	}
</style>

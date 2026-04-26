<template>
	<div>
		<div class="register-container">
			<el-form v-if="pageFlag=='register'" ref="ruleForm" class="rgs-form animate__animated animate__backInDown" :model="ruleForm" :rules="rules">
				<div class="rgs-form2">
					<div class="title">仓库管理系统注册</div>
					<el-form-item class="list-item" v-if="tableName=='yonghu'">
						<div class="lable" :class="changeRules('zhanghao')?'required':''">账号：</div>
						<el-input  v-model="ruleForm.zhanghao"  autocomplete="off" placeholder="账号"  type="text"  />
					</el-form-item>
					<el-form-item class="list-item" v-if="tableName=='yonghu'">
						<div class="lable" :class="changeRules('xingming')?'required':''">姓名：</div>
						<el-input  v-model="ruleForm.xingming"  autocomplete="off" placeholder="姓名"  type="text"  />
					</el-form-item>
					<el-form-item class="list-item" v-if="tableName=='yonghu'">
						<div class="lable" :class="changeRules('mima')?'required':''">密码：</div>
						<el-input  v-model="ruleForm.mima"  autocomplete="off" placeholder="密码"  type="password"  />
					</el-form-item>
					<el-form-item class="list-item" v-if="tableName=='yonghu'">
						<div class="lable" :class="changeRules('mima')?'required':''">确认密码：</div>
						<el-input  v-model="ruleForm.mima2" autocomplete="off" placeholder="确认密码" type="password" />
					</el-form-item>
					<el-form-item class="list-item" v-if="tableName=='yonghu'">
						<div class="lable" :class="changeRules('xingbie')?'required':''">性别：</div>
						<el-select v-model="ruleForm.xingbie" placeholder="请选择性别" >
							<el-option
								v-for="(item,index) in yonghuxingbieOptions"
								v-bind:key="index"
								:label="item"
								:value="item">
							</el-option>
						</el-select>
					</el-form-item>
					<el-form-item class="list-item" v-if="tableName=='yonghu'">
						<div class="lable" :class="changeRules('lianxidianhua')?'required':''">联系电话：</div>
						<el-input  v-model="ruleForm.lianxidianhua"  autocomplete="off" placeholder="联系电话"  type="text"  />
					</el-form-item>
					<el-form-item class="list-item" v-if="tableName=='yonghu'">
						<div class="lable" :class="changeRules('touxiang')?'required':''">头像：</div>
						<file-upload
							tip="点击上传头像"
							action="file/upload"
							:limit="3"
							:multiple="true"
							:fileUrls="ruleForm.touxiang?ruleForm.touxiang:''"
							@change="yonghutouxiangUploadChange"
						></file-upload>
					</el-form-item>
					<el-form-item class="list-item" v-if="tableName=='yonghu'">
						<div class="lable" :class="changeRules('dianziyoujian')?'required':''">电子邮件：</div>
						<el-input  v-model="ruleForm.dianziyoujian"  autocomplete="off" placeholder="电子邮件"  type="text"  />
					</el-form-item>
					<div class="register-btn">
						<div class="register-btn1">
							<button type="button" class="r-btn" @click="login()">注册</button>
						</div>
						<div class="register-btn2">
							<div class="r-login" @click="close()">已有账号，直接登录</div>
						</div>
					</div>
				</div>
				<div class="idea-box2">输入您的账号和密码以注册帐户</div>
			</el-form>
		</div>
	</div>
</template>

<script>
	import 'animate.css'
export default {
	data() {
		return {
			ruleForm: {
			},
			forgetForm: {},
            pageFlag : '',
			tableName:"",
			rules: {},
            yonghuxingbieOptions: [],
		};
	},
	mounted(){
		this.pageFlag = this.$route.query.pageFlag
		// 进入注册页即初始化；无 loginTable 时默认 yonghu，避免请求发到 /register 等错误路径
		if (this.$route.name === 'register' || (this.$route.path && this.$route.path.indexOf('register') >= 0) || this.$route.query.pageFlag === 'register') {
			let table = this.$storage.get("loginTable")
			if (!table) {
				table = 'yonghu'
				this.$storage.set("loginTable", "yonghu")
			}
			this.tableName = table
			if(this.tableName=='yonghu'){
				this.ruleForm = {
					zhanghao: '',
					xingming: '',
					mima: '',
					mima2: '',
					xingbie: '',
					lianxidianhua: '',
					touxiang: '',
					dianziyoujian: '',
				}
			}
			if ('yonghu' == this.tableName) {
				this.rules.zhanghao = [{ required: true, message: '请输入账号', trigger: 'blur' }]
			}
			if ('yonghu' == this.tableName) {
				this.rules.xingming = [{ required: true, message: '请输入姓名', trigger: 'blur' }]
			}
			if ('yonghu' == this.tableName) {
				this.rules.mima = [{ required: true, message: '请输入密码', trigger: 'blur' }]
			}
			this.yonghuxingbieOptions = "男,女".split(',')
		}
	},
	created() {
	},
	destroyed() {
		  	},
	methods: {
		changeRules(name){
			if(this.rules[name]){
				return true
			}
			return false
		},
		// 获取uuid
		getUUID () {
			return new Date().getTime();
		},
		close(){
			this.$router.push({ path: "/login" });
		},
        yonghutouxiangUploadChange(fileUrls) {
            this.ruleForm.touxiang = fileUrls;
        },

        // 多级联动参数


		// 注册
		login() {
			var tname = this.tableName || this.$storage.get("loginTable") || "yonghu"
			if (!this.tableName) {
				this.tableName = tname
			}
			var url = tname + "/register"
			if((!this.ruleForm.zhanghao) && `yonghu` == this.tableName){
				this.$message.error(`账号不能为空`);
				return
			}
			if((!this.ruleForm.xingming) && `yonghu` == this.tableName){
				this.$message.error(`姓名不能为空`);
				return
			}
			if((!this.ruleForm.mima) && `yonghu` == this.tableName){
				this.$message.error(`密码不能为空`);
				return
			}
			if((!this.ruleForm.mima2) && `yonghu` == this.tableName){
				this.$message.error(`请再次输入确认密码`);
				return
			}
			if((this.ruleForm.mima!=this.ruleForm.mima2) && `yonghu` == this.tableName){
				this.$message.error(`两次密码输入不一致`);
				return
			}
			if(`yonghu` == this.tableName && this.ruleForm.lianxidianhua &&(!this.$validate.isMobile(this.ruleForm.lianxidianhua))){
				this.$message.error(`联系电话应输入手机格式`);
				return
			}
            if(this.ruleForm.touxiang!=null) {
                this.ruleForm.touxiang = this.ruleForm.touxiang.replace(new RegExp(this.$base.url,"g"),"");
            }
			if(`yonghu` == this.tableName && this.ruleForm.dianziyoujian &&(!this.$validate.isEmail(this.ruleForm.dianziyoujian))){
				this.$message.error(`电子邮件应输入邮件格式`);
				return
			}
			// Map frontend form fields to backend entity fields
			let payload = this.ruleForm
			if (this.tableName === 'yonghu') {
				payload = {
					yonghuzhanghao: this.ruleForm.zhanghao,
					yonghuxingming: this.ruleForm.xingming,
					mima: this.ruleForm.mima,
					xingbie: this.ruleForm.xingbie,
					lianxifangshi: this.ruleForm.lianxidianhua,
					touxiang: this.ruleForm.touxiang,
					dianziyoujian: this.ruleForm.dianziyoujian,
				}
			}

			this.$http({
				url: url,
				method: "post",
				data: payload
			}).then(({ data }) => {
				if (data && data.code === 0) {
					this.$message({
						message: "注册成功",
						type: "success",
						duration: 1500,
						onClose: () => {
							this.$router.replace({ path: "/login" });
						}
					});
				} else {
					this.$message.error((data && data.msg) ? data.msg : "注册失败");
				}
			}).catch((err) => {
				const msg = (err && err.response && err.response.data && (err.response.data.message || err.response.data.msg))
					? (err.response.data.message || err.response.data.msg)
					: (err && err.message) ? err.message : "网络异常，注册请求失败"
				this.$message.error(msg);
			});
		}
	}
};
</script>

<style lang="scss" scoped>
.register-container {
	position: relative;
	background-repeat: no-repeat;
	background-size: 100% 100%;
	display: flex;
	width: 100%;
	min-height: 100vh;
	justify-content: center;
	align-items: center;
	background-image:  url(http://codegen.caihongy.cn/20240926/a12a8b82de814927adbd5783c98082da.jpg);
	background-position: center center;
	position: relative;
	.rgs-form {
		.rgs-form2 {
		background: #fff;
		width: 75%;
		}
		border-radius: 0;
		padding: 0px 69px 40px 25%;
		box-shadow: inset 0px 0px 0px 0px #000;
		margin: auto;
		z-index: 1000;
		flex-direction: column;
		background: url("http://codegen.caihongy.cn/20240926/ee2e080ca41941cf8b9064ea8d1df11d.png") left center /  48% 130% no-repeat, #fff;
		display: flex;
		width: 1200px;
		align-items: flex-end;
		height: auto;
		.title {
			padding: 0 0px;
			margin: 30px 30px 30px -120px;
			color: #000000;
			background: none;
			font-weight: 500;
			width: calc(100% + 160px);
			font-size: 18px;
			font-family: Source Han Sans-Medium;
			line-height: 20px;
			text-align: center;
		}
		.list-item {
			padding: 0 0 0 0px;
			margin: 0 0 15px 120px;
			width: calc(100% - 120px);
			position: relative;
			height: auto;
			::v-deep .el-form-item__content {
				display: block;
			}
			.lable {
				padding: 0 10px 0 0;
				color: #000;
				left: -120px;
				letter-spacing: 1px;
				width: 120px;
				font-size: 16px;
				border-color: #000000;
				border-width: 0 0 0px 0;
				position: absolute!important;
				border-style: solid;
				text-align: right;
				height: 46px;
			}
			.el-input {
				width: 100%;
			}
			.el-input ::v-deep .el-input__inner {
				border-radius: 0;
				padding: 0 20px;
				color: #666;
				background: #fff;
				flex: 1;
				font-size: 16px;
				border-color: #000;
				border-width: 0 0 2px 0;
				border-style: solid;
				height: 46px;
			}
			.el-input ::v-deep .el-input__inner:focus {
				border-radius: 0;
				padding: 0 20px;
				color: #666;
				background: #fff;
				flex: 1;
				font-size: 16px;
				border-color: #000;
				border-width: 0 0 2px 0;
				border-style: solid;
				height: 46px;
			}
			.el-input-number {
				width: 100%;
			}
			.el-input-number ::v-deep .el-input__inner {
				text-align: center;
				border-radius: 0;
				padding: 0 20px;
				color: #666;
				background: #fff;
				flex: 1;
				font-size: 16px;
				border-color: #000;
				border-width: 0 0 2px 0;
				border-style: solid;
				height: 46px;
			}
			.el-input-number ::v-deep .el-input__inner:focus {
				border-radius: 0;
				padding: 0 20px;
				color: #666;
				background: #fff;
				flex: 1;
				font-size: 16px;
				border-color: #000;
				border-width: 0 0 2px 0;
				border-style: solid;
				height: 46px;
			}
			.el-input-number ::v-deep .el-input-number__decrease {
				display: none;
			}
			.el-input-number ::v-deep .el-input-number__increase {
				display: none;
			}
			.el-select {
				width: 100%;
			}
			.el-select ::v-deep .el-input__inner {
				border-radius: 0;
				padding: 0 20px;
				color: #666;
				background: #fff;
				flex: 1;
				font-size: 16px;
				border-color: #000;
				border-width: 0 0 2px 0;
				border-style: solid;
				height: 46px;
			}
			.el-select ::v-deep .el-input__inner:focus {
				border-radius: 0;
				padding: 0 20px;
				color: #666;
				background: #fff;
				flex: 1;
				font-size: 16px;
				border-color: #000;
				border-width: 0 0 2px 0;
				border-style: solid;
				height: 46px;
			}
			.el-date-editor {
				width: 100%;
			}
			.el-date-editor ::v-deep .el-input__inner {
				border-radius: 0;
				padding: 0 20px 0 30px;
				color: #666;
				background: #fff;
				flex: 1;
				font-size: 16px;
				border-color: #000;
				border-width: 0 0 2px 0;
				border-style: solid;
				height: 46px;
			}
			.el-date-editor ::v-deep .el-input__inner:focus {
				border-radius: 0;
				padding: 0 20px 0 30px;
				color: #666;
				background: #fff;
				flex: 1;
				font-size: 16px;
				border-color: #000;
				border-width: 0 0 2px 0;
				border-style: solid;
				height: 46px;
			}
			.el-date-editor.el-input {
				width: 100%;
			}
			::v-deep .el-upload--picture-card {
				background: transparent;
				border: 0;
				border-radius: 0;
				width: auto;
				height: auto;
				line-height: initial;
				vertical-align: middle;
			}
			::v-deep .upload .upload-img {
				border: 1px solid #efeff7;
				cursor: pointer;
				border-radius: 0px;
				margin: 0 45px;
				color: #999;
				background: #fff;
				width: 90px;
				font-size: 24px;
				line-height: 60px;
				text-align: center;
				height: 60px;
			}
			::v-deep .el-upload-list .el-upload-list__item {
				border: 1px solid #efeff7;
				cursor: pointer;
				border-radius: 0px;
				margin: 0 45px;
				color: #999;
				background: #fff;
				width: 90px;
				font-size: 24px;
				line-height: 60px;
				text-align: center;
				height: 60px;
			}
			::v-deep .el-upload .el-icon-plus {
				border: 1px solid #efeff7;
				cursor: pointer;
				border-radius: 0px;
				margin: 0 45px;
				color: #999;
				background: #fff;
				width: 90px;
				font-size: 24px;
				line-height: 60px;
				text-align: center;
				height: 60px;
			}
			::v-deep .el-upload__tip {
				color: #666;
				font-size: 15px;
			}
			::v-deep .el-input__inner::placeholder {
				color: #999;
				font-size: 16px;
			}
			.required {
				position: relative;
			}
			.required::after{
				z-index: 9;
				color: red;
				left: 110px;
				position: inherit;
				content: "*";
				order: -1;
			}
			.editor {
				margin: 0 0 0 10px;
				background: #fff;
				width: 100%;
				height: auto;
			}
			.editor>.avatar-uploader {
				line-height: 0;
				height: 0;
			}
		}
		.list-item.email {
			input {
				border-radius: 0;
				padding: 0 20px;
				color: #666;
				background: #fff;
				flex: 1;
				font-size: 16px;
				border-color: #000;
				border-width: 0 0 2px 0;
				border-style: solid;
				height: 46px;
			}
			input:focus {
				border-radius: 0;
				padding: 0 20px;
				color: #666;
				background: #fff;
				flex: 1;
				font-size: 16px;
				border-color: #000;
				border-width: 0 0 2px 0;
				border-style: solid;
				height: 46px;
			}
			input::placeholder {
				color: #999;
				font-size: 16px;
			}
			button {
				border: 0px solid #efeff7;
				cursor: pointer;
				border-radius: 0 4px 4px 0;
				padding: 0;
				margin: 1px 0 0;
				color: #333;
				background: #0d6efd20;
				width: 150px;
				font-size: 15px;
				height: 46px;
			}
			button:hover {
				opacity: 0.8;
			}
		}
		.register-btn {
			width: 100%;
		}
		.register-btn1 {
			margin: 85px 40px 0  35px;
			display: block;
			width: 100%;
		}
		.register-btn2 {
			width: 100%;
		}
		.r-btn {
			border: 0px solid rgba(0, 0, 0, 1);
			cursor: pointer;
			border-radius: 60px 60px 60px 60px;
			padding: 0 10px;
			margin: 0 0 10px;
			color: #fff;
			background: linear-gradient( 137deg, #57A5FF 0%, #90F4FC 100%);
			font-weight: 700;
			letter-spacing: 2px;
			font-size: 30px;
			min-width: 174px;
			height: 69px;
		}
		.r-btn:hover {
			border: 0px solid rgba(0, 0, 0, 1);
			opacity: 0.8;
		}
		.r-login {
			cursor: pointer;
			padding: 0;
			color: #666;
			display: inline-block;
			text-decoration: underline;
			width: 100%;
			font-size: 15px;
			line-height: 40px;
			text-align: right;
		}
		.r-login:hover {
			opacity: 1;
		}
	}
	.idea-box2 {
		margin: 5px 0 40px;
		background: #fff;
		display: none;
		width: 100%;
		font-size: 16px;
		height: 30px;
		order: -1;
	}
}
	
	::-webkit-scrollbar {
	  display: none;
	}
</style>

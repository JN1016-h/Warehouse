<template>
	<div :style='{"padding":"20px 30px","fontSize":"15px"}'>
		<!-- 用户身份信息卡片 - 需求 3.1, 3.2, 3.3 -->
		<el-card class="identity-card" :style='{"marginBottom":"20px"}' v-if="flag=='yonghu' && userIdentityInfo">
			<div slot="header" class="clearfix">
				<span>用户身份信息</span>
			</div>
			<el-descriptions :column="2" border>
				<el-descriptions-item label="用户名">
					{{ userIdentityInfo.username || ruleForm.zhanghao }}
				</el-descriptions-item>
				<el-descriptions-item label="姓名">
					{{ userIdentityInfo.name || ruleForm.xingming }}
				</el-descriptions-item>
				<el-descriptions-item label="联系电话">
					{{ userIdentityInfo.phone || ruleForm.lianxidianhua }}
				</el-descriptions-item>
				<el-descriptions-item label="电子邮件">
					{{ userIdentityInfo.email || ruleForm.dianziyoujian }}
				</el-descriptions-item>
				<el-descriptions-item label="用户角色">
					<el-tag :type="roleType" size="small" effect="plain">
						<i :class="roleIcon"></i>
						{{ roleDisplayName }}
					</el-tag>
				</el-descriptions-item>
				<el-descriptions-item label="账户创建时间">
					{{ formatDate(userIdentityInfo.createTime) }}
				</el-descriptions-item>
			</el-descriptions>
		</el-card>

		<!-- 原有的编辑表单 -->
		<el-card>
			<div slot="header" class="clearfix">
				<span>编辑个人信息</span>
			</div>
			<el-form
				:style='{"padding":"40px 80px 80px 0","borderColor":"#eee","margin":"0","flexWrap":"wrap","borderWidth":"0px 0 0","background":"#FFFFFF","display":"flex","borderStyle":"solid"}'
				class="add-update-preview"
				ref="ruleForm"
				:model="ruleForm"
				label-width="180px"
			>
					<el-form-item :style='{"border":"0px solid #eee","width":"100%","padding":"0","margin":"0 0 26px 0","display":"inline-block"}'   v-if="flag=='yonghu'"  label="账号" prop="zhanghao">
						<el-input v-model="ruleForm.zhanghao" readonly						placeholder="账号" clearable></el-input>
					</el-form-item>
					<el-form-item :style='{"border":"0px solid #eee","width":"100%","padding":"0","margin":"0 0 26px 0","display":"inline-block"}'   v-if="flag=='yonghu'"  label="姓名" prop="xingming">
						<el-input v-model="ruleForm.xingming" 						placeholder="姓名" clearable></el-input>
					</el-form-item>
					<el-form-item :style='{"border":"0px solid #eee","width":"100%","padding":"0","margin":"0 0 26px 0","display":"inline-block"}' v-if="flag=='yonghu'"  label="性别" prop="xingbie">
						<el-select v-model="ruleForm.xingbie"  placeholder="请选择性别">
							<el-option
								v-for="(item,index) in yonghuxingbieOptions"
								v-bind:key="index"
								:label="item"
								:value="item">
							</el-option>
						</el-select>
					</el-form-item>
					<el-form-item :style='{"border":"0px solid #eee","width":"100%","padding":"0","margin":"0 0 26px 0","display":"inline-block"}'   v-if="flag=='yonghu'"  label="联系电话" prop="lianxidianhua">
						<el-input v-model="ruleForm.lianxidianhua" 						placeholder="联系电话" clearable></el-input>
					</el-form-item>
					<el-form-item :style='{"border":"0px solid #eee","width":"100%","padding":"0","margin":"0 0 26px 0","display":"inline-block"}' v-if="flag=='yonghu'" label="头像" prop="touxiang">
						<file-upload
							tip="点击上传头像"
							action="file/upload"
							:limit="3"
							:multiple="true"
							:fileUrls="ruleForm.touxiang?ruleForm.touxiang:''"
							@change="yonghutouxiangUploadChange"
						></file-upload>
					</el-form-item>
					<el-form-item :style='{"border":"0px solid #eee","width":"100%","padding":"0","margin":"0 0 26px 0","display":"inline-block"}'   v-if="flag=='yonghu'"  label="电子邮件" prop="dianziyoujian">
						<el-input v-model="ruleForm.dianziyoujian" 						placeholder="电子邮件" clearable></el-input>
					</el-form-item>
					<el-form-item :style='{"border":"0px solid #eee","width":"100%","padding":"0","margin":"0 0 26px 0","display":"inline-block"}' v-if="flag=='users'" label="用户名" prop="username">
						<el-input v-model="ruleForm.username" placeholder="用户名"></el-input>
					</el-form-item>
					<el-form-item :style='{"border":"0px solid #eee","width":"100%","padding":"0","margin":"0 0 26px 0","display":"inline-block"}' v-if="flag=='users'" label="头像" prop="image">
						<file-upload
							tip="点击上传头像"
							action="file/upload"
							:limit="1"
							:multiple="false"
							:fileUrls="ruleForm.image?ruleForm.image:''"
							@change="usersimageUploadChange"
						></file-upload>
					</el-form-item>
					<el-form-item :style='{"padding":"0","margin":"20px 0 0"}'>
						<el-button class="btn3" :style='{"border":"0px solid #ccc","cursor":"pointer","padding":"0 10px","margin":"0 10px 0 0","color":"#fff","borderRadius":"4px","background":"#60E495","width":"auto","fontSize":"16px","minWidth":"110px","height":"40px"}' type="primary" @click="onUpdateHandler">
							<span class="icon iconfont icon-xihuan" :style='{"margin":"0 2px","fontSize":"14px","color":"#fff","display":"none","height":"40px"}'></span>
							提交
						</el-button>
					</el-form-item>
			</el-form>
		</el-card>
	</div>
</template>
<script>
// 校验引入
import { 
	isEmail,
	isMobile,
} from "@/utils/validate";

export default {
	data() {
		return {
			ruleForm: {},
			flag: '',
			usersFlag: false,
			yonghuxingbieOptions: [],
			userIdentityInfo: null, // 用户身份信息 - 需求 3.1, 3.2, 3.3
		};
	},
	computed: {
		/**
		 * 根据角色返回中文显示名称
		 * 需求: 3.2
		 */
		roleDisplayName() {
			if (!this.userIdentityInfo || !this.userIdentityInfo.role) {
				return '未知角色';
			}
			const roleMap = {
				'DEALER': '经销商',
				'INTERNAL_STAFF': '内部员工',
				'PLATFORM_ADMIN': '平台管理员',
				'WAREHOUSE_ADMIN': '仓库管理员'
			};
			return roleMap[this.userIdentityInfo.role] || this.userIdentityInfo.roleDisplayName || '未知角色';
		},
		/**
		 * 根据角色返回Element UI Tag的type（颜色）
		 * 需求: 3.2
		 */
		roleType() {
			if (!this.userIdentityInfo || !this.userIdentityInfo.role) {
				return '';
			}
			const typeMap = {
				'DEALER': 'info',
				'INTERNAL_STAFF': 'success',
				'PLATFORM_ADMIN': 'danger',
				'WAREHOUSE_ADMIN': 'warning'
			};
			return typeMap[this.userIdentityInfo.role] || '';
		},
		/**
		 * 根据角色返回对应的图标
		 * 需求: 3.2
		 */
		roleIcon() {
			if (!this.userIdentityInfo || !this.userIdentityInfo.role) {
				return 'el-icon-user';
			}
			const iconMap = {
				'DEALER': 'el-icon-user',
				'INTERNAL_STAFF': 'el-icon-s-custom',
				'PLATFORM_ADMIN': 'el-icon-s-tools',
				'WAREHOUSE_ADMIN': 'el-icon-office-building'
			};
			return iconMap[this.userIdentityInfo.role] || 'el-icon-user';
		}
	},
	mounted() {
		var table = this.$storage.get("sessionTable");
		this.flag = table;
		this.$http({
			url: `${this.$storage.get("sessionTable")}/session`,
			method: "get"
		}).then(({ data }) => {
			if (data && data.code === 0) {
				this.ruleForm = data.data;
				// 获取用户身份信息 - 需求 3.1, 3.2, 3.3
				if (this.flag === 'yonghu' && this.ruleForm.id) {
					this.fetchUserIdentityInfo(this.ruleForm.id);
				}
			} else {
				this.$message.error(data.msg);
			}
		});
		this.yonghuxingbieOptions = "男,女".split(',')
	},
	methods: {
		/**
		 * 获取用户身份信息
		 * 需求: 3.1, 3.2, 3.3
		 */
		fetchUserIdentityInfo(userId) {
			this.$http({
				url: `/yonghu/getUserInfo/${userId}`,
				method: "get"
			}).then(({ data }) => {
				if (data && data.code === 0) {
					this.userIdentityInfo = data.data;
					// 同步到Vuex store
					if (this.$store.state.user) {
						this.$store.commit('user/SET_USER_INFO', data.data);
					}
				} else {
					console.error('获取用户身份信息失败:', data.msg);
					// 如果API不存在，使用现有数据构建身份信息
					this.userIdentityInfo = {
						id: this.ruleForm.id,
						username: this.ruleForm.zhanghao,
						name: this.ruleForm.xingming,
						phone: this.ruleForm.lianxidianhua,
						email: this.ruleForm.dianziyoujian,
						role: this.ruleForm.userRole || 'DEALER',
						roleDisplayName: this.ruleForm.roleDisplayName || '经销商',
						createTime: this.ruleForm.addtime
					};
				}
			}).catch(error => {
				console.error('获取用户身份信息失败:', error);
				// 如果API调用失败，使用现有数据构建身份信息
				this.userIdentityInfo = {
					id: this.ruleForm.id,
					username: this.ruleForm.zhanghao,
					name: this.ruleForm.xingming,
					phone: this.ruleForm.lianxidianhua,
					email: this.ruleForm.dianziyoujian,
					role: this.ruleForm.userRole || 'DEALER',
					roleDisplayName: this.ruleForm.roleDisplayName || '经销商',
					createTime: this.ruleForm.addtime
				};
			});
		},
		/**
		 * 格式化日期
		 * 需求: 3.3
		 */
		formatDate(dateStr) {
			if (!dateStr) {
				return '未知';
			}
			try {
				const date = new Date(dateStr);
				const year = date.getFullYear();
				const month = String(date.getMonth() + 1).padStart(2, '0');
				const day = String(date.getDate()).padStart(2, '0');
				const hours = String(date.getHours()).padStart(2, '0');
				const minutes = String(date.getMinutes()).padStart(2, '0');
				const seconds = String(date.getSeconds()).padStart(2, '0');
				return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
			} catch (error) {
				return dateStr;
			}
		},
		yonghutouxiangUploadChange(fileUrls) {
			this.ruleForm.touxiang = fileUrls;
		},
		usersimageUploadChange(fileUrls) {
			this.ruleForm.image = fileUrls;
		},
		onUpdateHandler() {
			if((!this.ruleForm.zhanghao)&& 'yonghu'==this.flag){
				this.$message.error('账号不能为空');
				return
			}


			if((!this.ruleForm.xingming)&& 'yonghu'==this.flag){
				this.$message.error('姓名不能为空');
				return
			}


			if((!this.ruleForm.mima)&& 'yonghu'==this.flag){
				this.$message.error('密码不能为空');
				return
			}





			if( 'yonghu' ==this.flag && this.ruleForm.lianxidianhua&&(!isMobile(this.ruleForm.lianxidianhua))){
				this.$message.error(`联系电话应输入手机格式`);
				return
			}


			if(this.ruleForm.touxiang!=null) {
				this.ruleForm.touxiang = this.ruleForm.touxiang.replace(new RegExp(this.$base.url,"g"),"");
			}


			if( 'yonghu' ==this.flag && this.ruleForm.dianziyoujian&&(!isEmail(this.ruleForm.dianziyoujian))){
				this.$message.error(`电子邮件应输入邮箱格式`);
				return
			}
			if('users'==this.flag && this.ruleForm.username.trim().length<1) {
				this.$message.error(`用户名不能为空`);
				return	
			}
			if(this.flag=='users'){
				this.ruleForm.image = this.ruleForm.image.replace(new RegExp(this.$base.url,"g"),"")
			}
			this.$http({
				url: `${this.$storage.get("sessionTable")}/update`,
				method: "post",
				data: this.ruleForm
			}).then(({ data }) => {
				if (data && data.code === 0) {
					this.$message({
						message: "修改信息成功",
						type: "success",
						duration: 1500,
						onClose: () => {
							if(this.flag=='users'){
								this.$storage.set('headportrait',this.ruleForm.image)
							}
							// 更新成功后重新获取用户身份信息
							if (this.flag === 'yonghu' && this.ruleForm.id) {
								this.fetchUserIdentityInfo(this.ruleForm.id);
							}
						}
					});
				} else {
					this.$message.error(data.msg);
				}
			});
		}
	}
};
</script>
<style lang="scss" scoped>
	.el-date-editor.el-input {
		width: auto;
	}
	
	// 用户身份信息卡片样式 - 需求 3.1, 3.2, 3.3
	.identity-card {
		.clearfix {
			font-weight: bold;
			font-size: 16px;
		}
		
		::v-deep .el-descriptions-item__label {
			font-weight: 500;
			color: #606266;
		}
		
		::v-deep .el-descriptions-item__content {
			color: #303133;
		}
		
		.el-tag {
			i {
				margin-right: 4px;
			}
		}
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
	
	.add-update-preview ::v-deep .el-upload--picture-card {
		background: transparent;
		border: 0;
		border-radius: 0;
		width: auto;
		height: auto;
		line-height: initial;
		vertical-align: middle;
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
	
	.add-update-preview .btn3 {
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
			}
	
	.add-update-preview .btn3:hover {
				opacity: 0.8;
			}
	
	.editor>.avatar-uploader {
		line-height: 0;
		height: 0;
	}
</style>

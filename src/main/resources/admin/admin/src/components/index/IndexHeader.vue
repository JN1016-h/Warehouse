<template>
	<div class="navbar">
		<div class="title">
			<span class="title-name">{{this.$project.projectName}}</span>
		</div>
		<identity-badge class="identity-badge-box" />
		<el-dropdown class="dropdown-box" @command="handleCommand" trigger="click">
			<div class="el-dropdown-link">
				<el-image
					v-if="user"
					:src="displayAvatar"
					fit="cover"
				>
					<div slot="error" class="image-slot" style="display:flex;align-items:center;justify-content:center;height:100%;">
						<img :src="defaultAvatar" alt="avatar" style="width:100%;height:100%;object-fit:cover;border-radius:100%;" />
					</div>
				</el-image>
				<span class="label">欢迎您，</span>
				<span class="nickname">{{this.$storage.get('adminName')}}</span>
				<span class="icon iconfont icon-xiala"></span>
			</div>
			<el-dropdown-menu class="top-el-dropdown-menu" slot="dropdown">
				<el-dropdown-item class="item1" :command="''">
					<span class="icon iconfont icon-home19"></span>
					首页
				</el-dropdown-item>
				<el-dropdown-item class="item2" :command="'center'">
					<span class="icon iconfont icon-touxiang03"></span>
					个人中心
				</el-dropdown-item>
				<el-dropdown-item class="item4" :command="'logout'">
					<span class="icon iconfont icon-fanhui13"></span>
					退出登录
				</el-dropdown-item>
			</el-dropdown-menu>
		</el-dropdown>
	</div>
</template>

<script>
	import {
		Loading
	} from 'element-ui';
	import axios from 'axios';
	import IdentityBadge from '@/components/common/IdentityBadge.vue';
	
	export default {
		components: {
			IdentityBadge
		},
		data() {
			return {
				dialogVisible: false,
				ruleForm: {},
				user: null,
				defaultAvatar: require('@/assets/img/avator.png'),
			};
		},
		created() {
		},
		computed: {
			avatar(){
				return this.$storage.get('headportrait')?this.$storage.get('headportrait'):''
			},
			/** 头像：相对路径拼 base；无效时用默认，避免 404 显示「加载失败」 */
			displayAvatar() {
				if (!this.avatar) {
					return this.defaultAvatar
				}
				const a = String(this.avatar).trim()
				if (!a || a === 'null' || a === 'undefined') {
					return this.defaultAvatar
				}
				if (a.indexOf('http://') === 0 || a.indexOf('https://') === 0) {
					return a
				}
				const base = (this.$base && this.$base.url) ? this.$base.url : ''
				return base + a.replace(/^\//, '')
			},
		},
		mounted() {
			let sessionTable = this.$storage.get("sessionTable")
			this.$http({
				url: sessionTable + '/session',
				method: "get"
			}).then(({
				data
			}) => {
				if (data && data.code === 0) {
					if(sessionTable == 'yonghu') {
						const t = data.data.touxiang
						this.$storage.set('headportrait', t && String(t).trim() ? t : '')
					}
					if(sessionTable == 'users') {
						// users 表无 image 字段时勿写入无效值
						const im = data.data.image
						this.$storage.set('headportrait', im && String(im).trim() ? im : '')
					}
					this.$storage.set('userForm',JSON.stringify(data.data))
					this.user = data.data;
					this.$storage.set('userid',data.data.id);
					
					// 更新Vuex store中的用户信息（Yonghu 使用 yonghuxingming / yonghuzhanghao）
					const d = data.data
					const displayName = sessionTable === 'yonghu'
						? (d.yonghuxingming || d.yonghuzhanghao || this.$storage.get('adminName') || '用户')
						: (d.username || d.xingming || '管理员')
					const rawRole = d.userRole || d.role || ''
					const userInfo = {
						id: d.id,
						username: d.yonghuzhanghao || d.username,
						name: displayName,
						role: rawRole,
						roleDisplayName: this.getRoleDisplayName(rawRole, sessionTable)
					};
					this.$store.commit('user/SET_USER_INFO', userInfo);
				} else {
					let message = this.$message
					message.error(data.msg);
				}
			});
		},
		methods: {
			getRoleDisplayName(role, sessionTable) {
				const st = sessionTable || this.$storage.get("sessionTable")
				const roleMap = {
					'DEALER': '经销商',
					'INTERNAL_STAFF': '内部员工',
					'PLATFORM_ADMIN': '平台管理员',
					'WAREHOUSE_ADMIN': '仓库管理员',
					'管理员': '系统管理员',
				}
				// 后台 users 表：role 存中文「管理员」
				if (st === 'users') {
					if (role && roleMap[role]) {
						return roleMap[role]
					}
					return '系统管理员'
				}
				// yonghu 未设 user_role 时，用登录时选择的「用户」等
				if (st === 'yonghu' && (role == null || role === '')) {
					return this.$storage.get('role') || '用户'
				}
				return roleMap[role] || role || this.$storage.get('role') || '用户'
			},
			handleCommand(name) {
				if (name == 'logout') {
					this.onLogout()
				} 
				else {
					let router = this.$router
					name = '/'+name
					router.push(name)
				}
			},
			onLogout() {
				let storage = this.$storage
				let router = this.$router
				storage.clear()
				this.$store.dispatch('tagsView/delAllViews')
				router.replace({
					name: "login"
				});
			},
			onIndexTap(){
				window.location.href = `${this.$base.indexUrl}`
			},
		}
	};
</script>


<style lang="scss" scoped>
	.navbar {
		.title {
			top: 10px;
			left: 40px;
			display: block;
			position: absolute;
			.title-name {
				padding: 0;
				color: #132749;
				font-weight: 700;
				font-size: 36px;
				line-height: 44px;
				float: left;
			}
		}
		.identity-badge-box {
			position: absolute;
			right: 280px;
			top: 50%;
			transform: translateY(-50%);
		}
		.dropdown-box {
			color: inherit;
			display: flex;
			font-size: inherit;
			right: 20px;
			.el-dropdown-link {
				display: flex;
				align-items: center;
				.el-image {
					border-radius: 100%;
					margin: 0 10px;
					object-fit: cover;
					display: inline-block;
					width: 42px;
					height: 42px;
				}
				.label {
					color: inherit;
					display: none;
					font-size: inherit;
					line-height: 32px;
				}
				.nickname {
					color: inherit;
					display: none;
					font-size: inherit;
					line-height: 32px;
				}
				.iconfont {
					margin: 0 0 0 5px;
					color: rgba(255,255,255,.6);
					font-size: 14px;
				}
			}
			.top-el-dropdown-menu {
				border: 1px solid #EBEEF5;
				border-radius: 4px;
				padding: 10px 0;
				box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
				margin: 18px 0;
				color: #132749;
				background: #FFF;
				width: 200px;
				li.el-dropdown-menu__item.item1 {
					cursor: pointer;
					padding: 0 20px;
					margin: 0;
					outline: 0;
					color: #132749;
					background: #fff;
					font-size: 14px;
					line-height: 36px;
					list-style: none;
					.iconfont {
						margin: 0 4px 0 0;
							color: #132749;
							font-size: 14px;
						}
				}
				li.el-dropdown-menu__item.item1:hover {
					background: #ecf5ff;
				}
				li.el-dropdown-menu__item.item2 {
					cursor: pointer;
					padding: 0 20px;
					margin: 0;
					outline: 0;
					color: #132749;
					background: #fff;
					font-size: 14px;
					line-height: 36px;
					list-style: none;
					.iconfont {
						margin: 0 4px 0 0;
							color: #132749;
							font-size: 14px;
						}
				}
				li.el-dropdown-menu__item.item2:hover {
					background: #ecf5ff;
				}
				li.el-dropdown-menu__item.item4 {
					cursor: pointer;
					padding: 0 20px;
					margin: 0;
					color: #606266;
					background: #fff;
					font-size: 14px;
					line-height: 36px;
					list-style: none;
					.iconfont {
						margin: 0 4px 0 0;
						color: #132749;
						font-size: 14px;
					}
				}
				li.el-dropdown-menu__item.item4:hover {
					background: #ecf5ff;
				}
			}
		}
	}
</style>

<template>
  <div class="identity-badge">
    <el-tag :type="roleType" size="small" effect="plain">
      <i :class="roleIcon"></i>
      {{ userName }} - {{ roleDisplayName }}
    </el-tag>
  </div>
</template>

<script>
export default {
  name: 'IdentityBadge',
  computed: {
    /**
     * 从Vuex获取用户名
     */
    userName() {
      return this.$store.getters['user/userName'] || this.$store.state.user.name || '未知用户';
    },
    /**
     * 从Vuex获取用户角色
     */
    userRole() {
      return this.$store.getters['user/userRole'] || this.$store.state.user.role || '';
    },
    /**
     * 根据角色返回中文显示名称
     * 需求: 2.4, 2.5, 2.6, 2.7
     */
    roleDisplayName() {
      // 如果是管理员表登录，显示系统管理员
      const sessionTable = this.$storage.get('sessionTable');
      if (sessionTable === 'users') {
        return '系统管理员';
      }
      
      const roleMap = {
        'DEALER': '经销商',
        'INTERNAL_STAFF': '内部员工',
        'PLATFORM_ADMIN': '平台管理员',
        'WAREHOUSE_ADMIN': '仓库管理员'
      };
      return roleMap[this.userRole] || this.$store.getters['user/roleDisplayName'] || '未知角色';
    },
    /**
     * 根据角色返回Element UI Tag的type（颜色）
     * DEALER: info (灰色)
     * INTERNAL_STAFF: success (绿色)
     * PLATFORM_ADMIN: danger (红色)
     * WAREHOUSE_ADMIN: warning (橙色)
     * 系统管理员: danger (红色)
     */
    roleType() {
      // 如果是管理员表登录，使用红色标签
      const sessionTable = this.$storage.get('sessionTable');
      if (sessionTable === 'users') {
        return 'danger';
      }
      
      const typeMap = {
        'DEALER': 'info',
        'INTERNAL_STAFF': 'success',
        'PLATFORM_ADMIN': 'danger',
        'WAREHOUSE_ADMIN': 'warning'
      };
      return typeMap[this.userRole] || '';
    },
    /**
     * 根据角色返回对应的图标
     * DEALER: 用户图标
     * INTERNAL_STAFF: 员工图标
     * PLATFORM_ADMIN: 管理工具图标
     * WAREHOUSE_ADMIN: 仓库图标
     * 系统管理员: 管理工具图标
     */
    roleIcon() {
      // 如果是管理员表登录，使用管理工具图标
      const sessionTable = this.$storage.get('sessionTable');
      if (sessionTable === 'users') {
        return 'el-icon-s-tools';
      }
      
      const iconMap = {
        'DEALER': 'el-icon-user',
        'INTERNAL_STAFF': 'el-icon-s-custom',
        'PLATFORM_ADMIN': 'el-icon-s-tools',
        'WAREHOUSE_ADMIN': 'el-icon-office-building'
      };
      return iconMap[this.userRole] || 'el-icon-user';
    }
  }
}
</script>

<style lang="scss" scoped>
.identity-badge {
  display: inline-block;
  
  .el-tag {
    i {
      margin-right: 4px;
    }
  }
}
</style>

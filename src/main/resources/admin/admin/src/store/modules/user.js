import http from '@/utils/http'

const state = {
  id: null,
  username: '',
  name: '',
  role: '',
  roleDisplayName: '',
  token: ''
}

const mutations = {
  SET_USER_INFO(state, userInfo) {
    state.id = userInfo.id
    state.username = userInfo.username
    state.name = userInfo.name
    // 如果role是对象（枚举），取其name属性；否则直接使用字符串
    state.role = typeof userInfo.role === 'object' ? userInfo.role.name : userInfo.role
    state.roleDisplayName = userInfo.roleDisplayName
  },
  SET_TOKEN(state, token) {
    state.token = token
  },
  CLEAR_USER_INFO(state) {
    state.id = null
    state.username = ''
    state.name = ''
    state.role = ''
    state.roleDisplayName = ''
    state.token = ''
  }
}

const actions = {
  /**
   * 获取用户信息
   * 调用后端API获取用户详细信息（包含角色）
   */
  async getUserInfo({ commit }, userId) {
    try {
      const response = await http.get(`/yonghu/getUserInfo/${userId}`)
      if (response.data && response.data.code === 0) {
        const userInfo = response.data.data
        commit('SET_USER_INFO', userInfo)
        return userInfo
      } else {
        throw new Error(response.data.msg || '获取用户信息失败')
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
      throw error
    }
  },

  /**
   * 设置token
   */
  setToken({ commit }, token) {
    commit('SET_TOKEN', token)
  },

  /**
   * 清除用户信息（用于退出登录）
   */
  clearUserInfo({ commit }) {
    commit('CLEAR_USER_INFO')
  }
}

const getters = {
  userId: state => state.id,
  username: state => state.username,
  userName: state => state.name,
  userRole: state => state.role,
  roleDisplayName: state => state.roleDisplayName,
  token: state => state.token
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}

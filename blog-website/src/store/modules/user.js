export default {
  namespaced: true,
  state: {
    token: localStorage.getItem('token') || '',
    userInfo: null
  },
  mutations: {
    SET_TOKEN(state, token) {
      state.token = token
      localStorage.setItem('token', token)
    },
    SET_USER_INFO(state, userInfo) {
      state.userInfo = userInfo
    },
    CLEAR_USER(state) {
      state.token = ''
      state.userInfo = null
      localStorage.removeItem('token')
    }
  },
  actions: {
    // 登录
    async login({ commit }, loginForm) {
      // TODO: 调用登录API
    },
    // 登出
    async logout({ commit }) {
      commit('CLEAR_USER')
    },
    // 获取用户信息
    async getUserInfo({ commit }) {
      // TODO: 调用获取用户信息API
    }
  }
} 
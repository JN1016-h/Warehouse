import Vue from 'vue'
import Vuex from 'vuex'
import tagsView from './modules/tagsView'
import user from './modules/user'

Vue.use(Vuex)

const store = new Vuex.Store({
  modules: {
    tagsView,
    user
  }
})

export default store

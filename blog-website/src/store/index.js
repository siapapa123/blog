import { createStore } from 'vuex'
import user from './modules/user'
import article from './modules/article'

export default createStore({
  modules: {
    user,
    article
  }
}) 
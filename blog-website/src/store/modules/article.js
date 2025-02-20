export default {
  namespaced: true,
  state: {
    articles: [],
    currentArticle: null
  },
  mutations: {
    SET_ARTICLES(state, articles) {
      state.articles = articles
    },
    SET_CURRENT_ARTICLE(state, article) {
      state.currentArticle = article
    }
  },
  actions: {
    // 获取文章列表
    async getArticles({ commit }) {
      // TODO: 调用获取文章列表API
    },
    // 获取文章详情
    async getArticleById({ commit }, id) {
      // TODO: 调用获取文章详情API
    }
  }
} 
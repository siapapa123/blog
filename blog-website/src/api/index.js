import axios from 'axios'
import { message } from 'ant-design-vue'
import store from '@/store'

const service = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = store.state.user.token
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      message.error(res.message)
      return Promise.reject(new Error(res.message))
    }
    return res.data
  },
  error => {
    message.error(error.message)
    return Promise.reject(error)
  }
)

export default service 
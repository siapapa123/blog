<template>
  <div class="login-form">
    <!-- 切换登录/注册的标签页 -->
    <a-tabs 
      v-model:activeKey="activeKey" 
      centered
    >
      <a-tab-pane key="login" tab="登录">
        <a-form
          :model="loginForm"
          @finish="handleLoginFinish"
          layout="vertical"
        >
          <a-form-item
            name="account"
            :rules="[{ required: true, message: '请输入用户名或邮箱' }]"
          >
            <a-input 
              v-model:value="loginForm.account"
              placeholder="用户名/邮箱"
              size="large"
            >
              <template #prefix>
                <UserOutlined />
              </template>
            </a-input>
          </a-form-item>

          <a-form-item
            name="password"
            :rules="[{ required: true, message: '请输入密码' }]"
          >
            <a-input-password
              v-model:value="loginForm.password"
              placeholder="密码"
              size="large"
            >
              <template #prefix>
                <LockOutlined />
              </template>
            </a-input-password>
          </a-form-item>

          <a-form-item>
            <a-button
              type="primary"
              html-type="submit"
              size="large"
              block
              :loading="loading"
            >
              登录
            </a-button>
          </a-form-item>
        </a-form>
      </a-tab-pane>

      <a-tab-pane key="register" tab="注册">
        <a-form
          :model="registerForm"
          @finish="handleRegisterFinish"
          layout="vertical"
        >
          <a-form-item
            name="username"
            :rules="[
              { required: true, message: '请输入用户名' },
              { min: 3, message: '用户名至少3个字符' }
            ]"
          >
            <a-input 
              v-model:value="registerForm.username"
              placeholder="用户名"
              size="large"
            >
              <template #prefix>
                <UserOutlined />
              </template>
            </a-input>
          </a-form-item>

          <a-form-item
            name="email"
            :rules="[
              { required: true, message: '请输入邮箱' },
              { type: 'email', message: '请输入有效的邮箱地址' }
            ]"
          >
            <a-input 
              v-model:value="registerForm.email"
              placeholder="邮箱"
              size="large"
            >
              <template #prefix>
                <MailOutlined />
              </template>
            </a-input>
          </a-form-item>

          <a-form-item
            name="password"
            :rules="[
              { required: true, message: '请输入密码' },
              { min: 6, message: '密码至少6个字符' }
            ]"
          >
            <a-input-password
              v-model:value="registerForm.password"
              placeholder="密码"
              size="large"
            >
              <template #prefix>
                <LockOutlined />
              </template>
            </a-input-password>
          </a-form-item>

          <a-form-item>
            <a-button
              type="primary"
              html-type="submit"
              size="large"
              block
              :loading="loading"
            >
              注册
            </a-button>
          </a-form-item>
        </a-form>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script>
import { UserOutlined, LockOutlined, MailOutlined } from '@ant-design/icons-vue'

export default {
  name: 'LoginForm',
  components: {
    UserOutlined,
    LockOutlined,
    MailOutlined
  },
  data() {
    return {
      activeKey: 'login',
      loading: false,
      loginForm: {
        account: '',
        password: ''
      },
      registerForm: {
        username: '',
        email: '',
        password: ''
      }
    }
  },
  methods: {
    async handleLoginFinish(values) {
      this.loading = true
      try {
        // 这里添加登录逻辑
        await new Promise(resolve => setTimeout(resolve, 1000))
        this.$emit('success', { type: 'login', data: values })
      } catch (error) {
        console.error('登录失败:', error)
      } finally {
        this.loading = false
      }
    },
    async handleRegisterFinish(values) {
      this.loading = true
      try {
        // 这里添加注册逻辑
        await new Promise(resolve => setTimeout(resolve, 1000))
        this.$emit('success', { type: 'register', data: values })
      } catch (error) {
        console.error('注册失败:', error)
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.login-form {
  width: 100%;
}

:deep(.ant-tabs-nav) {
  margin-bottom: 24px;
}

:deep(.ant-input-affix-wrapper) {
  height: 40px;
  .anticon {
    margin-right: 12px;
    color: #bfbfbf;
  }

  .ant-input,
  .ant-input-password {
    padding-left: 4px;
  }
}

:deep(.ant-btn) {
  height: 40px;
}

:deep(.ant-form-item:last-child) {
  margin-bottom: 0;
}
</style> 
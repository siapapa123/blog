<template>
  <a-layout class="layout">
    <a-layout-header class="header">
      <!-- Logo -->
      <div class="logo" />

      <!-- 左侧菜单 -->
      <div class="menu-container">
        <a-menu
          v-model:selectedKeys="selectedKeys"
          mode="horizontal"
          class="main-menu"
        >
          <a-menu-item key="home">
            <router-link to="/">主页</router-link>
          </a-menu-item>
          <a-menu-item key="collection">
            <router-link to="/collection">合集</router-link>
          </a-menu-item>
        </a-menu>
      </div>

      <!-- 右侧用户头像 -->
      <div class="user-container">
        <span class="about-link">关于我</span>
        <span class="divider">|</span>
        <a-avatar 
          :size="26"
          class="user-avatar"
          @click="handleAvatarClick"
        >
          <template #icon><UserOutlined /></template>
        </a-avatar>
      </div>

      <!-- 移动端菜单按钮 -->
      <a-button
        v-if="isMobile"
        type="text"
        class="mobile-menu-btn"
        @click="showDrawer"
      >
        <menu-outlined />
      </a-button>
    </a-layout-header>

    <!-- 移动端抽屉菜单 -->
    <a-drawer
      placement="right"
      :visible="drawerVisible"
      @close="closeDrawer"
      :bodyStyle="{ padding: 0 }"
    >
      <a-menu theme="light" mode="inline" v-model:selectedKeys="selectedKeys">
        <a-menu-item key="home" @click="closeDrawer">
          <router-link to="/">主页</router-link>
        </a-menu-item>
        <a-menu-item key="collection" @click="closeDrawer">
          <router-link to="/collection">合集</router-link>
        </a-menu-item>
      </a-menu>
    </a-drawer>

    <!-- 登录弹窗 -->
    <a-modal
      v-model:visible="loginModalVisible"
      :footer="null"
      :closable="true"
      :maskClosable="false"
      width="400px"
      class="login-modal"
      @cancel="handleModalClose"
    >
      <template #title>
        <div class="modal-title">登录</div>
      </template>
      <login-form @success="handleLoginSuccess" />
    </a-modal>

    <!-- 内容区域 -->
    <a-layout-content class="content">
      <router-view></router-view>
    </a-layout-content>

    <a-layout-footer class="footer">
      Blog ©2024 Created by You
    </a-layout-footer>
  </a-layout>
</template>

<script>
import { MenuOutlined, UserOutlined } from '@ant-design/icons-vue'
import { useWindowSize } from '@vueuse/core'
import { computed } from 'vue'
import LoginForm from '@/components/LoginForm.vue'

export default {
  name: 'BasicLayout',
  components: {
    MenuOutlined,
    UserOutlined,
    LoginForm
  },
  data() {
    return {
      selectedKeys: ['home'],
      drawerVisible: false,
      loginModalVisible: false
    }
  },
  setup() {
    const { width } = useWindowSize()
    return {
      isMobile: computed(() => width.value < 768)
    }
  },
  methods: {
    showDrawer() {
      this.drawerVisible = true
    },
    closeDrawer() {
      this.drawerVisible = false
    },
    handleAvatarClick() {
      this.loginModalVisible = true
    },
    handleModalClose() {
      this.loginModalVisible = false
    },
    handleLoginSuccess() {
      this.loginModalVisible = false
      // 处理登录成功后的逻辑
    }
  }
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
}

.header {
  display: flex;
  align-items: center;
  padding: 0 24px;
  position: fixed;
  width: 100%;
  top: 0;
  z-index: 1000;
  background: #fff !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  height: 56px;
  box-sizing: border-box; /* 确保padding计入高度 */
}

.logo {
  width: 120px;
  height: 28px;
  margin-right: 24px;
  background: #1e80ff;
  display: flex;
  align-items: center;
}

.menu-container {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  height: 100%;
}

.main-menu {
  background: transparent;
  border-bottom: none;
  height: 56px;
  line-height: normal;
}

/* 修改菜单项样式 */
:deep(.ant-menu-horizontal) {
  border-bottom: none;
}

:deep(.ant-menu-item) {
  color: #1e1e1e !important;
  font-size: 15px;
  padding: 0 16px !important;
  font-weight: 500;
  margin: 0 4px !important;
  position: relative;
  height: 56px;
  line-height: 56px;
  display: flex;
  align-items: center;
  text-align: center;
}

:deep(.ant-menu-item:hover) {
  color: #1e80ff !important;
}

:deep(.ant-menu-item-selected) {
  color: #1e80ff !important;
  font-weight: 600;
}

/* 修改下划线样式 */
:deep(.ant-menu-item::after) {
  display: none !important;
}

:deep(.ant-menu-item-selected::after) {
  bottom: 0;
  left: 50%;
  width: 28px;
  height: 3.5px;
  background: #1e80ff;
  display: block !important;
  align-items: center;
  border-radius: 2px;
}

.user-container {
  margin-left: auto;
  display: flex;
  align-items: center;
  height: 100%;
}

.about-link {
  color: #1e1e1e;
  font-size: 14px;
  cursor: pointer;
  transition: color 0.3s;
}

.about-link:hover {
  color: #1e80ff;
}

.divider {
  color: #e5e6eb;
  margin: 0 12px;
}

.user-avatar {
  cursor: pointer;
  background: #1e80ff;
}

.mobile-menu-btn {
  display: none;
  color: #1e1e1e;
  height: 100%;
  display: flex;
  align-items: center;
}

.content {
  padding: 24px;
  min-height: 280px;
  margin-top: 56px; /* 调整内容区域顶部间距 */
}

.footer {
  text-align: center;
  padding: 24px;
}

@media (max-width: 768px) {
  .menu-container {
    display: none;
  }

  .mobile-menu-btn {
    display: block;
    margin-left: 16px;
  }

  .logo {
    width: 80px;
  }

  .content {
    padding: 12px;
  }
}

/* 登录弹窗样式 */
:deep(.login-modal .ant-modal-content) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.login-modal .ant-modal-header) {
  border-bottom: none;
  padding: 24px 24px 0;
}

:deep(.login-modal .ant-modal-body) {
  padding: 24px;
}

:deep(.login-modal .modal-title) {
  font-size: 18px;
  font-weight: 600;
  color: #1e1e1e;
}

:deep(.login-modal .ant-modal-close) {
  top: 16px;
  right: 16px;
}
</style> 
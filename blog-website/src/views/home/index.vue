<template>
  <div class="home">
    <a-row :gutter="[16, 16]">
      <!-- 文章列表 -->
      <a-col :xs="24" :sm="24" :md="16" :lg="18">
        <a-card>
          <h1>最新文章</h1>
          <!-- 文章列表内容 -->
        </a-card>
      </a-col>
      
      <!-- 侧边栏 -->
      <a-col :xs="24" :sm="24" :md="8" :lg="6">
        <a-card>
          <h2>分类</h2>
          <!-- 分类列表 -->
        </a-card>
        
        <a-card>
          <h2>标签</h2>
          <!-- 标签云 -->
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script>
export default {
  name: 'Home'
}
</script>

<style scoped>
/* 移动端样式调整 */
@media (max-width: 768px) {
  .home {
    padding: 12px;
  }
}
</style> 
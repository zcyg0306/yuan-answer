<template>
  <a-row id="globalHeader" align="center" :wrap="false">
    <a-col flex="auto">
      <a-menu
          mode="horizontal"
          :selected-keys="selectedKeys"
          @menu-item-click="doMenuClick"
      >
        <a-menu-item
            key="0"
            :style="{ padding: 0, marginRight: '38px' }"
            disabled
        >
          <div class="titleBar">
            <img class="logo" src="../assets/logo.png" />
            <div class="title">源 AI</div>
          </div>
        </a-menu-item>
        <a-menu-item v-for="item in visibleRoutes" :key="item.path">
          {{ item.name }}
        </a-menu-item>
      </a-menu>
    </a-col>
    <a-col flex="100px">
      <div v-if="loginUserStore.loginUser.id">
        <!-- 点击用户名显示/隐藏注销按钮 -->
        <div @click="toggleLogout">
          {{ loginUserStore.loginUser.userName ?? "无名" }}
        </div>
        <a-dropdown v-if="showLogout">
          <a-menu>
            <a-menu-item @click="logout">
              注销
            </a-menu-item>
          </a-menu>
        </a-dropdown>
      </div>
      <div v-else>
        <a-button type="primary" href="/user/login">登录</a-button>
      </div>
    </a-col>
  </a-row>
</template>

<script setup lang="ts">
import { routes } from "@/router/routes";
import { useRouter } from "vue-router";
import { computed, ref } from "vue";
import { useLoginUserStore } from "@/store/userStore";
import checkAccess from "@/access/checkAccess";
import {userLogoutUsingPost} from "@/api/userController";

const loginUserStore = useLoginUserStore();
const showLogout = ref(false);

const router = useRouter();
// 当前选中的菜单项
const selectedKeys = ref(["/"]);
// 路由跳转时，自动更新选中的菜单项
router.afterEach((to, from, failure) => {
  selectedKeys.value = [to.path];
});

// 展示在菜单栏的路由数组
const visibleRoutes = computed(() => {
  return routes.filter((item) => {
    if (item.meta?.hideInMenu) {
      return false;
    }
    // 根据权限过滤菜单
    if (!checkAccess(loginUserStore.loginUser, item.meta?.access as string)) {
      return false;
    }
    return true;
  });
});

// 点击菜单跳转到对应页面
const doMenuClick = (key: string) => {
  router.push({
    path: key,
  });
};

// 注销功能
// 切换显示注销按钮的状态
const toggleLogout = () => {
  showLogout.value = !showLogout.value;
};

// 注销函数
const logout = async () => {
  try {
    // 调用后端的注销接口
    const response = await userLogoutUsingPost();
    if (response && response.data) {
      // 手动清空 store 中的用户状态
      loginUserStore.setLoginUser({ userName: "未登录" }); // 或者使用默认未登录状态
      showLogout.value = false;

      // 刷新页面，确保状态更新
      window.location.reload(); // 彻底刷新页面，清除缓存的状态
    } else {
      console.error("注销失败");
    }
  } catch (error) {
    console.error("注销过程中发生错误:", error);
  }
};
</script>

<style scoped>
#globalHeader {
}

.titleBar {
  display: flex;
  align-items: center;
}

.title {
  margin-left: 16px;
  color: black;
}

.logo {
  height: 48px;
}
</style>
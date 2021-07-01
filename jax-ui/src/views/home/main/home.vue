<template>
  <div class="height-full flex-column">
    <v-header v-if="$root.userInfo" :user-info="$root.userInfo">
      <menu-list class="ml-50" />
    </v-header>
    <default-v-header v-else />
    <router-view />
  </div>
</template>
<script>
import { DefaultVHeader } from '../v-header';
import { MenuList } from '../menu-list';

export default {
  components: {
    DefaultVHeader,
    MenuList,
  },
};
</script>
<style lang="scss" scoped>
/deep/.v-header .logo-box {
  background: url("~@images/logo-w.svg") no-repeat center !important;
}
</style>

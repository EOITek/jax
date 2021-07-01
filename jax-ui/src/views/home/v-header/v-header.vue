<template>
  <nav class="v-default-header">
    <div class="logo-box"></div>
    <menu-list class="ml-50" />
  </nav>
</template>
<script>
import { MenuList } from '../menu-list';

export default {
  components: {
    MenuList,
  },
};
</script>
<style lang="scss" src="./v-header.scss" scoped />

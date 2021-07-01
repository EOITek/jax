<template>
  <div class="flex">
    <img class="mr-10 fs-20 pointer"
         :src="currentType === icon.type ? icon.iconActive : icon.icon"
         v-for="icon in iconList"
         :key="icon.type"
         @click="handlerClick(icon.type)" />
  </div>
</template>
<script lang="ts" src="./data-show-type.ts"></script>
<style lang="scss" scoped>
img {
  width: 20px;
}
</style>

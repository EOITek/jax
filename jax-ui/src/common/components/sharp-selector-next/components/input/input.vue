<template>
  <input :class="{ edit: editing }"
         ref="Input"
         class="input"
         v-model="item.value"
         placeholder="请输入"
         @keydown.enter="blur()"
         @focus="focus()"
         @blur="blur()" />
</template>
<script src="./input.ts"></script>
<style src="../input-style.scss" lang="scss" scoped></style>

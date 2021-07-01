<template>
  <div class="flex height-full">
    <div class="left height-full mr-10 mt-5" style="width: 49%">
      <i class="el-icon-s-promotion fs-20 mr-10 mb-10 pointer" style="float: right" @click="send"></i>
      <el-input type="textarea"
                placeholder="输入json字符串，每行一个json对象，按行发送数据"
                class="simulate-inject"
                v-model="dataStr"/>
    </div>
    <div class="right height-full flex-1 flex-column">
      <div class="mb-10 info plr-10">已注入数据</div>
      <data-show class="flex-1" :currentData="currentData" />
    </div>
  </div>
</template>
<script lang="ts" src="./simulate-inject.ts"></script>
<style lang="scss" scoped>
.info {
  line-height: 30px;
  background: $primary-color;
  width: fit-content;
  color: #fff;
}

.simulate-inject {
  height: calc(100% - 30px);

  ::v-deep textarea {
    height: 100%;
  }
}
</style>

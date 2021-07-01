<template>
  <div class="flex-cloumn log-box height-full" ref="logBox">
    <pre v-for="(log, key) in runningLogObj"
        :key="key"
        class="text-wrap"
        :class="log.type === RunningLogType.error && 'fc-red'">
      {{ log.message }}
    </pre>
  </div>
</template>
<script lang="ts" src="./running-log.ts"></script>
<style lang="scss" scoped>
.log-box {
  border: 1px #eee solid;
  overflow-y: auto;
  padding: 8px;
}
</style>

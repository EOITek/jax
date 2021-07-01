<template>
  <div class="flex-column height-full" style="padding: 10px">
    <div class="flex-1 mb-10">
      <RunningLog v-show="currentModel === 2" :runningLogList="runningLogList" />
      <ObserveDebug v-show="currentModel === 1" :observeDebugList="observeDebugList" ref="ObserveDebug" />
      <SimulateInject v-show="currentModel === 0"
                      @sendMessage="sendMessage"
                      :isStartDebug="isStartDebug"
                      :mockId="mockId" />
    </div>
    <ul class="flex">
      <li class="model pointer width-100"
          v-for="model in modelList"
          :key="model.id"
          @click="currentModel = model.id"
          :class="currentModel === model.id && 'active'">
        {{ model.label }}
      </li>
    </ul>
  </div>
</template>
<script lang="ts" src="./job-debug-drawer.ts"></script>
<style src="./job-debug-drawer.scss" scoped lang="scss"></style>

<template>
  <div :data-value="value"
       class="pipeline-status">{{PipelineStatus[value]}}</div>
</template>
<style lang="scss" src="./pipeline-status.scss" />
<script src="./pipeline-status.ts" />

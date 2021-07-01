<template>
  <vue-ace-editor :style="{ height }"
                  :options="{ wrap: true, printMargin: false, ...options }"
                  :readonly="isReadonly"
                  v-model="json"
                  :lang="lang"
                  theme="textmate" />
</template>
<script src="./json-modal.ts" />

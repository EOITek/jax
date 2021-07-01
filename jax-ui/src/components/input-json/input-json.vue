<template>
  <vue-ace-editor :options="{ wrap: true, printMargin: false , ...options }"
                  :readonly="isReadonly"
                  :style="{ height }"
                  :value="jsonValue"
                  @input="tempJson = $event"
                  @blur="onBlur"
                  @focus="tempJson = jsonStr"
                  ref="aceEditor"
                  lang="json"
                  theme="textmate" />
</template>
<script src="./input-json.ts"></script>

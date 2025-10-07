module.exports = {
    root: true,
    env: {
        node: true,
        browser: true,
        es2021: true
    },
    extends: [
        "eslint:recommended",
        "plugin:vue/vue3-recommended"
    ],
    parserOptions: {
        ecmaVersion: "latest",
        sourceType: "module"
    },
    globals: {
        defineProps: "readonly",
        defineEmits: "readonly",
        defineExpose: "readonly",
        withDefaults: "readonly"
    },
    rules: {
        // 这里可以加自定义规则
    }
}

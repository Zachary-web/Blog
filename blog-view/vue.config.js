module.exports = {
  configureWebpack: {
    resolve: {
      alias: {
        'assets': '@/assets',
        'common': '@/common',
        'components': '@/components',
        'api': '@/api',
        'views': '@/views',
        'plugins': '@/plugins'
      }
    }
  },
  // 关键修复：正确配置自定义元素识别
  chainWebpack: config => {
    config.module
        .rule('vue')
        .use('vue-loader')
        .tap(options => {
          // 确保compilerOptions存在
          options.compilerOptions = options.compilerOptions || {};
          // 配置isCustomElement，识别以meting-js为标签的元素为自定义元素
          options.compilerOptions.isCustomElement = (tag) => {
            return tag === 'meting-js'; // 精确匹配meting-js标签
          };
          return options;
        });
  },
  // 额外配置：确保运行时也能正确识别（Vue 3特需）
  runtimeCompiler: true,
  lintOnSave: false // 关闭eslint语法检查
};


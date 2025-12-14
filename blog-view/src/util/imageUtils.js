// src/utils/imageUtils.js

// 修复图片URL函数
export const fixImageUrl = (url) => {
    if (!url) return ''

    // 将HTTP强制转换为HTTPS
    if (url.startsWith('http://')) {
        return url.replace('http://', 'https://')
    }

    return url
}

// 图片错误处理函数
export const handleImageError = (e, fallbackImage = '/default-blog-image.png') => {
    const target = e.target
    console.warn('图片加载失败:', target.src)

    // 如果不是回退图片本身出错，就设置回退图片
    if (!target.src.includes(fallbackImage)) {
        target.src = fallbackImage
        target.alt = '图片加载失败'
    }
}

// 处理整个HTML内容中的图片
export const processHtmlImages = (htmlContent) => {
    if (!htmlContent) return ''

    const tempDiv = document.createElement('div')
    tempDiv.innerHTML = htmlContent

    const images = tempDiv.getElementsByTagName('img')
    for (let img of images) {
        const originalSrc = img.getAttribute('src')
        // 修复URL
        img.src = fixImageUrl(originalSrc)
        // 添加错误处理
        img.onerror = (e) => handleImageError(e)
        // 添加加载样式和属性
        img.loading = 'lazy'
        img.style.maxWidth = '100%'
        img.style.height = 'auto'
        img.style.borderRadius = '4px'
    }

    return tempDiv.innerHTML
}

# Arknights Helper
[ArknightsAutoHelper](https://github.com/ninthDevilHAUNSTER/ArknightsAutoHelper)的 Android 移植

还在早期

## 现在有什么
- 通过 Root 交互
- 两个丑陋的悬浮窗用于测试

## 将来会有什么
- [ArknightsAutoHelper](https://github.com/ninthDevilHAUNSTER/ArknightsAutoHelper) 的除开机自启动批处理以外的全部功能
- [ArknightsTap](https://github.com/IcebemAst/ArknightsTap) 的全部功能
- 更好的界面和反馈
- 免 Root 交互方式

# 警告
- 字符串硬编码, 不国际化
- Arknights Helper 假设你的环境与测试时相同, 即
  - 屏幕尺寸 1080x2160
  - 无刘海屏
  - 无颜色矫正或颜色反转
  - 最小宽度 585dp
  - 游戏全屏运行
  - 游戏异型屏适配值为 0
  - 已 Root 且授权
  - 给予悬浮窗权限

## TODO
- 分离`*.traineddata`

## 从源代码编译注意
- 下载 [OpenCV 4.5.0 Android SDK](https://sourceforge.net/projects/opencvlibrary/files/4.5.0/opencv-4.5.0-android-sdk.zip/download) 解压至`$ProjectRoot/third_party`

## Thanks

[AOSP](https://source.android.com)

[ArknightsAutoHelper](https://github.com/ninthDevilHAUNSTER/ArknightsAutoHelper)

[ArknightsTap](https://github.com/IcebemAst/ArknightsTap)

[Material](https://material.io)

[OpenCV](https://opencv.org/)

[Tesseract OCR](https://tesseract-ocr.github.io/)

[libsu](https://github.com/topjohnwu/libsu)

[preferencex-android](https://github.com/takisoft/preferencex-android)

[tess-two](https://github.com/alexcohn/tess-two)
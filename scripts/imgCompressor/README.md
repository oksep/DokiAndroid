# 图片压缩工具

运行环境为 python2，mac应该都有预装。

`python compressor.py compress /path/to/your/project`

程序会自动检索目录下的所有 `*.png`, `*.jpg`, `*.jpeg` 结尾的图片文件，并尝试压缩。

如果压缩后体积有所缩小，则保留，否则就跳过。

## 建议

Android 项目先删除目录下的 build 文件夹，以免对编译产生的图片文件进行不必要的压缩。

## 改进TODO

`./bin/pngquant-openmp` 是一个可执行 Binary 文件，会有移植问题。如果做成 gradle 插件，压缩工具也是基于 java 的话会更稳定。


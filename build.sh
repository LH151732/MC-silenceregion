#!/bin/bash
# SilenceRegion 插件编译脚本

cd "$(dirname "$0")"
mvn clean package

if [ $? -eq 0 ]; then
    echo "编译成功！"
    echo "插件文件位置: target/silenceregion-1.0-SNAPSHOT.jar"
else
    echo "编译失败！"
    exit 1
fi

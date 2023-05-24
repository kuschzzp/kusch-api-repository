# 基于 Node.js 镜像构建
FROM node:latest
# 创建一个工作目录
WORKDIR /app

# 将 package.json 和 package-lock.json 复制到工作目录
COPY package*.json ./

# 安装依赖
RUN npm install

# 将其他文件复制到工作目录
COPY . .
# 暴露端口
EXPOSE 3000

# 运行应用程序
CMD [ "node", "/app/index.js" ]

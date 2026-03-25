-----

# 智慧图库 (qianhu-picture)


> **一站式企业级图片素材存储与协同管理平台，打破个人空间与团队协作的边界。**

-----

## 📖 项目介绍

**智慧图库 (qianhu-picture)** 智慧图库是一个图片素材存储平台，用户可以在上面找到自己喜欢的图片并且上传自己喜欢的图片，并且每个用户都有自己的私人空间和团队空间，可以作为个人相册网盘一样使用，对于一个团队，他们可以对图片进行协同编辑，也可以存储团队的相关图片。

## 🖼️ 页面展示

公共图库：所有用户都可以在此处查看公共空间的图片，支持多种查询条件方便用户快速搜索到想要的图片，还免费提供图片下载功能。
<img width="1920" height="1001" alt="image" src="https://github.com/user-attachments/assets/5c1ed2fc-b401-40fe-80cc-80f74782edd2" />
<img width="1920" height="998" alt="image" src="https://github.com/user-attachments/assets/228ad110-5376-49fc-acf3-1c1a4f99af95" />

公共图库上传：用户可以在此处上传自己喜欢的图片到公共图库中，提供给其他人下载使用。支持两种不同上传方式:文件上传和URL上传，可以在此处为你喜欢的图片执行改名、图片介绍和图片分类操作，提供图片编辑和AI扩图功能。
<img width="1920" height="999" alt="image" src="https://github.com/user-attachments/assets/f34606db-beb2-406a-be14-40b84eb8b1a1" />

图片管理：管理员可以在此处看到公共图库上传图片的详细信息并进行审核管理，只有审核通过的图片才允许在公共图库中展示。
<img width="1920" height="999" alt="image" src="https://github.com/user-attachments/assets/4459aca5-a386-4051-b9da-8478f5003fab" />

个人空间/团队空间：用户/团队成员可以在空间中上传自己的图片，空间中上传的图片不会被展示在公共图库当中，并且可以对该空间内的图片进行管理(新增、删除、修改名称等)。对于团队空间，团队管理员可以为自己的团队空间添加成员并给予权限，根据不同权限，成员在团队空间内可执行的操作也有所限制。
<img width="1920" height="999" alt="image" src="https://github.com/user-attachments/assets/2fb7eea3-e577-49d0-9d40-21d498fb395f" />
<img width="1920" height="998" alt="image" src="https://github.com/user-attachments/assets/db40aaf3-33b0-4a15-bcc4-58f1519f55ca" />

协同编辑：同个团队空间的成员可以对图片进行编辑并实时展示给其他成员查看
<img width="1995" height="996" alt="image" src="https://github.com/user-attachments/assets/92912169-63a7-4897-a557-ea703f6060cd" />

## 🛠️ 技术选型

| 分类 | 技术 |
|------|------|
| 基础框架 | Spring Boot |
| 数据层 | MySQL + MyBatis Plus |
| 缓存 | Redis + Caffeine |
| 实时通信 | WebSocket |
| 存储 | 腾讯云 COS |
| 权限控制 | Sa-Token |

## 💡 项目亮点

### 🔐 权限管理
基于 Sa-Token 实现 RBAC 权限模型，支持用户、角色、权限三级管理，团队空间内成员权限可细化到按钮级别。

### 🤝 协作编辑
基于 WebSocket 实现多人在线编辑时的状态同步，成员编辑图片信息时，其他成员可实时看到更新。

### 🚀 查询优化
采用 Redis 分布式缓存 + Caffeine 本地缓存，降低数据库压力，提升热点数据访问速度。

### ☁️ 图片存管
集成腾讯云 COS 对象存储，实现图片的云端存储与下载服务，实现图片压缩和格式转换减少图片文件大小，降低成本并提高图片加载速度。






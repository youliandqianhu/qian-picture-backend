-----

# 千户图阁 (qianhu-picture)


> **一站式图片素材存储与协同管理平台，打破个人空间与团队协作的边界。**

-----

## 📖 项目介绍

**千户图阁 (qianhu-picture)** 千户图阁是一个图片素材存储平台，用户可以在上面找到自己喜欢的图片并且上传自己喜欢的图片，并且每个用户都有自己的私人空间和团队空间，可以作为个人相册网盘一样使用，对于一个团队，他们可以对图片进行协同编辑，也可以存储团队的相关图片。

公共图库：所有用户都可以在此处查看公共空间的图片，支持多种查询条件方便用户快速搜索到想要的图片，还免费提供图片下载功能。
<img width="1920" height="998" alt="image" src="https://github.com/user-attachments/assets/92f0deb4-99e1-4c36-98e0-0bf6586a5a24" />
<img width="1920" height="998" alt="image" src="https://github.com/user-attachments/assets/41310a44-f341-4ce5-9822-18e3bfb0f348" />

公共图库上传：用户可以在此处上传自己喜欢的图片到公共图库中，提供给其他人下载使用。支持两种不同上传方式:文件上传和URL上传，可以在此处为你喜欢的图片执行改名、图片介绍和图片分类操作，提供图片编辑和AI扩图功能。
<img width="1920" height="998" alt="image" src="https://github.com/user-attachments/assets/3345222b-badb-4be9-8cd9-563ac20bc868" />

图片管理：管理员可以在此处看到公共图库上传图片的详细信息并进行审核管理，只有审核通过的图片才允许在公共图库中展示。
<img width="1920" height="998" alt="image" src="https://github.com/user-attachments/assets/1f7de280-8752-4d38-96db-a6b75109c9a4" />

个人空间/团队空间：用户/团队成员可以在空间中上传自己的图片，空间中上传的图片不会被展示在公共图库当中，并且可以对该空间内的图片进行管理(新增、删除、修改名称等)。对于团队空间，团队管理员可以为自己的团队空间添加成员并给予权限，根据不同权限，成员在团队空间内可执行的操作也有所限制。
<img width="1920" height="998" alt="image" src="https://github.com/user-attachments/assets/03038c6c-d138-4967-bf1e-ce564096c606" />
<img width="1920" height="998" alt="image" src="https://github.com/user-attachments/assets/0153f53c-3375-4222-aefe-7ea939a0603c" />

协同编辑：同个团队空间的成员可以对图片进行编辑并实时展示给其他成员查看
<img width="1911" height="995" alt="image" src="https://github.com/user-attachments/assets/c968e0f2-be2e-43a2-8d35-dff50aa93278" />


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






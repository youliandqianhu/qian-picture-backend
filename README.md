-----

# 智慧图库 (qianhu-picture)

<img width="1920" height="980" alt="image" src="https://github.com/user-attachments/assets/223188fd-0e38-4fee-951a-9e940707f1b9" />


> **一站式企业级图片素材存储与协同管理平台，打破个人空间与团队协作的边界。**

-----

## 📖 项目介绍

**智慧图库 (qianhu-picture)** 智慧图库是一个图片素材存储平台，用户可以在上面找到自己喜欢的图片并且上传自己喜欢的图片，并且每个用户都有自己的私人空间和团队空间，可以作为个人相册网盘一样使用，对于一个团队，他们可以对图片进行协同编辑，也可以存储团队的相关图片。

## 🛠️ 技术选型

### 后端架构

| **Spring Boot** |

| **MySQL数据库 + MyBatis Plus** |

| **Redis分布式缓存 + Caffeine 本地缓存** |

| **WebSocket 双向通信** |

| **腾讯云 COS** |

## 🖼️ 页面展示

# 主页
所有用户都可以在此处看到公共空间下的图片，支持搜索快速找到想要的图片。
<img width="1920" height="1001" alt="image" src="https://github.com/user-attachments/assets/5c1ed2fc-b401-40fe-80cc-80f74782edd2" />

# 创建图片
用户可以在此处上传自己喜欢的图片到公共图库中，提供给其他人下载使用。
<img width="1920" height="999" alt="image" src="https://github.com/user-attachments/assets/f34606db-beb2-406a-be14-40b84eb8b1a1" />

# 用户管理
管理员可以在此处对站内用户进行管理
<img width="1920" height="999" alt="image" src="https://github.com/user-attachments/assets/26cfc302-1bd8-496c-9ca7-7c54ec686c6d" />

# 图片管理
管理员可以在此处对公共图库上传的图片进行审核，只有审核通过的图片才允许在公共图库中展示。
<img width="1920" height="999" alt="image" src="https://github.com/user-attachments/assets/4459aca5-a386-4051-b9da-8478f5003fab" />

# 图片预览
此处可以查看该图片的相关信息，例如图片大小、长宽、图片格式、图片上传作者等...
<img width="1920" height="998" alt="image" src="https://github.com/user-attachments/assets/228ad110-5376-49fc-acf3-1c1a4f99af95" />

# 个人空间/团队空间
用户/团队成员可以在空间中上传自己的图片，空间中上传的图片不会被展示在公共图库当中，并且可以对该空间内的图片进行管理(新增、删除、修改名称等)。
对于团队空间，团队管理员可以为自己的团队空间添加成员并给予权限，根据不同权限，成员在团队空间内可执行的操作也有所限制。
<img width="1920" height="999" alt="image" src="https://github.com/user-attachments/assets/2fb7eea3-e577-49d0-9d40-21d498fb395f" />
<img width="1920" height="998" alt="image" src="https://github.com/user-attachments/assets/db40aaf3-33b0-4a15-bcc4-58f1519f55ca" />

# 空间管理
管理员可以在此次对各个用户的空间进行管理和分析
<img width="1920" height="999" alt="image" src="https://github.com/user-attachments/assets/71d43b86-baaa-4fd9-8a7b-4d820f555a11" />

# websocket协同编辑
同个团队空间的成员可以对图片进行编辑并实时展示给其他成员查看
<img width="1995" height="996" alt="image" src="https://github.com/user-attachments/assets/92912169-63a7-4897-a557-ea703f6060cd" />








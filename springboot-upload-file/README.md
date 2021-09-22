### 单文件上传
```html
<form enctype="multipart/form-data" action="/upload" method="post">
    <input type="file" name="uploadFile" value="请选择文件">
    <button type="submit">上传</button>
</form>
```

这里直接上传至静态资源目录，方便直接通过静态资源进行查看，仅供测试。

实际生产环境一般采用单独的目录或独立的文件服务进行存储。

- 默认单文件上传时，限制大小为1M

> org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException: The field uploadFile exceeds its maximum permitted size of 1048576 bytes.

可通过配置文件修改：

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 2MB # 1M by default
      max-request-size: 20MB # 10M by default
```

### 多文件上传
```html
<form enctype="multipart/form-data" action="/uploads" method="post">
    <input type="file" name="uploadFiles" value="请选择文件" multiple>
    <button type="submit">上传</button>
</form>
```

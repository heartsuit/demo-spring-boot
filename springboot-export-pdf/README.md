### 背景

日常工作中，曾遇到过导出数据为 `PDF` 的需求，这里做个简单总结。当前业务共涉及到四个实体类，后台将不同实体的数据组装后导出为 `PDF` 文件。

* 领域模型

1. StdCommittee
2. StdCommitteeBranch
3. StdCommitteeSecretariat
4. StdCommitteeSecretariatStaff
w
* 实体关系

![2022-01-02-ObjectRelation.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2022-01-02-ObjectRelation.jpg)

* 涉及技术

`SpringBoot` 、 `MyBatisPlus` 、 `itextpdf` 、 `ConfigProperties` 自定义配置。

* 导出接口

```java
/**
 * @Author Heartsuit
 * @Date 2022-01-02
 */
@RestController
@RequestMapping("committee")
public class StdCommitteeController {
    @Autowired
    private IStdCommitteeService stdCommitteeService;

    /**
     * 导出申报书
     */
    @GetMapping("/download/{id}")
    public void downloadPdf(@PathVariable Long id, HttpServletResponse response)
    {
        StdCommittee stdCommittee = stdCommitteeService.getById(id);

        OutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(response.getOutputStream());
            //生成pdf文件
            stdCommitteeService.generatePdf(stdCommittee, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
```

### 导出数据为PDF

* 配置文件

```yml
server:
  port: 8080

# spring配置
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/standard-core?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
    username: root
    password: root

# mybatisplus配置
mybatis-plus:
  # 搜索指定包别名
  typeAliasesPackage: com.heartsuit.domain
  # 配置mapper的扫描，找到所有的mapper.xml映射文件
  mapper-locations: classpath:mapper/**/*.xml
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

* 核心服务

```java
/**
 * @Author Heartsuit
 * @Date 2022-01-02
 */
@Service
public class StdCommitteeServiceImpl extends ServiceImpl<StdCommitteeMapper, StdCommittee> implements IStdCommitteeService {

    @Autowired
    private IStdCommitteeSecretariatService stdCommitteeSecretariatService;

    @Autowired
    private IStdCommitteeSecretariatStaffService stdCommitteeSecretariatStaffService;

    @Autowired
    private IStdCommitteeBranchService stdCommitteeBranchService;

    private void buildCommittee(StdCommittee stdCommittee) {
        // one 2 one
        StdCommitteeSecretariat stdCommitteeSecretariat = stdCommitteeSecretariatService.getOne(new QueryWrapper<StdCommitteeSecretariat>()
                .lambda().eq(StdCommitteeSecretariat::getCommitteeId, stdCommittee.getId()));

        // one 2 many
        List<StdCommitteeSecretariatStaff> staffs = stdCommitteeSecretariatStaffService.list(new QueryWrapper<StdCommitteeSecretariatStaff>()
                .lambda().eq(StdCommitteeSecretariatStaff::getCommitteeSecretariatId, stdCommitteeSecretariat.getId()));
        stdCommitteeSecretariat.setStdCommitteeSecretariatStaffs(staffs);
        stdCommittee.setStdCommitteeSecretariat(stdCommitteeSecretariat);

        // one 2 many
        List<StdCommitteeBranch> branches = stdCommitteeBranchService.list(new QueryWrapper<StdCommitteeBranch>()
                .lambda().eq(StdCommitteeBranch::getCommitteeId, stdCommittee.getId()));
        stdCommittee.setStdCommitteeBranches(branches);
    }

    @Override
    public void generatePdf(StdCommittee stdCommittee, OutputStream outputStream) {
        buildCommittee(stdCommittee);
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // 解决中文不显示问题
            BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

            // 标题加粗
            Font fontChina18 = new Font(bfChinese, PDFConstant.FONT_SIZE_18, Font.BOLD);
            Font fontChina15 = new Font(bfChinese, PDFConstant.FONT_SIZE_15, Font.BOLD);
            Font fontChina10 = new Font(bfChinese, PDFConstant.FONT_SIZE_10);

            // 获取pdf头文件信息
            Map<String, String> headInfo = new HashMap<>();
            headInfo.put("firstHeadInfo", "标准化技术委员会登记表");
            // 空格
            Paragraph blank1 = new Paragraph(" ", new Font(bfChinese, PDFConstant.FONT_SIZE_5));

            Paragraph firstTitle = new Paragraph(headInfo.get("firstHeadInfo"), fontChina18);
            firstTitle.setAlignment(Element.ALIGN_CENTER);// 居中
            document.add(firstTitle);

            // 添加空格
            document.add(blank1);

            // 3 创建表格
            PdfPTable table = new PdfPTable(PDFConstant.TABLE_COLUMN_NUMBER_8);// 表格总共几列
            table.setWidthPercentage(PDFConstant.TABLE_WIDTH_PERCENTAGE);// 表格宽度为100%

            PdfUtil.addTableCell(table, "名称", fontChina10, false, false, 0, 0);
            PdfUtil.addTableCell(table, stdCommittee.getName(), fontChina10, true, false, 3, 0);//跨三列

            PdfUtil.addTableCell(table, "编号", fontChina10, false, false, 0, 0);
            PdfUtil.addTableCell(table, stdCommittee.getCode(), fontChina10, true, false, 3, 0);//跨三列

            PdfUtil.addTableCell(table, "本届是第几届", fontChina10, false, false, 0, 0);
            PdfUtil.addTableCell(table, stdCommittee.getNumberSession().toString(), fontChina10, true, false, 3, 0);//跨三列

            PdfUtil.addTableCell(table, "本届成立时间", fontChina10, false, false, 0, 0);
            PdfUtil.addTableCell(table, DateFormatUtils.format(stdCommittee.getEstablishDate(), "yyyy-MM-dd"), fontChina10, true, false, 3, 0);//跨三列

            PdfUtil.addTableCell(table, "负责制修订地方标准的专业领域", fontChina10, false, true, 0, 3); //跨三行
            PdfUtil.addTableCell(table, stdCommittee.getProfessionalField(), fontChina10, true, true, 7, 3);//跨七列

            List<StdCommitteeSecretariatStaff> staffs = stdCommittee.getStdCommitteeSecretariat().getStdCommitteeSecretariatStaffs();
            PdfUtil.addTableCell(table, "技术委员会秘书处工作人员", fontChina10, false, true, 0, staffs.size() + 1);
            PdfUtil.addTableCell(table, "姓名", fontChina10, true, false, 0, 0);
            PdfUtil.addTableCell(table, "秘书类型", fontChina10, true, false, 0, 0);
            PdfUtil.addTableCell(table, "职务/职称", fontChina10, true, false, 0, 0);
            PdfUtil.addTableCell(table, "出生年月", fontChina10, true, false, 0, 0);
            PdfUtil.addTableCell(table, "学历", fontChina10, true, false, 0, 0);
            PdfUtil.addTableCell(table, "电话", fontChina10, true, false, 2, 0);
            staffs.forEach(staff -> {
                PdfUtil.addTableCell(table, staff.getName(), fontChina10, true, false, 0, 0);
                PdfUtil.addTableCell(table, staff.getType(), fontChina10, true, false, 0, 0);
                PdfUtil.addTableCell(table, staff.getProfessionalTitle(), fontChina10, true, false, 0, 0);
                PdfUtil.addTableCell(table, DateFormatUtils.format(staff.getBirthday(), "yyyy-MM-dd"), fontChina10, true, false, 0, 0);
                PdfUtil.addTableCell(table, staff.getQualification(), fontChina10, true, false, 0, 0);
                PdfUtil.addTableCell(table, staff.getPhone(), fontChina10, true, false, 2, 0);
            });

            PdfUtil.addTableCell(table, "技术委员会下设分技术委员会或标准化技术专家组", fontChina15, true, false, 8, 0);//跨8列
            PdfUtil.addTableCell(table, "id", fontChina10, true, false, 2, 0);
            PdfUtil.addTableCell(table, "编号", fontChina10, true, false, 2, 0);
            PdfUtil.addTableCell(table, "名称", fontChina10, true, false, 2, 0);
            PdfUtil.addTableCell(table, "委员数", fontChina10, true, false, 2, 0);
            stdCommittee.getStdCommitteeBranches().forEach(branch -> {
                PdfUtil.addTableCell(table, branch.getId().toString(), fontChina10, true, false, 2, 0);
                PdfUtil.addTableCell(table, branch.getCode(), fontChina10, true, false, 2, 0);
                PdfUtil.addTableCell(table, branch.getName(), fontChina10, true, false, 2, 0);
                PdfUtil.addTableCell(table, branch.getNumberMember().toString(), fontChina10, true, false, 2, 0);
            });
            document.add(table);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }
}
```

* 导出效果

直接浏览器访问： `http://localhost:8080/committee/download/1477482360448090113`

![2022-01-02-ExportPDF-Browser.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2022-01-02-ExportPDF-Browser.jpg)

或者在 `PostMan` 中访问： `http://localhost:8080/committee/download/1477482360448090113` ，不过需要保存为文件后再查看生成的PDF文件内容。

![2022-01-02-ExportPDF-PostMan.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2022-01-02-ExportPDF-PostMan.jpg)

### 添加文字水印

* 配置文件

为了方便控制是否启用文字水印，我在原配置中添加了以下自定义配置（实际生产中一般与配置中心配合使用，实现动态控制开关）：

```yml
pdf:
  watermark:
    text:
      enabled: true
      content: 'Heartsuit文字水印666'
```

* 配置类

```java
/**
 * @Author Heartsuit
 * @Date 2022-01-02
 */
@Configuration
@ConfigurationProperties(prefix = "pdf.watermark")
@Data
public class PdfConfigProperties
{
    private TextProperties text = new TextProperties();

    @Data
    public static class TextProperties{
        private Boolean enabled;
        private String content;
    }

}
```

* 核心服务类

```java
public class TextWaterMark extends PdfPageEventHelper {
    private String waterMarkText;

    public TextWaterMark(String waterMarkText) {
        this.waterMarkText = waterMarkText;
    }

    public void onEndPage(PdfWriter writer, Document document) {
        try {
            float pageWidth = document.right() + document.left();//获取pdf内容正文页面宽度
            float pageHeight = document.top() + document.bottom();//获取pdf内容正文页面高度
            //设置水印字体格式
            BaseFont base = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font waterMarkFont = new Font(base, 20, Font.BOLD, BaseColor.LIGHT_GRAY);
            PdfContentByte waterMarkPdfContent = writer.getDirectContentUnder();
            Phrase phrase = new Phrase(waterMarkText, waterMarkFont);
            //两行三列
            ColumnText.showTextAligned(waterMarkPdfContent, Element.ALIGN_CENTER, phrase,
                    pageWidth * 0.25f, pageHeight * 0.2f, 45);
            ColumnText.showTextAligned(waterMarkPdfContent, Element.ALIGN_CENTER, phrase,
                    pageWidth * 0.25f, pageHeight * 0.5f, 45);
            ColumnText.showTextAligned(waterMarkPdfContent, Element.ALIGN_CENTER, phrase,
                    pageWidth * 0.25f, pageHeight * 0.8f, 45);
            ColumnText.showTextAligned(waterMarkPdfContent, Element.ALIGN_CENTER, phrase,
                    pageWidth * 0.65f, pageHeight * 0.2f, 45);
            ColumnText.showTextAligned(waterMarkPdfContent, Element.ALIGN_CENTER, phrase,
                    pageWidth * 0.65f, pageHeight * 0.5f, 45);
            ColumnText.showTextAligned(waterMarkPdfContent, Element.ALIGN_CENTER, phrase,
                    pageWidth * 0.65f, pageHeight * 0.8f, 45);
        } catch (DocumentException | IOException de) {
            de.printStackTrace();
        }
    }
}
```

* 导出PDF代码需修改的部分

![2022-01-02-WaterMarkTextChange.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2022-01-02-WaterMarkTextChange.jpg)

* 导出效果

只有一页时，导出的PDF文件效果：

![2022-01-02-WaterMarkText.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2022-01-02-WaterMarkText.jpg)

当有多页时，导出的PDF文件效果：

![2022-01-02-WaterMarkTextPage.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2022-01-02-WaterMarkTextPage.jpg)

### 添加图片水印

* 配置文件

为了方便控制是否启用图片水印，我在原配置中添加了以下自定义配置（实际生产中一般与配置中心配合使用，实现动态控制开关）：
另外，图片文件我直接放到了 `resources` 根目录下，然后代码中通过 `Resource` 类读取到路径，传递给生产带图片水印的核心服务层方法。

```yml
pdf:
  watermark:
    text:
      enabled: false
      content: 'Heartsuit文字水印666'
    image:
      enabled: true
      file: 'avatar.jpg'
```

* 核心服务类

```java
public class ImageWaterMark extends PdfPageEventHelper {

    private String waterMarkFullFilePath;
    private Image waterMarkImage;

    public ImageWaterMark(String waterMarkFullFilePath) {
        this.waterMarkFullFilePath = waterMarkFullFilePath;
    }

    public void onEndPage(PdfWriter writer, Document document) {
        try {
            float pageWidth = document.right() + document.left();//获取pdf内容正文页面宽度
            float pageHeight = document.top() + document.bottom();//获取pdf内容正文页面高度
            PdfContentByte waterMarkPdfContent = writer.getDirectContentUnder();
            //仅设置一个图片实例对象，整个PDF文档只应用一个图片对象，极大减少因为增加图片水印导致PDF文档大小增加
            if (waterMarkImage == null) {
                waterMarkImage = Image.getInstance(waterMarkFullFilePath);
            }
            //添加水印图片，三行两列
            waterMarkPdfContent.addImage(getSingletonWaterMarkImage(waterMarkImage, pageWidth * 0.2f, pageHeight * 0.1f));
            waterMarkPdfContent.addImage(getSingletonWaterMarkImage(waterMarkImage, pageWidth * 0.2f, pageHeight * 0.4f));
            waterMarkPdfContent.addImage(getSingletonWaterMarkImage(waterMarkImage, pageWidth * 0.2f, pageHeight * 0.7f));
            waterMarkPdfContent.addImage(getSingletonWaterMarkImage(waterMarkImage, pageWidth * 0.6f, pageHeight * 0.2f));
            waterMarkPdfContent.addImage(getSingletonWaterMarkImage(waterMarkImage, pageWidth * 0.6f, pageHeight * 0.5f));
            waterMarkPdfContent.addImage(getSingletonWaterMarkImage(waterMarkImage, pageWidth * 0.6f, pageHeight * 0.8f));
            PdfGState gs = new PdfGState();
            gs.setFillOpacity(0.5f);//设置透明度
            waterMarkPdfContent.setGState(gs);
        } catch (DocumentException | IOException de) {
            de.printStackTrace();
        }
    }

    /**
     * 对一个图片对象设置展示位置等信息，该对象重复利用，减少PDF文件大小
     *
     * @param waterMarkImage
     * @param xPosition
     * @param yPosition
     * @return
     */
    private Image getSingletonWaterMarkImage(Image waterMarkImage, float xPosition, float yPosition) {
        waterMarkImage.setAbsolutePosition(xPosition, yPosition);//坐标
        waterMarkImage.setRotation(-20);//旋转 弧度
        waterMarkImage.setRotationDegrees(-45);//旋转 角度
        waterMarkImage.scalePercent(100);//依照比例缩放
        return waterMarkImage;
    }
}
```

* 导出PDF代码需修改的部分

![2022-01-02-WaterMarkImageChange.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2022-01-02-WaterMarkImageChange.jpg)

* 导出效果

![2022-01-02-WaterMarkImage.jpg](https://github.com/heartsuit/heartsuit.github.io/raw/master/pictures/2022-01-02-WaterMarkImage.jpg)

### Source Code

完整源码见 `GitHub` ：[https://github.com/heartsuit/demo-spring-boot/tree/master/springboot-export-pdf](https://github.com/heartsuit/demo-spring-boot/tree/master/springboot-export-pdf)，附数据库表模型与数据，图片水印文件以及导出的 `PDF` 样例。

### Reference

[https://blog.csdn.net/xue2xue/article/details/88663391](https://blog.csdn.net/xue2xue/article/details/88663391)

---

***If you have any questions or any bugs are found, please feel free to contact me.***

***Your comments and suggestions are welcome!***

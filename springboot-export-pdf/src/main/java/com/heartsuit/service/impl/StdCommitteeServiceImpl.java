package com.heartsuit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heartsuit.config.PdfConfigProperties;
import com.heartsuit.domain.StdCommittee;
import com.heartsuit.domain.StdCommitteeBranch;
import com.heartsuit.domain.StdCommitteeSecretariat;
import com.heartsuit.domain.StdCommitteeSecretariatStaff;
import com.heartsuit.mapper.StdCommitteeMapper;
import com.heartsuit.service.IStdCommitteeBranchService;
import com.heartsuit.service.IStdCommitteeSecretariatService;
import com.heartsuit.service.IStdCommitteeSecretariatStaffService;
import com.heartsuit.service.IStdCommitteeService;
import com.heartsuit.utils.ImageWaterMark;
import com.heartsuit.utils.PDFConstant;
import com.heartsuit.utils.PdfUtil;
import com.heartsuit.utils.TextWaterMark;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private PdfConfigProperties pdfConfigProperties;

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
            PdfWriter instance = PdfWriter.getInstance(document, outputStream);
            document.open();

            // 添加文字水印
            document.newPage();

            if (pdfConfigProperties.getText().getEnabled()) {
                instance.setPageEvent(new TextWaterMark(pdfConfigProperties.getText().getContent()));
            }

            // 添加图片水印
            if (pdfConfigProperties.getImage().getEnabled()) {
                Resource resource = new ClassPathResource(pdfConfigProperties.getImage().getFile());
                instance.setPageEvent(new ImageWaterMark(resource.getURL().getPath()));
            }

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

package com.mathfusion.domain.wrongnote.service;

import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfGeneratorService {

    private static final float MARGIN = 40;
    private static final float GAP_BETWEEN_IMAGES = 20;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    private static final float HALF_WIDTH = (PAGE_WIDTH - 2 * MARGIN - GAP_BETWEEN_IMAGES) / 2;

    public ByteArrayOutputStream generatePdfFromUrls(List<String> imageUrls) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (PDDocument document = new PDDocument()) {
            // 기본 설정
            PDRectangle pageSize = PDRectangle.A4;
            float margin = 50;
            float gutter = 20;
            float columnWidth = (pageSize.getWidth() - 2 * margin - gutter) / 2;
            float yStart = pageSize.getHeight() - margin - 20;
            int maxImagesPerColumn = 2;
            int problemNumber = 1;

            // 폰트 로드
            InputStream fontStream = getClass().getResourceAsStream("/fonts/NanumGothic-Bold.ttf");
            if (fontStream == null) throw new RuntimeException("폰트 파일을 찾을 수 없습니다.");
            PDType0Font font = PDType0Font.load(document, fontStream, true);

            // 첫 페이지 생성
            PDPage page = new PDPage(pageSize);
            document.addPage(page);
            PDPageContentStream content = new PDPageContentStream(document, page);

            // 첫 페이지 제목
            content.beginText();
            content.setFont(font, 18);
            content.newLineAtOffset(pageSize.getWidth() / 2 - 100, yStart);
            content.showText("MAPI 오답노트 모의고사");
            content.endText();

            float titleGap = 70;
            float leftY = yStart - titleGap;
            float rightY = yStart - titleGap;

            int column = 0;
            int imagesInColumn = 0;

            for (String imageUrl : imageUrls) {
                BufferedImage bufferedImage;
                try (InputStream in = new URL(imageUrl).openStream()) {
                    bufferedImage = ImageIO.read(in);
                }
                PDImageXObject image = LosslessFactory.createFromImage(document, bufferedImage);

                float scale = Math.min(columnWidth / image.getWidth(), 150f / image.getHeight());
                float drawWidth = image.getWidth() * scale;
                float drawHeight = image.getHeight() * scale;

                float x = margin + column * (columnWidth + gutter);
                float y = (column == 0 ? leftY : rightY) - drawHeight;

                // 문제 번호
                content.beginText();
                content.setFont(font, 12);
                content.newLineAtOffset(x, y + drawHeight + 5);
                content.showText(String.valueOf(problemNumber++));
                content.endText();

                // 이미지 삽입
                content.drawImage(image, x, y, drawWidth, drawHeight);

                // 풀이 공간 확보
                y -= 200;
                if (column == 0) leftY = y; else rightY = y;

                imagesInColumn++;
                if (imagesInColumn >= maxImagesPerColumn) {
                    if (column == 0) {
                        column = 1; // 오른쪽으로 전환
                        imagesInColumn = 0;
                    } else {
                        // 페이지 가득 찼을 때
                        content.close();
                        page = new PDPage(pageSize);
                        document.addPage(page);
                        content = new PDPageContentStream(document, page);
                        leftY = rightY = yStart;
                        column = 0;
                        imagesInColumn = 0;
                    }
                }
            }

            // 페이지 번호 및 세로선 추가
            int totalPages = document.getNumberOfPages();
            for (int i = 0; i < totalPages; i++) {
                PDPage p = document.getPage(i);
                PDPageContentStream cs = new PDPageContentStream(document, p, PDPageContentStream.AppendMode.APPEND, true);

                // 세로 구분선
                float xLine = margin + columnWidth + gutter / 2;
                cs.setLineWidth(1);
                cs.moveTo(xLine, margin);
                if (i == 0) {
                    cs.lineTo(xLine, pageSize.getHeight() - margin - 60);
                    cs.stroke();
                } else {
                    cs.lineTo(xLine, pageSize.getHeight() - margin);
                    cs.stroke();}

                // 페이지 번호
                cs.beginText();
                cs.setFont(font, 12);
                cs.newLineAtOffset(xLine - 3, margin / 2);
                cs.showText(String.valueOf(i + 1));
                cs.endText();

                cs.close();
            }

            content.close();
            document.save(out); // 파일로 저장하지 않고 메모리에 저장
        }

        return out;
    }
}
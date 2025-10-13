package com.mathfusion.domain.ai.rendering;

import com.aspose.tex.MathRendererOptions;
import com.aspose.tex.SvgMathRenderer;
import com.aspose.tex.SvgMathRendererOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;


@Service
@RequiredArgsConstructor
public class SegmentSvgService {

    private final SwitchSvgService switchSvgService;

    public String finalRender(String finalJson){

        String firstPass = switchSvgService.replaceLatexWithSvg(finalJson);
        // 여전히 변환이 안된 수식이 있다면
        if(containsLatex(firstPass)){
            return renderWithAspose(firstPass);
        }
        return firstPass;
    }

    // 변환 안되고 있는 라텍스 후보
    private boolean containsLatex(String text){
        return text.contains("\\frac") ||
                text.contains("\\sqrt") ||
                text.contains("\\sum") ||
                text.contains("\\int") ||
                text.contains("\\times") ||
                text.contains("\\cdot") ||
                text.contains("\\log") ||
                text.contains("\\sin") ||
                text.contains("\\cos") ||
                text.contains("\\tan") ||
                text.contains("^") ||  // 지수
                text.contains("_");   // 아래첨자
    }

    // Aspose 호출
    private String renderWithAspose(String input) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            MathRendererOptions options = new SvgMathRendererOptions();
            options.setPreamble("\\usepackage{amsmath}\n"
                    + "\\usepackage{amsfonts}\n"
                    + "\\usepackage{amssymb}");
            options.setScale(3000);
            options.setBackgroundColor(Color.WHITE);
            options.setTextColor(Color.BLACK);

            String latex = "$$" + input + "$$";
            // String latex = "\\(" + input + "\\)";

            new SvgMathRenderer().render(latex, out, options);

            return out.toString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return input;
        }
    }
}
// https://tutorials.aspose.com/tex/ko/java/customizing-output/render-lafigures-svg/
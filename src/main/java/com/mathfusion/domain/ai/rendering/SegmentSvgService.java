package com.mathfusion.domain.ai.rendering;

import com.aspose.tex.MathRendererOptions;
import com.aspose.tex.SvgMathRenderer;
import com.aspose.tex.SvgMathRendererOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;


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
        return Pattern.compile("\\\\(sum|int|log|cdot|times|sin|cos|tan|lim)\\b").matcher(text).find()
                || text.contains("\\underset")
                || text.contains("\\overset")
                || Pattern.compile("[\\^_]").matcher(text).find();
    }

    // Aspose 호출
    private String renderWithAspose(String input) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            MathRendererOptions mathoptions = new SvgMathRendererOptions();
            mathoptions.setPreamble("\\usepackage{amsmath}\n\\usepackage{amssymb}");
            mathoptions.setScale(3000);
            mathoptions.setBackgroundColor(Color.WHITE);
            mathoptions.setTextColor(Color.BLACK);

            String latex = "$$" + input + "$$";
            new SvgMathRenderer().render(latex, out, mathoptions);

            return out.toString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return input;
        }
    }
}
// https://tutorials.aspose.com/tex/ko/java/customizing-output/render-lafigures-svg/
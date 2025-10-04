package com.mathfusion.domain.ai.rendering;

import com.aspose.tex.Size2D;
import com.aspose.tex.SvgMathRenderer;
import com.aspose.tex.rendering.SvgMathRendererOptions;
import com.mathfusion.domain.ai.service.SwitchSvgService;
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
        return text.contains("\\frac")||
                text.contains("\\sqrt")||
                text.contains("\\sum")||
                text.contains("\\int");
    }

    // Aspose 호출
    private String renderWithAspose(String input){
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()){
            // Aspose api 삽입
            SvgMathRendererOptions options = new SvgMathRendererOptions();
            options.setPreamble("\\usepackage{amsmath}\n"
                    + "\\usepackage{amsfonts}\n"
                    + "\\usepackage{amssymb}");
            options.setScale(3000);
            options.setBackgroundColor(Color.WHITE);
            options.setTextColor(Color.BLACK);

            Size2D size = new Size2D.Float();
            String latex = "\\begin{equation*}\n" + input + "\n\\end{equation*}";

            new SvgMathRenderer().render(latex, out, options, size);

            return out.toString(StandardCharsets.UTF_8);
        }catch(Exception e){
            return input;
        }
    }
}
// https://tutorials.aspose.com/tex/ko/java/customizing-output/render-lafigures-svg/
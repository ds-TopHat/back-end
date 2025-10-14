package com.mathfusion.domain.ai.rendering;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class SwitchSvgService {
    private static final Pattern INLINE  = Pattern.compile("\\\\\\((.+?)\\\\\\)", Pattern.DOTALL);
    private static final Pattern DISPLAY = Pattern.compile("\\\\\\[(.+?)\\\\\\]", Pattern.DOTALL);
    private static final Pattern DOLLAR  = Pattern.compile("\\$\\$(.+?)\\$\\$", Pattern.DOTALL);

    private static final Pattern NON_LATIN = Pattern.compile("[\\p{IsHangul}\\p{InCJKUnifiedIdeographs}\\p{InHiragana}\\p{InKatakana}]+");

    // JSON 전체 문자열 순환 -> LaTeX -> SVG XML
    public String replaceLatexWithSvg(String finalJson){
        String working = finalJson;
        for(Pattern p : List.of(DOLLAR, DISPLAY, INLINE)){
            working = replacePattern(working, p);
        }
        return working;
    }

    // 특정 라텍스 패턴 -> 이미지 형태의 수식으로 치환
    private String replacePattern(String text, Pattern pattern) {
        Matcher m = pattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String latex = m.group(1).trim();
            //
            latex = latex.replaceAll("\\\\frac\\{([^}]+)\\}\\{([^}]+)\\}", "($1)/($2)");
            latex = latex.replaceAll("\\\\sqrt\\{([^}]+)\\}", "√($1)");
            latex = latex.replaceAll("\\\\cdot", "·");
            latex = latex.replaceAll("\\\\times", "×");
            latex = latex.replaceAll("\\\\log", "log");
            latex = latex.replaceAll("\\\\sin", "sin");
            latex = latex.replaceAll("\\\\cos", "cos");
            latex = latex.replaceAll("\\\\tan", "tan");
            //
            String svg = renderLatexToSvg(latex);
            String base64 = Base64.getEncoder().encodeToString(svg.getBytes(StandardCharsets.UTF_8));
            String imgTag = "<img alt=\"math\" src=\"data:image/svg+xml;base64," + base64 + "\" style=\"vertical-align:middle;\"/>"; // 보류
            m.appendReplacement(sb, Matcher.quoteReplacement(imgTag));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    // LaTeX -> SVG XML
    private String renderLatexToSvg(String latex){
        try{
            // implementation 'org.scilab.forge:jlatexmath:1.0.7'
            var formula = new org.scilab.forge.jlatexmath.TeXFormula(
                    "\\usepackage{amsmath}\n" +
                    "\\usepackage{amssymb}\n" +
                            latex);
            var icon = formula.createTeXIcon(org.scilab.forge.jlatexmath.TeXConstants.STYLE_DISPLAY, 20);
            icon.setInsets(new Insets(2,2,2,2));

            var domImpl = org.apache.batik.dom.GenericDOMImplementation.getDOMImplementation();
            var document = domImpl.createDocument("http://www.w3.org/2000/svg", "svg", null);
            var svgGen = new org.apache.batik.svggen.SVGGraphics2D(document);
            svgGen.setSVGCanvasSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));

            Graphics2D g2 = (Graphics2D) svgGen.create();
            icon.paintIcon(null, g2, 0, 0);
            g2.dispose();

            StringWriter sw = new StringWriter();
            svgGen.stream(sw, true);
            String svg = sw.toString()
                    .replace("font-family:'cmr10'", "font-family:'Noto Sans', sans-serif")
                    .replace("font-family:'cmmi10'", "font-family:'Noto Sans', sans-serif")
                    .replace("font-family:'cmsy10'", "font-family:'Noto Sans', sans-serif")
                    .replace("font-family:'cmex10'", "font-family:'Noto Sans', sans-serif");

            return svg;
        }catch(Exception e){
            return "<svg xmlns=\"http://www.w3.org/2000/svg\"><text>" + latex + "</text></svg>";
        }
    }

    // 한글/비라틴 제거
    private String sanitizeForMath(String s) {
        // 수식 내부에 섞인 한글은 제거(또는 필요시 \text{...}로 치환)
        return NON_LATIN.matcher(s).replaceAll("");
    }
}
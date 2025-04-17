package com.mathfusion.domain.qwen;

import com.alibaba.dashscope.aigc.multimodalconversation.*;
import com.alibaba.dashscope.common.*;
import com.alibaba.dashscope.exception.*;
import com.alibaba.dashscope.utils.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;

@Service
public class QwenService {

    @Value("${dashscope.api-key}")
    private String apiKey;

    public String runMultiModalExample(String imageUrl) throws ApiException, NoApiKeyException, UploadFileException {
        Constants.baseHttpApiUrl = "https://dashscope-intl.aliyuncs.com/api/v1";

        MultiModalConversation conv = new MultiModalConversation();
        MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
                .content(Arrays.asList(
                        Collections.singletonMap("image", imageUrl),
                        Collections.singletonMap("text",
                                "Please convert the math problem in the image into clear and complete text. I will use your text output as input for another language model.")
                )).build();

        MultiModalConversationParam param = MultiModalConversationParam.builder()
                .apiKey(apiKey)
                .model("qwen-vl-max")
                .message(userMessage)
                .build();

        MultiModalConversationResult result = conv.call(param);
        System.out.println(JsonUtils.toJson(result));
        return JsonUtils.toJson(result);
    }
}
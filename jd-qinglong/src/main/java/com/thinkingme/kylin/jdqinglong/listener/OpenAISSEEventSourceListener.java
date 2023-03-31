package com.thinkingme.kylin.jdqinglong.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thinkingme.kylin.bot.core.Contact;
import com.thinkingme.kylin.bot.message.support.TextMessage;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 描述：OpenAIEventSourceListener
 *
 * @author https:www.unfbx.com
 * @date 2023-02-22
 */
@Slf4j
public class OpenAISSEEventSourceListener extends EventSourceListener {

    private Contact contact;

    private StringBuffer stringBuffer = new StringBuffer();

    public OpenAISSEEventSourceListener(Contact contact) {
        this.contact = contact;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onOpen(EventSource eventSource, Response response) {
        log.info("OpenAI建立sse连接...");
    }

    /**
     * {@inheritDoc}
     */
    @SneakyThrows
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {

        if (data.equals("[DONE]")&&stringBuffer.length()>0) {
            contact.sendMessage(new TextMessage(stringBuffer.toString()));
            log.info("OpenAI返回数据：{}", stringBuffer.toString());
            stringBuffer.setLength(0);
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        // 读取Json
        ChatCompletionResponse completionResponse = mapper.readValue(data, ChatCompletionResponse.class);
        String content = completionResponse.getChoices().get(0).getDelta().getContent();
        if(!StringUtils.isEmpty(content)&&!"null".equals(content)){
            stringBuffer.append(content);
        }

    }


    @Override
    public void onClosed(EventSource eventSource) {
        log.info("OpenAI关闭sse连接...");
    }


    @SneakyThrows
    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {
        if(Objects.isNull(response)){
            return;
        }
        ResponseBody body = response.body();
        if (Objects.nonNull(body)) {
            log.error("OpenAI  sse连接异常data：{}，异常：{}", body.string(), t);
        } else {
            log.error("OpenAI  sse连接异常data：{}，异常：{}", response, t);
        }
        eventSource.cancel();
    }
}

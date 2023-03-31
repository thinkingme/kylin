package com.thinkingme.kylin.jdqinglong.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.thinkingme.kylin.bot.core.Friend;
import com.thinkingme.kylin.jdqinglong.LocalCache.LocalCache;
import com.thinkingme.kylin.jdqinglong.listener.OpenAISSEEventSourceListener;
import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * chatGpt
 * </p>
 *
 * @author: huige
 * @date: 2023/3/31 10:37
 */
@Service
public class ChatGptService {

    @Value("${chatGpt.apiKey}")
    private String apiKey;

    public void sendMessage(String msg, Friend friend){
        String messageContext = (String) LocalCache.CACHE.get(String.valueOf(friend.getUserId()));
        List<Message> messages = new ArrayList<>();
        if (StrUtil.isNotBlank(messageContext)) {
            messages = JSONUtil.toList(messageContext, Message.class);
            if (messages.size() >= 10) {
                messages = messages.subList(1, 10);
            }
            Message currentMessage = Message.builder().content(msg).role(Message.Role.USER).build();
            messages.add(currentMessage);
        } else {
            Message currentMessage = Message.builder().content(msg).role(Message.Role.USER).build();
            messages.add(currentMessage);
        }
        OpenAiStreamClient client = OpenAiStreamClient.builder()
                .apiKey(Arrays.asList(apiKey))
                //自己做了代理就传代理地址，没有可不不传
//                .apiHost("https://自己代理的服务器地址/")
                .build();
        //聊天模型：gpt-3.5
        ChatCompletion chatCompletion = ChatCompletion.builder().messages(messages).build();
        client.streamChatCompletion(chatCompletion, new OpenAISSEEventSourceListener(friend));
        LocalCache.CACHE.put(String.valueOf(friend.getUserId()), JSONUtil.toJsonStr(messages), LocalCache.TIMEOUT);
    }
}

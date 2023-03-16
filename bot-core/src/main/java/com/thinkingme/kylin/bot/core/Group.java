package com.thinkingme.kylin.bot.core;

import com.thinkingme.kylin.bot.message.MessageChain;
import com.thinkingme.kylin.bot.message.support.ForwardNodeMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author xiaoxu
 * @since 2022-05-24 10:19
 */
@AllArgsConstructor
@Getter
@Slf4j
public class Group implements Contact {

    private final long groupId;

    private final String groupName;

    private final Bot bot;

    public Member getMember(long userId) {
        try {
            return this.bot.getMember(this.groupId, userId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public int sendMessage(MessageChain messageChain) throws Exception {
        return this.bot.sendGroupMessage(this.groupId, messageChain);
    }

    public int sendTempMessage(long userId, MessageChain messageChain) {
        return this.getMember(userId).sendMessage(messageChain);
    }

    public int sendGroupForwardMessage(List<ForwardNodeMessage> messageList) throws Exception {
        return this.bot.sendGroupForwardMessage(this.groupId, messageList);
    }

    public void groupBan() throws Exception {
        this.bot.groupBan(this.groupId);
    }

    public void groupPardon() throws Exception {
        this.bot.groupPardon(this.groupId);
    }

    public void setGroupSpecialTitle(long userId, String specialTitle, Number duration) throws Exception {
        this.bot.setGroupSpecialTitle(userId, specialTitle, duration, this.groupId);
    }
}

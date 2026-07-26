package com.ai.service;

import com.ai.dao.AiChatMessageDao;
import com.ai.dao.AiChatSessionDao;
import com.ai.entity.AiChatMessageEntity;
import com.ai.entity.AiChatSessionEntity;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 对话历史持久化（NFR6 / FR8）
 */
@Service
public class ChatHistoryService {

    private final AiChatSessionDao sessionDao;
    private final AiChatMessageDao messageDao;

    public ChatHistoryService(AiChatSessionDao sessionDao, AiChatMessageDao messageDao) {
        this.sessionDao = sessionDao;
        this.messageDao = messageDao;
    }

    public AiChatSessionEntity getOrCreateSession(Long sessionId, Long userId, String tableName, String style, String firstQuestion) {
        if (sessionId != null) {
            AiChatSessionEntity exist = sessionDao.selectById(sessionId);
            if (exist != null && userId != null && userId.equals(exist.getUserId())) {
                exist.setUpdateTime(new Date());
                if (style != null) {
                    exist.setStyle(style);
                }
                sessionDao.updateById(exist);
                return exist;
            }
        }
        AiChatSessionEntity session = new AiChatSessionEntity();
        session.setUserId(userId);
        session.setTableName(tableName);
        session.setStyle(style == null ? "SIMPLE" : style);
        session.setTitle(truncate(firstQuestion, 40));
        Date now = new Date();
        session.setCreateTime(now);
        session.setUpdateTime(now);
        sessionDao.insert(session);
        return session;
    }

    public AiChatMessageEntity saveMessage(Long sessionId, String role, String content, String snapshot,
                                           boolean degraded, String intent) {
        AiChatMessageEntity msg = new AiChatMessageEntity();
        msg.setSessionId(sessionId);
        msg.setRole(role);
        msg.setContent(content);
        msg.setDataSnapshot(snapshot);
        msg.setDegraded(degraded ? 1 : 0);
        msg.setIntent(intent);
        msg.setCreateTime(new Date());
        messageDao.insert(msg);
        return msg;
    }

    public List<AiChatSessionEntity> listSessions(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        EntityWrapper<AiChatSessionEntity> ew = new EntityWrapper<AiChatSessionEntity>();
        ew.eq("user_id", userId).orderBy("update_time", false);
        List<AiChatSessionEntity> list = sessionDao.selectList(ew);
        return list == null ? Collections.<AiChatSessionEntity>emptyList() : list;
    }

    public List<AiChatMessageEntity> listMessages(Long sessionId, Long userId) {
        AiChatSessionEntity session = sessionDao.selectById(sessionId);
        if (session == null || userId == null || !userId.equals(session.getUserId())) {
            return Collections.emptyList();
        }
        EntityWrapper<AiChatMessageEntity> ew = new EntityWrapper<AiChatMessageEntity>();
        ew.eq("session_id", sessionId).orderBy("create_time", true);
        List<AiChatMessageEntity> list = messageDao.selectList(ew);
        return list == null ? Collections.<AiChatMessageEntity>emptyList() : list;
    }

    public AiChatMessageEntity getMessage(Long messageId, Long userId) {
        AiChatMessageEntity msg = messageDao.selectById(messageId);
        if (msg == null) {
            return null;
        }
        AiChatSessionEntity session = sessionDao.selectById(msg.getSessionId());
        if (session == null || userId == null || !userId.equals(session.getUserId())) {
            return null;
        }
        return msg;
    }

    private String truncate(String text, int max) {
        if (text == null) {
            return "新对话";
        }
        String t = text.trim();
        if (t.length() <= max) {
            return t;
        }
        return t.substring(0, max) + "...";
    }
}
